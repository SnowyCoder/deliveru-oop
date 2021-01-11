package it.unimore.s273693.deliveru.ui.table;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.db.Delivery;
import it.unimore.s273693.deliveru.db.DeliveryState;
import it.unimore.s273693.deliveru.db.InsuredDelivery;
import it.unimore.s273693.deliveru.db.User;
import it.unimore.s273693.deliveru.ui.util.MoneyStringConverter;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.fxmisc.easybind.EasyBind;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Custom TableView to visualize a Delivery.
 * Should be extended to specialize some behaviour.
 * The AppContext is needed so remember to call {@link #setCtx(AppContext)}.
 */
public class DeliveryTableView extends TableView<Delivery> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * The application context.
     *
     * @param ctx The new context
     * @return The current context
     */
    @Getter
    @Setter
    private AppContext ctx;

    public DeliveryTableView() {
        super();
        this.setRowFactory(this::createRow);
    }

    /**
     * Resets the table columns selecting only the ones passed as argument.
     *
     * @param columnTypes The columns to show.
     */
    public void setColumnTypes(EnumSet<DeliveryColumnType> columnTypes) {
        var cols = this.getColumns();
        cols.clear();

        if (columnTypes.contains(DeliveryColumnType.CODE)) {
            var uuidCol = new TableColumn<Delivery, UUID>("Code");
            uuidCol.setCellValueFactory(new PropertyValueFactory<>("code"));
            cols.add(uuidCol);
        }

        if (columnTypes.contains(DeliveryColumnType.SENDER)) {
            var senderCol = new TableColumn<Delivery, String>("Sender");
            senderCol.setCellValueFactory(cell -> {
                UUID uuid = cell.getValue().getSender();
                String name = ctx.getUsers().getUserById(uuid)
                        .map(User::getUsername)
                        .orElseGet(uuid::toString);
                return new ReadOnlyObjectWrapper<>(name);
            });
            cols.add(senderCol);
        }

        if (columnTypes.contains(DeliveryColumnType.DATE)) {
            var dateCol = new TableColumn<Delivery, LocalDate>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            dateCol.setCellFactory(column -> new TableCell<Delivery, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(DATE_FORMAT.format(item));
                    }
                }
            });
            cols.add(dateCol);
        }

        if (columnTypes.contains(DeliveryColumnType.DESTINATION)) {
            var destinationCol = new TableColumn<Delivery, String>("Destination");
            destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
            cols.add(destinationCol);
        }

        if (columnTypes.contains(DeliveryColumnType.WEIGHT)) {
            var weightCol = new TableColumn<Delivery, String>("Weight");
            weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
            cols.add(weightCol);
        }

        if (columnTypes.contains(DeliveryColumnType.INSURED_VALUE)) {
            TableColumn<Delivery, String> valCol = new TableColumn<>("InsVal");
            valCol.setCellValueFactory(x -> {
                String value = "";
                var delivery = x.getValue();
                if (delivery instanceof InsuredDelivery) {
                    BigInteger v = ((InsuredDelivery) delivery).getInsuredValue();
                    value = MoneyStringConverter.INSTANCE.toString(v);
                }
                return new ReadOnlyObjectWrapper<>(value);
            });
            cols.add(valCol);
        }

        if (columnTypes.contains(DeliveryColumnType.STATE)) {
            var stateCol = new TableColumn<Delivery, DeliveryState>("State");
            stateCol.setCellValueFactory(x -> x.getValue().stateProperty());
            cols.add(stateCol);
        }
    }

    /**
     * Called once a row for the Table is created.
     * binds the color, you can override this to customize it's behaviour
     * (adding context menus, as an example).
     *
     * @param deliveryTableView The table
     * @return The created row
     */
    protected TableRow<Delivery> createRow(TableView<Delivery> deliveryTableView) {
        var row = new TableRow<Delivery>();

        // Color this row's background
        row.backgroundProperty().bind(
                EasyBind.monadic(row.itemProperty())
                        .flatMap(Delivery::stateProperty)
                        .map(state -> new Background(
                                new BackgroundFill(colorFromState(state), CornerRadii.EMPTY, Insets.EMPTY)))
        );
        return row;
    }

    /**
     * Selects a color based on a delivery state.
     *
     * @param state The state of the delivery
     * @return A color associated with it.
     */
    protected Color colorFromState(DeliveryState state) {
        switch (state) {
            case IN_PREPARATION:  return Color.LIGHTGRAY;
            case IN_TRANSIT:      return Color.LIGHTCYAN;
            case RECEIVED:        return Color.LIGHTGREEN;
            case FAILED:          return Color.ORANGERED;
            case REFUND_REQUIRED: return Color.LIGHTBLUE;
            case REFUND_PAID:     return Color.LIGHTYELLOW;
            default:
                throw new IllegalArgumentException("Unknown state " + state);
        }
    }
}
