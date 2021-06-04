/*
 *  @author albua
 *  created on 20/05/2021
 */
package repository;

import model.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class EmployeeRepository {
    private SessionFactory sessionFactory;

    public EmployeeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Employee save(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(employee);
                transaction.commit();
                return null;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return employee;
    }

    public Employee findOne(String employeeUsername) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Employee employee = session.createQuery("FROM Employee WHERE employeeUsername = :employeeUsername", Employee.class)
                        .setString("employeeUsername", employeeUsername)
                        .getSingleResult();
                transaction.commit();
                return employee;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }

    public List<Employee> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Employee> employees = session.createQuery("FROM Employee", Employee.class).list();
                transaction.commit();
                return employees;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }

    public Employee update(Employee updatedEmployee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Employee employee = session.get(Employee.class, updatedEmployee.getEmployeeUsername());
                employee.setName(updatedEmployee.getName());
                employee.setPassword(updatedEmployee.getPassword());
                transaction.commit();
                return employee;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }

    public Employee updateStatus(String employeeUsername, String status, String time) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Employee employee = session.get(Employee.class, employeeUsername);
                employee.setStatus(status);
                employee.setTime(time);
                transaction.commit();
                return employee;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }

    public Employee delete(String employeeUsername) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Employee employee = session.get(Employee.class, employeeUsername);
                session.delete(employee);
                transaction.commit();
                return employee;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }
}