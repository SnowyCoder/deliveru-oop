/**
 * App Controllers.
 *
 * <p>
 * This App tries to implement a MVC model, the models are in {@link it.unimore.s273693.deliveru.db}, the views
 * are in resources/gui and here are all of the controllers that bind together the views and the models.
 * </p>
 *
 * <p>
 * You should find a 1-to-1 correspondence between the controllers and the views (think of the views as HTML files
 * and the controllers as their JS code, it's not a perfect analogue but it can explain the situation well enough).
 * </p>
 *
 * <p>
 * Some of the classes contained are *Modal and not *Controller, this is just a naming convention and indicates that
 * that class doesn't need to be mounted using an {@link it.unimore.s273693.deliveru.ui.mount.UiMounter} but it can
 * be shown on its own. Other than that the Modals are also controllers of their view, so the analogy stands.
 * </p>
 */
package it.unimore.s273693.deliveru.ui.controllers;