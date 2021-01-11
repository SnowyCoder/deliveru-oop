package it.unimore.s273693.deliveru.ui.util;

import javafx.util.StringConverter;

/**
 * Converts a percentage value represented as {@link String} into a {@link Double} (from 0 to 100).
 */
public class PercentStringConverter extends StringConverter<Double> {
    /**
     * An immutable instance of this class.
     */
    public static final PercentStringConverter INSTANCE = new PercentStringConverter();

    @Override
    public String toString(Double value) {
        return value + "%";
    }

    @Override
    public Double fromString(String string) {
        if (string.endsWith("%")) {
            string = string.substring(0, string.length() - 1);
        }
        return Math.min(100.0, Math.max(0.0, Double.parseDouble(string)));
    }
}
