package C195;

import C195.View_Controller.LoginController;
import C195.View_Controller.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class C195 extends Application {
    public Stage primaryStage;
    public VBox rootLayout;
    public RootLayoutController rootLayoutController;
    public ResourceBundle rb;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("C195 - Appointment Management");
        // This sets the local for the project
        Locale.setDefault(new Locale("nl", "NL"));
        //Locale.setDefault(new Locale("en", "US"));
        rb = ResourceBundle.getBundle("lang", Locale.getDefault());
        rootLayout = new VBox();

        initRootLayout();
        showLoginScreen(rootLayout, primaryStage);
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
