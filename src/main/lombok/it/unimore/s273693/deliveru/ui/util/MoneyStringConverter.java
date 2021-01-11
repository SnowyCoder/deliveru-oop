package it.unimore.s273693.deliveru.ui.util;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * Converts a positive money value represented as {@link String} into a {@link BigInteger} that is the
 * expected money value multiplied by 100 (to fit into an integer).
 */
public class MoneyStringConverter extends StringConverter<BigInteger> {
    /**
     * Immutable instance of this class.
     */
    public static final MoneyStringConverter INSTANCE = new MoneyStringConverter();
    /**
     * Pattern accepted by this class.
     */
    public static final Pattern PATTERN = Pattern.compile("^\\d*(\\.\\d{0,2})?$");

    // Don't mind me, I'm immutable
    private static final BigInteger ONE_HUNDRED = BigInteger.valueOf(100);

    @Override
    public String toString(BigInteger value) {
        var res = value.abs()
                .divideAndRemainder(ONE_HUNDRED);

        String right = res[1].toString();
        if (right.length() < 2) {
            right = '0' + right;
        }

        return res[0].toString() + "." + right;
    }

    @Override
    public BigInteger fromString(String string) {
        return new BigDecimal(string.replaceAll("\\s+", ""))
                .multiply(BigDecimal.valueOf(100))
                .toBigInteger();
    }

    /**
     * Creates a new {@link TextFormatter} with the provided default value.
     *
     * @param defaultValue The default value of the formatter
     * @return A new {@link TextFormatter}
     */
    public static TextFormatter<BigInteger> textFormatter(BigInteger defaultValue) {
        return new TextFormatter<>(INSTANCE, defaultValue, new RegexFilter(PATTERN));
    }
}
