package com.thevoxelbox.voxelguest.api;


import com.thevoxelbox.voxelguest.api.modules.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles module registration and state transitions.
 * @author Monofraps
 */
public interface ModuleManager
{
    /**
     * Automatically detects and registers modules in a package.
     * @param packageName The package to scan (subpackages will be scanned as well)
     */
    void autoRegisterModulesInPackage(@NotNull final String packageName);

    /**
     * Registers a guest module and enables it immediately if parameter enable is true.
     *
     * @param module The instance of the module class to register.
     * @param enable If set to true, the method will enable the module immediately after registration by calling enableModule
     */
    void registerModule(@NotNull final Module module, final boolean enable);

    /**
     * Registers a module by class and enables it immediately if parameter enable is true.
     * @param module The module class to register.
     * @param enable If set to true, the method will enable the module immediately after registration by calling enableModule
     */
    void registerModule(@NotNull final Class<? extends Module> module, final boolean enable);

    /**
     * Enables all registered modules of type [module].
     *
     * @param moduleType The type of the modules to enable.
     */
    void enableModule(final Class<? extends Module> moduleType);

    /**
     * Enables all registered modules of type [module].
     *
     * @param moduleType The type of the modules to enable.
     */
    void disableModule(final Class<? extends Module> moduleType);

    /**
     * Restarts or just enables a given module.
     *
     * @param moduleType The instance of the module to restart.
     */
    void restartModule(final Class<? extends Module> moduleType);

    /**
     * Shuts down the module manager by disabling all modules.
     */
    void shutdown();

    /**
     * Finds a module state container based on the class name.
     * @param moduleTypeName Name of the module's class.
     * @return Returns a module state container or null.
     */
    @Nullable
    ModuleStateContainer findStateContainer(@NotNull final String moduleTypeName);

    /**
     * Finds a module state container based on the module's class.
     * @param moduleType The class of the module.
     * @return Returns a module state container or null.
     */
    @Nullable
    ModuleStateContainer findStateContainer(@NotNull final Class<? extends Module> moduleType);

    /**
     * Finds a module state container by module instance.
     * @param module An instance of a module.
     * @return Returns the module state container or null.
     */
    @Nullable
    ModuleStateContainer findStateContainer(@NotNull final Module module);

    /**
     * Returns all currently registered module types.
     * @return Returns all currently registered module types.
     */
    Set<Class<? extends Module>> getRegisteredModules();
}
