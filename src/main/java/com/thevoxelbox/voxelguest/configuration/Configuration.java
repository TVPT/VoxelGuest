package com.thevoxelbox.voxelguest.configuration;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationGetter;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationSetter;
import net.sf.morph.transform.transformers.SimpleDelegatingTransformer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author MikeMatrix
 */
public final class Configuration
{
    private static SimpleDelegatingTransformer transformer = new SimpleDelegatingTransformer();

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


                for (Method method : targetObject.getClass().getMethods())
                {
                    if (method.isAnnotationPresent(ConfigurationSetter.class))
                    {
                        Preconditions.checkState(method.getParameterTypes().length == 1, "ConfigurationSetter methods require to have exactly 1 argument.");

                        ConfigurationSetter setter = method.getAnnotation(ConfigurationSetter.class);

                        if (properties.containsKey(setter.value()))
                        {
                            final String property = properties.getProperty(setter.value());
                            final Class<?> destinationClass = method.getParameterTypes()[0];
                            Object object = transformer.convert(destinationClass, property);

                            method.invoke(targetObject, object);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
            catch (InvocationTargetException e)
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

            for (Method method : sourceObject.getClass().getMethods())
            {
                if (method.isAnnotationPresent(ConfigurationGetter.class))
                {
                    Preconditions.checkState(method.getParameterTypes().length == 0, "ConfigurationGetter methods require to have no arguments.");

                    ConfigurationGetter setter = method.getAnnotation(ConfigurationGetter.class);

                    Object result = Preconditions.checkNotNull(method.invoke(sourceObject), "ConfigurationGetter methods require not to return null.");
                    String resultString = (String) transformer.convert(String.class, result);


                    properties.setProperty(setter.value(), resultString);
                }
            }

            properties.store(fileWriter, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (InvocationTargetException e)
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
