package it.unimore.s273693.deliveru;

import it.unimore.s273693.deliveru.db.DeliveryStore;
import it.unimore.s273693.deliveru.db.User;
import it.unimore.s273693.deliveru.db.UserProvider;
import it.unimore.s273693.deliveru.password.PasswordAuthenticator;
import it.unimore.s273693.deliveru.ui.mount.MountableScene;
import it.unimore.s273693.deliveru.ui.mount.UiMounter;
import it.unimore.s273693.deliveru.workers.DeliveryWorker;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * The App context holder.
 *
 * <p>
 *  This class contains every info and control the app should need without having a central Singleton.
 *  It also contains a bit of logic to load and save data and settings, but it's mainly here for easier
 *  accessibility and once the logic becomes too dense it should be composited along one of the other
 *  systems.
 * </p>
 */
public class AppContext {
    private static final Logger logger = LogManager.getLogger(AppContext.class);
    /**
     * App file paths (immutable).
     */
    public static final AppFiles FILES = new AppFiles();

    /**
     * The user storage.
     *
     * @return The User storage
     */
    @Getter
    private final UserProvider users;

    /**
     * The delivery storage.
     *
     * @return The Delivery storage
     */
    @Getter
    private final DeliveryStore deliveries;

    /**
     * The app JavaFX stage.
     *
     * <p>Useful for modals and to know which Stage is displaying all of the GUI.</p>
     *
     * @return The JavaFX stage
     */
    @Getter
    private final Stage appStage;

    /**
     * The UI helper.
     *
     * @see UiMounter
     * @return The UI Helper
     */
    @Getter
    private final UiMounter uiMounter = new UiMounter(this);

    /**
     * The Password Authenticator.
     *
     * @see PasswordAuthenticator
     * @return The Password Authenticator
     */
    @Getter
    private final PasswordAuthenticator passwordAuthenticator = new PasswordAuthenticator();

    /**
     * The Automatic Delivery Worker.
     *
     * @see DeliveryWorker
     * @return The Automatic Delivery Worker
     */
    @Getter
    private final DeliveryWorker deliveryWorker = new DeliveryWorker(this);

    /**
     * Current settings.
     *
     * <p>Note that those are immutable, the only way to change them is by using {@link #setSettings(AppSettings)}</p>
     */
    @NonNull
    private SimpleObjectProperty<AppSettings> settingsProperty;

    /**
     * The current logged in user, if null it's an administrator.
     *
     * @return The current user
     */
    @Getter
    private User currentUser = null;

    /**
     * True only if an user is logged in.
     *
     * <p>This can also be true if {@link #currentUser} is null, meaning that the admin is logged in</p>
     *
     * @return True if the user is logged in
     */
    @Getter
    private boolean loggedIn = false;

    /**
     * Main constructor.
     * Unless you have a really good explaination consider using {@link AppContext#load(Stage)}
     *
     * @param users The users storage
     * @param deliveries The deliveries storage
     * @param appStage The app stage
     * @param settings The app settings
     */
    public AppContext(UserProvider users, DeliveryStore deliveries, Stage appStage, AppSettings settings) {
        this.users = users;
        this.deliveries = deliveries;
        this.appStage = appStage;
        this.settingsProperty = new SimpleObjectProperty<>();
        this.setSettings(settings);
    }


    /**
     * Changes the current scene to the provided one.
     *
     * @param scene The new scene
     */
    public void setScene(MountableScene scene) {
        this.uiMounter.mount(scene);
        appStage.sizeToScene();
    }

    public ReadOnlyObjectProperty<AppSettings> settingsProperty() {
        return this.settingsProperty;
    }

    /**
     * Current settings.
     *
     * <p>Note that those are immutable, the only way to change them is by using {@link #setSettings(AppSettings)}</p>
     *
     * @return The current settings
     */
    public AppSettings getSettings() {
        return this.settingsProperty.getValue();
    }

    /**
     * Changes the current settings with the one passed as arguments and reloads the program.
     *
     * @param settings the new settings
     */
    public void setSettings(@NonNull AppSettings settings) {
        this.settingsProperty.setValue(settings);
        // Set password storage strategy
        this.passwordAuthenticator.setDefaultStrategy(
                this.passwordAuthenticator.getStrategies().get(settings.passwordStorageStrategy));

        // Set automatic delivery options
        deliveryWorker.setFailRate(settings.deliveryFailRate);
        deliveryWorker.setTimesPerMinute(settings.deliveryIntensity);

        // Enable/disable automatic delivery
        if (settings.deliveryEnabled && loggedIn) {
            deliveryWorker.start();
        }
        if (!settings.deliveryEnabled) {
            deliveryWorker.stop();
        }
    }

    /**
     * Logs in with the provided user, if null then the user is interpreted as the admin.
     *
     * @param user The user to log in with (or null if it's the admin)
     */
    public void login(User user) {
        this.currentUser = user;
        this.loggedIn = true;
        if (getSettings().deliveryEnabled) this.deliveryWorker.start();
    }

    /**
     * Logs out of the session (if any is present).
     *
     * <p>This does NOT do any GUI change, you should do it yourself after calling this method</p>
     */
    public void logout() {
        this.currentUser = null;
        this.loggedIn = false;
        this.deliveryWorker.stop();
    }

    /**
     * Internal helper method to avoid code duplication, opens the file creating the
     * necessary directories and creates an OutputStream tha then is provided to the
     * Consumer.
     *
     * @param name The name of the saved resource (used only for error logging)
     * @param file The file where the resource will be saved
     * @param action The action to do once the output is provided
     */
    private void saveAny(@NonNull String name, @NonNull File file, @NonNull IOConsumer<OutputStream> action) {
        file.getParentFile().mkdirs();

        try (OutputStream out = new FileOutputStream(file)) {
            action.accept(out);
        } catch (IOException e) {
            logger.warn("Failed to save " + name, e);
        }
    }

    /**
     * Saves the users data.
     */
    public void saveUsers() {
        saveAny("users", FILES.usersFile, users::save);
    }

    /**
     * Saves the deliveries data.
     */
    public void saveDeliveries() {
        saveAny("deliveries", FILES.deliveriesFile, deliveries::save);
    }

    /**
     * Saves the current settings.
     */
    public void saveSettings() {
        saveAny("settings", FILES.settingsFile, getSettings()::save);
    }

    /**
     * Saves everything (users, deliveries and settings) to the filesystem.
     */
    public void save() {
        this.saveUsers();
        this.saveDeliveries();
        this.saveSettings();
    }

    /**
     * Saves everything and closes the program.
     * This method never returns
     */
    public void quit() {
        this.logout();
        this.save();
        Platform.exit();
        LogManager.shutdown();
        System.exit(0);
    }

    /**
     * Loads everything from the file system and then creates an instance of AppContext.
     *
     * @param appStage The GUI stage
     * @return A nwe instance of AppContext with the loaded data.
     */
    public static AppContext load(Stage appStage) {
        var users   = UserProvider.load(FILES.usersFile);
        var deliveries  = DeliveryStore.load(FILES.deliveriesFile);
        var settings = AppSettings.load(FILES.settingsFile);

        return new AppContext(users, deliveries, appStage, settings);
    }

    /**
     * Same as {@link Consumer} but also provides a {@code throws IOException} in the method signature.
     *
     * @param <T> The only parameter of the {@link IOConsumer#accept(T)} function
     */
    @FunctionalInterface
    private interface IOConsumer<T> {
        void accept(T p) throws IOException;
    }
}
