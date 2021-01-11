package it.unimore.s273693.deliveru;

import it.unimore.s273693.deliveru.ui.controllers.EntryController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static it.unimore.s273693.deliveru.Constants.APP_NAME;

/**
 * The App entry point.
 * This does nothing more than some bootstrap, that is context loading and starting the GUI.
 * Note that nothing in this app is a Singleton, and as such neither is the entry point,
 * instead a context (called AppContext) is passed around.
 */
public class App extends Application {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private AppContext context;

    @Override
    public void start(Stage primaryStage) {
        var capitalizedName = APP_NAME.substring(0, 1).toUpperCase(Locale.US) + APP_NAME.substring(1);
        LOGGER.info(capitalizedName + " starting, hold tight!");
        LOGGER.info("File paths: {}", AppContext.FILES);
        this.context = AppContext.load(primaryStage);

        var mounter = this.context.getUiMounter();
        mounter.mount(new EntryController(context));
        primaryStage.setScene(mounter.getScene());
        primaryStage.setTitle(capitalizedName);
        primaryStage.setOnCloseRequest(event -> context.quit());
        primaryStage.show();
    }

    public static void main(String[] args) {
        App.launch(App.class, args);
    }
}
