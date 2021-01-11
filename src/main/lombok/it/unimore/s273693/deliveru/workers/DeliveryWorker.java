package it.unimore.s273693.deliveru.workers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.AppSettings;
import it.unimore.s273693.deliveru.db.Delivery;
import it.unimore.s273693.deliveru.db.DeliveryState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class implements the Automatic Delivery logic.
 *
 * <p>
 * It spawns a new thread and once in a while it chooses a new delivery to work on,
 * it then chooses a new state for it and commits the change in the main thread.
 * </p>
 *
 * <p>
 * The generic db cannot be made asynchronous without major rewrites due to the
 * use of JavaFX ObjectProperty API so an event is passed to the main thread to
 * commit the change computed asynchronously.
 * </p>
 *
 * <p>
 * To know what deliveries can be changed a list of delivery ids is maintained
 * every operation on it is synchronized to avoid async errors.
 * To keep a list of updatable deliveries a listener is attached to each delivery and
 * when the 'state' property changes it is added or removed to the list.
 * </p>
 *
 * <p>
 * The time intervals between each action is calculated simulating an exponential
 * distribution so it should be similar to a Poisson process of specified interval.
 * </p>
 */
@RequiredArgsConstructor
public class DeliveryWorker {
    private static final Logger logger = LogManager.getLogger(DeliveryWorker.class);
    private static final double MILLISECONDS_IN_MINUTE = 1000 * 60;

    private final AppContext ctx;
    private final Random random = new Random();

    // There's no AtomicDouble in Java
    // https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/atomic/package-summary.html
    private final AtomicLong timesPerMinute = new AtomicLong(Double.doubleToLongBits(AppSettings.DEFAULT.deliveryIntensity));
    private final AtomicLong failRate = new AtomicLong(Double.doubleToLongBits(AppSettings.DEFAULT.deliveryFailRate));

    // List of processable deliveries, always synchronize on this before using it!
    private final List<Delivery> possibleDeliveries = new ArrayList<>();

    private final Map<Delivery, DeliveryListener> registeredListeners = new HashMap<>();
    private final DeliveryListListener listListener = new DeliveryListListener();

    // The running thread, only null when the worker is stopped.
    private Thread runThread;

    /**
     * Starts the worker unless it's already running.
     */
    public void start() {
        if (runThread != null) {
            return; // Already started
        }
        registerListeners();
        runThread = new Thread(this::startAsync);
        runThread.setName("Async delivery manager");
        runThread.start();
        logger.info("Started");
    }

    /**
     * Stops the worker unless it's already stopped.
     */
    public void stop() {
        if (runThread == null) return;
        runThread.interrupt();
        try {
            runThread.join();
        } catch (InterruptedException e) {
            logger.error("Error while waiting for async thread end", e);
        }
        unregisterListeners();
        possibleDeliveries.clear();

        runThread = null;
        logger.info("Stopped");
    }

    /**
     * Gets the average times per minute the an action is called.
     *
     * @return the intensity per minute
     */
    public double getTimesPerMinute() {
        return Double.longBitsToDouble(this.timesPerMinute.get());
    }

    /**
     * Changes the average actions per minute of the process.
     * The change is computed only when the previous wait finishes.
     *
     * @param timesPerMinute the new rate
     */
    public void setTimesPerMinute(double timesPerMinute) {
        this.timesPerMinute.set(Double.doubleToLongBits(timesPerMinute));
    }

    /**
     * Gets the fail rate.
     * That's the rate of times a delivery fails, with 0 meaning it cannot fail
     * and 1 meaning it always fail, any number between them is interpreted as a ratio.
     *
     * @return the current fail rate
     */
    public double getFailRate() {
        return Double.longBitsToDouble(this.failRate.get());
    }

    /**
     * Changes the delivery fail rate.
     *
     * @param failRate the new fail rate
     */
    public void setFailRate(double failRate) {
        this.failRate.set(Double.doubleToLongBits(failRate));
    }

    /**
     * Generates wait times according to the Exponential distribution.
     * This simulates the time to wait in a Poisson process.
     * The rate is the average events that will occur in an unit of time.
     * Ex: let's say that you want to trigger an event with a average of 2 times per minute,
     * The time you have to wait between each event will be (on average) 1/2 = 0.5m.
     * This function simulates the wait times.
     *
     * @param rate The rate of the event to simulate
     * @return The wait time before the next event.
     */
    private double getExpTime(double rate) {
        // Note: we could also write nextDouble() instead of 1.0 - nextDouble() but we could
        // incur in a Probabilistic error: if nextDouble returns 0 (that is possible, but not probable) Math.log(0)
        // = +Infinite, so we'll be waiting an infinite time. Using 1.0 - nextDouble() will remove that probability
        // since nextDouble returns a random double from [0, 1) (1 excluded).
        return  Math.log(1.0 - random.nextDouble()) / (-rate);
    }

    private long getWaitMs() {
        return (long) (getExpTime(getTimesPerMinute()) * MILLISECONDS_IN_MINUTE);
    }

    private void tick() {
        Delivery target;
        DeliveryState currentState;

        synchronized (this.possibleDeliveries) {
            int deliveryCount = this.possibleDeliveries.size();
            if (deliveryCount == 0) return;
            int chosen = this.random.nextInt(deliveryCount);
            target = this.possibleDeliveries.get(chosen);
            // Query inside of the synchronous code so we're sure it doesn't change
            currentState = target.getState();
        }

        DeliveryState nextState;
        switch (target.getState()) {
            case IN_PREPARATION:
                nextState = DeliveryState.IN_TRANSIT;
                break;
            case IN_TRANSIT:
                var failed = Math.random() < getFailRate();
                nextState = failed ? DeliveryState.FAILED : DeliveryState.RECEIVED;
                break;
            case REFUND_REQUIRED:
                nextState = DeliveryState.REFUND_PAID;
                break;
            default:
                logger.warn("Unknown delivery state received: {}", currentState);
                return;
        }

        logger.info("{} from {} to {}", target.getCode(), currentState, nextState);

        // Now we need to update the state, this code updates the UI so the data
        // cannot be modified asynchronously, post a runnable in the main thread
        // and change the data from there.
        Platform.runLater(
                () -> ctx.getDeliveries()
                        .getById(target.getCode())
                        .filter(pkt -> pkt.getState() == currentState)
                        .ifPresent(pkt -> pkt.setState(nextState))
        );
    }

    /**
     * Called when the thread starts.
     * Sleep, call {@link #tick()} and repeat
     */
    private void startAsync() {
        logger.info("Async started");
        // Intellij says that this is a busy-waiting loop, and I agree,
        // in a real application there would be no visible Thread.sleep,
        // this should be done synchronously with some kind of delayed execution
        // but this is an exercise on multi-threading so I think this is fine.
        while (!Thread.interrupted()) {
            var waitTime = getWaitMs();
            logger.debug("Sleeping for {}ms", waitTime);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                break;
            }
            tick();
        }
        logger.info("Async stopped");
    }

    // Of all of the reactive methods that I know this is one of the leas efficient that I know,
    // We need to have N + 1 listeners just to track the deliveries status, this is quite heavy on
    // RAM, but it's the JavaFX way of doing things.

    /**
     * Registers the listener on the main delivery list and adds a listener to each one of the
     * deliveries already present.
     */
    private void registerListeners() {
        this.ctx.getDeliveries().getDeliveries().addListener(listListener);
        for (Delivery p : ctx.getDeliveries().getDeliveries()) {
            DeliveryListener l = new DeliveryListener(p);
            registeredListeners.put(p, l);
            l.register();
            if (canChangeState(p.getState())) addDelivery(p);
        }
    }

    /**
     * Removes all of the listeners registered by this worker.
     */
    private void unregisterListeners() {
        this.ctx.getDeliveries().getDeliveries().removeListener(listListener);
        for (DeliveryListener listener : this.registeredListeners.values()) {
            listener.unregister();
        }
        this.registeredListeners.clear();
    }

    /**
     * Returns true only if the delivery parameter can be changed by this worker.
     *
     * @param state The state to process
     * @return true only if the delivery can be changed
     */
    private static boolean canChangeState(DeliveryState state) {
        return state == DeliveryState.IN_PREPARATION ||
                state == DeliveryState.IN_TRANSIT ||
                state == DeliveryState.REFUND_REQUIRED;
    }

    /**
     * Adds the delivery to the processable list.
     *
     * @param delivery The delivery to add
     */
    private void addDelivery(Delivery delivery) {
        synchronized (possibleDeliveries) {
            possibleDeliveries.add(delivery);
        }
    }

    /**
     * Removes the delivery from the list.
     *
     * @param delivery The delivery to remove
     */
    private void removeDelivery(Delivery delivery) {
        synchronized (possibleDeliveries) {
            // O(N), not efficient!
            // but we also need that sweet O(1) random access time in the async worker
            possibleDeliveries.remove(delivery);
        }
    }

    /**
     * Listener of the main delivery list, this adds {@link DeliveryListener} to each new
     * delivery of the list and removes it from the removed deliveries.
     */
    private class DeliveryListListener implements ListChangeListener<Delivery> {
        @Override
        public void onChanged(Change<? extends Delivery> c) {
            while (c.next()) {
                for (Delivery p : c.getAddedSubList()) {
                    DeliveryListener l = new DeliveryListener(p);
                    registeredListeners.put(p, l);
                    l.register();
                    if (canChangeState(p.getState())) addDelivery(p);
                }
                for (Delivery p : c.getRemoved()) {
                    DeliveryListener l = registeredListeners.remove(p);
                    l.unregister();
                }
            }
        }
    }

    /**
     * Adds or removes the delivery from the processable list according to its {@link DeliveryState}.
     */
    @RequiredArgsConstructor
    private class DeliveryListener implements ChangeListener<DeliveryState> {
        @Getter
        private final Delivery delivery;

        @Override
        public void changed(ObservableValue<? extends DeliveryState> observable, DeliveryState oldValue,
                            DeliveryState newValue) {
            boolean useOld = canChangeState(oldValue);
            boolean useNew = canChangeState(newValue);
            if (useOld && !useNew) {
                removeDelivery(delivery);
            } else if (!useOld && useNew) {
                addDelivery(delivery);
            }
        }

        private void register() {
            this.delivery.stateProperty().addListener(this);
        }

        private void unregister() {
            this.delivery.stateProperty().removeListener(this);
        }
    }

}
