package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final IntegerProperty customerID, addressID;
    private final StringProperty customerName;

    public Customer() {
        this.customerID = new SimpleIntegerProperty();
        this.addressID = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
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

    public int getAddressID() {
        return addressID.get();
    }

    public IntegerProperty addressIDProperty() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID.set(addressID);
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
}
