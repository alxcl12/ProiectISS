/*
 *  @author albua
 *  created on 20/05/2021
 */
package repository;

import model.Manager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ManagerRepository {
    private SessionFactory sessionFactory;

    public ManagerRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Manager save(Manager manager) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(manager);
                transaction.commit();
                return null;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return manager;
    }

    public Manager findOne(String managerUsername) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Manager manager = session.createQuery("FROM Manager WHERE managerUsername = :managerUsername", Manager.class).setString("managerUsername", managerUsername).getSingleResult();
                transaction.commit();
                return manager;
            }
            catch (Exception exception) {
                if (transaction != null) transaction.rollback();
            }
        }
        return null;
    }
}
