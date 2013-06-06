package com.thevoxelbox.voxelguest.configuration;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationProperty;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author MikeMatrix
 */
public final class Configuration
{
    private static Gson transformer = new Gson();

    private Configuration()
    {
    }

    /**
     * Loads a configuration from file.
     *
     * @param configurationFile The configuration file.
     * @param targetObject      The object which is supposed to be filled with variables from the configuration file.
     *
     * @return Returns a boolean indicating whether the loading was successful. (true = success)
     */
    public static boolean loadConfiguration(final File configurationFile, final Object targetObject)
    {
        Preconditions.checkNotNull(configurationFile, "Configuration File cannot be null.");
        Preconditions.checkNotNull(targetObject, "Target Object cannot be null.");

        if (configurationFile.exists())
        {
            Preconditions.checkState(configurationFile.isFile(), "Configuration File expected to be a file.");

            Properties properties = new Properties();
            try (FileReader fileReader = new FileReader(configurationFile))
            {
                properties.load(fileReader);


                for (Field field : targetObject.getClass().getDeclaredFields())
                {
                    if (field.isAnnotationPresent(ConfigurationProperty.class))
                    {
                        field.setAccessible(true);
                        ConfigurationProperty property = field.getAnnotation(ConfigurationProperty.class);

                        if (properties.containsKey(property.value()))
                        {
                            final String value = properties.getProperty(property.value());
                            final Class<?> destinationClass = field.getType();
                            Object object = transformer.fromJson(value, destinationClass);
                            field.set(targetObject, object);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * Saves an object to a file. Getters annotated with @ConfigurationGetter will be called to get the values to save.
     *
     * @param configurationFile The file to write to.
     * @param sourceObject      The object which is supposed to be saved.
     *
     * @return Returns a boolean indicating whether the operation was successful or not. (true = success)
     */
    public static boolean saveConfiguration(final File configurationFile, final Object sourceObject)
    {
        Preconditions.checkNotNull(configurationFile, "Configuration File cannot be null.");
        Preconditions.checkNotNull(sourceObject, "Source Object cannot be null.");

        try (FileWriter fileWriter = new FileWriter(configurationFile))
        {
            Properties properties = new Properties();

            for (Field field : sourceObject.getClass().getDeclaredFields())
            {
                if (field.isAnnotationPresent(ConfigurationProperty.class))
                {
                    field.setAccessible(true);
                    ConfigurationProperty property = field.getAnnotation(ConfigurationProperty.class);

                    Object result = Preconditions.checkNotNull(field.get(sourceObject), "ConfigurationProperty must not be null.");
                    String resultString = transformer.toJson(result);

                    properties.setProperty(property.value(), resultString);
                }
            }

            properties.store(fileWriter, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
