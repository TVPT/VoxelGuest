package com.thevoxelbox.voxelguest.commands;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.ModuleStateContainer;
import com.thevoxelbox.voxelguest.api.modules.Module;
import com.thevoxelbox.voxelguest.commands.arguments.ModulesCommandArguments;
import com.thevoxelbox.voxelguest.utils.A4JUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Handles /vmodules commands.
 *
 * @author Monofraps
 */
public final class ModulesCommandExecutor implements TabExecutor
{
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        final List<String> matches = new ArrayList<>();

        for (int i = 0; i < args.length; i++)
        {
            final String arg = args[i];

            if ((arg.startsWith("-e") || arg.startsWith("-d")) && i < (args.length - 1))
            {
                final Set<Class<? extends Module>> registeredModules = VoxelGuest.getModuleManagerInstance().getRegisteredModules();
                for (Class<? extends Module> moduleType : registeredModules)
                {
                    final String className = moduleType.getSimpleName();
                    if (className.toLowerCase().startsWith(arg.toLowerCase()))
                    {
                        matches.add(className);
                    }
                }
            }
            else if (arg.startsWith("-"))
            {
                matches.add("-enable");
                matches.add("-disable");
                matches.add("-list");
            }
        }

        Collections.sort(matches);
        return matches;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args)
    {
        // we assume the player wants to see the usage if he does not enter any argument
        if (args.length == 0)
        {
            return false;
        }

        final ModulesCommandArguments arguments = A4JUtils.parse(ModulesCommandArguments.class, args, sender);
        if (arguments == null)
        {
            return false;
        }

        if (arguments.isListModules())
        {
            listModules(sender);
        }

        if (!arguments.getModuleToEnable().isEmpty())
        {
            enableModule(sender, arguments.getModuleToEnable());
        }

        if (!arguments.getModuleToDisable().isEmpty())
        {
            disableModule(sender, arguments.getModuleToDisable());
        }

        return true;
    }

    private void listModules(final CommandSender sender)
    {
        final Set<Class<? extends Module>> registeredModules = VoxelGuest.getModuleManagerInstance().getRegisteredModules();

        sender.sendMessage(ChatColor.GREEN + "Registered Modules");
        sender.sendMessage(ChatColor.GRAY + "-------------------");

        for (final Class<? extends Module> moduleType : registeredModules)
        {
            final String className = moduleType.getSimpleName();
            final ModuleStateContainer stateContainer = VoxelGuest.getModuleManagerInstance().findStateContainer(moduleType);
            sender.sendMessage((stateContainer.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + className + ChatColor.GRAY + " (" + ChatColor.WHITE + stateContainer.getModule().getName() + ChatColor.GRAY + ")");
        }
    }

    private boolean enableModule(final CommandSender sender, final String moduleClassName)
    {
        final ModuleStateContainer stateContainer = VoxelGuest.getModuleManagerInstance().findStateContainer(moduleClassName);
        if (stateContainer == null)
        {
            sender.sendMessage(ChatColor.RED + "No such module.");
            return false;
        }

        if (stateContainer.enable())
        {
            sender.sendMessage(ChatColor.GRAY + moduleClassName + " has been enabled!");
            return true;
        }
        else
        {
            sender.sendMessage(ChatColor.GRAY + moduleClassName + " is already enabled!");
            return true;
        }
    }

    private boolean disableModule(final CommandSender sender, final String moduleClassName)
    {
        final ModuleStateContainer stateContainer = VoxelGuest.getModuleManagerInstance().findStateContainer(moduleClassName);
        if (stateContainer == null)
        {
            sender.sendMessage(ChatColor.RED + "No such module.");
            return false;
        }

        if (stateContainer.disable())
        {
            sender.sendMessage(ChatColor.GRAY + moduleClassName + " has been disabled!");
            return true;
        }
        else
        {
            sender.sendMessage(ChatColor.GRAY + moduleClassName + " is not enabled!");
            return true;
        }
    }

}
