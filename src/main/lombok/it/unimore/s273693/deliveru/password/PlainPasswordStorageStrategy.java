package it.unimore.s273693.deliveru.password;

import java.security.SecureRandom;

/**
 * Simplest implementation of {@link PasswordStorageStrategy}.
 * This is here only as example (and as a debug helper), please do not use this.
 *
 * <p>
 * For a still simplistic but more useful implementation check {@link Sha256PasswordStorageStrategy}<br>
 * For a cryptographically secure implementation check {@link PBKDF2PasswordStorageStrategy}
 * </p>
 */
public class PlainPasswordStorageStrategy implements PasswordStorageStrategy {
    @Override
    public long id() {
        return 0;
    }

    @Override
    public String name() {
        return "Plain (DON'T USE)";
    }

    @Override
    public String encode(String password, SecureRandom random) {
        return password;
    }

    @Override
    public boolean check(String encoded, String password) {
        return encoded.equals(password);
    }
}
