package C195.View_Controller;

import C195.C195;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private VBox rootLayout;
    private Stage primaryStage;
    private final String requiredPassword = "test";
    @FXML private TextField textFieldUsername, textFieldPassword;

    public LoginController(VBox rootLayout, Stage primaryStage) {
        this.rootLayout = rootLayout;
        this.primaryStage = primaryStage;
    }

    public void onEnter() throws IOException { //TODO: Really set up password stuff.
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();

        if(password.equals(requiredPassword)) { // Password is correct
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful!");
            alert.setHeaderText("Well done.  You guessed the password!");
            alert.setContentText("Password really is a good password.");
            alert.showAndWait();
            showAppointments();
        } else  {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login FAILED!");
            alert.setHeaderText("HALT!");
            alert.setContentText("Are you a hacker?");
            alert.showAndWait();
        }
    }

    public void showAppointments() throws IOException {
        // Instantiate the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Appointments.fxml"));
        AppointmentsController controller = new AppointmentsController(rootLayout);
        loader.setController(controller);

        AnchorPane appointments = loader.load();
        // Removes just the login screen from root
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(appointments);
        primaryStage.setHeight(appointments.getPrefHeight());
        primaryStage.setWidth(appointments.getPrefWidth());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}