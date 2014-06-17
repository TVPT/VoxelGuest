package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.api.ModuleStateContainer;
import com.thevoxelbox.voxelguest.api.modules.Module;
import com.thevoxelbox.voxelguest.configuration.Configuration;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

/**
 * @author monofraps
 */
public class GuestModuleStateContainer implements ModuleStateContainer
{
    private Module module;
    private boolean                      isEnabled            = false;
    private Set<Listener>                moduleEventListeners = new HashSet<>();
    private Map<String, CommandExecutor> commandExecutors     = new HashMap<>();

    @Override
    public boolean enable()
    {
        checkNotNull(module, "GuestModuleStateContainer has not been associated.");

        if (isEnabled)
        {
            return false;
        }

        if (module.getConfiguration() != null)
        {
            Configuration.loadConfiguration(new File(VoxelGuest.getPluginInstance().getDataFolder() + File.separator + module.getConfigFileName() + ".properties"), module.getConfiguration());
        }

        module.onEnable();

        // try block prevents loose event listeners from floating around, when the registration fails at some point
        moduleEventListeners = checkNotNull(module.getListeners(), String.format("Module %s returned null when asked for a list of listeners.", module.toString()));

        if (!moduleEventListeners.isEmpty())
        {

            int numRegisteredListeners = 0;
            for (Listener listener : moduleEventListeners)
            {
                Bukkit.getPluginManager().registerEvents(listener, VoxelGuest.getPluginInstance());
                numRegisteredListeners++;
            }

            Bukkit.getLogger().info(String.format("Registered %d of %d event listeners for module %s", numRegisteredListeners, moduleEventListeners.size(), module.toString()));
        }

        final Map<String, CommandExecutor> allCommandExecutors = module.getCommandMappings();
        checkNotNull(allCommandExecutors, "Module %s returned null when asked for command executor mappings.", module.toString());
        if (!allCommandExecutors.isEmpty())
        {
            for (final String command : allCommandExecutors.keySet())
            {
                final PluginCommand pluginCommand = VoxelGuest.getPluginInstance().getCommand(command);
                if (pluginCommand == null)
                {
                    Bukkit.getLogger().info(String.format("You need to put %s into the plugin.yml - command not registered", command));
                    continue;
                }

                pluginCommand.setExecutor(allCommandExecutors.get(command));
                commandExecutors.put(command, allCommandExecutors.get(command));
            }
        }

        isEnabled = true;
        return true;
    }

    @Override
    public boolean disable()
    {
        checkNotNull(module, "GuestModuleStateContainer has not been associated.");
        if(!isEnabled) return true;

        if (module.getConfiguration() != null)
        {
            Configuration.saveConfiguration(new File(VoxelGuest.getPluginInstance().getDataFolder() + File.separator + module.getConfigFileName() + ".properties"), module.getConfiguration());
        }

        commandExecutors = module.getCommandMappings();
        checkNotNull(commandExecutors, "Module %s returned null when asked for command executor mappings.", module.toString());
        if (!commandExecutors.isEmpty())
        {
            for (final String command : commandExecutors.keySet())
            {
                try
                {
                    VoxelGuest.getPluginInstance().getCommand(command).setExecutor(null);
                }
                catch (Exception ex)
                {
                    Bukkit.getLogger().warning("Failed to unregister module command: " + command);
                    ex.printStackTrace();
                }
            }
        }

        final Set<Listener> moduleListeners = module.getListeners();
        checkNotNull(moduleListeners, "Module %s returned null when asked for a list of listeners.", module.toString());

        for (final Listener listener : moduleListeners)
        {
            if(listener == null) continue;
            moduleEventListeners.remove(listener);
            HandlerList.unregisterAll(listener);
        }

        module.onDisable();

        return true;
    }

    @Override
    public void associateModule(@NotNull final Module module)
    {
        checkState(this.module == null, "GuestModuleStateContainer has already been associated.");

        this.module = checkNotNull(module, "Parameter module must not be null.");
    }

    @Override
    public void persist()
    {
        checkNotNull(module, "GuestModuleStateContainer has not been associated.");

        final ModuleMeta moduleMeta = new ModuleMeta();
        moduleMeta.setModuleName(module.getName());
        moduleMeta.setEnabled(isEnabled);

        Persistence.getInstance().save(moduleMeta);
    }

    @Override
    public boolean loadStateFromPersistence()
    {
        checkNotNull(module, "GuestModuleStateContainer has not been associated.");

        final Map<String, Object> queryRestrictions = new HashMap<>();
        queryRestrictions.put("moduleName", module.getName());

        final List<ModuleMeta> moduleMetas = Persistence.getInstance().loadAll(ModuleMeta.class, queryRestrictions);
        if (!moduleMetas.isEmpty())
        {
            final ModuleMeta meta = moduleMetas.get(0);
            if (meta.isEnabled() && !isEnabled)
            {
                enable();
                return true;
            }
        }

        return false;
    }

    @Override
    public Module getModule()
    {
        return module;
    }

    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }


}
