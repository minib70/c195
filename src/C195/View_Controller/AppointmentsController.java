package C195.View_Controller;

import C195.C195;
import C195.Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

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

    public AppointmentsController(C195 main) {
        this.main = main;
    }

    private void loadAppointments() {
        appointments = new FXCollections.observableArrayList();

        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, "
                    + "appointment.description, appointment.start, appointment.end, appointment.createdBy, "
                    + "customer.customerName, customer.customerId "
                    + "FROM appointment,customer "
                    + "WHERE appointment.customerId = customer.customerId AND appointment.createdBy = ? "
                    + "ORDER BY start"
            );
            stmt.setString(1, main.currentUser.getUsername());
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentID(rs.getInt("appointment,appointmentId"));
                appt.setCustomerID(rs.getInt("appointment.customerId"));
                appt.setTitle(rs.getString("appointment.title"));
                appt.setDescription(rs.getString("appointment.description"));
                appt.setLocation(rs.getString("appointment.location"));
                appt.setContact(rs.getString("appointment.contact"));
                appt.setStart(rs.getString("appointment.start"));
                appt.setEnd(rs.getString("appointment.end"));
                appointments.add(appt);
            }

            tableViewMonthly.getItems().setAll(appointments);

        } catch(SQLException e){
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO: Potentially translate this.
        labelTitle.setText("Appointments - " + main.currentUser.getUsername());

        // Populate Appointments
        loadAppointments();
    }
}
