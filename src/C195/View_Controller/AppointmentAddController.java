package C195.View_Controller;

import C195.C195;
import C195.Model.Customer;
import C195.Model.DBMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class AppointmentAddController implements Initializable {
    private C195 main;
    private ObservableList<Customer> customers;
    @FXML private TableView<Customer> tableViewCustomers;
    @FXML private TableColumn<Customer, String> tableColumnCustomerName;
    @FXML private ComboBox<String> comboBoxStartTime, comboBoxEndTime, comboBoxType;
    private final ObservableList<String> startTimes, endTimes, appointmentTypes;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    @FXML private DatePicker datePicker;


    public AppointmentAddController(C195 main) {
        this.main = main;
        customers = FXCollections.observableArrayList();
        startTimes = FXCollections.observableArrayList();
        endTimes = FXCollections.observableArrayList();
        appointmentTypes = FXCollections.observableArrayList();
    }

    @FXML public void buttonCancelClicked() throws IOException {
        main.showAppointmentsScreen();
    }

    public void populateTimes() {
        // Set up start times
        LocalTime time = LocalTime.of(8, 0);
        do {
            startTimes.add(time.format(timeFormatter));
            endTimes.add(time.format(timeFormatter));
            time = time.plusMinutes(15);
        } while(!time.equals(LocalTime.of(17, 15)));
        comboBoxStartTime.setItems(startTimes);
        comboBoxEndTime.setItems(endTimes);
    }

    @SuppressWarnings("Duplicates")
    private void showCustomerDataTable() {
        FilteredList<Customer> filteredCustomers = new FilteredList<>(customers, p -> true);
        SortedList<Customer> sortedCustomers = new SortedList<>(filteredCustomers);

        // Bind fields to tableview
        sortedCustomers.comparatorProperty().bind(tableViewCustomers.comparatorProperty());
        tableColumnCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableViewCustomers.setItems(sortedCustomers);
        tableViewCustomers.refresh();
        if(tableViewCustomers.getItems().size() > 0) {
            tableViewCustomers.getSelectionModel().clearAndSelect(0);
        }
    }

    private void populateAppointmentTypes() {
        String[] types = {
                "New Customer Introduction",
                "Insider Trading Meeting",
                "Team Gossip Session",
                "Blame Assignment"
        };
        appointmentTypes.addAll(types);
        comboBoxType.setItems(appointmentTypes);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize
        customers = DBMethods.getCustomers();

        // Show customer table
        showCustomerDataTable();

        populateTimes();

        populateAppointmentTypes();
    }
}
