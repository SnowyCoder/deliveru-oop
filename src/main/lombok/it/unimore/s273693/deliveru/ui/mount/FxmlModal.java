package it.unimore.s273693.deliveru.ui.mount;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A base class for a simple modal.
 * No need to {@link UiMounter#mount(MountableScene)} the scene,
 * it can also be displayed while another window is present, but the other will be blocked.
 */
public class FxmlModal extends BaseFxmlScene {
    private final Stage dialog = new Stage();

    /**
     * Creates a new instance of {@link FxmlModal}.
     *
     * @param fxmlPath The fxml file path
     * @param parentStage the parent stage
     */
    public FxmlModal(String fxmlPath, Stage parentStage) {
        super(fxmlPath);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle(this.getTitle());
    }

    /**
     * Loads and opens the modal.
     */
    public void show() {
        if (dialog.getScene() == null) {
            dialog.setScene(new Scene(this.getRoot()));
        }
        dialog.show();
    }

    public String getTitle() {
        return "Modal";
    }

    /**
     * Closes the modal.
     */
    public void hide() {
        dialog.hide();
    }
}
