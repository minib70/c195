package C195.View_Controller;

import C195.C195;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

public class RootLayoutController {
    private C195 c195; // TODO: May not need this

    public RootLayoutController(C195 c195) {
        this.c195 = c195;
    }

    @FXML public MenuItem fileCloseMenuItem;
    // Handle fileCloseMenuClick
    @FXML public void handleClose(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Shows About Dialog
     */
    @FXML public void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("C195 Project");
        alert.setHeaderText("About");
        alert.setContentText("Author: Taylor Vories\nC195 Java Project");

        // Show alert
        alert.showAndWait();
    }

}
