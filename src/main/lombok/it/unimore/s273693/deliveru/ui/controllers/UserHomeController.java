package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.ui.mount.BaseController;
import it.unimore.s273693.deliveru.ui.table.UserDeliveryTableView;
import javafx.fxml.FXML;

/**
 * Controller for the user home GUI.
 * most of the logic is implemented in {@link UserDeliveryTableView}.
 */
public class UserHomeController extends BaseController {
    private final AppContext ctx;

    @FXML
    private UserDeliveryTableView table;

    public UserHomeController(AppContext ctx) {
        super("gui/user_home.fxml");
        this.ctx = ctx;
    }

    @FXML
    private void initialize() {
        table.setCtx(ctx);
        table.setItems(ctx.getDeliveries().getByAuthor(ctx.getCurrentUser().getId()));
    }

    @FXML
    private void onCreate() {
        // Open in Modal
        CreateDeliveryModal pkt = new CreateDeliveryModal(this.ctx);
        pkt.show();
    }
}
