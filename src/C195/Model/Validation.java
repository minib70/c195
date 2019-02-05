/*
 * Author: Taylor Vories
 * WGU C195 Project
 * This class provides methods shared across the controllers to validate user input
 */

package C195.Model;

public class Validation {
    public static String validateUsername(String name) {
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
}
