package com.thevoxelbox.voxelguest.persistence;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * @author MikeMatrix
 */
public class Persistence
{
    private static Persistence instance = new Persistence();
    private Configuration configuration = new Configuration();
    private SessionFactory sessionFactory;

    private Persistence()
    {
    }

    public static Persistence getInstance()
    {
        return instance;
    }

    public void initialize(final File file)
    {
        file.getParentFile().mkdirs();
        configuration
                .setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect")
                .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                .setProperty("hibernate.connection.url", "jdbc:sqlite:" + file.getPath())
                .setProperty("hibernate.hbm2ddl.auto", "update");
    }

    public void registerPersistentClass(Class<?> persistentClass)
    {
        configuration.addAnnotatedClass(persistentClass);
    }

    public void rebuildSessionFactory()
    {
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public void save(Object object)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.saveOrUpdate(object);

        session.getTransaction().commit();
        session.close();
    }

    public void saveAll(List<Object> objects)
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

    public Object load(Class<?> clazz, Serializable id)
    {
	    Session session = sessionFactory.openSession();

        Object result = session.load(clazz, id);

        session.close();

        return result;
    }

    public List<Object> loadAll(Class<?> clazz)
    {
	    Session session = sessionFactory.openSession();

        final List result = session.createCriteria(clazz).list();

        session.close();

        return result;
    }

    public List<Object> loadAll(Class<?> clazz, Criterion... criterion)
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

    public void delete(final Object greylistee)
    {
	    Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(greylistee);

        session.getTransaction().commit();
        session.close();
    }
}
