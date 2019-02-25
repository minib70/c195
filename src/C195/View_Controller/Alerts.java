/*
 * Author: Taylor Vories
 * WGU C195 Project
 * Class used to generate alerts to be shared throughout the project.
 */

package C195.View_Controller;

import javafx.scene.control.Alert;

class Alerts {
    /**
     * Pops up an error alert with whatever message is provided.
     * @param message String message to go into the content text of the alert box.
     */
    static void warningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText("There may be an issue.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Pops up a reminder alert with a list of Appointments provided.
     * @param message String of appointments for the reminder.
     */
    static void reminderAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Appointments");
        alert.setHeaderText("Reminder");
        alert.setContentText("The following appointments are coming up within 15 minutes:\n\n" + message);
        alert.showAndWait();
    }
}
