package C195;

import C195.Model.User;
import C195.Util.DB;
import C195.View_Controller.LoginController;
import C195.View_Controller.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;
import java.util.ResourceBundle;

public class C195 extends Application {
    public Stage primaryStage;
    public VBox rootLayout;
    public RootLayoutController rootLayoutController;
    public ResourceBundle rb;
    public static Connection dbConnection;
    public User currentUser;

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
        rb = ResourceBundle.getBundle("lang", Locale.getDefault());
        rootLayout = new VBox();

        initRootLayout();
        showLoginScreen(rootLayout, primaryStage);
    }

    public static void main(String[] args) {
        DB.connectDB();
        dbConnection = DB.getDbConnection();
        launch(args);
        DB.disconnect();
    }

    private void initRootLayout() throws IOException {
        // Load root layout fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/RootLayout.fxml"));
        rootLayoutController = new RootLayoutController(this);
        loader.setController(rootLayoutController);
        rootLayout = loader.load();

        Scene scene = new Scene(rootLayout, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        rootLayoutController.hideViewMenu();
    }

    private void showLoginScreen(VBox rootLayout, Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Login.fxml"));
        LoginController controller = new LoginController(this);
        loader.setController(controller);

        AnchorPane login = loader.load();
        rootLayout.getChildren().add(login);
    }
}
