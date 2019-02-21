package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private final IntegerProperty appointmentID, customerID;
    private final StringProperty title, description, location, contact, start, end, customerName, localStart, localEnd;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

    public Appointment() {
        this.appointmentID = new SimpleIntegerProperty();
        this.customerID = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.contact = new SimpleStringProperty();
        this.start = new SimpleStringProperty();
        this.end = new SimpleStringProperty();
        this.customerName = new SimpleStringProperty();
        this.localStart = new SimpleStringProperty();
        this.localEnd = new SimpleStringProperty();
    }

    public int getAppointmentID() {
        return appointmentID.get();
    }

    public IntegerProperty appointmentIDProperty() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID.set(appointmentID);
    }

    public int getCustomerID() {
        return customerID.get();
    }

    public IntegerProperty customerIDProperty() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getContact() {
        return contact.get();
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public String getStart() {
        return start.get();
    }

    public StringProperty startProperty() {
        return start;
    }

    public void setStart(String start) {
        this.start.set(start);
        Instant localInstant = Instant.parse(this.start.get());
        ZonedDateTime localZone = localInstant.atZone(ZoneId.systemDefault());
        this.localStart.set(dtf.format(localZone));
    }

    public String getEnd() {
        return end.get();
    }

    public StringProperty endProperty() {
        return end;
    }

    public void setEnd(String end) {
        this.end.set(end);
        Instant localInstant = Instant.parse(this.end.get());
        ZonedDateTime localZone = localInstant.atZone(ZoneId.systemDefault());
        this.localEnd.set(dtf.format(localZone));
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public void setLocalStart(String localStart) {
        this.localStart.set(localStart);
        Instant localInstant = Instant.parse(localStart);
        ZonedDateTime startUTC = localInstant.atZone(ZoneId.of("UTC"));
        Instant startInstantUTC = startUTC.toInstant();
        this.start.set(startInstantUTC.toString());
    }

    public String getLocalStart() {
        return localStart.get();
    }

    public StringProperty localStartProperty() {
        return localStart;
    }

    public String getLocalEnd() {
        return localEnd.get();
    }

    public void setLocalEnd(String localEnd) {
        this.localEnd.set(localEnd);
        Instant localInstant = Instant.parse(localEnd);
        ZonedDateTime endUTC = localInstant.atZone(ZoneId.of("UTC"));
        Instant endInstantUTC = endUTC.toInstant();
        this.end.set(endInstantUTC.toString());
    }

    public StringProperty localEndProperty() {
        return localEnd;
    }
}
