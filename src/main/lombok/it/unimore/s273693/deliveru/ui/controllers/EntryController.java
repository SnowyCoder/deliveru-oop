package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.ui.mount.BaseController;
import javafx.fxml.FXML;

/**
 * Controller of the main App entrypoint.
 * It leads to either {@link AdminAuthController} or to {@link UserAuthController}.
 */
public class EntryController extends BaseController {
    private final AppContext ctx;

    public EntryController(AppContext ctx) {
        super("gui/entry.fxml");
        this.ctx = ctx;
    }

    @Override
    public boolean isPopup() {
        return true;
    }

    @FXML
    private void onEnterAdmin() {
        ctx.setScene(new AdminAuthController(ctx));
    }

    @FXML
    private void onEnterUser() {
        ctx.setScene(new UserAuthController(ctx));
    }
}
