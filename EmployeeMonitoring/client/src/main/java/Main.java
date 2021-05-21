/*
 *  @author albua
 *  created on 20/05/2021
 */

import gui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.Proxy;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Proxy proxy = new Proxy("127.0.0.1", 55556);

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/view/loginView.fxml"));
        Parent logonLayout = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.set(proxy, primaryStage);
        Scene loginScene = new Scene(logonLayout);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
