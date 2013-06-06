package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.asshat.mute.Mutelist;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Executes /unmute commands.
 *
 * @author Monofraps
 */
public class UnmuteCommandExecutor implements TabExecutor
{
    private final AsshatModuleConfiguration configuration;

    /**
     * Creates a new unmute command executor.
     *
     * @param module The owning module.
     */
    public UnmuteCommandExecutor(final AsshatModule module)
    {
        configuration = (AsshatModuleConfiguration) module.getConfiguration();
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if (args.length < 1)
        {
            commandSender.sendMessage("You need to specify the name of the player to unmute.");
            return false;
        }

        final String playerName = args[0].toLowerCase();
        boolean silentFlag = false;

        for (String arg : args)
        {
            if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si") || arg.equalsIgnoreCase("-s"))
            {
                silentFlag = true;
            }
        }

        if (!Mutelist.isPlayerMuted(playerName))
        {
            commandSender.sendMessage(String.format("Player %s is not muted.", playerName));
            return true;
        }

        safeUnmute(playerName, commandSender, silentFlag);

        return true;
    }

    private void safeUnmute(final String playerName, final CommandSender commandSender, final boolean silentFlag)
    {
        try
        {
            Mutelist.unmute(playerName);
            Bukkit.getLogger().info(String.format("%s unmuted by %s", playerName, commandSender.getName()));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(AsshatModule.formatBroadcastMessage(configuration.getUngagBroadcastMsg(), playerName, commandSender.getName(), ""));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            commandSender.sendMessage(String.format("Something went wrong: %s", ex.getMessage()));
        }
    }

    @Override
    public final List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        if (sender.hasPermission("voxelguest.asshat.unmute"))
        {
            final List<String> mutedNamesList = Mutelist.getMutedNames();
            if (args.length == 0)
            {
                return mutedNamesList;
            }
            else
            {
                final List<String> tmpMatchList = new ArrayList<>();
                final String completingParam = args[args.length - 1];
                for (String mutedName : mutedNamesList)
                {
                    if (mutedName.toLowerCase().startsWith(completingParam.toLowerCase()))
                    {
                        tmpMatchList.add(mutedName);
                    }
                }
                return tmpMatchList;
            }
        }
        return Collections.emptyList();
    }
}
