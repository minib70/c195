package C195.View_Controller;

import C195.C195;
import C195.Model.City;
import C195.Model.Customer;
import C195.Model.Validation;
import com.sun.org.apache.bcel.internal.generic.Select;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import oracle.jrockit.jfr.openmbean.RecordingOptionsType;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public CustomersController(C195 main) {
        this.main = main;
        this.customers = FXCollections.observableArrayList();
        this.cities = FXCollections.observableArrayList();
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
                customer.setAddress(rs.getString("address.address"));
                customer.setAddress2(rs.getString("address.address2"));
                customer.setCity(rs.getString("city.city"));
                customer.setPostalCode(rs.getString("address.postalCode"));
                customer.setCountry(rs.getString("country.country"));
                customers.add(customer);
            }

            // Populate cities and countries
            PreparedStatement citystmt = C195.dbConnection.prepareStatement(
                    "SELECT city.city, country.country "
                    + "FROM city, country "
                    + "WHERE city.countryId = country.countryId"
            );

            ResultSet cityrs = citystmt.executeQuery();
            while(cityrs.next()) {
                City city = new City();
                city.setCity(cityrs.getString("city.city"));
                city.setCountry(cityrs.getString("country.country"));
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
        textFieldCustomerID.setText(String.valueOf(selectedCustomer.getCustomerID()));
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
        labelCustomerStatus.setText("MODIFYING CUSTOMER - " + modifyCustomer.getName());
        labelCustomerStatus.setStyle("-fx-background-color: WHITE; -fx-font-size: 120%;");
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
            alert.setTitle("Save Customer - " + modifyCustomer.getName());
            alert.setHeaderText("Are you sure you want to save?");
            alert.setContentText("When you save, it will save new or overwrite existing customer.  Are you sure?");
            Optional<ButtonType> optional = alert.showAndWait();
            if(optional.get() == ButtonType.OK) {
                Customer customerToSave = new Customer();
                customerToSave.setName(textFieldName.getText());
                customerToSave.setAddress(textFieldAddress1.getText());
                customerToSave.setAddress2(textFieldAddress2.getText());
                customerToSave.setCity(comboBoxCity.getSelectionModel().getSelectedItem().getCity());
                customerToSave.setCountry(textFieldCountry.getText());
                customerToSave.setPostalCode(textFieldPostalCode.getText());
                customerToSave.setPhone(textFieldPhone.getText());
                saveCustomerToDb(customerToSave);
                editMode(false);
                //TODO: refresh table data
            }
            editMode(false);
        }
    }

    private void saveCustomerToDb(Customer customerToSave) {

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
