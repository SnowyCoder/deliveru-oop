package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.Constants;
import it.unimore.s273693.deliveru.ui.mount.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.fxmisc.easybind.EasyBind;


/**
 * Controller of the admin authentication GUI.
 *
 * <p>The "login" button is disabled when the username or the password are empty.</p>
 *
 * <p>After the login navigates to {@link AdminHomeController}</p>
 */
public class AdminAuthController extends BaseController  {
    private final AppContext ctx;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginBtn;

    public AdminAuthController(AppContext ctx) {
        super("gui/admin_auth.fxml");
        this.ctx = ctx;
    }

    @FXML
    private void initialize() {
        loginBtn.disableProperty().bind(EasyBind.combine(username.textProperty(), password.textProperty(),
                (usr, psw) -> username.getText().isEmpty() || password.getText().isEmpty()));
    }

    @Override
    public boolean isPopup() {
        return true;
    }

    @FXML
    private void onBack() {
        ctx.setScene(new EntryController(ctx));
    }

    @FXML
    private void onLogin() {
        if (!username.getText().equals(Constants.ADMIN_USERNAME)) {
            new Alert(Alert.AlertType.ERROR, "Wrong username").showAndWait();
            return;
        }
        if (!password.getText().equals(Constants.ADMIN_PASSWORD)) {
            new Alert(Alert.AlertType.ERROR, "Wrong password").showAndWait();
            return;
        }

        ctx.login(null);
        ctx.setScene(new AdminHomeController(ctx));
    }
}
