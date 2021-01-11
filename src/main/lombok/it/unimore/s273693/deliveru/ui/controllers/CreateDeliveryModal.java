package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.db.Delivery;
import it.unimore.s273693.deliveru.db.DeliveryType;
import it.unimore.s273693.deliveru.db.InsuredDelivery;
import it.unimore.s273693.deliveru.db.User;
import it.unimore.s273693.deliveru.ui.mount.FxmlModal;
import it.unimore.s273693.deliveru.ui.util.DoubleStringConverter;
import it.unimore.s273693.deliveru.ui.util.MoneyStringConverter;
import javafx.beans.binding.Binding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.NonNull;
import org.fxmisc.easybind.EasyBind;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Modal for the creation of a new delivery.
 */
public class CreateDeliveryModal extends FxmlModal {
    private static final Pattern NON_ZERO_NUM = Pattern.compile("[1-9]");
    private final AppContext ctx;

    @FXML
    private ComboBox<DeliveryType> typeCbx;
    @FXML
    private TextField destination;
    @FXML
    private TextField weight;
    @FXML
    private TextField insuredValue;

    @FXML
    private Button create;


    public CreateDeliveryModal(AppContext ctx) {
        super("gui/create_delivery.fxml", ctx.getAppStage());
        this.ctx = ctx;
    }

    @Override
    public String getTitle() {
        return "Create Delivery";
    }

    @FXML
    private void initialize() {
        typeCbx.getItems().setAll(DeliveryType.values());
        weight.setTextFormatter(DoubleStringConverter.textFormatter(0.0));
        insuredValue.setTextFormatter(MoneyStringConverter.textFormatter(BigInteger.ZERO));

        typeCbx.setValue(DeliveryType.STANDARD);

        User user = ctx.getCurrentUser();
        if (user != null) {
            destination.setText(user.getAddress());
        }

        Binding<Boolean> isCreateDisabled = EasyBind.combine(destination.textProperty(), weight.textProperty(),
                typeCbx.valueProperty(), insuredValue.textProperty(), (des, wei, type, insVal) -> {
                    var invalid = false;
                    invalid |= des == null || des.isEmpty();
                    invalid |= wei == null || !validateField(this.weight, this::validateNum);
                    invalid |= typeCbx.getValue() == DeliveryType.INSURED &&
                            !validateField(insuredValue, this::validateNum);

                    return invalid;
                });

        create.disableProperty().bind(isCreateDisabled);

        insuredValue.disableProperty().bind(typeCbx.valueProperty().isEqualTo(DeliveryType.STANDARD));
    }

    /**
     * Validates the TextField and puts an error style if invalid.
     *
     * @param field The TextField to validate
     * @param validator The validator to use (returns true if valid)
     * @return true if the field is valid, false otherwise
     */
    private boolean validateField(@NonNull TextField field, @NonNull Function<TextField, Boolean> validator) {
        field.getStyleClass().remove("error");

        var v = field.getText();
        if (!validator.apply(field)) {
            field.getStyleClass().add("error");
            return false;
        }
        return true;
    }

    private boolean validateNum(@NonNull TextField field) {
        // We can be sure that the value, if present, is a number and is non-negative
        // as the TextFormatter ensures it.
        String txt = field.getText();
        return !txt.isEmpty() && NON_ZERO_NUM.matcher(txt).find();
    }

    @FXML
    private void onCreate() {
        User user = ctx.getCurrentUser();
        DeliveryType type = typeCbx.getValue();
        String dest = destination.getText();
        double weight = (Double) this.weight.getTextFormatter().getValue();
        BigInteger insuredValue = (BigInteger) this.insuredValue.getTextFormatter().getValue();

        UUID uuid = UUID.randomUUID();
        LocalDate now = LocalDate.now();

        Delivery delivery;
        switch (type) {
            case STANDARD:
                delivery = new Delivery(uuid, user.getId(), now, dest, weight);
                break;
            case INSURED:
                delivery = new InsuredDelivery(uuid, user.getId(), now, dest, weight, insuredValue);
                break;
            default:
                throw new IllegalStateException("Unknown delivery type: " + type);
        }
        ctx.getDeliveries().add(delivery);
        this.hide();
    }
}
