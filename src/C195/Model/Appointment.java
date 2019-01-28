package C195.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {
    private final IntegerProperty appointmentID;
    private final StringProperty title;
    private final StringProperty description;

    public Appointment() {
        this.appointmentID = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
    }

    //TODO: Setters and getters
}
