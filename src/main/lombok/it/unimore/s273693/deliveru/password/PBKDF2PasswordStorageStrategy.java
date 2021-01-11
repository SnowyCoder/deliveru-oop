package it.unimore.s273693.deliveru.password;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import static it.unimore.s273693.deliveru.password.PasswordStorageUtil.SALT_DIVIDER;
import static it.unimore.s273693.deliveru.password.PasswordStorageUtil.extractHash;

/**
 * Implementation of {@link PasswordStorageStrategy} that uses PBKDF2 as an encoding strategy.
 * PBKDF2 = Password Based Key Derivation Function 2,
 * see <a href="https://en.wikipedia.org/wiki/PBKDF2">Wikipedia</a> for more details
 */
public class PBKDF2PasswordStorageStrategy implements PasswordStorageStrategy {
    private static final Logger logger = LogManager.getLogger(PBKDF2PasswordStorageStrategy.class);
    private static final int SALT_LENGTH_BYTES = 8;
    private static final int ITERATION_COUNT = 65535;
    private static final int KEY_LENGTH = 256;

    private final SecretKeyFactory keyFactory;

    /**
     * Main constructor.
     */
    public PBKDF2PasswordStorageStrategy() {
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Cannot find PBKDF2WithHmacSHA1 SecretKeyFactory, disabling");
        }
        keyFactory = factory;
    }


    @Override
    public long id() {
        return 2;
    }

    @Override
    public String name() {
        return "PBKDF2";
    }

    @Override
    public boolean isSupported() {
        return keyFactory != null;
    }

    private byte[] generateHash(byte[] salt, String psw) {
        var keySpec = new PBEKeySpec(psw.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        try {
            return keyFactory.generateSecret(keySpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Error generating key:", e);
        }
    }

    @Override
    public String encode(String password, SecureRandom random) {
        // Generate salt
        var salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);

        var hash = generateHash(salt, password);

        var encoder = Base64.getEncoder();
        return encoder.encodeToString(salt) + SALT_DIVIDER + encoder.encodeToString(hash);
    }

    @Override
    public boolean check(String encoded, String password) {
        if (!this.isSupported()) return false;

        var psw = extractHash(encoded);
        if (psw == null) return false; // No hash found

        var salt = Base64.getDecoder().decode(psw.getKey());
        var expected = Base64.getDecoder().decode(psw.getValue());

        var hash = generateHash(salt, password);

        return Arrays.equals(hash, expected);
    }
}
