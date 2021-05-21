/*
 *  @author albua
 *  created on 20/05/2021
 */
package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Employee;
import model.Manager;
import networking.Proxy;

import java.io.IOException;

public class LoginController {
    @FXML
    public TextField usernameTextField;

    @FXML
    public PasswordField passwordTextField;

    @FXML
    public Button loginButton;

    private Proxy proxy;
    private Stage primaryStage;

    public void set(Proxy proxy, Stage primaryStage) {
        this.proxy = proxy;
        this.primaryStage = primaryStage;
    }

    @FXML
    public void handleLogin() throws IOException, InterruptedException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        passwordTextField.setText("");

        if (proxy.loginManager(username, password) != null) {
            Manager manager = proxy.loginManager(username, password);

            FXMLLoader managerLoader = new FXMLLoader();
            managerLoader.setLocation(getClass().getResource("/view/managerView.fxml"));

            Parent managerLayout = managerLoader.load();
            ManagerController managerController = managerLoader.getController();

            proxy.setManagerController(managerController);
            managerController.set(proxy, manager, primaryStage);
            Scene managerScene = new Scene(managerLayout);
            primaryStage.setScene(managerScene);
        }
        else if (proxy.loginEmployee(username, password) != null) {
            Employee employee = proxy.loginEmployee(username, password);

            FXMLLoader employeeLoader = new FXMLLoader();
            employeeLoader.setLocation(getClass().getResource("/view/employeeView.fxml"));

            Parent employeeLayout = employeeLoader.load();
            EmployeeController employeeController = employeeLoader.getController();
            proxy.setEmployeeController(employeeController);
            employeeController.set(proxy, employee, primaryStage);

            Scene employeeScene = new Scene(employeeLayout);
            primaryStage.setScene(employeeScene);
        }
        else {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Login failed");
            message.setContentText("Wrong username and/or password");
            message.showAndWait();
        }
    }
}
