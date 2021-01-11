package it.unimore.s273693.deliveru.ui.mount;

import it.unimore.s273693.deliveru.AppContext;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Manages the connection between the current UI and the JavaFX scene.
 * In detail some UIs need an App container, some don't.
 * The same container is used for all of them, so it is reused once built.
 */
@RequiredArgsConstructor
public class UiMounter {
    private final AppContext ctx;

    private MountableScene current = null;

    /**
     * The parent JavaFX scene on which everything is mounted.
     *
     * @return JavaFX scene
     */
    @Getter
    private Scene scene;
    private AppContainer app = null;

    private AppContainer getApp() {
        if (app == null) {
            app = new AppContainer(ctx);
        }
        return app;
    }

    private void setSceneRoot(Parent root) {
        if (this.scene == null) {
            scene = new Scene(root);
        } else {
            scene.setRoot(root);
        }
    }

    /**
     * Mounts the scene, unmounting the previous one.
     *
     * @param newScene The new {@link MountableScene} to mount
     */
    public void mount(MountableScene newScene) {
        if (this.current != null) this.current.unmount();
        this.current = newScene;
        var sceneRoot = this.current.mount();

        if (!newScene.isPopup()) {
            var app = getApp();
            app.setMounted(sceneRoot);
            setSceneRoot(app.mount());
        } else {
            setSceneRoot(sceneRoot);
        }
    }
}
