package it.unimore.s273693.deliveru.password;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import static it.unimore.s273693.deliveru.password.PasswordStorageUtil.SALT_DIVIDER;
import static it.unimore.s273693.deliveru.password.PasswordStorageUtil.extractHash;

/**
 * Implementation of {@link PasswordStorageStrategy} that uses SHA256 as an encoding strategy.
 * For a cryptographically secure implementation check {@link PBKDF2PasswordStorageStrategy}
 */
public class Sha256PasswordStorageStrategy implements PasswordStorageStrategy {
    private static final Logger logger = LogManager.getLogger(Sha256PasswordStorageStrategy.class);
    private static final int SALT_LENGTH_BYTES = 8;

    private final MessageDigest shaDigest;

    /**
     * Main constructor.
     */
    public Sha256PasswordStorageStrategy() {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Cannot find SHA-256 MessageDigest, disabling");
        }
        shaDigest = digest;
    }


    @Override
    public long id() {
        return 1;
    }

    @Override
    public String name() {
        return "SHA256";
    }

    @Override
    public boolean isSupported() {
        return shaDigest != null;
    }

    @Override
    public String encode(String password, SecureRandom random) {
        // Generate salt
        var salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);

        shaDigest.reset();
        shaDigest.update(salt);
        shaDigest.update(password.getBytes(StandardCharsets.UTF_8));
        var out = shaDigest.digest();

        var encoder = Base64.getEncoder();
        return encoder.encodeToString(salt) + SALT_DIVIDER + encoder.encodeToString(out);
    }

    @Override
    public boolean check(String encoded, String password) {
        if (!this.isSupported()) return false;

        var psw = extractHash(encoded);
        if (psw == null) return false; // No hash found

        var salt = Base64.getDecoder().decode(psw.getKey());
        var expected = Base64.getDecoder().decode(psw.getValue());

        shaDigest.reset();
        shaDigest.update(salt);
        shaDigest.update(password.getBytes(StandardCharsets.UTF_8));
        var result = shaDigest.digest();

        return Arrays.equals(result, expected);
    }
}
