/**
 * Welcome to DeliverU, the best Delivery management Utility available on the whole UniMoRe camp.
 * On a more serious note, this app is only developed for academic purposes, while it seeks for industry-standard
 * quality it is not developed by professionals and it should not be used in a real-world environment.<br>
 * <br>
 * The App is divided logically using packages, below you can find an useful list on where you can find examples of
 * OOP paradigms (and Java features) being used in the code.
 * Something similar to a MVC pattern is implemented, the Models are in the db package, the Controllers are under
 * ui.controllers and the Views are in the resources director, in the gui subfolder.<br>
 * <br>
 * Examples of polymorphism:
 * <ul>
 *  <li>{@link it.unimore.s273693.deliveru.ui.mount.MountableScene}</li>
 *  <li>{@link it.unimore.s273693.deliveru.password.PasswordStorageStrategy}</li>
 *  <li>{@link it.unimore.s273693.deliveru.ui.table.DeliveryTableView}</li>
 *  <li>{@link it.unimore.s273693.deliveru.db.InsuredDelivery}</li>
 * </ul>
 * Examples of hashCode and equals overriding:
 * <ul>
 *  <li>{@link it.unimore.s273693.deliveru.db.Delivery}</li>
 * </ul>
 * Examples of user-defined classes using generics:
 * <ul>
 *  <li>{@link it.unimore.s273693.deliveru.AppContext} (IOConsumer, it's private)</li>
 * </ul>
 * Examples of java classes with generics used:
 * <ul>
 *  <li>{@link it.unimore.s273693.deliveru.db.DeliveryStore}</li>
 *  <li>{@link it.unimore.s273693.deliveru.db.UserProvider}</li>
 *  <li>{@link it.unimore.s273693.deliveru.password.PasswordAuthenticator}</li>
 *  <li>{@link it.unimore.s273693.deliveru.password.PasswordStorageUtil}</li>
 *  <li>And others</li>
 * </ul>
 * Examples of annotations (defined externally)
 * <ul>
 *  <li>Lombok annotations (in almost every class)</li>
 *  <li>JavaFX annotations (in every controller or modal defined in {@link it.unimore.s273693.deliveru.ui.controllers})</li>
 * </ul>
 */
package it.unimore.s273693.deliveru;