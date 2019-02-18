package C195.View_Controller;

import C195.C195;
import C195.Model.Appointment;
import C195.Model.Customer;
import C195.Model.DBMethods;
import C195.Model.Validation;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentAddController implements Initializable {
    private C195 main;
    private ObservableList<Customer> customers;
    @FXML private TableView<Customer> tableViewCustomers;
    @FXML private TableColumn<Customer, String> tableColumnCustomerName;
    @FXML private ComboBox<String> comboBoxStartTime, comboBoxEndTime, comboBoxType;
    @FXML private TextField textFieldAppointmentTitle;
    private final ObservableList<String> startTimes, endTimes, appointmentTypes;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    @FXML private DatePicker datePickerAppointmentDate;
    @FXML private Label labelCurrentAppointment;
    private Appointment appointmentToUpdate;
    private boolean isModify;
    private final ZoneId zoneID = ZoneId.systemDefault();


    public AppointmentAddController(C195 main, Appointment appointmentToUpdate) {
        this.main = main;
        this.appointmentToUpdate = appointmentToUpdate;
        customers = FXCollections.observableArrayList();
        startTimes = FXCollections.observableArrayList();
        endTimes = FXCollections.observableArrayList();
        appointmentTypes = FXCollections.observableArrayList();
    }

    @FXML private void buttonCancelClicked() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel modify customer?");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Are you sure?  Your changes will be lost.");
        Optional<ButtonType> optional = alert.showAndWait();
        if(optional.get() == ButtonType.OK) {
            main.showAppointmentsScreen();
        }
    }

    private void populateTimes() {
        // Set up start times
        LocalTime time = LocalTime.of(8, 0);
        do {
            startTimes.add(time.format(timeFormatter));
            endTimes.add(time.format(timeFormatter));
            time = time.plusMinutes(15);
        } while(!time.equals(LocalTime.of(17, 15)));
        comboBoxStartTime.setItems(startTimes);
        comboBoxEndTime.setItems(endTimes);
    }

    @SuppressWarnings("Duplicates")
    private void showCustomerDataTable() {
        FilteredList<Customer> filteredCustomers = new FilteredList<>(customers, p -> true);
        SortedList<Customer> sortedCustomers = new SortedList<>(filteredCustomers);

        // Bind fields to tableview
        sortedCustomers.comparatorProperty().bind(tableViewCustomers.comparatorProperty());
        tableColumnCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableViewCustomers.setItems(sortedCustomers);
        tableViewCustomers.refresh();
        if(tableViewCustomers.getItems().size() > 0) {
            tableViewCustomers.getSelectionModel().clearAndSelect(0);
        }
        //TODO: select current customer from list.
    }

    private void populateAppointmentTypes() {
        String[] types = {
                "New Customer Introduction",
                "Insider Trading Meeting",
                "Team Gossip Session",
                "Blame Assignment"
        };
        appointmentTypes.addAll(types);
        comboBoxType.setItems(appointmentTypes);
    }

    @SuppressWarnings("Duplicates")
    private String validationErrors(Control field, String validations) {
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

    @SuppressWarnings("Duplicates")
    @FXML private void saveButtonClicked() throws IOException {
        String nameValidation = Validation.validateName(textFieldAppointmentTitle.getText());
        String startEndValidation = Validation.validateStartEndTimes(comboBoxStartTime.getSelectionModel().getSelectedItem(), comboBoxEndTime.getSelectionModel().getSelectedItem());

        // Check for errors
        StringBuilder errors = new StringBuilder();
        errors.append(validationErrors(textFieldAppointmentTitle, nameValidation));
        errors.append(validationErrors(comboBoxStartTime, startEndValidation));
        errors.append(validationErrors(comboBoxEndTime, startEndValidation));

        // Alert if errors, if not continue
        if(errors.length() >= 1) {
            Alerts.warningAlert(errors.toString());
        } else { // no errors
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if(isModify) {
                alert.setTitle("Save modified Appointment - " + textFieldAppointmentTitle.getText());
                alert.setContentText("When you save, it will overwrite existing appointment.");
            } else {
                alert.setTitle("Save new Appointment - " + textFieldAppointmentTitle.getText());
                alert.setContentText("Saving new Appointment.  Are you sure?");
            }
            alert.setHeaderText("Are you sure you want to save?");
            Optional<ButtonType> optional = alert.showAndWait();
            if(optional.get() == ButtonType.OK) {
                Appointment appointmentToSave = new Appointment();
                if(isModify) { // Modify appointment
                    appointmentToSave.setAppointmentID(appointmentToUpdate.getAppointmentID());
                }
                // Convert times to LocalDateTime
                LocalDate localDate = datePickerAppointmentDate.getValue();
                LocalTime startLocal = LocalTime.parse(comboBoxStartTime.getSelectionModel().getSelectedItem(), timeFormatter);
                LocalTime endLocal = LocalTime.parse(comboBoxEndTime.getSelectionModel().getSelectedItem(), timeFormatter);
                LocalDateTime startLocalDate = LocalDateTime.of(localDate, startLocal);
                LocalDateTime endLocalDate = LocalDateTime.of(localDate, endLocal);
                // Convert to UTC
                ZonedDateTime startUTC = startLocalDate.atZone(zoneID).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime endUTC = endLocalDate.atZone(zoneID).withZoneSameInstant(ZoneId.of("UTC"));

                appointmentToSave.setTitle(textFieldAppointmentTitle.getText());
                appointmentToSave.setStart(startUTC.toString());
                appointmentToSave.setEnd(endUTC.toString());
                appointmentToSave.setDescription(comboBoxType.getSelectionModel().getSelectedItem());
                DBMethods.saveAppointment(appointmentToSave, main.currentUser.getUsername(), tableViewCustomers.getSelectionModel().getSelectedItem().getCustomerId());
                labelCurrentAppointment.setText(null);
                main.showAppointmentsScreen();
            }
        }
    }

    private void populateAppointmentToModify() {
        textFieldAppointmentTitle.setText(appointmentToUpdate.getTitle());
        comboBoxType.setValue(appointmentToUpdate.getDescription());
        LocalDateTime start = LocalDateTime.parse(appointmentToUpdate.getStart(), dateFormatter);
        LocalDateTime end = LocalDateTime.parse(appointmentToUpdate.getEnd(), dateFormatter);
        comboBoxStartTime.setValue(start.toLocalTime().format(timeFormatter));
        comboBoxEndTime.setValue(end.toLocalTime().format(timeFormatter));
        datePickerAppointmentDate.setValue(LocalDate.parse(appointmentToUpdate.getStart(), dateFormatter));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize
        customers = DBMethods.getCustomers();

        // Listener for clicks on customer table
        tableViewCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                if(isModify) {
                    labelCurrentAppointment.setText("Modifying appointment with " + newSelection.getName() + ".");
                } else {
                    labelCurrentAppointment.setText("New appointment with " + newSelection.getName() + ".");
                }

            }
        });

        // Show customer table
        showCustomerDataTable();
        populateTimes();
        populateAppointmentTypes();

        // If is modify
        if(appointmentToUpdate != null) {
            System.out.println("We are modifying");
            isModify = true;
            populateAppointmentToModify();
            for(int i = 0; i < customers.size(); i++) {
                if(customers.get(i).getCustomerId() == appointmentToUpdate.getCustomerID()) { // we found a match
                    System.out.println("Match found: " + customers.get(i).getName());
                    tableViewCustomers.getSelectionModel().select(customers.get(i));
                }
            }
        } else {
            isModify = false;
        }
    }
}
