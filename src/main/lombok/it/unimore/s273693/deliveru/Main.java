package it.unimore.s273693.deliveru;

/**
 * No-op entry-point wrapper for {@link App}.
 */
public class Main {
    /**
     * The entry point of the Application.
     * Necessary because of this: https://stackoverflow.com/questions/52569724/javafx-11-create-a-jar-file-with-gradle/52571719#52571719
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
