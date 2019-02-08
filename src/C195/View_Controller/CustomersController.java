package C195.View_Controller;

import C195.C195;
import C195.Model.Customer;
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

public class CustomersController implements Initializable {
    private C195 main;
    private ObservableList<Customer> customers;
    @FXML private TextField textFieldCustomerID, textFieldName, textFieldAddress1, textFieldAddress2, textFieldCity, textFieldCountry, textFieldPostalCode, textFieldPhone;
    @FXML private TableView<Customer> tableViewCustomers;
    @FXML private TableColumn<Customer, String> tableColumnName, tableColumnPhone;

    public CustomersController(C195 main) {
        this.main = main;
        this.customers = FXCollections.observableArrayList();
    }

    private void loadCustomers() {
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT customer.customerName, address.phone "
                            + "FROM customer, address "
                            + "WHERE customer.addressId = address.addressId"
            );
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Customer customer = new Customer();
                customer.setName(rs.getString("customer.customerName"));
                customer.setPhone(rs.getString("address.phone"));
                customers.add(customer);
            }

            showCustomerData();
        } catch(SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    private void showCustomerData() {
        FilteredList<Customer> filteredCustomers = new FilteredList<>(customers, p -> true);
        //TODO: add search
        SortedList<Customer> sortedCustomers = new SortedList<>(filteredCustomers);

        // Bind fields to tableview
        sortedCustomers.comparatorProperty().bind(tableViewCustomers.comparatorProperty());
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tableViewCustomers.setItems(sortedCustomers);
        tableViewCustomers.refresh();
        if(tableViewCustomers.getItems().size() > 0) {
            tableViewCustomers.getSelectionModel().clearAndSelect(0);
        }
    }

    @FXML private void appointmentsButtonClicked() throws IOException {
        main.showAppointmentsScreen();
    }

    @FXML private void buttonRefreshDataClicked() {
        // Clear existing data
        customers.clear();
        loadCustomers();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO: Add translations

        // Lock down text fields by default
        textFieldCustomerID.setEditable(false);
        textFieldName.setEditable(false);
        textFieldAddress1.setEditable(false);
        textFieldAddress2.setEditable(false);
        textFieldCity.setEditable(false);
        textFieldCountry.setEditable(false);
        textFieldPostalCode.setEditable(false);
        textFieldPhone.setEditable(false);

        // Set table fields
        tableViewCustomers.setEditable(true);
        tableViewCustomers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewCustomers.setPlaceholder(new Label("No customers found."));

        // Populate Customers
        loadCustomers();
    }
}
