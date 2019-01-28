package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final IntegerProperty customerID;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty city;
    private final StringProperty zip;
    private final StringProperty phone;

    public Customer() {
        this.customerID = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.city = new SimpleStringProperty();
        this.zip = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
    }

    //TODO: Setters and getters
}
