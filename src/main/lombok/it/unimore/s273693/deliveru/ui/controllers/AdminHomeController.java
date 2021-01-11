package it.unimore.s273693.deliveru.ui.controllers;

import it.unimore.s273693.deliveru.AppContext;
import it.unimore.s273693.deliveru.ui.mount.BaseController;
import it.unimore.s273693.deliveru.ui.table.AdminDeliveryTableView;
import javafx.fxml.FXML;

/**
 * Controller of the admin home GUI.
 * most of the logic is implemented in {@link AdminDeliveryTableView}.
 */
public class AdminHomeController extends BaseController {
    private final AppContext ctx;

    @FXML
    private AdminDeliveryTableView table;

    public AdminHomeController(AppContext ctx) {
        super("gui/admin_home.fxml");
        this.ctx = ctx;
    }

    @FXML
    private void initialize() {
        table.setCtx(ctx);
        table.setItems(ctx.getDeliveries().getDeliveries());
    }
}
