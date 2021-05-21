/*
 *  @author albua
 *  created on 20/05/2021
 */
package networking;

import model.Employee;
import model.Manager;
import model.Task;
import service.Service;
import utils.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Worker implements Observer, Runnable {
    private Server server;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Service service;

    public Worker(Server server, Service service, Socket socket) throws IOException {
        this.server = server;
        this.service = service;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Request request = (Request) inputStream.readObject();

                if (request.type.equals("loginManager")) {
                    Manager manager = (Manager) request.data;
                    outputStream.writeObject(new Response("loginManager", service.loginManager(manager)));
                    outputStream.flush();
                }

                if (request.type.equals("loginEmployee")) {
                    Employee employee = (Employee) request.data;
                    outputStream.writeObject(new Response("loginEmployee", service.loginEmployee(employee)));
                    outputStream.flush();
                }

                if (request.type.equals("logoutManager")) {
                    server.removeObserver(this);
                    break;
                }

                if (request.type.equals("logoutEmployee")) {
                    String employeeUsername = (String) request.data;
                    outputStream.writeObject(new Response("logoutEmployee", service.logoutEmployee(employeeUsername)));
                    outputStream.flush();
                }

                if (request.type.equals("removeObserver")) {
                    server.removeObserver(this);
                    break;
                }

                if (request.type.equals("findAllMyEmployees")) {
                    Manager manager = (Manager) request.data;
                    outputStream.writeObject(new Response("findAllMyEmployees", service.findAllMyEmployees(manager)));
                    outputStream.flush();
                }

                if (request.type.equals("findAllMyLoggedInEmployees")) {
                    Manager manager = (Manager) request.data;
                    outputStream.writeObject(new Response("findAllMyLoggedInEmployees", service.findAllMyLoggedInEmployees(manager)));
                }

                if (request.type.equals("checkInEmployee")) {
                    List<Object> objects = (List<Object>) request.data;
                    String employeeUsername = (String) objects.get(0);
                    String checkInTime = (String) objects.get(1);
                    outputStream.writeObject(new Response("angajatPrezent", service.checkInEmployee(employeeUsername, checkInTime)));
                    outputStream.flush();
                }

                if (request.type.equals("addEmployee")) {
                    Employee employee = (Employee) request.data;
                    outputStream.writeObject(new Response("addEmployee", service.employeeRepository.save(employee)));
                    outputStream.flush();
                }

                if (request.type.equals("updateEmployee")) {
                    Employee employee = (Employee) request.data;
                    outputStream.writeObject(new Response("updateEmployee", service.employeeRepository.update(employee)));
                    outputStream.flush();
                }

                if (request.type.equals("deleteEmployee")) {
                    String username = (String) request.data;
                    outputStream.writeObject(new Response("deleteEmployee", service.employeeRepository.delete(username)));
                    outputStream.flush();
                }

                if (request.type.equals("addTask")) {
                    Task task = (Task) request.data;
                    outputStream.writeObject(new Response("addTask", service.taskRepository.save(task)));
                    outputStream.flush();
                }

                if (request.type.equals("findAllMyTasks")) {
                    Manager manager = (Manager) request.data;
                    outputStream.writeObject(new Response("findAllMyTasks", service.findAllMyTasks(manager)));
                    outputStream.flush();
                }

                if (request.type.equals("findAllMyTasksEmployee")) {
                    Employee employee = (Employee) request.data;
                    outputStream.writeObject(new Response("findAllMyTasksEmployee", service.findAllMyTasks(employee)));
                    outputStream.flush();
                }

                if (request.type.equals("finalizeTask")) {
                    Task task = (Task) request.data;
                    outputStream.writeObject(new Response("finalizeTask", service.taskRepository.update(task)));
                    outputStream.flush();
                }

                if (request.type.equals("notifyServer")) {
                    Manager manager = service.employeeRepository.findOne((String) request.data).getManager();
                    server.notifyObservers(manager);
                }

                if (request.type.equals("notifyEmployeeNewTask")) {
                    Employee employee = ((Task) request.data).getEmployee();
                    server.notifyEmployeeNewTask(employee);
                }

                if (request.type.equals("notifyManagerTaskCompleted")) {
                    Manager manager = ((Task) request.data).getManager();
                    server.notifyObservers(manager);
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Manager manager) {
        List<Object> objects = new ArrayList<>();
        objects.add(manager);
        objects.add(service.findAllMyLoggedInEmployees(manager));
        objects.add(service.findAllMyTasks(manager));
        Response response = new Response("update", objects);
        try {
            outputStream.writeObject(response);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployeeNewTask(Employee employee) {
        List<Object> objects = new ArrayList<>();
        objects.add(employee);
        objects.add(service.findAllMyTasks(employee));
        Response response = new Response("updateEmployeeNewTask", objects);
        try {
            outputStream.writeObject(response);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
