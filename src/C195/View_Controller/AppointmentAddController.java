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
    private final DateTimeFormatter dtfComboBox = DateTimeFormatter.ofPattern("h:mm a");
    private final DateTimeFormatter dtfAppt = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    @FXML private DatePicker datePickerAppointmentDate;
    @FXML private Label labelCurrentAppointment;
    private Appointment appointmentToUpdate;
    private ObservableList<Appointment> existingAppointments;
    private boolean isModify;
    private final ZoneId zoneID = ZoneId.systemDefault();


    public AppointmentAddController(C195 main, Appointment appointmentToUpdate, ObservableList<Appointment> existingAppointments) {
        this.main = main;
        this.appointmentToUpdate = appointmentToUpdate;
        this.existingAppointments = existingAppointments;
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
        String datePickerValidation = Validation.validateDatePicker(datePickerAppointmentDate.getValue());


        // Check for errors
        StringBuilder errors = new StringBuilder();
        errors.append(validationErrors(textFieldAppointmentTitle, nameValidation));
        errors.append(validationErrors(comboBoxStartTime, startEndValidation));
        errors.append(validationErrors(comboBoxEndTime, startEndValidation));
        errors.append(validationErrors(datePickerAppointmentDate, datePickerValidation));

        // Alert if errors, if not continue
        if(errors.length() >= 1) {
            Alerts.warningAlert(errors.toString());
        } else { // Check appointment times
            // Check for overlapping appointments
            boolean isOverlap = false;
            for(Appointment a: existingAppointments) {
                LocalDate date = datePickerAppointmentDate.getValue();
                LocalDateTime nStart = combineDateAndTime(comboBoxStartTime.getSelectionModel().getSelectedItem(), date);
                LocalDateTime nEnd = combineDateAndTime(comboBoxEndTime.getSelectionModel().getSelectedItem(), date);
                LocalDateTime eStart = LocalDateTime.parse(a.getLocalStart(), C195.dtf);
                LocalDateTime eEnd = LocalDateTime.parse(a.getLocalEnd(), C195.dtf);

                // Check for overlaps
                // if New Appointment start or end overlaps any existing appointment
                if(nStart.isAfter(eStart) && nStart.isBefore(eEnd) || nEnd.isAfter(eStart) && nEnd.isBefore(eEnd)) {
                    isOverlap = true;
                    break;
                }
            }
            if(isOverlap) {
                errors.append("New appointment overlaps one or more existing appointments.  Cannot overlap.");
                Alerts.warningAlert(errors.toString());
            } else {
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
                    LocalDateTime startLocalDate = combineDateAndTime(comboBoxStartTime.getSelectionModel().getSelectedItem(), localDate);
                    Instant startLocalInstant = Instant.from(startLocalDate.atZone(zoneID));
                    LocalDateTime endLocalDate = combineDateAndTime(comboBoxEndTime.getSelectionModel().getSelectedItem(), localDate);
                    Instant endLocalInstant = Instant.from(endLocalDate.atZone(zoneID));

                    // Create new appointment
                    appointmentToSave.setTitle(textFieldAppointmentTitle.getText());
                    appointmentToSave.setLocalStart(startLocalInstant.toString());
                    appointmentToSave.setLocalEnd(endLocalInstant.toString());
                    appointmentToSave.setDescription(comboBoxType.getSelectionModel().getSelectedItem());
                    DBMethods.saveAppointment(appointmentToSave, main.currentUser.getUsername(), tableViewCustomers.getSelectionModel().getSelectedItem().getCustomerId());
                    labelCurrentAppointment.setText(null);
                    main.showAppointmentsScreen();
                }
            }
        }
    }

    private LocalDateTime combineDateAndTime(String time, LocalDate date) {
        LocalTime lTime = LocalTime.parse(time, timeFormatter);
        return LocalDateTime.of(date, lTime);
    }

    private void populateAppointmentToModify() {
        textFieldAppointmentTitle.setText(appointmentToUpdate.getTitle());
        comboBoxType.setValue(appointmentToUpdate.getDescription());
        LocalDateTime start = LocalDateTime.parse(appointmentToUpdate.getLocalStart(), dtfAppt);
        LocalDateTime end = LocalDateTime.parse(appointmentToUpdate.getLocalEnd(), dtfAppt);
        comboBoxStartTime.setValue(dtfComboBox.format(start));
        comboBoxEndTime.setValue(dtfComboBox.format(end));
        datePickerAppointmentDate.setValue(start.toLocalDate());
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
