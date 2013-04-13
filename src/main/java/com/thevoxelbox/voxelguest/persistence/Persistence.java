package com.thevoxelbox.voxelguest.persistence;

import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Monofraps
 * @author MikeMatrix
 */
public final class Persistence
{
    private static Persistence instance = new Persistence();
    private ConnectionSource connectionSource;
    private Map<Class<?>, Dao> daoCache = new HashMap<>();
    private boolean initialized = false;

    private Persistence()
    {
    }

    /**
     * @return Returns the instance of the persistence system.
     */
    public static Persistence getInstance()
    {
        return instance;
    }

    /**
     * Initialized ORMlite.
     *
     * @param dbFile The file to use for persistence.
     *
     * @throws SQLException Thrown if the system fails to open the DB connection.
     */
    public void initialize(final File dbFile) throws SQLException
    {
        Preconditions.checkState(!initialized, "Persistence system has already been initialized.");
        dbFile.getParentFile().mkdirs();
        connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + dbFile.getPath());

        initialized = true;
    }

    /**
     * Clears the DAO cache and closes all remaining DB connections.
     *
     * @throws SQLException
     */
    public void shutdown() throws SQLException
    {
        Preconditions.checkState(initialized, "Persistence system has to be initialized before shutdown.");
        initialized = false;

        daoCache.clear();
        connectionSource.close();
    }

    /**
     * Creates a DAO.
     *
     * @param clazz The class of the objects for which you want the DAO for.
     * @param <E>   The type of the object for which you want the DAO for.
     *
     * @return Returns a DAO.
     *
     * @throws SQLException
     */
    public <E> Dao<E, ?> getDao(final Class<E> clazz) throws SQLException
    {
        checkState();

        if (!daoCache.containsKey(clazz))
        {
            TableUtils.createTableIfNotExists(connectionSource, clazz);
            daoCache.put(clazz, DaoManager.createDao(connectionSource, clazz));
        }
        return daoCache.get(clazz);
    }

    /**
     * Stores an object into the database.
     *
     * @param object The object to store into the database.
     * @param <V>    The type of the object to delete.
     * @param <ID>   The type of the object's id.
     */
    public <V, ID> void save(final V object)
    {
        checkState();

        try
        {
            Dao<V, ID> objectDao = (Dao<V, ID>) getDao(object.getClass());
            DatabaseConnection connection = objectDao.startThreadConnection();
            objectDao.createOrUpdate(object);

            if (!objectDao.isAutoCommit(connection))
            {
                objectDao.commit(connection);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Stores a list of objects into the database.
     *
     * @param objects The list of objects to store.
     * @param <V>     The type of the object to delete.
     * @param <ID>    The type of the object's id.
     */
    public <V, ID> void saveAll(final List<V> objects)
    {
        checkState();

        if (objects.size() == 0)
        {
            return;
        }

        try
        {
            Dao<V, ID> objectDao = (Dao<V, ID>) getDao(objects.get(0).getClass());
            DatabaseConnection connection = objectDao.startThreadConnection();

            for (Object object : objects)
            {
                objectDao.createOrUpdate((V) object);
            }

            if (!objectDao.isAutoCommit(connection))
            {
                objectDao.commit(connection);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Loads all objects from a table.
     *
     * @param clazz The class of the objects to load;
     * @param <V>   The type of the object to delete.
     * @param <ID>  The type of the object's id.
     *
     * @return Returns a list of all objects loaded.
     */
    public <V, ID> List<V> loadAll(final Class<?> clazz)
    {
        checkState();

        List<V> objects = Collections.emptyList();
        try
        {
            Dao<V, ID> objectDao = (Dao<V, ID>) getDao(clazz);
            DatabaseConnection connection = objectDao.startThreadConnection();

            objects = objectDao.queryForAll();

            if (!objectDao.isAutoCommit(connection))
            {
                objectDao.commit(connection);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return objects;
    }

    /**
     * Loads all objects from a table. SELECT query is restricted by [restrictions].
     *
     * @param clazz        The class of the objects to load.
     * @param restrictions A map of restrictions (column_name <<->> value)
     * @param <V>          The type of the object to delete.
     * @param <ID>         The type of the object's id.
     *
     * @return Returns a list of all DB entries matching the restrictions.
     */
    public <V, ID> List<V> loadAll(final Class<?> clazz, final Map<String, Object> restrictions)
    {
        checkState();

        List<V> objects = Collections.emptyList();
        try
        {
            Dao<V, ID> objectDao = (Dao<V, ID>) getDao(clazz);
            objects = objectDao.queryForFieldValues(restrictions);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return objects;
    }

    /**
     * Deletes a persistence entry in the database.
     *
     * @param object The object to delete.
     * @param <V>    The type of the object to delete.
     * @param <ID>   The type of the object's id.
     */
    public <V, ID> void delete(final Object object)
    {
        checkState();

        try
        {
            Dao<V, ID> objectDao = (Dao<V, ID>) getDao(object.getClass());
            DatabaseConnection connection = objectDao.startThreadConnection();

            objectDao.delete((V) object);

            if (!objectDao.isAutoCommit(connection))
            {
                objectDao.commit(connection);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void checkState()
    {
        Preconditions.checkState(initialized, "Persistence system has to be initialized before usage.");
    }
}
