package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class City {
    private final IntegerProperty cityID;
    private final StringProperty city, country;

    public City() {
        this.cityID = new  SimpleIntegerProperty();
        this.city = new SimpleStringProperty();
        this.country = new SimpleStringProperty();
    }

    public int getCityID() {
        return cityID.get();
    }

    public IntegerProperty cityIDProperty() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID.set(cityID);
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
