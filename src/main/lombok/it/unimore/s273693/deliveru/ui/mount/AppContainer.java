package it.unimore.s273693.deliveru.ui.mount;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.ui.controllers.EntryController;
import it.unimore.s273693.deliveru.ui.controllers.SettingsModal;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

/**
 * Provides a Desktop app container, it adds a top menu with the "logout", "quit", "settings' and "about" options.
 * In its center it can contain a mountable view.
 */
public class AppContainer extends BaseController {
    private final AppContext ctx;

    @FXML
    private BorderPane rootMountPoint;

    /**
     * Gets the mounted Node (if any is mounted).
     *
     * @return Mounted node or null
     */
    @Getter
    private Parent mounted = null;


    public AppContainer(AppContext ctx) {
        super("gui/app_container.fxml");
        this.ctx = ctx;
    }

    /**
     * Sets the center view.
     *
     * @param value the new view to be displayed
     */
    public void setMounted(Parent value) {
        this.mounted = value;

        // We are not initialized yet
        if (rootMountPoint == null) return;

        if (value != null) {
            rootMountPoint.setCenter(value);
        } else {
            rootMountPoint.getChildren().clear();
        }
    }

    /**
     * Called on initialization.
     */
    @FXML
    public void initialize() {
        // Late root init
        if (mounted != null) setMounted(mounted);
    }

    /**
     * Logs the user out and returns to the entry scene.
     */
    @FXML
    public void logout() {
        ctx.logout();
        ctx.setScene(new EntryController(ctx));
    }

    /**
     * Quits the program.
     */
    @FXML
    public void quit() {
        ctx.quit();
    }

    /**
     * Opens the "Settings" modal.
     */
    @FXML
    public void openSettings() {
        new SettingsModal(ctx).show();
    }

    /**
     * Opens the "About" modal.
     */
    @FXML
    public void openAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About PkManage");
        alert.setContentText("Program created by Lorenzo Rossi for the OOP course at UniMoRe");
        alert.show();
    }
}
