/*
 *  @author albua
 *  created on 20/05/2021
 */
package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Employee;
import model.Task;
import networking.Proxy;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EmployeeController {
    Stage primaryStage;

    @FXML
    Spinner<Integer> checkInTime;

    @FXML
    Button checkInButton;

    @FXML
    TableView<Task> tasksTableView;

    @FXML
    TableColumn<Task, String> descriptionTableColumn;

    @FXML
    TableColumn<Task, String> statusTableColumn;

    @FXML
    Button finishButton;

    @FXML
    Button logoutButton;

    private Proxy proxy;
    public Employee employee;

    private ObservableList<Task> modelTasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("status"));

        tasksTableView.setItems(modelTasks);
    }

    public void set(Proxy proxy, Employee employee, Stage primaryStage) throws IOException, InterruptedException {
        this.proxy = proxy;
        this.employee = employee;
        this.primaryStage = primaryStage;
        tasksTableView.setItems(modelTasks);
    }

    @FXML
    public void handleCheckIn() throws IOException, InterruptedException {
        String authTime = checkInTime.getValue().toString();
        try {
            LocalTime.parse(authTime);
            this.employee = proxy.angajatPrezent(employee.getEmployeeUsername(), authTime);
            proxy.notifyServer(employee.getEmployeeUsername());
            modelTasks.setAll(StreamSupport.stream(proxy.findAllMyTasks(employee).spliterator(), false).collect(Collectors.toList()));
        }
        catch (Exception exception) {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Time parse failed");
            message.setContentText("Wrong time format");
            message.showAndWait();
        }
    }
    @FXML
    public void handleFinish() throws IOException, InterruptedException {
        Task task = (Task) tasksTableView.getSelectionModel().getSelectedItem();
        if (task != null) {
            proxy.finalizeTask(task);
            proxy.notifyManagerTaskCompleted(task);
            modelTasks.setAll(StreamSupport.stream(proxy.findAllMyTasks(employee).spliterator(), false).collect(Collectors.toList()));
        }
        else {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Completion failed");
            message.setContentText("No task was selected");
            message.showAndWait();
        }
    }

    @FXML
    public void handleLogout() throws IOException, InterruptedException {
        primaryStage.close();
        this.employee = proxy.logoutEmployee(employee.getEmployeeUsername());
        proxy.notifyServer(employee.getEmployeeUsername());
        proxy.removeObserver();
    }

    public void update(List<Task> tasks) {
        modelTasks.setAll(StreamSupport.stream(tasks.spliterator(), false).collect(Collectors.toList()));
    }
}
