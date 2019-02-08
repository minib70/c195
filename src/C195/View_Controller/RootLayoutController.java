package C195.View_Controller;

import C195.C195;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;

public class RootLayoutController {
    private C195 main;
    private final int NUM_DUMMY_DATA = 5;

    public RootLayoutController(C195 main) {
        this.main = main;
    }
    @FXML public MenuItem fileCloseMenuItem, menuItemLogout;
    @FXML private Menu menuView, menuLoggedInUser;
    // Handle fileCloseMenuClick
    @FXML public void handleClose(ActionEvent event) {
        System.exit(0);
    }

    public void showViewMenu() {
        menuView.setVisible(true);
    }

    public void hideViewMenu() {
        menuView.setVisible(false);
    }

    /**
     * Shows About Dialog
     */
    @FXML public void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("C195 Project");
        alert.setHeaderText("About");
        alert.setContentText("Author: Taylor Vories\nC195 Java Project");

        // Show alert
        alert.showAndWait();
    }

    public void logoutUser() throws IOException {
        if(main.currentUser != null) {
            // Set current user to Null
            main.currentUser = null;
            main.showLoginScreen();
        }
    }

    @FXML public void loadDummyData() {
        System.out.println("Loading dummy data.");
        try {
            String[] customers = {"John Doe", "Jane Doe", "Sally Sutherton", "Taylor Coolguy", "Jimbo Jones"};
            clearDB();
            PreparedStatement stmt;
            // Address table
            for(int i = 0; i < NUM_DUMMY_DATA; i++) {
                // Address
                stmt = C195.dbConnection.prepareStatement("INSERT INTO `address` (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ('123 Main','',?,CONCAT('1111',?),'555-1212','2019-02-20 16:16:23','test','2019-01-06 16:16:38','test')");
                stmt.setInt(1, i);
                stmt.setInt(2, i);
                stmt.execute();

                // Appointment
                stmt = C195.dbConnection.prepareStatement("INSERT INTO `appointment` (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate,lastUpdateBy) VALUES (?,CONCAT('Appointment Title',?),CONCAT('Appointment Description',?),'location','contact','url','2019-01-10 16:00:00','2019-01-10 17:00:00','2019-01-06 16:23:08','test','2019-01-06 16:27:17', 'test')");
                stmt.setInt(1, i);
                stmt.setInt(2, 1);
                stmt.setInt(3, i);
                stmt.execute();

                // Customer
                if(i <= customers.length) {
                    stmt = C195.dbConnection.prepareStatement("INSERT INTO `customer` (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, 1, ?, 'dummyData', ?, 'dummyData')");
                    stmt.setString(1, customers[i]);
                    stmt.setInt(2, i+1);
                    stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                    stmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                    stmt.execute();
                }
            }
            // Customers
//            stmt = C195.dbConnection.prepareStatement("INSERT INTO `customer` (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ('John Doe',1,1,'2019-01-06 16:19:19','test','2019-01-06 16:19:19','dummyData'),('Jane Doe',2,1,'0000-00-00 00:00:00','test','0000-00-00 00:00:00','dummyData'),('Sally Test',3,1,'0000-00-00 00:00:00','test','0000-00-00 00:00:00','dummyData');");
//            stmt.execute();

            // Users
            // Removing for now since it shouldn't delete the user accounts.
//            stmt = C195.dbConnection.prepareStatement("INSERT INTO `user` (userName, password, active, createBy, createDate, lastUpdate, lastUpdatedBy) VALUES ('test','test',1,'test','2019-01-06 16:00:37','2019-01-06 16:00:37','dummyData');");
//            stmt.execute();
//            stmt = C195.dbConnection.prepareStatement("INSERT INTO `user` (userName, password, active, createBy, createDate, lastUpdate, lastUpdatedBy) VALUES ('taylor','tayloriscool',1,'test','2019-01-06 16:00:37','2019-01-06 16:00:37','dummyData');");
//            stmt.execute();

        } catch (SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    @FXML public void clearDBData() {
        //TODO: can probably combine this with the below method
        System.out.println("Clearing db data.");
        clearDB();
    }

    private static void clearDB() {
        try {
            Statement stmt = C195.dbConnection.createStatement();
            stmt.execute("TRUNCATE address");
            stmt.execute("TRUNCATE appointment");
            stmt.execute("TRUNCATE city");
            stmt.execute("TRUNCATE country");
            stmt.execute("TRUNCATE customer");
            stmt.execute("TRUNCATE incrementtypes");
            stmt.execute("TRUNCATE reminder");
            // Leaving out user so that the program can still log in
            // stmt.execute("TRUNCATE user");

        } catch(SQLException e) {
            System.out.println("Issue with SQL");
            e.printStackTrace();
        }
    }

    @FXML public void setLoggedInUser(String loggedInUser) {
        menuLoggedInUser.setText(main.rb.getString("logged_in_user") + " " + loggedInUser);
        menuItemLogout.setDisable(false);
    }
}
