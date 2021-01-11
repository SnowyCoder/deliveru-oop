package it.unimore.s273693.deliveru.ui.util;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.regex.Pattern;

/**
 * Converts between {@link Double} and {@link String} (only with positive numbers), useful in JavaFX.
 */
public class DoubleStringConverter extends StringConverter<Double> {
    /**
     * An immutable instance of this class.
     */
    public static final DoubleStringConverter INSTANCE = new DoubleStringConverter();
    /**
     * Pattern accepted by this class.
     */
    public static final Pattern PATTERN = Pattern.compile("^\\d*(\\.\\d*)?$");

    @Override
    public String toString(Double object) {
        return object.toString();
    }

    @Override
    public Double fromString(String string) {
        return Double.parseDouble(string);
    }

    /**
     * Creates a new {@link TextFormatter} with the provided default value.
     *
     * @param defaultValue The default value of the formatter
     * @return A new {@link TextFormatter}
     */
    public static TextFormatter<Double> textFormatter(double defaultValue) {
        return new TextFormatter<>(INSTANCE, defaultValue, new RegexFilter(PATTERN));
    }
}
