package C195.View_Controller;

import C195.C195;
import C195.Model.Appointment;
import C195.Model.DBMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {
    private C195 main;
    private ObservableList<Appointment> appointments;
    @FXML private Label labelAppointmentTitle;
    @FXML private TableView<Appointment> tableViewAppointments;
    @FXML private TableColumn<Appointment, String> columnAppointmentsTitle;
    @FXML private TableColumn<Appointment, String> columnAppointmentsDescription;
    @FXML private TableColumn<Appointment, String> columnAppointmentsCustomer;
    @FXML private TableColumn<Appointment, String> columnAppointmentsStart;
    @FXML private TableColumn<Appointment, String> columnAppointmentsEnd;
    @FXML private ToggleGroup toggleGroupAppointmentView;
    @FXML private RadioButton radioAllAppointments, radioMonthlyAppointments, radioWeeklyAppointments;
    @FXML private Button buttonNewAppointment, buttonModifyAppointment, buttonDeleteAppointment;

    public AppointmentsController(C195 main) {
        this.main = main;
        this.appointments = FXCollections.observableArrayList();
    }

    private void loadAppointments() {
        appointments = DBMethods.getAppointments();
        showApptData();
    }

    private void showApptData() {
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(appointments, p -> true);
        //todo: add search?

        // Wrap filtered list in sorted list
        SortedList<Appointment> sortedAppointments = new SortedList<>(filteredAppointments);

        // Bind sorted list to TableVIew
        sortedAppointments.comparatorProperty().bind(tableViewAppointments.comparatorProperty());
        columnAppointmentsTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnAppointmentsDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnAppointmentsCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnAppointmentsStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        columnAppointmentsEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        tableViewAppointments.setItems(appointments);
        tableViewAppointments.refresh();
    }

    @FXML private void customersButtonClicked() throws IOException {
        main.showCustomersScreen();
    }

    @FXML private void buttonRefreshDataClicked() {
        // Clear existing data
        appointments.clear();
        loadAppointments();
    }

    @FXML private void userClickedOnAppointmentTable() {
        buttonDeleteAppointment.setDisable(false);
        buttonModifyAppointment.setDisable(false);
    }

    @FXML private void buttonNewAppointmentClicked() throws IOException {
        main.showAppointmentAddScreen(null);
    }

    @FXML private void buttonDeleteAppointmentClicked() {
        ObservableList<Appointment> appointmentsToDelete = tableViewAppointments.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete appointment?");
        alert.setHeaderText("Are you sure you want to delete the following appointment(s)?");
        StringBuilder apptsToDelete = new StringBuilder();
        // Iterate through matching parts to get names to confirm
        for(Appointment appointment: appointmentsToDelete) {
            apptsToDelete.append(appointment.getTitle()).append("\n");
        }
        alert.setContentText("Remove Appointments:\n\n" + apptsToDelete);
        Optional<ButtonType> optional = alert.showAndWait();
        if(optional.get() == ButtonType.OK) {
            for(Appointment appointment: appointmentsToDelete) {
                DBMethods.deleteAppointment(appointment);
            }
            buttonRefreshDataClicked();
        }
    }

    @FXML private void buttonModifyAppointmentClicked() throws IOException {
        if(tableViewAppointments.getSelectionModel().getSelectedItems().size() == 1){
            main.showAppointmentAddScreen(tableViewAppointments.getSelectionModel().getSelectedItem());
        } else {
            Alerts.warningAlert("Can only modify one item at a time.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labelAppointmentTitle.setText(main.rb.getString("appointment_title"));

        // Default to All appointments
        radioAllAppointments.fire();

        // Listener for radio buttons
        toggleGroupAppointmentView.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(toggleGroupAppointmentView.selectedToggleProperty() != null) {
                if(radioAllAppointments.isSelected()) {
                    // TODO: Write action
                } else if(radioMonthlyAppointments.isSelected()) {
                    // TODO: Write action
                } else if(radioWeeklyAppointments.isSelected()) {
                    //TODO: Write action.
                }
            }
        });

        // Set tableView Settings
        tableViewAppointments.setEditable(true);
        tableViewAppointments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewAppointments.setPlaceholder(new Label("No appointments found."));

        // Populate Appointments
        loadAppointments();
    }
}
