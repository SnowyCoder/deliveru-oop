package it.unimore.s273693.deliveru.ui.mount;

import javafx.scene.Parent;

/**
 * Generic scene interface.
 * <p>
 * Only one of this can be visualized at a time, before mounting the {@link #mount()} method is called
 * And before unmounting the {@link #unmount()} method is called.
 * You can use the {@link #isPopup()} method to control the container of this scene.
 * </p>
 * These instances are mounted by the {@link UiMounter}.
 */
public interface MountableScene {
    /**
     * Called when the scene is mounted.
     * Mounts the scene and returns the root node of the UI to mount
     *
     * @return the {@link Parent} of the scene to mount
     */
    Parent mount();

    /**
     * Called before the scene is unmounted.
     */
    void unmount();

    /**
     * If false the scene will have an  upper menu (with things like About, Close, ecc).
     *
     * @return true to disable the application menu
     */
    default boolean isPopup() {
        return false;
    }
}
