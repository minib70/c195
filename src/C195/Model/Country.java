package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Country {
    private final IntegerProperty countryID;
    private final StringProperty country;

    public Country() {
        this.countryID = new SimpleIntegerProperty();
        this.country = new SimpleStringProperty();
    }

    public int getCountryID() {
        return countryID.get();
    }

    public IntegerProperty countryIDProperty() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID.set(countryID);
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
