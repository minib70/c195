/*
 * Author: Taylor Vories
 * WGU C195 Project
 * This class encapsulates a couple tables from the Database and helps connect the data
 * together for the application.
 */

package C195.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class City {
    private int cityId, countryId;
    private final StringProperty city, country;

    /**
     * Constructor
     */
    public City() {
        this.city = new SimpleStringProperty();
        this.country = new SimpleStringProperty();
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

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}
