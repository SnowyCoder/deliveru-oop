package it.unimore.s273693.deliveru.ui.mount;

import javafx.scene.Parent;

/**
 * A {@link MountableScene} that loads the view from a fxml file (using the logic in {@link BaseFxmlScene}).
 * It's called a controller because the loading instance becomes the controller of the loaded GUI.
 */
public class BaseController extends BaseFxmlScene implements MountableScene {

    /**
     * Creates a new {@link BaseController}.
     *
     * @param fxmlPath The path to the fxml file
     */
    public BaseController(String fxmlPath) {
        super(fxmlPath);
    }

    /**
     * {@inheritDoc}
     */
    public Parent mount() {
        return getRoot();
    }

    /**
     * {@inheritDoc}
     */
    public void unmount() {
    }
}
