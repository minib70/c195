package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class City {
    private final IntegerProperty cityID, countryID;
    private final StringProperty city;

    public City() {
        this.cityID = new  SimpleIntegerProperty();
        this.countryID = new SimpleIntegerProperty();
        this.city = new SimpleStringProperty();
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

    public int getCountryID() {
        return countryID.get();
    }

    public IntegerProperty countryIDProperty() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID.set(countryID);
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
}
