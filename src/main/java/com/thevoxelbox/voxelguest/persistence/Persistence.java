package com.thevoxelbox.voxelguest.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author MikeMatrix
 */
public class Persistence
{
    private static Persistence instance = new Persistence();
    private Configuration configuration = new Configuration();
    private SessionFactory sessionFactory = null;

    private Persistence()
    {
    }

    public static Persistence getInstance()
    {
        return instance;
    }

    public void initialize(String connectionString, String username, String password)
    {
        configuration
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
                .setProperty("hibernate.connection.url", "jdbc:mysql://" + connectionString)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.connection.pool_size", "1");
    }

    public void registerPersistentClass(Class<?> persistentClass)
    {
        configuration.addAnnotatedClass(persistentClass);
    }

    public void rebuildSessionFactory()
    {
        if (sessionFactory == null)
        {
            ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
    }

    public void save(Object object)
    {
        synchronized (sessionFactory)
        {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            session.saveOrUpdate(object);

            session.getTransaction().commit();
            session.close();
        }
    }

    public void saveAll(List<Object> objects)
    {
        synchronized (sessionFactory)
        {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            for (Object object : objects)
            {
                session.saveOrUpdate(object);
            }

            session.getTransaction().commit();
            session.close();
        }
    }

    public Object load(Class<?> clazz, Serializable id)
    {
        synchronized (sessionFactory)
        {
            Session session = sessionFactory.openSession();

            Object result = session.load(clazz, id);

            session.close();

            return result;
        }
    }

    public List<Object> loadAll(Class<?> clazz)
    {
        synchronized (sessionFactory)
        {
            Session session = sessionFactory.openSession();

            final List result = session.createCriteria(clazz).list();

            session.close();

            return result;
        }
    }

    public List<Object> loadAll(Class<?> clazz, Criterion... criterion)
    {
        synchronized (sessionFactory)
        {
            Session session = sessionFactory.openSession();

            final Criteria criteria = session.createCriteria(clazz);
            for (Criterion currentCriterion : criterion)
            {
                criteria.add(currentCriterion);
            }
            final List result = criteria.list();

            session.close();

            return result;
        }
    }

    public void delete(final Object greylistee)
    {
        synchronized (sessionFactory)
        {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            session.delete(greylistee);

            session.getTransaction().commit();
            session.close();
        }
    }
}
