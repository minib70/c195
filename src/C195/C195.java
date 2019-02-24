package C195;

import C195.Model.Appointment;
import C195.Model.User;
import C195.Util.DB;
import C195.View_Controller.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class C195 extends Application {
    public Stage primaryStage;
    public VBox rootLayout;
    public RootLayoutController rootLayoutController;
    public ResourceBundle rb;
    public static Connection dbConnection;
    public User currentUser;
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
    public static BufferedWriter bufferedWriter;
    private static String logFile = "logging.txt";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize user object
        this.currentUser = new User();

        this.primaryStage = primaryStage;
        // Set application icon
        this.primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("View_Controller/appicon.png")));
        this.primaryStage.setTitle("C195 - Appointment Management");
        // This sets the local for the project
        //Locale.setDefault(new Locale("nl", "NL"));
        Locale.setDefault(new Locale("en", "US"));
        // Optionally set the timezone.

        rb = ResourceBundle.getBundle("lang", Locale.getDefault());
        rootLayout = new VBox();

        initRootLayout();
        showLoginScreen();
    }

    public static void main(String[] args) {
        DB.connectDB();
        dbConnection = DB.getDbConnection();
        setupLogging();
        launch(args);
        // Disconnect DB
        DB.disconnect();

        // close BufferedWriter
        try {
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRootLayout() throws IOException {
        // Load root layout fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/RootLayout.fxml"));
        rootLayoutController = new RootLayoutController(this);
        loader.setController(rootLayoutController);
        rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        rootLayoutController.hideViewMenu();
    }

    public void showLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Login.fxml"));
        LoginController controller = new LoginController(this);
        loader.setController(controller);

        AnchorPane login = loader.load();
        primaryStage.setHeight(login.getPrefHeight());
        primaryStage.setWidth(login.getPrefWidth());
        // For logging out this clears the layout and re-adds only the login screen.
        if(rootLayout.getChildren().size() > 1) {
            rootLayout.getChildren().remove(1);
        }
        rootLayout.getChildren().add(login);
    }

    @SuppressWarnings("Duplicates")
    public void showAppointmentsScreen() throws  IOException {
        // Instantiate the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Appointments.fxml"));
        AppointmentsController controller = new AppointmentsController(this);
        loader.setController(controller);

        AnchorPane appointments = loader.load();
        // Removes just the login screen from root
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(appointments);
        primaryStage.setHeight(appointments.getPrefHeight() + 35);
        primaryStage.setWidth(appointments.getPrefWidth() + 35);
        rootLayoutController.showViewMenu();
    }

    /**
     * @throws IOException When fxml doesn't load
     */
    @SuppressWarnings("Duplicates")
    public void showCustomersScreen() throws IOException {
        // Instantiate the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Customers.fxml"));
        CustomersController controller = new CustomersController(this);
        loader.setController(controller);

        AnchorPane customers = loader.load();
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(customers);
        rootLayoutController.showViewMenu();
    }

    @SuppressWarnings("Duplicates")
    public void showAppointmentAddScreen(Appointment appointmentToUpdate, ObservableList<Appointment> existingAppointments) throws IOException {
        // Instantiate the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_controller/AppointmentAdd.fxml"));
        AppointmentAddController controller = new AppointmentAddController(this, appointmentToUpdate, existingAppointments);
        loader.setController(controller);

        AnchorPane appointment = loader.load();
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(appointment);
        rootLayoutController.showViewMenu();
    }

    @SuppressWarnings("Duplicates")
    public void showReportsScreen() throws IOException {
        // Instantiate the controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Reports.fxml"));
        ReportsController controller = new ReportsController(this);
        loader.setController(controller);

        AnchorPane reports = loader.load();
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(reports);
    }

    private static void setupLogging() {
        bufferedWriter = null;
        try {
            File file = new File(logFile);
            // create file if it doesn't exist
            if(!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
