/*
 *  @author albua
 *  created on 20/05/2021
 */
package networking;

import gui.EmployeeController;
import gui.ManagerController;
import model.Employee;
import model.Manager;
import model.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Proxy {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public ManagerController managerController = null;
    public EmployeeController employeeController = null;

    public Proxy(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        Thread thread = new Thread(new Reader());
        thread.start();
    }

    public void setManagerController(ManagerController managerController) {
        this.managerController = managerController;
    }

    public void setEmployeeController(EmployeeController employeeController) {
        this.employeeController = employeeController;
    }

    public Manager loginManager(String username, String password) throws IOException, InterruptedException {
        Request request = new Request("loginManager", new Manager(username, password));
        outputStream.writeObject(request);
        outputStream.flush();
        return (Manager) ((Response) queue.take()).data;
    }

    public Employee loginEmployee(String username, String password) throws IOException, InterruptedException {
        Request request = new Request("loginEmployee", new Employee(username, password));
        outputStream.writeObject(request);
        outputStream.flush();
        return (Employee) ((Response) queue.take()).data;
    }

    public void logoutManager() throws IOException {
        Request request = new Request("logoutManager", null);
        outputStream.writeObject(request);
        outputStream.flush();
    }

    public Employee logoutEmployee(String employeeUsername) throws IOException, InterruptedException {
        Request request = new Request("logoutEmployee", employeeUsername);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Employee) ((Response) queue.take()).data;
    }

    public void removeObserver() throws IOException {
        Request request = new Request("removeObserver", null);
        outputStream.writeObject(request);
        outputStream.flush();
    }

    public List<Employee> findAllMyEmployees(Manager manager) throws IOException, InterruptedException {
        Request request = new Request("findAllMyEmployees", manager);
        outputStream.writeObject(request);
        outputStream.flush();
        return (List<Employee>) ((Response) queue.take()).data;
    }

    public List<Employee> findAllMyLoggedInEmployees(Manager manager) throws IOException, InterruptedException {
        Request request = new Request("findAllMyLoggedInEmployees", manager);
        outputStream.writeObject(request);
        outputStream.flush();
        return (List<Employee>) ((Response) queue.take()).data;
    }

    public Employee angajatPrezent(String employeeUsername, String time) throws IOException, InterruptedException {
        List<Object> objects = new ArrayList<>();
        objects.add(employeeUsername); objects.add(time);
        Request request = new Request("checkIn", objects);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Employee) ((Response) queue.take()).data;
    }

    public Employee addEmployee(Employee employee) throws IOException, InterruptedException {
        Request request = new Request("addEmployee", employee);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Employee) ((Response) queue.take()).data;
    }

    public Employee updateEmployee(Employee employee) throws IOException, InterruptedException {
        Request request = new Request("updateEmployee", employee);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Employee) ((Response) queue.take()).data;
    }

    public Employee deleteEmployee(String username) throws IOException, InterruptedException {
        Request request = new Request("deleteEmployee", username);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Employee) ((Response) queue.take()).data;
    }

    public Task addTask(Task task) throws IOException, InterruptedException {
        Request request = new Request("addTask", task);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Task) ((Response) queue.take()).data;
    }

    public List<Task> findAllMyTasks(Manager manager) throws IOException, InterruptedException {
        Request request = new Request("findAllMyTasks", manager);
        outputStream.writeObject(request);
        outputStream.flush();
        return (List<Task>) ((Response) queue.take()).data;
    }

    public List<Task> findAllMyTasks(Employee employee) throws IOException, InterruptedException {
        Request request = new Request("findAllMyTasksEmployee", employee);
        outputStream.writeObject(request);
        outputStream.flush();
        return (List<Task>) ((Response) queue.take()).data;
    }

    public Task finalizeTask(Task task) throws IOException, InterruptedException {
        Request request = new Request("finalizeTask", task);
        outputStream.writeObject(request);
        outputStream.flush();
        return (Task) ((Response) queue.take()).data;
    }

    public void notifyServer(String employeeUsername) throws IOException {
        Request request = new Request("notifyServer", employeeUsername);
        outputStream.writeObject(request);
        outputStream.flush();
    }

    public void notifyEmployeeNewTask(Task task) throws IOException {
        Request request = new Request("notifyEmployeeNewTask", task);
        outputStream.writeObject(request);
        outputStream.flush();
    }

    public void notifyManagerTaskCompleted(Task task) throws IOException {
        Request request = new Request("notifyManagerTaskCompleted", task);
        outputStream.writeObject(request);
        outputStream.flush();
    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object object = inputStream.readObject();

                    if (object instanceof Response && ((Response) object).type.equals("update")) {
                        List<Object> objects = (List<Object>) ((Response) object).data;
                        Manager manager = (Manager) objects.get(0);
                        List<Employee> employees = (List<Employee>) objects.get(1);
                        List<Task> tasks = (List<Task>) objects.get(2);
                        if (managerController != null && managerController.manager.getManagerUsername().equals(manager.getManagerUsername())) {
                            managerController.update(employees);
                            managerController.updateTasks(tasks);
                        }
                        continue;
                    }

                    if (object instanceof Response && ((Response) object).type.equals("updateEmployeeNewTask")) {
                        List<Object> objects = (List<Object>) ((Response) object).data;
                        Employee employee = (Employee) objects.get(0);
                        List<Task> tasks = (List<Task>) objects.get(1);
                        if (employeeController != null && employeeController.employee.getEmployeeUsername().equals(employee.getEmployeeUsername()))
                            employeeController.update(tasks);
                        continue;
                    }

                    queue.put(object);
                } catch (InterruptedException | ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
