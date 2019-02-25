/*
 * Author: Taylor Vories
 * WGU C195 Project
 * Appointment screen controller.  Shows existing appointments and the options to manipulate that data.
 */

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
import java.time.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {
    private C195 main;
    private ObservableList<Appointment> appointments, weeklyAppointments, monthlyAppointments;
    private ArrayList<String> appointmentAlertList;
    @FXML private Label labelAppointmentTitle;
    @FXML private TableView<Appointment> tableViewAppointments;
    @FXML private TableColumn<Appointment, String> columnAppointmentsTitle;
    @FXML private TableColumn<Appointment, String> columnAppointmentsDescription;
    @FXML private TableColumn<Appointment, String> columnAppointmentsCustomer;
    @FXML private TableColumn<Appointment, String> columnAppointmentsStart;
    @FXML private TableColumn<Appointment, String> columnAppointmentsEnd;
    @FXML private TextField textFieldApptSearch;
    @FXML private ToggleGroup toggleGroupAppointmentView;
    @FXML private RadioButton radioAllAppointments, radioMonthlyAppointments, radioWeeklyAppointments;
    @FXML private Button buttonNewAppointment, buttonModifyAppointment, buttonDeleteAppointment;

    /**
     * Constructor
     * @param main Instance of C195 main to allow for shared methods.
     */
    public AppointmentsController(C195 main) {
        this.main = main;
        this.appointments = FXCollections.observableArrayList();
        this.weeklyAppointments = FXCollections.observableArrayList();
        this.monthlyAppointments = FXCollections.observableArrayList();
        this.appointmentAlertList = new ArrayList<>();
    }

    /**
     * Loads the existing appointments in the database.
     */
    private void loadAppointments() {
        appointments = DBMethods.getAppointments();
        // Load up weekly and monthly appointments
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextWeek = now.plusWeeks(1);
        ZonedDateTime nextMonth = now.plusMonths(1);
        ZonedDateTime fifteenMinutes = now.plusMinutes(15);

        for(Appointment apt: appointments) {
            ZonedDateTime aptStart = ZonedDateTime.from(Instant.parse(apt.getStart()).atZone(ZoneId.systemDefault()));
            // Weekly
            if(aptStart.isBefore(nextWeek) && !aptStart.isBefore(now)) {
                weeklyAppointments.add(apt);
            }
            // Monthly
            if(aptStart.isBefore(nextMonth) && !aptStart.isBefore(now)) {
                monthlyAppointments.add(apt);
            }
            // 15 minute alert
            if(aptStart.isBefore(fifteenMinutes) && !aptStart.isBefore(now)) {
                appointmentAlertList.add(apt.getTitle());
            }
        }
        showApptData();
    }

    /**
     * Shows appointments in the appointment table.
     */
    @SuppressWarnings("Duplicates")
    private void showApptData() {
        ObservableList<Appointment> appts;
        if(radioAllAppointments.isSelected()) {
            appts = appointments;
        } else if(radioWeeklyAppointments.isSelected()) {
            appts = weeklyAppointments;
        } else {
            appts = monthlyAppointments;
        }
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(appts, p -> true);

        // Wrap filtered list in sorted list
        SortedList<Appointment> sortedAppointments = new SortedList<>(filteredAppointments);

        textFieldApptSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAppointments.setPredicate(appointment -> {
                if(newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Build filter for search
                String lowerCaseFilter = newValue.toLowerCase();

                if(appointment.getTitle().toLowerCase().contains(lowerCaseFilter)) { // search matches title
                    return true;
                } else if(appointment.getDescription().toLowerCase().contains(lowerCaseFilter)) { // search matches description
                    return true;
                } else if(appointment.getCustomerName().toLowerCase().contains(lowerCaseFilter)) { // search matches customer name
                    return true;
                }
                return false;
            });
        });

        // Bind sorted list to TableView
        sortedAppointments.comparatorProperty().bind(tableViewAppointments.comparatorProperty());
        columnAppointmentsTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnAppointmentsDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnAppointmentsCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnAppointmentsStart.setCellValueFactory(new PropertyValueFactory<>("localStart"));
        columnAppointmentsEnd.setCellValueFactory(new PropertyValueFactory<>("localEnd"));
        tableViewAppointments.setItems(sortedAppointments);
        tableViewAppointments.refresh();
    }

    /**
     * Clears the search field for appointment searching.
     */
    @FXML private void clearTextFieldAppointmentSearch() {
        textFieldApptSearch.clear();
    }

    /**
     * Handles the customer buttong click.  Shows the Customers screen.
     */
    @FXML private void customersButtonClicked() throws IOException {
        main.showCustomersScreen();
    }

    /**
     * Clears the table and refreshes it from the database.
     */
    @FXML private void buttonRefreshDataClicked() {
        // Clear existing data
        appointments.clear();
        weeklyAppointments.clear();
        monthlyAppointments.clear();
        loadAppointments();

    }

    /**
     * Handles the disabled buttons when an appointment is selected.
     * This prevents a user from clicking delete or modify when no appointment is selected.
     */
    @FXML private void userClickedOnAppointmentTable() {
        buttonDeleteAppointment.setDisable(false);
        buttonModifyAppointment.setDisable(false);
    }

    /**
     * Handles the new appointment button click.  Shows the appointment add screen.
     */
    @FXML private void buttonNewAppointmentClicked() throws IOException {
        main.showAppointmentAddScreen(null, appointments);
    }

    /**
     * Handles the delete button click.  Confirms before deleting selected appointment
     */
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

    /**
     * Handles button modify click.  Shows the modify screen with existing appointment.
     */
    @FXML private void buttonModifyAppointmentClicked() throws IOException {
        if(tableViewAppointments.getSelectionModel().getSelectedItems().size() == 1){
            main.showAppointmentAddScreen(tableViewAppointments.getSelectionModel().getSelectedItem(), appointments);
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
                showApptData();
            }
        });

        // Set tableView Settings
        tableViewAppointments.setEditable(true);
        tableViewAppointments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewAppointments.setPlaceholder(new Label("No appointments found."));

        // Populate Appointments
        loadAppointments();

        // Alert if an appointment starts within 15 minutes
        if(!appointmentAlertList.isEmpty()) {
            StringBuilder alert = new StringBuilder();
            for(String apt: appointmentAlertList) {
                alert.append(apt + "\n");
            }
            Alerts.reminderAlert(alert.toString());
        }
    }
}
