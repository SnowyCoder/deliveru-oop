package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.AppSettings;
import it.unimore.s273693.deliveru.password.PasswordStorageStrategy;
import it.unimore.s273693.deliveru.ui.mount.FxmlModal;
import it.unimore.s273693.deliveru.ui.util.PercentStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.util.StringConverter;
import org.fxmisc.easybind.EasyBind;

/**
 * Modal for the settings panel.
 * Controls:
 * <ul>
 *  <li>Default password storage strategy</li>
 *  <li>Enable automatic/manual delivery</li>
 *  <li>Change automatic delivery intensity and fail rate</li>
 * </ul>
 */
public class SettingsModal extends FxmlModal {
    private final AppContext ctx;

    @FXML
    private ComboBox<PasswordStorageStrategy> passwordStorage;

    @FXML
    private ToggleButton deliveryMode;

    @FXML
    private Spinner<Double> deliveryIntensity;

    @FXML
    private Spinner<Double> deliveryFailRate;


    public SettingsModal(AppContext ctx) {
        super("gui/settings.fxml", ctx.getAppStage());
        this.ctx = ctx;
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @FXML
    private void initialize() {
        AppSettings settings = this.ctx.getSettings();
        passwordStorage.setConverter(new StringConverter<PasswordStorageStrategy>() {
            @Override
            public String toString(PasswordStorageStrategy strategy) {
                return strategy.name();
            }

            @Override
            public PasswordStorageStrategy fromString(String name) {
                return passwordStorage.getItems()
                        .stream()
                        .filter(x -> x.name().equals(name))
                        .findAny()
                        .orElse(null);
            }
        });
        passwordStorage.getItems().setAll(ctx.getPasswordAuthenticator().getStrategies().values());

        deliveryMode.textProperty().bind(EasyBind.map(deliveryMode.selectedProperty(), x -> x ? "Auto" : "Manual"));

        deliveryIntensity.setValueFactory(new DoubleSpinnerValueFactory(0., 60., settings.deliveryIntensity,
                .1));

        DoubleSpinnerValueFactory deliveryFailRateFactory = new DoubleSpinnerValueFactory(0., 100.,
                settings.deliveryFailRate * 100, 1.);
        deliveryFailRateFactory.setConverter(PercentStringConverter.INSTANCE);

        deliveryFailRate.setValueFactory(deliveryFailRateFactory);

        loadSettings();
    }

    private void loadSettings() {
        AppSettings settings = this.ctx.getSettings();
        passwordStorage.setValue(ctx.getPasswordAuthenticator().getDefaultStrategy());
        deliveryMode.setSelected(settings.deliveryEnabled);
        deliveryFailRate.getValueFactory().setValue(settings.deliveryFailRate * 100);
        deliveryIntensity.getValueFactory().setValue(settings.deliveryIntensity);
    }

    @FXML
    private void onCancel() {
        this.hide();
    }

    @FXML
    private void onApply() {
        this.ctx.setSettings(new AppSettings(
                passwordStorage.getValue().id(),
                deliveryMode.isSelected(),
                deliveryIntensity.getValue(),
                deliveryFailRate.getValue() / 100.0
        ));
        this.ctx.saveSettings();
    }

    @FXML
    private void onSave() {
        onApply();
        onCancel();
    }
}
