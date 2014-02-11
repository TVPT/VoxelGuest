package com.thevoxelbox.voxelguest.api.modules;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;

/**
 * @author MikeMatrix
 * @author Monofraps
 */
public interface Module
{
    /**
     * Gets called when the {@link com.thevoxelbox.voxelguest.GuestModuleManager ModuleManager} enables the module.
     */
    void onEnable();

    /**
     * Gets called when the {@link com.thevoxelbox.voxelguest.GuestModuleManager ModuleManager} disables the module.
     */
    void onDisable();

    /**
     * Returns a HashSet of listeners the module wants the module manager to register.
     *
     * @return HashSet of listeners to register
     */
    Set<Listener> getListeners();

    /**
     * Returns the name of the module.
     *
     * @return the module name
     */
    String getName();

    /**
     * Returns the instantiated configuration object.
     *
     * @return Returns the instantiated configuration object.
     */
    Object getConfiguration();

    /**
     * Returns the configuration file name.
     *
     * @return Returns the configuration file name.
     */
    String getConfigFileName();

    /**
     * Returns command executor mappings as [command] <--> [executor].
     *
     * @return Returns command executor mappings.
     */
    Map<String, CommandExecutor> getCommandMappings();
}
