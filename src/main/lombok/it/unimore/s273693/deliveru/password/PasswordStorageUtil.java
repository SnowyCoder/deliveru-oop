package it.unimore.s273693.deliveru.password;

import javafx.util.Pair;
import lombok.NonNull;

/**
 * Contains utilities for the password class.
 */
public final class PasswordStorageUtil {
    public static final char SALT_DIVIDER = '$';

    /**
     * Extracts the hash from the provided password.
     * The format of the input should be SALT + SALT_DIVIDER + REST
     * if it is the return value is the pair (SALT, REST) else null
     * is returned.
     *
     * @param psw The password
     * @return The pair (salt, rest) or null
     */
    public static Pair<String, String> extractHash(@NonNull String psw) {
        var saltDivider = psw.indexOf(SALT_DIVIDER);
        if (saltDivider < 0) return null;
        return new Pair<>(psw.substring(0, saltDivider), psw.substring(saltDivider + 1));
    }

    private PasswordStorageUtil() {}
}
