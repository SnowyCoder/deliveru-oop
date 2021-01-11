/**
 * Contains the Delivery tables used in the FXMLs.
 *
 * <p>
 * They are implemented using inheritance to reduce code duplication, note that the actual column creation is done
 * by {@link it.unimore.s273693.deliveru.ui.table.DeliveryTableView#setColumnTypes(java.util.EnumSet)}
 * this is not optimal as that function has all of the possible columns hard-coded (and it's really long). A better way
 * to do the same thing would be to abstract the JavaFX Column creation task to it's own interface and to create a
 * class for each of the column types, but it's left as an exercise to the reader.
 * </p>
 *
 * <p>
 * To explain why we can't just use the same table for oth Admin and User view here are the difference they need to have:
 * While the Admin view can see all of the column types the User needs to hide one (the sender).
 * The context menu on the tables works in two completely different ways.
 * Those differences could be sorted out in better ways (like composition instead of inheritance) but this is a good
 * way to do it and it works fine with JavaFX.
 * </p>
 */
package it.unimore.s273693.deliveru.ui.table;