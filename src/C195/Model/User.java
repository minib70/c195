/*
 * Author: Taylor Vories
 * WGU C195 Project
 * Class to manage a user of the application.
 */
package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final IntegerProperty userID, active;
    private final StringProperty username, password;

    /**
     * Constructor
     * @param username Name of the user
     * @param password Password for the user
     */
    public User(String username, String password) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.userID = new SimpleIntegerProperty();
        this.active = new SimpleIntegerProperty();
    }

    public User() {
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.userID = new SimpleIntegerProperty();
        this.active = new SimpleIntegerProperty();
    }

    public int getUserID() {
        return userID.get();
    }

    public IntegerProperty userIDProperty() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID.set(userID);
    }

    public int getActive() {
        return active.get();
    }

    public IntegerProperty activeProperty() {
        return active;
    }

    public void setActive(int active) {
        this.active.set(active);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
}
