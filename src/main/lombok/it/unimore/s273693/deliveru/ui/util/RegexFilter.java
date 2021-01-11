package it.unimore.s273693.deliveru.ui.util;

import javafx.scene.control.TextFormatter;
import lombok.AllArgsConstructor;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Filters a {@link TextFormatter.Change} using a provided Pattern.
 */
@AllArgsConstructor
public class RegexFilter implements UnaryOperator<TextFormatter.Change> {
    private final Pattern pattern;

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        var newText = change.getControlNewText();
        if (pattern.matcher(newText).matches()) {
            return change;
        } else {
            return null;
        }
    }
}
