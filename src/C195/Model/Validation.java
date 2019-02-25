/*
 * Author: Taylor Vories
 * WGU C195 Project
 * This class provides methods shared across the controllers to validate user input
 */

package C195.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class Validation {
    private static ResourceBundle rb = ResourceBundle.getBundle("lang", Locale.getDefault());

    /**
     * Validates user input for a name type
     * @param name Name to validate
     * @return Returns error if errors found, null if no errors found.
     */
    public static String validateName(String name) {
        StringBuilder errors = new StringBuilder();
        // Ensure name is not null
        if(name == null || name.isEmpty()) {
            errors.append(rb.getString("login_error_noname"));
        }
        return errors.toString();
    }

    /**
     * Validates user input for a password type
     * @param password Password to validate
     * @return Returns error if errors found, null if no errors found.
     */
    public static String validatePassword(String password) {
        StringBuilder errors = new StringBuilder();
        // Ensure name is not null
        if(password == null || password.isEmpty()) {
            errors.append(rb.getString("login_error_nopassword"));
        }
        return errors.toString();
    }

    /**
     * Validates user input for an address type
     * @param address Address string to validate
     * @return Returns error if errors found, null if no errors found.
     */
    public static String validateAddress(String address) {
        StringBuilder errors = new StringBuilder();
        // Ensure address is not null
        if(address == null || address.isEmpty()) {
            errors.append("Address must contain at least 1 character.");
        }
        return errors.toString();
    }

    /**
     * Validates user input for a zipcode type
     * @param zip Zipcode string to validate
     * @return Returns error if errors found, null if no errors found.
     */
    public static String validateZip(String zip) {
        StringBuilder errors = new StringBuilder();
        // Ensure zip is not null
        if( zip == null || zip.isEmpty()) {
            errors.append("Postal Code must contain at least 1 character.");
        }
        return errors.toString();
    }

    /**
     * Validates user input for a phone number type
     * @param phone Phone number string to validate
     * @return Returns error if errors found, null if no errors found.
     */
    public static String validatePhone(String phone) {
        StringBuilder errors = new StringBuilder();
        // Ensure phone is not null
        if(phone == null || phone.isEmpty()) {
            errors.append("Phone number must contain at least 1 character.");
        }
        return errors.toString();
    }

    /**
     * Validates that a start and an end time occur in the correct order (start can't occur after end)
     * Validates that the strings can be formatted into a date.
     * @param startTime String value of the start time of an appointment.
     * @param endTime String value of the end time of an appointment.
     * @return Returns errors if errors found, null if no errors found.
     */
    public static String validateStartEndTimes(String startTime, String endTime) {
        StringBuilder errors = new StringBuilder();
        // Ensure timestamps are valid
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        try {
            LocalTime startLocal = LocalTime.parse(startTime, timeFormatter);
            LocalTime endLocal = LocalTime.parse(endTime, timeFormatter);

            // Ensure start time occurs before end time
            if(startLocal.isAfter(endLocal)) {
                errors.append("Start time occurs AFTER end time, please fix.");
            }
        } catch(Exception e) {
            errors.append("Issue parsing time.  Very broken");
        }
        return errors.toString();
    }

    /**
     * Validates that a datepicker date is not invalid
     * @param datePicker Value taken from a DatePicker to be validated.
     * @return Returns error if errors found, null if no errors found.
     */
    public static String validateDatePicker(LocalDate datePicker) {
        StringBuilder errors = new StringBuilder();
        // Ensure datepicker isn't empty
        if(datePicker == null) {
            errors.append("Date value is empty.");
        }
        return errors.toString();
    }
}
