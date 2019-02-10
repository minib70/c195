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
    @FXML private Label labelCurrentCustomer;
    @FXML private TextField textFieldCustomerID, textFieldName, textFieldAddress1, textFieldAddress2, textFieldCity, textFieldCountry, textFieldPostalCode, textFieldPhone;
    @FXML private TableView<Customer> tableViewCustomers;
    @FXML private TableColumn<Customer, String> tableColumnName, tableColumnPhone, tableColumnID;
    @FXML private Button buttonModifyCustomer, buttonDeleteCustomer, buttonAddCustomer;

    public CustomersController(C195 main) {
        this.main = main;
        this.customers = FXCollections.observableArrayList();
    }

    private void loadCustomers() {
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT customer.customerName, customer.customerId, address.phone, "
                            + "address.address, address.address2, address.postalCode, city.city, "
                            + "country.country "
                            + "FROM customer, address, city, country "
                            + "WHERE customer.addressId = address.addressId "
                            + "AND address.cityId = city.cityId AND city.countryId = country.countryId"
            );
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Customer customer = new Customer();
                customer.setName(rs.getString("customer.customerName"));
                customer.setPhone(rs.getString("address.phone"));
                customer.setCustomerID(rs.getInt("customer.customerId"));
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
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
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
        //TODO: Move this to load customers?
        customers.clear();
        loadCustomers();
    }
    
    private void setCustomerTextFieldEditable(boolean editable) {
        textFieldName.setEditable(editable);
        textFieldAddress1.setEditable(editable);
        textFieldAddress2.setEditable(editable);
        textFieldCity.setEditable(editable);
        textFieldCountry.setEditable(editable);
        textFieldPostalCode.setEditable(editable);
        textFieldPhone.setEditable(editable);
    }

    @FXML private void userClickedOnCustomerTable() {
        buttonModifyCustomer.setDisable(false);
        buttonDeleteCustomer.setDisable(false);
    }

    private void showCustomerDetails(Customer selectedCustomer) {
        textFieldCustomerID.setText(String.valueOf(selectedCustomer.getCustomerID()));
        textFieldName.setText(selectedCustomer.getName());
        textFieldPhone.setText(selectedCustomer.getPhone());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO: Add translations

        // Listener for clicks on table
        tableViewCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                showCustomerDetails(newSelection);
            }
        });

        // Lock down text fields by default
        setCustomerTextFieldEditable(false);
        // Always set id to uneditable
        textFieldCustomerID.setEditable(false);

        // Set table fields
        tableViewCustomers.setEditable(true);
        tableViewCustomers.setPlaceholder(new Label("No customers found."));



        // Populate Customers
        loadCustomers();
    }
}
