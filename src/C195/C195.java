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

public class C195 extends Application {
    private Stage primaryStage;
    private VBox rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("C195 - Appointment Management");
        rootLayout = new VBox();

        initRootLayout();
        showLoginScreen(rootLayout);
    }

    private void initRootLayout() throws IOException {
        // Load root layout fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/RootLayout.fxml"));
        RootLayoutController controller = new RootLayoutController(this);
        loader.setController(controller);
        rootLayout = loader.load();

        Scene scene = new Scene(rootLayout, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    private void showLoginScreen(VBox rootLayout) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195.class.getResource("View_Controller/Login.fxml"));
        LoginController controller = new LoginController(rootLayout);
        loader.setController(controller);

        AnchorPane login = loader.load();
        rootLayout.getChildren().add(login);
    }
}
