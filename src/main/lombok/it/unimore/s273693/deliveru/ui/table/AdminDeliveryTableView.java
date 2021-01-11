package it.unimore.s273693.deliveru.ui.table;

import it.unimore.s273693.deliveru.db.Delivery;
import it.unimore.s273693.deliveru.db.DeliveryState;
import it.unimore.s273693.deliveru.db.InsuredDelivery;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.*;
import org.fxmisc.easybind.EasyBind;

import java.util.EnumSet;

/**
 * Specialized table for the Admin view.
 *
 * <p>
 *  Every column is enabled and a menu is added, the menu contains the "Set state" sub-menu and
 *  a "Remove" item (for deliveries in final state).
 * </p>
 */
public class AdminDeliveryTableView extends DeliveryTableView {
    public AdminDeliveryTableView() {
        super();
        this.setColumnTypes(EnumSet.allOf(DeliveryColumnType.class));
    }

    @Override
    protected TableRow<Delivery> createRow(TableView<Delivery> deliveryTableView) {
        var row = super.createRow(deliveryTableView);

        ContextMenu rowMenu = createContextMenu(row.itemProperty());

        // only display context menu for non-empty rows:
        row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(rowMenu)
        );

        return row;
    }

    /**
     * Create a ContextMenu that works with JavaFX bindings.
     *
     * @param delivery The delivery to bind on
     * @return A new context menu that reacts to the changes on delivery
     */
    private ContextMenu createContextMenu(ObjectProperty<Delivery> delivery) {
        var menu = new ContextMenu();

        var chItem = new Menu("Set state");

        // flatMap also handles null values (dealing with null-pointers is always funny, right?).
        // This can be summarized in: chItem.disabled = delivery.isInFinalState || settings.deliveryEnabled
        var disabled = EasyBind.combine(
                EasyBind.monadic(delivery)
                        .flatMap(Delivery::isInFinalStateProperty)
                        .orElse(true), // The delivery is null, disable all
                EasyBind.map(this.getCtx().settingsProperty(), s -> s.deliveryEnabled),
                // Disable the "set state" feature when the delivery is enabled
                (a, b) -> a || b
        );

        chItem.disableProperty().bind(disabled);

        var isDeliveryInsured = EasyBind.map(delivery, x -> x instanceof InsuredDelivery);

        for (var state : DeliveryState.values()) {
            var item = new MenuItem(state.getName());
            item.setOnAction(e -> delivery.get().setState(state));

            // If this state needs an insured delivery only show it when the delivery is insured.
            if (state.isInsuranceRequired()) {
                item.visibleProperty().bind(isDeliveryInsured);
            }
            chItem.getItems().add(item);
        }

        var remove = new MenuItem("Remove");
        remove.setOnAction(a -> getCtx().getDeliveries().remove(delivery.get()));

        // This can be summarized in: remove.disabled = !delivery.isInFinalState
        remove.disableProperty().bind(
                EasyBind.monadic(delivery)
                        .flatMap(x -> x.isInFinalStateProperty().not())
                        .orElse(true)
        );

        menu.getItems().setAll(chItem, remove);

        return menu;
    }
}
