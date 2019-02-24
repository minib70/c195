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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    private C195 main;
    private ObservableList<Appointment> userAppointments;
    @FXML private TableView<Appointment> tableViewUserSchedule;
    @FXML private TableColumn<Appointment, String> columnUserScheduleTitle;
    @FXML private TableColumn<Appointment, String> columnUserScheduleDescription;
    @FXML private TableColumn<Appointment, String> columnUserScheduleCustomer;
    @FXML private TableColumn<Appointment, String> columnUserScheduleStart;
    @FXML private TableColumn<Appointment, String> columnUserScheduleEnd;
    @FXML private BarChart barChartApptTypeByMonth, barChartCustomerStats;
    @FXML private CategoryAxis categoryAxisApptTypeByMonth, categoryAxisCustomerStatsCustomer;
    @FXML private NumberAxis numberAxisApptTypeByMonth, numberAxisCustomerStatsNumAppts;

    public ReportsController(C195 main) {
        this.main = main;
        this.userAppointments = FXCollections.observableArrayList();
    }

    private void loadUserAppointments() {
        userAppointments = DBMethods.getUserAppointments(main.currentUser.getUsername());
        showApptData();
    }

    @SuppressWarnings("Duplicates")
    private void showApptData() {
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(userAppointments, p -> true);
        SortedList<Appointment> sortedAppointments = new SortedList<>(filteredAppointments);
        // Bind sorted list to TableView
        sortedAppointments.comparatorProperty().bind(tableViewUserSchedule.comparatorProperty());
        columnUserScheduleTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnUserScheduleDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnUserScheduleCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnUserScheduleStart.setCellValueFactory(new PropertyValueFactory<>("localStart"));
        columnUserScheduleEnd.setCellValueFactory(new PropertyValueFactory<>("localEnd"));
        tableViewUserSchedule.setItems(sortedAppointments);
        tableViewUserSchedule.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserAppointments();
    }
}
