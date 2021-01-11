package it.unimore.s273693.deliveru.ui.table;

import it.unimore.s273693.deliveru.db.Delivery;
import it.unimore.s273693.deliveru.db.DeliveryState;
import it.unimore.s273693.deliveru.db.InsuredDelivery;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.fxmisc.easybind.EasyBind;

import java.util.EnumSet;

/**
 * Specialized table for the User view.
 * Only some columns are enabled and a ContextMenu is added with a "Request refund" item.
 */
public class UserDeliveryTableView extends DeliveryTableView {
    /**
     * Main constructor.
     */
    public UserDeliveryTableView() {
        super();
        this.setColumnTypes(EnumSet.of(DeliveryColumnType.CODE, DeliveryColumnType.DATE, DeliveryColumnType.DESTINATION,
                DeliveryColumnType.WEIGHT, DeliveryColumnType.INSURED_VALUE, DeliveryColumnType.STATE));
    }

    @Override
    protected TableRow<Delivery> createRow(TableView<Delivery> deliveryTableView) {
        var row = super.createRow(deliveryTableView);

        var menu = new ContextMenu();

        var item = new MenuItem("Request refund");
        //item.disabled = !(delivery.isInsured && delivery.status == FAILED)
        item.disableProperty().bind(
                EasyBind.monadic(row.itemProperty())
                        .filter(x -> x instanceof InsuredDelivery)
                        .flatMap(Delivery::stateProperty)
                        .map(x -> x != DeliveryState.FAILED)
                        .orElse(true)
        );
        item.setOnAction(a -> row.getItem().setState(DeliveryState.REFUND_REQUIRED));

        menu.getItems().setAll(item);

        // only display context menu for non-empty rows:
        row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(menu)
        );

        return row;
    }
}
