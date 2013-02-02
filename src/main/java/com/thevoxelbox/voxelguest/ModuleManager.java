package com.thevoxelbox.voxelguest;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thevoxelbox.voxelguest.configuration.Configuration;
import com.thevoxelbox.voxelguest.modules.Module;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Monofraps
 */
public class ModuleManager      // implements ModuleManager -- TODO: export API stuff
{
    // maps module <-> registered event listeners
    private HashMap<Module, HashSet<Listener>> registeredModules = new HashMap<>();

    /**
     * Registers a guest module and enables it immediately if parameter enable is true.
     *
     * @param module
     *         The instance of the module class to register.
     * @param enable
     *         If set to true, the method will enable the module immediately after registration by calling enableModule
     */
    public final void registerGuestModule(final Module module, final boolean enable)
    {
        checkNotNull(module, "Parameter module must not be null.");

        if (this.registeredModules.containsKey(module))
        {
            Bukkit.getLogger().severe(String.format("Module already registered. (Module: %s)", module.toString()));
            return;
        }

        this.registeredModules.put(module, new HashSet<Listener>());

        if (enable)
        {
            try
            {
                enableModuleByInstance(module);
            }
            catch (Exception ex)
            {
                Bukkit.getLogger().severe(String.format("Failed to enable module %s", module.toString()));
                ex.printStackTrace();
            }
        }
    }

    /**
     * Enables a give module.
     *
     * @param module
     *         The instance of the module to enable.
     */
    public final void enableModuleByInstance(final Module module)
    {
        checkNotNull(module, "Parameter module must not be null.");
        checkState(this.registeredModules.containsKey(module), "Module must be registered.");
        checkState(!module.isEnabled(), "Module already enabled. (Module: %s)", module.toString());

        if (module.getConfiguration() != null)
        {
            Configuration.loadConfiguration(new File(VoxelGuest.getPluginInstance().getDataFolder() + File.separator + module.getConfigFileName() + ".properties"), module.getConfiguration());
        }

        module.onEnable();

        // try block prevents loose event listeners from floating around, when the registration fails at some point
        // it also prevents incomplete modules from staying enabled
        try
        {
            final Set<Listener> moduleListeners = module.getListeners();
            checkNotNull(moduleListeners, "Module %s returned null when asked for a list of listeners.", module.toString());

            if (!moduleListeners.isEmpty())
            {
                Set<Listener> internalListenerTracker = this.registeredModules.get(module);

                int numRegisteredListeners = 0;
                for (Listener listener : moduleListeners)
                {
                    Bukkit.getPluginManager().registerEvents(listener, VoxelGuest.getPluginInstance());
                    internalListenerTracker.add(listener);
                    numRegisteredListeners++;
                }

                Bukkit.getLogger().info(String.format("Registered %d event listeners for module %s", numRegisteredListeners, module.toString()));
            }
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().severe(String.format("Exception while enabling module: %s", ex.getMessage()));
            ex.printStackTrace();
            disableModuleByInstance(module);
        }

        final Map<String, CommandExecutor> commandExecutors = module.getCommandMappings();
        checkNotNull(commandExecutors, "Module %s returned null when asked for command executor mappings.", module.toString());
        if (!commandExecutors.isEmpty())
        {
            for (String command : commandExecutors.keySet())
            {
                final PluginCommand pluginCommand = VoxelGuest.getPluginInstance().getCommand(command);
                checkNotNull(pluginCommand);
                pluginCommand.setExecutor(commandExecutors.get(command));
            }
        }
    }

    /**
     * Enables all registered modules of type [module].
     *
     * @param module
     *         The type of the modules to enable.
     */
    public final void enableModuleByType(final Class<? extends Module> module)
    {
        checkNotNull(module, "Parameter module must not be null.");

        for (Module registeredModule : this.registeredModules.keySet())
        {
            if (module.isAssignableFrom(registeredModule.getClass()))
            {
                try
                {
                    enableModuleByInstance(registeredModule);
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().severe(String.format("Exception while enabling module: %s", ex.getMessage()));
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Disables a give module.
     *
     * @param module
     *         The instance of the module to disable.
     */
    public final void disableModuleByInstance(final Module module)
    {
        checkNotNull(module, "Parameter module must not be null.");
        checkState(this.registeredModules.containsKey(module), "Module must be registered.");
        checkState(module.isEnabled(), "Module already disabled. (Module: %s)", module.toString());

        if (module.getConfiguration() != null)
        {
            Configuration.saveConfiguration(new File(VoxelGuest.getPluginInstance().getDataFolder() + File.separator + module.getConfigFileName() + ".properties"), module.getConfiguration());
        }

        final Map<String, CommandExecutor> commandExecutors = module.getCommandMappings();
        checkNotNull(commandExecutors, "Module %s returned null when asked for command executor mappings.", module.toString());
        if (!commandExecutors.isEmpty())
        {
            for (String command : commandExecutors.keySet())
            {
                VoxelGuest.getPluginInstance().getCommand(command).setExecutor(null);
            }
        }

        try
        {
            // unregister the module listeners
            try
            {
                // get listeners from module (eventually contains self registered listeners)
                // and merge stored listeners to make sure we don't forget any listener
                final Set<Listener> moduleListeners = module.getListeners();
                checkNotNull(moduleListeners, "Module %s returned null when asked for a list of listeners.", module.toString());
                moduleListeners.addAll(this.registeredModules.get(module));
                this.registeredModules.get(module).clear();

                if (!moduleListeners.isEmpty())
                {
                    for (Listener listener : moduleListeners)
                    {
                        HandlerList.unregisterAll(listener);
                    }
                }
            }
            catch (Exception ex)
            {
                Bukkit.getLogger().severe("Failed to unregister module listeners.");
                ex.printStackTrace();
            }
            finally
            {
                registeredModules.get(module).clear();
            }

            module.onDisable();
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().severe(String.format("Exception while disabling module: %s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    /**
     * Disables all modules of type [module].
     *
     * @param module
     *         The type of the modules to disable.
     */
    public final void disableModuleByType(final Class<? extends Module> module)
    {
        checkNotNull(module, "Parameter module must not be null.");

        for (Module registeredModule : this.registeredModules.keySet())
        {
            if (module.isAssignableFrom(registeredModule.getClass()))
            {
                try
                {
                    disableModuleByInstance(registeredModule);
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().severe(String.format("Exception while disabling module: %s", ex.getMessage()));
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Restarts or just enables a give module. It calls disableModuleByInstance and enableModuleByInstance internally.
     *
     * @param module
     *         The instance of the module to restart.
     */
    public final void restartModule(final Module module)
    {
        checkNotNull(module, "Parameter module must not be null.");

        if (!module.isEnabled())
        {
            enableModuleByInstance(module);
            return;
        }

        try
        {
            disableModuleByInstance(module);
            enableModuleByInstance(module);
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().severe(String.format("Failed to restart module %s because: %s", module.toString(), ex.getMessage()));
            ex.printStackTrace();
        }
    }

    /**
     * Shuts down the module manager by disabling all modules.
     */
    public final void shutdown()
    {
        for (Module module : registeredModules.keySet())
        {
            if (module.isEnabled())
            {
                try
                {
                    disableModuleByInstance(module);
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().severe(String.format("Failed to disable module %s because: %s", module.toString(), ex.getMessage()));
                    ex.printStackTrace();
                }
            }
        }

        registeredModules.clear();
    }
}
