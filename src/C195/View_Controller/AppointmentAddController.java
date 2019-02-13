package C195.View_Controller;

import C195.C195;
import C195.Model.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentAddController implements Initializable {
    private C195 main;
    private ObservableList<Customer> customers;

    public AppointmentAddController(C195 main) {
        this.main = main;
    }

    @FXML public void buttonCancelClicked() throws IOException {
        main.showAppointmentsScreen();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize
    }
}
