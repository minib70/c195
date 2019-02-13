package C195.View_Controller;

import C195.C195;
import C195.Model.City;
import C195.Model.Customer;
import C195.Model.Validation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {
    private C195 main;
    private ObservableList<Customer> customers;
    private ObservableList<City> cities;
    @FXML private Label labelCustomerStatus;
    @FXML private TextField textFieldCustomerID, textFieldName, textFieldAddress1, textFieldAddress2, textFieldCountry, textFieldPostalCode, textFieldPhone;
    @FXML private TableView<Customer> tableViewCustomers;
    @FXML private TableColumn<Customer, String> tableColumnName, tableColumnPhone, tableColumnID;
    @FXML private Button buttonModifyCustomer, buttonDeleteCustomer, buttonAddCustomer, buttonSave, buttonCancel;
    @FXML private ComboBox<City> comboBoxCity;
    private TableView.TableViewSelectionModel<Customer> defaultSelectionModel;
    private Customer modifyCustomer;
    private boolean isModify;

    public CustomersController(C195 main) {
        this.main = main;
        this.customers = FXCollections.observableArrayList();
        this.cities = FXCollections.observableArrayList();
    }

    private void loadCustomers() {
        try {
            PreparedStatement stmt = C195.dbConnection.prepareStatement(
                    "SELECT customer.customerName, customer.customerId, address.phone, "
                            + "address.address, address.address2, address.postalCode, address.addressId, "
                            + "city.city, city.cityId, country.country, country.countryId "
                            + "FROM customer, address, city, country "
                            + "WHERE customer.addressId = address.addressId "
                            + "AND address.cityId = city.cityId AND city.countryId = country.countryId"
            );
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Customer customer = new Customer();
                customer.setName(rs.getString("customer.customerName"));
                customer.setPhone(rs.getString("address.phone"));
                customer.setCustomerId(rs.getInt("customer.customerId"));
                customer.setAddress(rs.getString("address.address"));
                customer.setAddress2(rs.getString("address.address2"));
                customer.setCity(rs.getString("city.city"));
                customer.setPostalCode(rs.getString("address.postalCode"));
                customer.setCountry(rs.getString("country.country"));
                customer.setAddressId(rs.getInt("address.addressId"));
                customer.setCityId(rs.getInt("city.cityId"));
                customer.setCountryId(rs.getInt("country.countryId"));
                customers.add(customer);
            }

            // Populate cities and countries
            PreparedStatement citystmt = C195.dbConnection.prepareStatement(
                    "SELECT city.city, city.cityId, country.country, country.countryId "
                    + "FROM city, country "
                    + "WHERE city.countryId = country.countryId"
            );

            ResultSet cityrs = citystmt.executeQuery();
            while(cityrs.next()) {
                City city = new City();
                city.setCity(cityrs.getString("city.city"));
                city.setCountry(cityrs.getString("country.country"));

                city.setCityId(cityrs.getInt("city.cityId"));
                city.setCountryId(cityrs.getInt("country.countryId"));
                cities.add(city);
            }

            showCustomerDataTable();
            populateCityComboBox();
        } catch(SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    private void showCustomerDataTable() {
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
        // ComboBox is a bit different than text field, so it is the opposite of editable
        comboBoxCity.setDisable(!editable);
        comboBoxCity.setStyle("-fx-text-fill: -fx-text-base-color;-fx-opacity: 1;");
        // this will be auto populated
        // textFieldCountry.setEditable(editable);
        textFieldPostalCode.setEditable(editable);
        textFieldPhone.setEditable(editable);
    }

    @FXML private void userClickedOnCustomerTable() {
        buttonModifyCustomer.setDisable(false);
        buttonDeleteCustomer.setDisable(false);
    }

    private void showCustomerDetails(Customer selectedCustomer) {
        textFieldCustomerID.setText(String.valueOf(selectedCustomer.getCustomerId()));
        textFieldName.setText(selectedCustomer.getName());
        textFieldAddress1.setText(selectedCustomer.getAddress());
        textFieldAddress2.setText(selectedCustomer.getAddress2());
        City city = new City();
        city.setCity(selectedCustomer.getCity());
        city.setCountry(selectedCustomer.getCountry());
        comboBoxCity.setValue(city);
        textFieldCountry.setText(selectedCustomer.getCountry());
        textFieldPostalCode.setText(selectedCustomer.getPostalCode());
        textFieldPhone.setText(selectedCustomer.getPhone());
    }

    private void setButtonVisable(boolean visable) {
        buttonSave.setVisible(visable);
        buttonCancel.setVisible(visable);
    }

    @FXML private void modifyButtonClicked() {
        modifyCustomer = tableViewCustomers.getSelectionModel().getSelectedItem();
        editMode(true);
        populateCityComboBox();
        City city = new City();
        city.setCityId(modifyCustomer.getCityId());
        city.setCity(modifyCustomer.getCity());
        city.setCountryId(modifyCustomer.getCountryId());
        city.setCountry(modifyCustomer.getCountry());
        comboBoxCity.setValue(city);
        labelCustomerStatus.setText("MODIFYING CUSTOMER - " + modifyCustomer.getName());
        labelCustomerStatus.setStyle("-fx-background-color: WHITE; -fx-font-size: 120%;");
        isModify = true;
    }

    private void editMode(boolean editable) {
        setButtonVisable(editable);
        setCustomerTextFieldEditable(editable);
        buttonModifyCustomer.setDisable(editable);
        if(editable) {
            tableViewCustomers.setSelectionModel(null);
        } else {
            tableViewCustomers.setSelectionModel(defaultSelectionModel);
        }
    }

    @FXML private void cancelButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel modify customer?");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Are you sure?  Your changes will be lost.");
        Optional<ButtonType> optional = alert.showAndWait();
        if(optional.get() == ButtonType.OK) {
            editMode(false);
            Customer currentCustomer =  tableViewCustomers.getSelectionModel().getSelectedItem();
            // Clear textfield error highlight
            textFieldPostalCode.setStyle(null);
            textFieldAddress1.setStyle(null);
            textFieldPhone.setStyle(null);
            textFieldName.setStyle(null);
            labelCustomerStatus.setText(null);
            labelCustomerStatus.setStyle(null);
            showCustomerDetails(currentCustomer);
        }
    }

    @FXML private void saveButtonClicked() {
        // Validate fields
        String nameValidation = Validation.validateName(textFieldName.getText());
        String addressValidation = Validation.validateAddress(textFieldAddress1.getText());
        String zipValidation = Validation.validateZip(textFieldPostalCode.getText());
        String phoneValidation = Validation.validatePhone(textFieldPhone.getText());

        // Check for errors
        StringBuilder errors = new StringBuilder();
        errors.append(validationErrors(textFieldName, nameValidation));
        errors.append(validationErrors(textFieldAddress1, addressValidation));
        errors.append(validationErrors(textFieldPostalCode, zipValidation));
        errors.append(validationErrors(textFieldPhone, phoneValidation));

        // if no errors, continue
        if(errors.length() >= 1) {
            Alerts.warningAlert(errors.toString());
        } else {
            // Confirm user wants to save
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if(isModify) {
                alert.setTitle("Save Customer - " + modifyCustomer.getName());
                alert.setContentText("When you save, it will save new or overwrite existing customer.  Are you sure?");
            } else {
                alert.setTitle("Save New Customer - " + textFieldName.getText());
                alert.setContentText("Saving new customer.  Are you sure?");
            }
            alert.setHeaderText("Are you sure you want to save?");
            Optional<ButtonType> optional = alert.showAndWait();
            if(optional.get() == ButtonType.OK) {
                Customer customerToSave = new Customer();
                City customerCity = comboBoxCity.getSelectionModel().getSelectedItem();
                if(isModify) {
                    customerToSave.setCustomerId(modifyCustomer.getCustomerId());
                    customerToSave.setAddressId(modifyCustomer.getAddressId());
                }
                customerToSave.setName(textFieldName.getText());
                customerToSave.setAddress(textFieldAddress1.getText());
                if(textFieldAddress2 != null) {
                    customerToSave.setAddress2(textFieldAddress2.getText());
                } else {
                    customerToSave.setAddress2(" ");
                }

                customerToSave.setCity(customerCity.getCity());
                customerToSave.setCountry(customerCity.getCountry());
                customerToSave.setPostalCode(textFieldPostalCode.getText());
                customerToSave.setPhone(textFieldPhone.getText());
                customerToSave.setCityId(customerCity.getCityId());
                customerToSave.setCountryId(customerCity.getCountryId());
                saveCustomerToDb(customerToSave);
                labelCustomerStatus.setText(null);
                labelCustomerStatus.setStyle(null);
                editMode(false);
                buttonRefreshDataClicked(); //TODO: Set this to an action instead of button
                // Clears modify object
                modifyCustomer = null;
            }
            editMode(false);
        }
    }

    @FXML private void deleteButtonClicked() {
        Customer customerToDelete = tableViewCustomers.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer?");
        alert.setHeaderText("Are you sure you want to delete the following customer?");
        alert.setContentText("Delete user: \n\n" + customerToDelete.getName());
        Optional<ButtonType> optional = alert.showAndWait();
        if(optional.get() == ButtonType.OK) {
            deleteCustomer(customerToDelete);
            buttonRefreshDataClicked(); //TODO: set to a method and not a button
        }
    }

    private void deleteCustomer(Customer customerToDelete) {
        try {
            PreparedStatement custD = C195.dbConnection.prepareStatement(
                    "DELETE customer.*, address.* "
                    + "FROM customer, address "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId "
            );
            custD.setInt(1, customerToDelete.getCustomerId());
            custD.execute();
        } catch(SQLException e) {
            System.out.println("Issue with SQL.");
            e.printStackTrace();
        }
    }

    @FXML private void addCustomerButtonClicked() {
        editMode(true);
        populateCityComboBox();
        labelCustomerStatus.setText("ADDING NEW CUSTOMER");
        labelCustomerStatus.setStyle("-fx-background-color: WHITE; -fx-font-size: 120%;");
        clearTextFields();
        isModify = false;
    }

    private void clearTextFields() {
        textFieldName.setText(null);
        textFieldAddress1.setText(null);
        textFieldAddress2.setText(null);
        textFieldPostalCode.setText(null);
        textFieldPhone.setText(null);
        textFieldCustomerID.setText("VALUE GENERATED AUTOMATICALLY");
    }

    private void addNewCustomer(Customer customerToSave) {

    }

    @SuppressWarnings("Duplicates")
    private void saveCustomerToDb(Customer customerToSave) {
        // This tells us if this is a modify customer or a new customer
        if(customerToSave.getCustomerId() > 0) {
            // This is a modify so we need to match it to an existing record in the db
            try {
                // Modify address
                PreparedStatement addr = C195.dbConnection.prepareStatement(
                        "UPDATE address, customer, city, country "
                            + "SET address.address = ?, address.address2 = ?, address.cityId = ?, "
                            + "address.postalCode = ?, address.phone = ?, address.lastUpdate = ?, address.lastUpdateBy = ? "
                            + "WHERE customer.customerId = ? AND customer.addressId = address.addressId "
                            + "AND address.cityId = city.cityId AND city.countryId = country.countryId "
                );
                addr.setString(1, customerToSave.getAddress());
                addr.setString(2, customerToSave.getAddress2());
                addr.setInt(3, customerToSave.getCityId());
                addr.setString(4, customerToSave.getPostalCode());
                addr.setString(5, customerToSave.getPhone());
                addr.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                addr.setString(7, main.currentUser.getUsername());
                addr.setInt(8, customerToSave.getCustomerId());
                addr.execute();

                // Modify Customer
                PreparedStatement cust = C195.dbConnection.prepareStatement(
                        "UPDATE address, customer "
                        + "SET customer.customerName = ?, customer.lastUpdateBy = ?, customer.lastUpdateBy = ?  "
                        + "WHERE customer.customerId = ? AND customer.addressId = address.addressId "
                );
                cust.setString(1, customerToSave.getName());
                cust.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                cust.setString(3, main.currentUser.getUsername());
                cust.setInt(4, customerToSave.getCustomerId());
                cust.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else { // This is a new customer
            try {
                // Create new address
                PreparedStatement newAddr = C195.dbConnection.prepareStatement(
                        "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
                );
                newAddr.setString(1, customerToSave.getAddress());
                if(customerToSave.getAddress2() == null) {
                    newAddr.setString(2, "");
                } else {
                    newAddr.setString(2, customerToSave.getAddress2());
                }
                newAddr.setInt(3, customerToSave.getCityId());
                newAddr.setString(4, customerToSave.getPostalCode());
                newAddr.setString(5, customerToSave.getPhone());
                newAddr.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                newAddr.setString(7, main.currentUser.getUsername());
                newAddr.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
                newAddr.setString(9, main.currentUser.getUsername());
                newAddr.execute();

                // Assign address ID to customer
                ResultSet rs = newAddr.getGeneratedKeys();
                if(rs.next()) {
                    customerToSave.setAddressId(rs.getInt(1));
                } else {
                    System.out.println("No generated key returns, customer is flawed.");
                    customerToSave.setAddressId(-1);
                }

                // Create new Customer
                PreparedStatement newCust = C195.dbConnection.prepareStatement(
                        "INSERT INTO customer (customername, addressid, active, createdate, createdby, lastupdate, lastupdateby) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                );
                newCust.setString(1, customerToSave.getName());
                newCust.setInt(2, customerToSave.getAddressId());
                newCust.setInt(3, 1);
                newCust.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                newCust.setString(5, main.currentUser.getUsername());
                newCust.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                newCust.setString(7, main.currentUser.getUsername());
                newCust.execute();

            } catch(SQLException e) {
                System.out.println("Issue with SQL");
                e.printStackTrace();
            }
        }
    }

    private String validationErrors(TextField field, String validations) {
        StringBuilder errors = new StringBuilder();
        if(!validations.isEmpty()) {
            errors.append(validations);
            errors.append("\n");
            field.setStyle("-fx-border-color: #ba171c;");
        } else {
            field.setStyle(null);
        }
        return errors.toString();
    }

    @FXML private void mouseClickedCountryTextField() {
        textFieldCountry.getParent().requestFocus();
    }
    @FXML private void mouseClickedCustomerIDTextField() {
        textFieldCustomerID.getParent().requestFocus();
    }

    public void populateCityComboBox() {
        if(cities.size() > 0) {
            comboBoxCity.setItems(cities);
        } else {
            City city = new City();
            city.setCity("No Cities in database.");
            comboBoxCity.setValue(city);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO: Add translations

        // Hide save and cancel by default
        buttonSave.setVisible(false);
        buttonCancel.setVisible(false);

        // Listener for clicks on table
        tableViewCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                showCustomerDetails(newSelection);
            }
        });

        // Keep City and Country in sync
        comboBoxCity.valueProperty().addListener((obs, oldSelection, newSelection) -> {
                if(newSelection != null) {
                    textFieldCountry.setText(newSelection.getCountry());
                }
            });

        // Combobox setup
        comboBoxCity.setConverter(new StringConverter<City>() {

            @Override
            public String toString(City object) {
                return object.getCity();
            }

            @Override
            public City fromString(String string) {
                return comboBoxCity.getItems().stream().filter(ap ->
                        ap.getCity().equals(string)).findFirst().orElse(null);
            }
        });

        // Listener for clicks on Country
        textFieldCountry.setTooltip(new Tooltip("Country is set automatically when city is selected."));

        // Listener for clicks on ID
        textFieldCustomerID.setTooltip(new Tooltip("ID is calculated automatically."));

        // Lock down text fields by default
        setCustomerTextFieldEditable(false);
        // Always set id to uneditable
        textFieldCustomerID.setEditable(false);

        // Get default selection model to enable and disable table clicks during edit
        defaultSelectionModel = tableViewCustomers.getSelectionModel();

        // Set table fields
        tableViewCustomers.setEditable(true);
        tableViewCustomers.setPlaceholder(new Label("No customers found."));

        // Populate Customers
        loadCustomers();
    }
}
