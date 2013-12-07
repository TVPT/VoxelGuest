package com.thevoxelbox.voxelguest.commands;

import com.thevoxelbox.voxelguest.A4JUtils;
import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.Module;
import com.thevoxelbox.voxelguest.commands.arguments.ModulesCommandArguments;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
                final HashMap<Module, HashSet<Listener>> registeredModules = VoxelGuest.getModuleManagerInstance().getRegisteredModules();
                for (Module module : registeredModules.keySet())
                {
                    final String className = module.getClass().getName().replaceFirst(module.getClass().getPackage().getName().concat("."), "");
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
        final HashMap<Module, HashSet<Listener>> registeredModules = VoxelGuest.getModuleManagerInstance().getRegisteredModules();

        sender.sendMessage(ChatColor.GREEN + "Registered Modules");
        sender.sendMessage(ChatColor.GRAY + "-------------------");

        for (final Module module : registeredModules.keySet())
        {
            final String className = module.getClass().getName().replaceFirst(module.getClass().getPackage().getName().concat("."), "");
            sender.sendMessage((module.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + className + ChatColor.GRAY + " (" + ChatColor.WHITE + module.getName() + ChatColor.GRAY + ")");
        }
    }

    private boolean enableModule(final CommandSender sender, final String moduleClassName)
    {
        final HashMap<Module, HashSet<Listener>> registeredModules = VoxelGuest.getModuleManagerInstance().getRegisteredModules();

        for (Module module : registeredModules.keySet())
        {
            final String className = module.getClass().getName().replaceFirst(module.getClass().getPackage().getName().concat("."), "");
            if (className.equalsIgnoreCase(moduleClassName))
            {
                if (module.isEnabled())
                {
                    sender.sendMessage(ChatColor.RED + module.getName() + " is already enabled.");
                    return true;
                }
                VoxelGuest.getModuleManagerInstance().enableModuleByType(module.getClass());
                sender.sendMessage(ChatColor.GRAY + module.getName() + " has been enabled!");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "No such module.");
        return false;
    }

    private boolean disableModule(final CommandSender sender, final String moduleClassName)
    {
        final HashMap<Module, HashSet<Listener>> registeredModules = VoxelGuest.getModuleManagerInstance().getRegisteredModules();
        for (Module module : registeredModules.keySet())
        {
            if (module.getClass().getName().toLowerCase().endsWith(moduleClassName.toLowerCase()))
            {
                if (!module.isEnabled())
                {
                    sender.sendMessage(ChatColor.RED + module.getName() + " is not enabled.");
                    return true;
                }
                VoxelGuest.getModuleManagerInstance().disableModuleByType(module.getClass());
                sender.sendMessage(ChatColor.GRAY + module.getName() + " has been disabled!");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "No such module.");
        return false;
    }

}
