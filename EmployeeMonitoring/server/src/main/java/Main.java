/*
 *  @author albua
 *  created on 20/05/2021
 */

import networking.Server;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import repository.EmployeeRepository;
import repository.ManagerRepository;
import repository.TaskRepository;
import service.Service;

import java.io.IOException;

public class Main {
    static SessionFactory sessionFactory;

    static void initialize() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }
        catch (Exception exception) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static void main(String[] args) throws IOException {
        initialize();

        EmployeeRepository employeeRepository = new EmployeeRepository(sessionFactory);
        ManagerRepository managerRepository = new ManagerRepository(sessionFactory);
        TaskRepository taskRepository = new TaskRepository(sessionFactory);
        Service service = new Service(employeeRepository, managerRepository, taskRepository);

        Server server = new Server(55556, service);
        server.start();
    }
}
