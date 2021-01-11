package it.unimore.s273693.deliveru.password;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores and manages the password authentication and storage strategies.
 * <p>
 * Stores all of the registered storage strategies, it's responsible for support checking
 * and encoding/decoding of all of the passwords.
 * </p>
 * The encoded passwords are in the format ID + ID_DIVIDER + ENCODED
 */
public class PasswordAuthenticator {
    private static final Logger logger = LogManager.getLogger(PasswordAuthenticator.class);
    /**
     * Divider between the storage id and the encoded password.
     */
    public static final char ID_DIVIDER = '$';

    private final Map<Long, PasswordStorageStrategy> strategies = new HashMap<>();

    private final ObjectProperty<PasswordStorageStrategy> defaultStrategy = new SimpleObjectProperty<>();
    // This object is expensive, be sure to cache it
    private final SecureRandom random = new SecureRandom();

    /**
     * Creates the authenticator and registers the default strategies.
     */
    public PasswordAuthenticator() {
        initStrategies();
    }

    /**
     * Default storage property.
     *
     * @return The default storage property
     */
    public ObjectProperty<PasswordStorageStrategy> defaultStrategyProperty() {
        return defaultStrategy;
    }

    /**
     * Gets the current default strategy.
     *
     * @return The current default strategy
     */
    public PasswordStorageStrategy getDefaultStrategy() {
        return defaultStrategy.get();
    }

    /**
     * Sets the current default strategy.
     *
     * @param strategy the new strategy to use
     */
    public void setDefaultStrategy(PasswordStorageStrategy strategy) {
        defaultStrategy.set(strategy);
    }

    /**
     * Registers a new storage strategy.
     *
     * @param strategy The strategy to register
     */
    public void registerStrategy(PasswordStorageStrategy strategy) {
        this.strategies.put(strategy.id(), strategy);
        logger.info("Registered strategy {} with id {}", strategy.name(), strategy.id());

        var defStrategy = this.defaultStrategy.get();
        if (strategy.isSupported() && (defStrategy == null || strategy.id() > defStrategy.id())) {
            defaultStrategy.set(strategy);
        }
    }

    /**
     * Registers the default password strategies.
     */
    protected void initStrategies() {
        registerStrategy(new PlainPasswordStorageStrategy());
        registerStrategy(new Sha256PasswordStorageStrategy());
        registerStrategy(new PBKDF2PasswordStorageStrategy());
    }

    /**
     * Gets an immutable view of the registered strategies.
     *
     * @return The registered strategies
     */
    public Map<Long, PasswordStorageStrategy> getStrategies() {
        return Collections.unmodifiableMap(this.strategies);
    }

    /**
     * Encodes the password passed as parameter.
     *
     * @param password The password to encode
     * @return The encoded string
     */
    public String encode(String password) {
        var defStrategy = this.defaultStrategy.get();
        return defStrategy.id() + "" + ID_DIVIDER + defStrategy.encode(password, random);
    }

    /**
     * Checks an user provided password with one encoded previously.
     * If the used strategy is not registered or is not supported false is returned.
     *
     * @param hash The encoded password
     * @param password The user provided password
     * @return true only if they match
     */
    public boolean check(String hash, String password) {
        var divIndex = hash.indexOf(ID_DIVIDER);
        if (divIndex < 0) return false;

        long strategyId;
        try {
            strategyId = Long.parseLong(hash.substring(0, divIndex));
        } catch (NumberFormatException e) {
            logger.warn("Invalid password hash");
            return false;
        }
        PasswordStorageStrategy strategy = this.strategies.get(strategyId);
        if (strategy == null) {
            logger.error("Cannot find strategy {}", strategyId);
            return false;
        }
        if (!strategy.isSupported()) {
            logger.error("Strategy '{}' not supported", strategy.name());
        }
        try {
            return strategy.check(hash.substring(divIndex + 1), password);
        } catch (Exception e) {
            logger.warn("Error occurred on password check", e);
            return false;
        }
    }
}
