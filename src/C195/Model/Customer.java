package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final IntegerProperty customerID, addressID;
    private final StringProperty name, address, address2, city, postalCode, phone, country;

    public Customer() {
        this.customerID = new SimpleIntegerProperty();
        this.addressID = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.address2 = new SimpleStringProperty();
        this.city = new SimpleStringProperty();
        this.postalCode = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.country = new SimpleStringProperty();
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getAddress2() {
        return address2.get();
    }

    public StringProperty address2Property() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getCountry() {
        return country.get();
    }

    public StringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }
}
