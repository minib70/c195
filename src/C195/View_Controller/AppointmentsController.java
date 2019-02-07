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

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {
    private C195 main;
    private ObservableList<Appointment> appointments;
    @FXML private Label labelTitle;
    @FXML private TableView<Appointment> tableViewMonthly;
    @FXML private TableColumn<Appointment, String> columnMonthlyTitle;
    @FXML private TableColumn<Appointment, String> columnMonthlyDescription;
    @FXML private TableColumn<Appointment, String> columnMonthlyContact;
    @FXML private TableColumn<Appointment, String> columnMonthlyLocation;
    @FXML private TableColumn<Appointment, String> columnMonthlyStart;
    @FXML private TableColumn<Appointment, String> columnMonthlyEnd;
    @FXML private ToggleGroup toggleGroupAppointmentView;
    @FXML private RadioButton radioAllAppointments, radioMonthlyAppointments, radioWeeklyAppointments;

    public AppointmentsController(C195 main) {
        this.main = main;
        this.appointments = FXCollections.observableArrayList();
    }

    private void showMonthlyApptData(ObservableList<Appointment> appointments) {
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(appointments, p -> true);
        //todo: add search?

        // Wrap filtered list in sorted list
        SortedList<Appointment> sortedAppointments = new SortedList<>(filteredAppointments);

        // Bind sorted list to TableVIew
        sortedAppointments.comparatorProperty().bind(tableViewMonthly.comparatorProperty());
        columnMonthlyTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnMonthlyDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnMonthlyContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        columnMonthlyLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnMonthlyStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        columnMonthlyEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        tableViewMonthly.setItems(sortedAppointments);
        tableViewMonthly.refresh();
    }

    private void loadAppointments() {
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, "
                    + "appointment.description, appointment.start, appointment.end, appointment.createdBy, "
                    + "appointment.location, appointment.contact, customer.customerName, customer.customerId "
                    + "FROM appointment, customer "
                    + "WHERE appointment.customerId = customer.customerId "
                    + "ORDER BY start"
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
            showMonthlyApptData(appointments);

        } catch(SQLException e){
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO: Potentially translate this.
        labelTitle.setText("Appointments - " + main.currentUser.getUsername());

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

        // Populate Appointments
        loadAppointments();
    }
}
