/*
 * Author: Taylor Vories
 * WGU C195 Project
 * This class provides methods shared across the controllers to validate user input
 */

package C195.Model;

public class Validation {
    public static String validateName(String name) {
        StringBuilder errors = new StringBuilder();
        // Ensure name is not null
        if(name.isEmpty()) {
            errors.append("Name must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validatePassword(String password) {
        StringBuilder errors = new StringBuilder();
        // Ensure name is not null
        if(password.isEmpty()) {
            errors.append("Password must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validateAddress(String address) {
        StringBuilder errors = new StringBuilder();
        // Ensure address is not null
        if(address.isEmpty()) {
            errors.append("Address must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validateZip(String zip) {
        StringBuilder errors = new StringBuilder();
        // Ensure zip is not null
        if(zip.isEmpty()) {
            errors.append("Postal Code must contain at least 1 character.");
        }
        return errors.toString();
    }

    public static String validatePhone(String phone) {
        StringBuilder errors = new StringBuilder();
        // Ensure phone is not null
        if(phone.isEmpty()) {
            errors.append("Phone number must contain at least 1 character.");
        }
        return errors.toString();
    }
}
