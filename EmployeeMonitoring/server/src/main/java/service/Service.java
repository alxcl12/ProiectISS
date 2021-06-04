/*
 *  @author albua
 *  created on 20/05/2021
 */
package service;

import model.Employee;
import model.Manager;
import model.Task;
import repository.EmployeeRepository;
import repository.ManagerRepository;
import repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class Service {
    public EmployeeRepository employeeRepository;
    private ManagerRepository managerRepository;
    public TaskRepository taskRepository;

    public Service(EmployeeRepository employeeRepository, ManagerRepository managerRepository, TaskRepository taskRepository) {
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
        this.taskRepository = taskRepository;
    }

    public Manager loginManager(Manager manager) {
        Manager result = managerRepository.findOne(manager.getManagerUsername());
        if (result != null && result.getPassword().equals(manager.getPassword()))
            return result;
        else return null;
    }

    public Employee loginEmployee(Employee employee) {
        Employee result = employeeRepository.findOne(employee.getEmployeeUsername());
        if (result != null && result.getPassword().equals(employee.getPassword()))
            return result;
        return null;
    }

    public Employee logoutEmployee(String employeeUsername) {
        return employeeRepository.updateStatus(employeeUsername, "logged_out", null);
    }

    public List<Employee> findAllMyEmployees(Manager manager) {
        List<Employee> employees;
        employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            if (!employee.getManager().getManagerUsername().equals(manager.getManagerUsername()))
                employees.remove(employee);
        }
        return employees;
    }

    public Employee checkInEmployee(String employeeUsername, String oraAutentificare) {
        return employeeRepository.updateStatus(employeeUsername, "logged_in", oraAutentificare);
    }

    public List<Employee> findAllMyLoggedInEmployees(Manager manager) {
        List<Employee> employees = findAllMyEmployees(manager);
        List<Employee> loggedInEmployees = new ArrayList<>();
        for (Employee employee : employees)
            if (employee.getStatus() != null && employee.getStatus().equals("logged_in"))
                loggedInEmployees.add(employee);
        return loggedInEmployees;
    }

    public List<Task> findAllMyTasks(Employee employee) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : taskRepository.findAll()) {
            if (task.getEmployee().getEmployeeUsername().equals(employee.getEmployeeUsername()))
                tasks.add(task);
        }
        return tasks;
    }

    public List<Task> findAllMyTasks(Manager manager) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : taskRepository.findAll()) {
            if (task.getManager().getManagerUsername().equals(manager.getManagerUsername()))
                tasks.add(task);
        }
        return tasks;
    }
}
