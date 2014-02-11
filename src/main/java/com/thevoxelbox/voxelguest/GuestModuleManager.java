package com.thevoxelbox.voxelguest;


import com.google.common.base.Strings;
import com.thevoxelbox.voxelguest.api.AutoRegisterModule;
import com.thevoxelbox.voxelguest.api.ModuleManager;
import com.thevoxelbox.voxelguest.api.ModuleStateContainer;
import com.thevoxelbox.voxelguest.api.modules.Module;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

/**
 * @author Monofraps
 */
public final class GuestModuleManager implements ModuleManager
{
    private final HashMap<Class<? extends Module>, ModuleStateContainer> registeredModules = new HashMap<>();

    public void autoRegisterModulesInPackage(@NotNull final String packageName)
    {
        final Reflections reflections = new Reflections(packageName);
        for (final Class<?> type : reflections.getTypesAnnotatedWith(AutoRegisterModule.class))
        {
            Bukkit.getLogger().info(type.getSimpleName());
            if (Module.class.isAssignableFrom(type))
            {
                final AutoRegisterModule annotation = type.getAnnotation(AutoRegisterModule.class);
                registerModule((Class<? extends Module>) type, annotation.autoEnable());
            }
        }
    }

    @Override
    public void registerModule(@NotNull final Class<? extends Module> moduleType, final boolean enable)
    {
        final ModuleStateContainer stateContainer;

        if (isModuleRegistered(moduleType))
        {
            Bukkit.getLogger().warning(String.format("Module type %s already registered.", moduleType.getName()));
            stateContainer = findStateContainer(moduleType);
            if (!stateContainer.loadStateFromPersistence() && enable)
            {
                stateContainer.enable();
            }
        }
        else
        {
            final Module module;

            try
            {
                module = instantiateModule(moduleType);
            }
            catch (IllegalAccessException | InvocationTargetException | InstantiationException e)
            {
                Bukkit.getLogger().info(String.format("Failed to register module: %s", moduleType.getName()));
                e.printStackTrace();
                return;
            }

            registerModule(module, enable);
        }
    }

    private Module instantiateModule(@NotNull final Class<? extends Module> moduleType) throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
        for (final Constructor<?> constructor : moduleType.getConstructors())
        {
            if (constructor.getParameterTypes().length == 0)
            {
                return (Module) constructor.newInstance();
            }
        }

        throw new InstantiationException(String.format("Failed to instantiate module %s. No default constructor found.", moduleType.getName()));
    }

    private boolean isModuleRegistered(@NotNull final Class<? extends Module> moduleType)
    {
        return registeredModules.containsKey(moduleType);
    }

    @Override
    public void registerModule(@NotNull final Module module, final boolean enable)
    {
        checkNotNull(module, "Parameter module must not be null.");

        final ModuleStateContainer stateContainer;

        if (isModuleRegistered(module))
        {
            Bukkit.getLogger().warning(String.format("Module %s already registered", module.getName()));
            stateContainer = findStateContainer(module);
        }
        else
        {
            stateContainer = new GuestModuleStateContainer();
            stateContainer.associateModule(module);
            registeredModules.put(module.getClass(), stateContainer);
        }

        if (!stateContainer.loadStateFromPersistence() && enable)
        {
            stateContainer.enable();
        }
    }

    private boolean isModuleRegistered(final Module module)
    {
        return isModuleRegistered(module.getClass());
    }

    @Override
    public void enableModule(@NotNull final Class<? extends Module> moduleType)
    {
        findStateContainer(moduleType).enable();
    }

    @Override
    public void disableModule(@NotNull final Class<? extends Module> moduleType)
    {
        findStateContainer(moduleType).disable();
    }

    @Override
    public void restartModule(@NotNull final Class<? extends Module> moduleType)
    {
        final ModuleStateContainer stateContainer = findStateContainer(moduleType);
        stateContainer.disable();
        stateContainer.enable();
    }

    @Override
    @NotNull
    public ModuleStateContainer findStateContainer(@NotNull final Module module)
    {
        return findStateContainer(module.getClass());
    }

    @Override
    public Set<Class<? extends Module>> getRegisteredModules()
    {
        return registeredModules.keySet();
    }

    @Override
    @NotNull
    public ModuleStateContainer findStateContainer(@NotNull final Class<? extends Module> moduleType)
    {
        checkNotNull(moduleType, "Parameter moduleType must not be null.");
        checkState(isModuleRegistered(moduleType), "No module of type " + moduleType.getName() + " registered.");
        return registeredModules.get(moduleType);
    }

    @Override
    @Nullable
    public ModuleStateContainer findStateContainer(@NotNull final String moduleTypeName)
    {
        checkArgument(!Strings.nullToEmpty(moduleTypeName).isEmpty(), "Parameter moduleTypeName must not be null or empty.");

        for (final Class<? extends Module> moduleType : registeredModules.keySet())
        {
            if (moduleType.getSimpleName().equals(moduleTypeName))
            {
                return findStateContainer(moduleType);
            }
        }

        return null;
    }

    @Override
    public void shutdown()
    {
        for (ModuleStateContainer stateContainer : registeredModules.values())
        {
            stateContainer.persist();
            stateContainer.disable();
        }
    }
}
