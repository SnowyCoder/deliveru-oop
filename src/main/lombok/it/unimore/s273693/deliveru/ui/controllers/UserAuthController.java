package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.db.User;
import it.unimore.s273693.deliveru.password.PasswordAuthenticator;
import it.unimore.s273693.deliveru.ui.mount.BaseController;
import javafx.beans.binding.Binding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fxmisc.easybind.EasyBind;

/**
 * Controller for the user authentication GUI.
 * The login/register button is disabled when the username or the password
 * (or the address) are empty.
 * After the login navigates to {@link UserHomeController}.
 */
public class UserAuthController extends BaseController {
    private final AppContext ctx;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField address;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox isUserNew;


    public UserAuthController(AppContext ctx) {
        super("gui/user_auth.fxml");
        this.ctx = ctx;
    }

    @Override
    public boolean isPopup() {
        return true;
    }

    @FXML
    private void initialize() {
        Binding<Boolean> isCreateDisabled = EasyBind.combine(username.textProperty(), password.textProperty(),
                isUserNew.selectedProperty(), address.textProperty(), (usr, psw, isNew, addr) -> {
                    if (usr == null || usr.isEmpty()) return true;
                    if (psw == null || psw.isEmpty()) return true;
                    if (isNew && addr.isEmpty()) return true;
                    return false;
                });

        loginButton.disableProperty().bind(isCreateDisabled);
        address.disableProperty().bind(isUserNew.selectedProperty().not());
        loginButton.textProperty().bind(EasyBind.map(isUserNew.selectedProperty(), x -> x ? "Register" : "Login"));
    }

    @FXML
    private void onBack() {
        ctx.setScene(new EntryController(ctx));
    }

    @FXML
    private void onLogin() {
        if (this.isUserNew.isSelected()) {
            doRegister(username.getText(), password.getText(), address.getText());
        } else {
            doLogin(username.getText(), password.getText());
        }
    }

    private void afterLogin(User user) {
        ctx.login(user);
        ctx.setScene(new UserHomeController(ctx));
    }

    private void doLogin(String uname, String upassword) {
        User user = ctx.getUsers().getUserByName(uname).orElse(null);
        if (user == null) {
            new Alert(Alert.AlertType.ERROR, "Username not found").show();
            return;
        }
        PasswordAuthenticator authenticator = ctx.getPasswordAuthenticator();
        if (!authenticator.check(user.getPassword(), upassword)) {
            new Alert(Alert.AlertType.ERROR, "Wrong password").show();
            return;
        }
        afterLogin(user);
    }

    private void doRegister(String uname, String upassword, String address) {
        if (ctx.getUsers().getUserByName(uname).isPresent()) {
            new Alert(Alert.AlertType.ERROR, "Username already taken").show();
            return;
        }
        User user = new User(
                uname,
                ctx.getPasswordAuthenticator().encode(upassword),
                address
        );
        if (!ctx.getUsers().registerUser(user)) {
            new Alert(Alert.AlertType.ERROR, "Cannot register user").show();
            return;
        }
        ctx.saveUsers();
        afterLogin(user);
    }
}
