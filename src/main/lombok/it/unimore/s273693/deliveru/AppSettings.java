package it.unimore.s273693.deliveru;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Contains the changeable and savable/loadable App settings.
 * The contents are saved in JSON format.
 * Note that this class is immutable (like String) so you can keep an instance of it without worrying about mutation.
 * The only present methods are for saving and loading an instance.
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class AppSettings {
    /**
     * Default App settings (immutable).
     */
    public static final AppSettings DEFAULT = Constants.DEFAULT_SETTINGS;
    private static final Logger logger = LogManager.getLogger(AppSettings.class);

    /**
     * Password storage strategy (by id).
     */
    public final long passwordStorageStrategy;

    /**
     * If true the automatic delivery will be enabled.
     */
    public final boolean deliveryEnabled;

    /**
     * The automatic delivery average actions per minute.
     */
    public final double deliveryIntensity;

    /**
     * Delivery fail rate (from 0 to 1).
     */
    public final double deliveryFailRate;

    /**
     * Creates a new instance with the same values as the DEFAULT one.
     */
    public AppSettings() { // Why should you use this? Ask Jackson
        this(DEFAULT.passwordStorageStrategy, DEFAULT.deliveryEnabled, DEFAULT.deliveryIntensity,
                DEFAULT.deliveryFailRate);
    }

    // Load and save methods

    /**
     * Save settings in the provided OutputStream.
     *
     * @param out the Output stream where the settings will be saved
     * @throws IOException when an IO error occurs
     */
    public void save(OutputStream out) throws IOException {
        val mapper = new ObjectMapper();
        mapper.writeValue(out, this);
    }

    /**
     * Loads AppSettings from the provided InputStream and creates a nwe instance.
     *
     * @param in the provided InputStream
     * @return a new AppSettings instance with the loaded settings, or the default values if the JSON is invalid
     * @throws IOException when an error occurs while reading the data
     */
    public static AppSettings load(InputStream in) throws IOException {
        val mapper = new ObjectMapper();

        try {
            return mapper.readValue(in, AppSettings.class);
        } catch (JsonParseException | JsonMappingException e) {
            logger.error("Failed to parse JSON", e);
            return DEFAULT;
        }
    }

    /**
     * Loads an instance of AppSettings from the provided file.
     * If the procedure fails or the file is not present the default instance will be returned
     *
     * @param file The file from which the settings will be loaded
     * @return The loaded settings or the default instance
     */
    public static AppSettings load(File file) {
        try {
            return load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.info("Settings file not found");
        } catch (IOException e) {
            logger.error("Error reading settings file, using default", e);
        }
        return DEFAULT;
    }
}
