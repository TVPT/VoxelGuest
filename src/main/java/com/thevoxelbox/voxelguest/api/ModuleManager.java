package com.thevoxelbox.voxelguest.api;


import com.thevoxelbox.voxelguest.ModuleMeta;
import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.Module;
import com.thevoxelbox.voxelguest.configuration.Configuration;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

/**
 * @author Monofraps
 */
public interface ModuleManager
{
    /**
     * Registers a guest module and enables it immediately if parameter enable is true.
     *
     * @param module The instance of the module class to register.
     * @param enable If set to true, the method will enable the module immediately after registration by calling enableModule
     */
    void registerGuestModule(final Module module, final boolean enable);

    /**
     * Enables a given module.
     *
     * @param module The instance of the module to enable.
     */
    void enableModuleByInstance(final Module module);

    /**
     * Enables all registered modules of type [module].
     *
     * @param module The type of the modules to enable.
     */
    void enableModuleByType(final Class<? extends Module> module);

    /**
     * Disables a given module.
     *
     * @param module The instance of the module to disable.
     */
    void disableModuleByInstance(final Module module, final boolean isShutdown);

    void disableModuleByInstance(final Module module);

    /**
     * Disables all modules of type [module].
     *
     * @param module The type of the modules to disable.
     */
    void disableModuleByType(final Class<? extends Module> module, final boolean isShutdown);

    void disableModuleByType(final Class<? extends Module> module);

    /**
     * Restarts or just enables a give module. It calls disableModuleByInstance and enableModuleByInstance internally.
     *
     * @param module The instance of the module to restart.
     */
    void restartModule(final Module module);

    /**
     * Shuts down the module manager by disabling all modules.
     */
    void shutdown();

    /**
     * Returns the registered modules.
     *
     * @return Returns all registered modules in a hash map containing Module <--> EventListenersOfModule mapping.
     */
    HashMap<Module, HashSet<Listener>> getRegisteredModules();

    void loadFromPersistence();
}
