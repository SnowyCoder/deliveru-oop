package it.unimore.s273693.deliveru.ui.mount;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * Base class for any scene that loads its view from a fxml file.
 * The file is loaded and the loading instance is used as the controller.
 */
public class BaseFxmlScene {
    private final String fxmlPath;
    private Parent root;

    /**
     * Creates a new {@link BaseFxmlScene}.
     *
     * @param fxmlPath The url of the fxml file
     */
    public BaseFxmlScene(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    /**
     * If false the view is deleted and reloaded at every request..
     *
     * @param cacheRoot New value for caching
     * @return The current caching strategy
     */
    @Getter
    @Setter
    private boolean cacheRoot = true;

    private Parent loadRoot() {
        var fxml = getClass().getClassLoader().getResource(fxmlPath);

        if (fxml == null) {
            throw new IllegalStateException("Cannot find FXML (" + fxmlPath + ")");
        }

        var loader = new FXMLLoader(fxml);
        loader.setController(this);
        try {
            return loader.load(fxml.openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read FXML at " + fxmlPath, e);
        }
    }

    /**
     * Returns the FXML root of the scene, loading it if necessary.
     *
     * @return the root of the scene
     */
    public Parent getRoot() {
        if (root == null || this.cacheRoot) {
            root = loadRoot();
        }

        return root;
    }
}
