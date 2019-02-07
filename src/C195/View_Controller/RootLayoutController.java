package C195.View_Controller;

import C195.C195;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RootLayoutController {
    private C195 main;

    public RootLayoutController(C195 main) {
        this.main = main;
    }
    @FXML public MenuItem fileCloseMenuItem, menuItemLogout;
    @FXML private Menu menuView, menuLoggedInUser;
    // Handle fileCloseMenuClick
    @FXML public void handleClose(ActionEvent event) {
        System.exit(0);
    }

    public void showViewMenu() {
        menuView.setVisible(true);
    }

    public void hideViewMenu() {
        menuView.setVisible(false);
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

    public void logoutUser() throws IOException {
        if(main.currentUser != null) {
            // Set current user to Null
            main.currentUser = null;
            main.showLoginScreen();
        }
    }

    @FXML public void setLoggedInUser(String loggedInUser) {
        menuLoggedInUser.setText("Logged in User: " + loggedInUser);
        menuItemLogout.setDisable(false);
    }

}
