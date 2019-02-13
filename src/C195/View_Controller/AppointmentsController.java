package C195.View_Controller;

import C195.C195;
import C195.Model.Appointment;
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
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {
    private C195 main;
    private ObservableList<Appointment> appointments;
    @FXML private Label labelAppointmentTitle;
    @FXML private TableView<Appointment> tableViewAppointments;
    @FXML private TableColumn<Appointment, String> columnAppointmentsTitle;
    @FXML private TableColumn<Appointment, String> columnAppointmentsDescription;
    @FXML private TableColumn<Appointment, String> columnAppointmentsContact;
    @FXML private TableColumn<Appointment, String> columnAppointmentsLocation;
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
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, "
                    + "appointment.description, appointment.start, appointment.end, appointment.createdBy, "
                    + "appointment.location, appointment.contact "
                    + "FROM appointment"
            );
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentID(rs.getInt("appointment.appointmentId"));
                appt.setCustomerID(rs.getInt("appointment.customerId"));
                appt.setTitle(rs.getString("appointment.title"));
                appt.setDescription(rs.getString("appointment.description"));
                appt.setLocation(rs.getString("appointment.location"));
                appt.setContact(rs.getString("appointment.contact"));
                appt.setStart(rs.getString("appointment.start"));
                appt.setEnd(rs.getString("appointment.end"));
                appointments.add(appt);
            }
            showApptData();

        } catch(SQLException e){
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
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
        columnAppointmentsContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        columnAppointmentsLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnAppointmentsStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        columnAppointmentsEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        tableViewAppointments.setItems(appointments);
        tableViewAppointments.refresh();
    }

    @FXML private void customersButtonClicked() throws IOException {
        //TODO: Make sure this is fully implemented
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
        main.showAppointmentAddScreen();
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
