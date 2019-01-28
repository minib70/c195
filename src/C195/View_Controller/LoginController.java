package C195.View_Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private VBox rootLayout;
    private final String requiredPassword = "test";
    @FXML private TextField textFieldUsername, textFieldPassword;

    public LoginController(VBox rootLayout) {
        this.rootLayout = rootLayout;
    }

    public void onEnter() { //TODO: Really set up password stuff.
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();

        if(password.equals(requiredPassword)) { // Password is correct
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful!");
            alert.setHeaderText("Well done.  You guessed the password!");
            alert.setContentText("Password really is a good password.");
            alert.showAndWait();
        } else  {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login FAILED!");
            alert.setHeaderText("HALT!");
            alert.setContentText("Are you a hacker?");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}