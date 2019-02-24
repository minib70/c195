package C195.View_Controller;

import C195.C195;
import C195.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    private C195 main;
    private ObservableList<Appointment> allAppointments, userAppointments;
    private ObservableList<Customer> allCustomers;
    @FXML private TableView<Appointment> tableViewUserSchedule;
    @FXML private TableColumn<Appointment, String> columnUserScheduleTitle;
    @FXML private TableColumn<Appointment, String> columnUserScheduleDescription;
    @FXML private TableColumn<Appointment, String> columnUserScheduleCustomer;
    @FXML private TableColumn<Appointment, String> columnUserScheduleStart;
    @FXML private TableColumn<Appointment, String> columnUserScheduleEnd;
    @FXML private BarChart barChartApptTypeByMonth, barChartCustomerStats;
    @FXML private CategoryAxis categoryAxisApptTypeByMonth, categoryAxisCustomerStatsCustomer;
    @FXML private NumberAxis numberAxisApptTypeByMonth, numberAxisCustomerStatsNumAppts;
    private final String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private ArrayList<Month> months;
    private ArrayList<CustomerReport> customerReports;
    private final DateTimeFormatter monthOnly = DateTimeFormatter.ofPattern("MMMM");

    public ReportsController(C195 main) {
        this.main = main;
        this.userAppointments = FXCollections.observableArrayList();
        this.allCustomers = FXCollections.observableArrayList();
        this.months = new ArrayList<>();
        this.customerReports = new ArrayList<>();
    }

    private void loadUserAppointments() {
        userAppointments = DBMethods.getUserAppointments(main.currentUser.getUsername());
        allAppointments = DBMethods.getAppointments();
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

    private void showApptsByMonthBarChart() {
        ObservableList<XYChart.Data<String, Integer>> apptByMonthData = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for(Appointment appointment: allAppointments) {
            LocalDateTime d = LocalDateTime.parse(appointment.getLocalStart(), C195.dtf);
            String apptMonth = monthOnly.format(d);
            for(Month month: months) {
                if(month.getName().equals(apptMonth)) {
                    month.hit();
                    break;
                }
            }
        }
        for(Month month: months) {
            apptByMonthData.add(new XYChart.Data<>(month.getName(), month.getCount()));
        }
        series.getData().addAll(apptByMonthData);
        series.setName("Number of Appointments in Month");
        barChartApptTypeByMonth.getData().add(series);
    }

    private void showCustomerStatsBarChart() {
        ObservableList<XYChart.Data<String, Integer>> customerStats = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for(Appointment appointment: allAppointments) {
            for(CustomerReport cr: customerReports) {
                if(appointment.getCustomerName().equals(cr.getName())) {
                    cr.hit();
                    break;
                }
            }
        }
        for(CustomerReport cr: customerReports) {
            customerStats.add(new XYChart.Data<>(cr.getName(), cr.getCount()));
        }
        series.getData().addAll(customerStats);
        series.setName("Appointments Per Customer");
        barChartCustomerStats.getData().add(series);
    }

    private void createCustomerReportObjects() {
        allCustomers = DBMethods.getCustomers();
        for(Customer customer: allCustomers) {
            CustomerReport cr = new CustomerReport(customer.getName());
            customerReports.add(cr);
        }
    }

    private void createMonthObjects() {
        for(String month: monthNames) {
            Month m = new Month(month);
            months.add(m);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserAppointments();
        createMonthObjects();
        createCustomerReportObjects();
        showApptsByMonthBarChart();
        showCustomerStatsBarChart();
    }
}
