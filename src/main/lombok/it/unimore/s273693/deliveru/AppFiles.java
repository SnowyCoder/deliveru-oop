package it.unimore.s273693.deliveru;

import it.unimore.s273693.deliveru.db.DeliveryStore;
import lombok.ToString;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;

import static it.unimore.s273693.deliveru.Constants.APP_AUTHOR;
import static it.unimore.s273693.deliveru.Constants.APP_NAME;

/**
 * Holder of various data and settings file paths.
 * This will be updated with system-specific paths to the files, for example in *NIX it will follow the
 * XDG conventions (~/.cache/APP_NAME/*) and on Windows it will put the file in %APP_DATA%\Local.
 * Additionally, this instance is immutable and should be the same for each JVM instance.
 */
@ToString
public class AppFiles {
    /**
     * File where the users will be saved.
     *
     * @see it.unimore.s273693.deliveru.db.UserProvider
     */
    public final File usersFile;

    /**
     * File where the deliveries will be saved.
     *
     * @see DeliveryStore
     */
    public final File deliveriesFile;

    /**
     * File where the settings will be saved.
     *
     * @see AppSettings
     */
    public final File settingsFile;

    /**
     * Constructor.
     * It queries the directories and builds the file path
     */
    public AppFiles() {
        AppDirs dirs   = AppDirsFactory.getInstance();
        usersFile      = new File(dirs.getUserDataDir(APP_NAME, null, APP_AUTHOR), "users.json");
        deliveriesFile = new File(dirs.getUserDataDir(APP_NAME, null, APP_AUTHOR), "deliveries.json");
        settingsFile   = new File(dirs.getUserConfigDir(APP_NAME, null, APP_AUTHOR), "settings.json");
    }
}
