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
import model.Manager;
import model.Task;
import networking.Proxy;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManagerController {
    Stage primaryStage;

    @FXML
    public TableView<Employee> loggedInEmployeesTableView;

    @FXML
    public TableView<Task> sentTasksTableView;

    @FXML
    public TableView<Employee> employeesTableView;

    @FXML
    public TableColumn<Employee, String> employeeNameTableColumn;

    @FXML
    public TableColumn<Employee, String> loginTimeTableColumn;

    @FXML
    public TableColumn<Task, String> employeeTableColumn;

    @FXML
    public TableColumn<Task, String> descriptionTableColumn;

    @FXML
    public TableColumn<Task, String> statusTableColumn;

    @FXML
    public TableColumn<Employee, String> nameTableColumn;

    @FXML
    public TableColumn<Employee, String> usernameTableColumn;

    @FXML
    public TextField nameTextField;

    @FXML
    public TextField usernameTextField;

    @FXML
    public TextField passwordTextField;

    @FXML
    public TextField descriptionTextField;

    @FXML
    public Button addEmployeeButton;

    @FXML
    public Button updateEmployeeButton;

    @FXML
    public Button deleteEmployeeButton;

    @FXML
    public Button sendButton;

    @FXML
    public Button logoutButton;

    private ObservableList<Employee> modelEmployees = FXCollections.observableArrayList();
    private ObservableList<Employee> modelLoggedInEmployees = FXCollections.observableArrayList();
    private ObservableList<Task> modelTasks = FXCollections.observableArrayList();
    private Proxy proxy;
    public Manager manager;

    @FXML
    public void initialize() {
        employeeNameTableColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
        loginTimeTableColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("time"));

        nameTableColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
        usernameTableColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("employeeUsername"));

        employeeTableColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("employeeUsername"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("status"));

        employeesTableView.setItems(modelEmployees);
        loggedInEmployeesTableView.setItems(modelLoggedInEmployees);

        sentTasksTableView.setItems(modelTasks);
    }

    public void set(Proxy proxy, Manager manager, Stage primaryStage) throws IOException, InterruptedException {
        this.proxy = proxy;
        this.manager = manager;
        this.primaryStage = primaryStage;

        modelTasks.setAll(StreamSupport.stream(proxy.findAllMyTasks(manager).spliterator(), false).collect(Collectors.toList()));

        loggedInEmployeesTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadEmployeeDetails(newValue);
                    }
                });

        employeesTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null)
                        loadEmployeeDetails(newValue);
                });

        modelEmployees.setAll(StreamSupport.stream(proxy.findAllMyEmployees(manager).spliterator(), false).collect(Collectors.toList()));
        modelLoggedInEmployees.setAll(StreamSupport.stream(proxy.findAllMyLoggedInEmployees(manager).spliterator(), false).collect(Collectors.toList()));

    }

    public void update(List<Employee> employees) {
        modelLoggedInEmployees.setAll(StreamSupport.stream(employees.spliterator(), false).collect(Collectors.toList()));
    }

    public void updateTasks(List<Task> tasks) {
        modelTasks.setAll(StreamSupport.stream(tasks.spliterator(), false).collect(Collectors.toList()));
    }

    public void loadEmployeeDetails(Object object) {
        Employee employee = (Employee) object;
        nameTextField.setText(employee.getName());
        usernameTextField.setText(employee.getEmployeeUsername());
        passwordTextField.setText(employee.getPassword());
        usernameTextField.setEditable(false);
    }

    @FXML
    public void clearFields() {
        nameTextField.setText("");
        usernameTextField.setText("");
        passwordTextField.setText("");
        usernameTextField.setEditable(true);
    }

    @FXML
    public void handleAddEmployee() throws IOException, InterruptedException {
        if (usernameTextField.getText().equals("")) {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Operation failed");
            message.setContentText("Boxes are empty");
            message.showAndWait();
        }
        else {
            String nume = nameTextField.getText();
            String username = usernameTextField.getText();
            String password  = passwordTextField.getText();

            Employee employee = new Employee(username, password, nume, manager);
            if (proxy.addEmployee(employee) == null) {
                Alert message = new Alert(Alert.AlertType.CONFIRMATION);
                message.setHeaderText("Add successful");
                message.setContentText("The add has been performed successfully");
                message.showAndWait();
            }
            else {
                clearFields();
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setHeaderText("Add failed");
                message.setContentText("The employee already exists");
                message.showAndWait();
            }
            modelEmployees.setAll(StreamSupport.stream(proxy.findAllMyEmployees(manager).spliterator(), false).collect(Collectors.toList()));
        }
    }

    @FXML
    public void handleUpdateEmployee() throws IOException, InterruptedException {
        if (usernameTextField.getText().equals("")) {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Operation failed");
            message.setContentText("Boxes are empty");
            message.showAndWait();
        }
        else {
            String nume = nameTextField.getText();
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();

            Employee employee = new Employee(username, password, nume, manager);
            if (proxy.updateEmployee(employee) != null) {
                Alert message = new Alert(Alert.AlertType.CONFIRMATION);
                message.setHeaderText("Update successful");
                message.setContentText("The update has been performed successfully");
                message.showAndWait();
            } else {
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setHeaderText("Update failed");
                message.setContentText("The update has failed");
                message.showAndWait();
            }
            modelEmployees.clear();
            modelEmployees.setAll(StreamSupport.stream(proxy.findAllMyEmployees(manager).spliterator(), false).collect(Collectors.toList()));
        }
    }

    @FXML
    public void handleDeleteEmployee() throws IOException, InterruptedException {
        if (usernameTextField.getText().equals("")) {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Operation failed");
            message.setContentText("Boxes are empty");
            message.showAndWait();
        }
        else {
            String username = usernameTextField.getText();
            if (proxy.deleteEmployee(username) != null) {
                clearFields();
                Alert message = new Alert(Alert.AlertType.CONFIRMATION);
                message.setHeaderText("Delete successful");
                message.setContentText("The delete has been performed successfully");
                message.showAndWait();
            }
            else {
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setHeaderText("Delete failed");
                message.setContentText("The delete has failed");
                message.showAndWait();
            }
            modelEmployees.setAll(StreamSupport.stream(proxy.findAllMyEmployees(manager).spliterator(), false).collect(Collectors.toList()));
        }
    }

    @FXML
    public void handleSend() throws IOException, InterruptedException {
        if (!usernameTextField.getText().equals("")) {
            Task task = new Task();
            task.setDescription(descriptionTextField.getText());
            task.setStatus("pending");
            task.setManager(manager);
            Employee employee = null;
            for (Employee employee1 : proxy.findAllMyEmployees(manager)) {
                if (employee1.getEmployeeUsername().equals(usernameTextField.getText()))
                    employee = employee1;
            }
            task.setEmployee(employee);
            proxy.addTask(task);
            proxy.notifyEmployeeNewTask(task);
            modelTasks.setAll(StreamSupport.stream(proxy.findAllMyTasks(manager).spliterator(), false).collect(Collectors.toList()));
        }
        else {
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setHeaderText("Sending failed");
            message.setContentText("No employee was selected");
            message.showAndWait();
        }
    }

    @FXML
    public void handleLogout() throws IOException {
        primaryStage.close();
        proxy.logoutManager();
    }
}
