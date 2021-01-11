package it.unimore.s273693.deliveru.password;

import java.security.SecureRandom;

/**
 * Interface that every password storage strategy should respect.
 *
 * <p>
 * It's best to register every possible storage strategy and then to check whether it is supported
 * rather than not register anything at all.
 * </p>
 * For each string x check(encoded(x, random), x) should always return true.
 */
public interface PasswordStorageStrategy {
    /**
     * Unique ID of this strategy (they do not need to be assigned linearly).
     *
     * @return The strategy's unique id
     */
    long id();

    /**
     * User friendly name.
     *
     * @return The strategy name
     */
    String name();

    /**
     * Checks if the strategy is supported and returns true only if it is.
     * Please cache this method if possible
     *
     * @return true only if the strategy is supported
     */
    default boolean isSupported() {
        return true;
    }

    /**
     * Encodes the password using (when needed) the {@link SecureRandom} provided as parameter.
     *
     * @param password The password to encode
     * @param random The {@link SecureRandom} to use in the encoding process
     * @return An encoded string that represents the encoded password (used in the check method)
     */
    String encode(String password, SecureRandom random);

    /**
     * Checks an user-provided password against a previously encoded password..
     * Note that the encoded string might be loaded from a File, so do not trust it
     * completely (as it might contain unsafe or random data, so it could be used as an attack vector).
     *
     * @param encoded The string encoded with {@link #encode(String, SecureRandom)}
     * @param password The user-provided password
     * @return true only if the encoded password and the provided password match
     */
    boolean check(String encoded, String password);
}
