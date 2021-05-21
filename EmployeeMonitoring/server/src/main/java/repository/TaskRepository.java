/*
 *  @author albua
 *  created on 20/05/2021
 */
package repository;

import model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TaskRepository {
    private SessionFactory sessionFactory;

    public TaskRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Task save(Task task) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                int id;
                try { id = session.createQuery("SELECT MAX(taskId) FROM Task", Integer.class).getSingleResult() + 1; }
                catch (Exception exception) { id = 1; }
                task.setTaskId(id);
                session.save(task);
                transaction.commit();
                return null;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return task;
    }

    public Task findOne(int taskId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Task task = session.createQuery("FROM Task WHERE taskId = :taskId", Task.class).setInteger("taskId", taskId).getSingleResult();
                transaction.commit();
                return task;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }

    public List<Task> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Task> tasks = session.createQuery("FROM Task", Task.class).list();
                transaction.commit();
                return tasks;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }

    public Task update(Task task) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Task task1 = session.get(Task.class, task.getTaskId());
                task1.setStatus("completed");
                transaction.commit();
                return task1;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }
}
