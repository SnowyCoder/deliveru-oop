/**
 * Utility abstractions for the UI.
 *
 * <p>
 *  JavaFX only implements the View part of the whole MVC model, so a bit of infrastructure to manage the Models is
 *  still needed. This package implements a much dumber version of the first Android views, so there's just one
 *  "scene"
 *  that is viewed at a time and Modals are just additional "scene"s that can be shown without the need of this
 *  management layer. Note that "scene"s here are different than JavaFX scenes and just represent "something" on the
 *  screen that can be mounted (and returns a JavaFx {@link javafx.scene.Parent}).
 * </p>
 *
 * <p>
 *  To reduce code duplication everything that loads its views from a FXML file extends
 *  {@link it.unimore.s273693.deliveru.ui.mount.BaseFxmlScene} and that can be both a Controller (which base class is
 *  implemented in {@link it.unimore.s273693.deliveru.ui.mount.BaseController}) or a Modal
 *  ({@link it.unimore.s273693.deliveru.ui.mount.FxmlModal}). But your controller might not use FXML files, to make this
 *  possible the {@link it.unimore.s273693.deliveru.ui.mount.UiMounter} only needs the controllers to be
 *  {@link it.unimore.s273693.deliveru.ui.mount.MountableScene}.
 * </p>
 */
package it.unimore.s273693.deliveru.ui.mount;