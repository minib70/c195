/*
 * Author: Taylor Vories
 * WGU C195 Project
 * This class provides methods shared across the controllers to validate user input
 */

package C195.Model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Validation {
    public static String validateName(String name) {
        StringBuilder errors = new StringBuilder();
        // Ensure name is not null
        if(name == null || name.isEmpty()) {
            errors.append("Name must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validatePassword(String password) {
        StringBuilder errors = new StringBuilder();
        // Ensure name is not null
        if(password == null || password.isEmpty()) {
            errors.append("Password must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validateAddress(String address) {
        StringBuilder errors = new StringBuilder();
        // Ensure address is not null
        if(address == null || address.isEmpty()) {
            errors.append("Address must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validateZip(String zip) {
        StringBuilder errors = new StringBuilder();
        // Ensure zip is not null
        if( zip == null || zip.isEmpty()) {
            errors.append("Postal Code must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validatePhone(String phone) {
        StringBuilder errors = new StringBuilder();
        // Ensure phone is not null
        if(phone == null || phone.isEmpty()) {
            errors.append("Phone number must contain at least 1 character.");
        }
        return errors.toString();
    }

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
}
