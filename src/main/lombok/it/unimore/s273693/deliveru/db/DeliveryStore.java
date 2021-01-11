package it.unimore.s273693.deliveru.db;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Stores all of the deliveries.
 * Supports queries by UUID and by author, is also in charge of loading and saving the data.
 */
public class DeliveryStore {
    private static final Logger logger = LogManager.getLogger(DeliveryStore.class);

    /**
     * Observable list containing all of the registered deliveries.
     * To filter by author use {@link DeliveryStore#getByAuthor(UUID)}.
     *
     * @return All registered deliveries
     */
    @Getter
    private final ObservableList<Delivery> deliveries = FXCollections.observableArrayList();
    private final Map<UUID, Delivery> deliveriesById = new HashMap<>();
    private final Map<UUID, ObservableList<Delivery>> deliveriesBySender = new HashMap<>();

    /**
     * Adds a delivery.
     * If a delivery with the same UUID is found an erros is thrown
     *
     * @param delivery The delivery to add
     */
    public void add(Delivery delivery) {
        var previous = this.deliveriesById.putIfAbsent(delivery.getCode(), delivery);
        if (previous != null) throw new IllegalArgumentException("Delivery with the same UUID already registered");
        this.deliveries.add(delivery);
        this.deliveriesBySender.computeIfAbsent(delivery.getSender(), (id) -> FXCollections.observableList(new ArrayList<>()))
                .add(delivery);
        logger.info("Registered {}", delivery.getCode());
    }

    /**
     * Removes a delivery.
     * If it's not found an error is thrown
     *
     * @param delivery the delivery to remove
     */
    public void remove(Delivery delivery) {
        if (!delivery.isInFinalState()) throw new IllegalArgumentException("Cannot remove delivery with non-final state");
        if (deliveriesById.remove(delivery.getCode()) == null) return;
        deliveries.remove(delivery);
        var senderDeliveries = deliveriesBySender.get(delivery.getSender());
        senderDeliveries.remove(delivery);
    }

    /**
     * Queries the delivery by its UUID.
     *
     * @param code The UUID of the delivery to search
     * @return the delivery (or an empty optional)
     */
    public Optional<Delivery> getById(UUID code) {
        return Optional.ofNullable(this.deliveriesById.get(code));
    }

    /**
     * Queries all of the deliveries done by the user.
     *
     * @param user The user to search for
     * @return All of his deliveries
     */
    public ObservableList<Delivery> getByAuthor(UUID user) {
        return this.deliveriesBySender.computeIfAbsent(user, (id) -> FXCollections.observableList(new ArrayList<>()));
    }

    /**
     * Serializes the instance into a JSON-serioalizable class.
     *
     * @return A serializable instance with the same deliveries
     */
    private SerializedDb serialize() {
        var db = new SerializedDb();
        db.deliveries = this.deliveries;
        db.version = 1;

        return db;
    }

    /**
     * Clears this instance's data and deserializes the data.
     *
     * @param db The serialized data
     */
    private void deserialize(SerializedDb db) {
        this.deliveries.clear();
        this.deliveriesById.clear();
        this.deliveriesBySender.clear();

        if (db.version != 1) {
            logger.error("Failed to load deliveries, incompatible db version: {}", db.version);
            return;
        }

        for (var delivery : db.deliveries) {
            try {
                this.add(delivery);
            } catch (IllegalArgumentException e) {
                logger.error("Error adding delivery {}, is the db corrupted?", delivery.getCode(), e);
            }
        }

        logger.info("Loaded {} deliveries", db.deliveries.size());
    }

    /**
     * Saves the deliveries into the stream provided as parameter.
     *
     * @param out The output stream in which the data will be saved
     * @throws IOException when an error occurs while writing
     */
    public void save(OutputStream out) throws IOException {
        var mapper = new ObjectMapper();
        mapper.writeValue(out, this.serialize());
        logger.info("Saved {} deliveries", this.deliveries.size());
    }

    /**
     * Creates a new {@link DeliveryStore} with no delivery.
     *
     * @return an empty {@link DeliveryStore}
     */
    public static DeliveryStore createEmpty() {
        return new DeliveryStore();
    }

    /**
     * Creates a new instance of {@link DeliveryStore} with the data loaded from the provided {@link InputStream}.
     * If the read data is invalid an empty {@link DeliveryStore} will be returned
     *
     * @param in The stream from which the data will be read
     * @return A new instance of {@link DeliveryStore} with the loaded data
     * @throws IOException When an error occurs while reading the file
     */
    public static DeliveryStore load(InputStream in) throws IOException {
        var res = createEmpty();
        if (in == null) return res;

        var mapper = new ObjectMapper();
        SerializedDb db;
        try {
            db = mapper.readValue(in, SerializedDb.class);
        } catch (JsonParseException | JsonMappingException e) {
            logger.error("Failed to parse JSON", e);
            return res;
        }

        res.deserialize(db);
        return res;
    }

    /**
     * Creates a new instance of {@link DeliveryStore} with the data loaded from the provided file.
     * If the read data is invalid or the file does not exist an empty {@link DeliveryStore} will be returned
     *
     * @param file The file from which the data will be read
     * @return A new instance of {@link DeliveryStore} with the loaded data
     */
    public static DeliveryStore load(File file) {
        try {
            return load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.info("Deliveries file not found");
        } catch (IOException e) {
            logger.error("Error reading deliveries file, using empty db", e);
        }
        return createEmpty();
    }

    /**
     * Helper class that will be serialized into the OutputStream.
     */
    private static class SerializedDb {
        public long version;
        public List<Delivery> deliveries;
    }
}
