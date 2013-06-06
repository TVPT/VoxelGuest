package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.asshat.ban.Banlist;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Executes /unban commands.
 *
 * @author Monofraps
 */
public class UnbanCommandExecutor implements TabExecutor
{
    private final AsshatModuleConfiguration configuration;

    /**
     * Creates a new unban command executor.
     *
     * @param module The owning module.
     */
    public UnbanCommandExecutor(final AsshatModule module)
    {
        configuration = (AsshatModuleConfiguration) module.getConfiguration();
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if (args.length < 1)
        {
            commandSender.sendMessage("You need to specify the name of the player to unban.");
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

        if (!Banlist.isPlayerBanned(playerName))
        {
            commandSender.sendMessage(String.format("Player %s is not banned.", playerName));
            return true;
        }

        safeUnban(playerName, commandSender, silentFlag);

        return true;
    }

    private void safeUnban(final String playerName, final CommandSender commandSender, final boolean silentFlag)
    {
        try
        {
            Banlist.unban(playerName);
            Bukkit.getLogger().info(String.format("%s unbanned by %s", playerName, commandSender.getName()));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(AsshatModule.formatBroadcastMessage(configuration.getUnbanBroadcastMsg(), playerName, commandSender.getName(), ""));
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
        if (sender.hasPermission("voxelguest.asshat.unban"))
        {
            final List<String> bannedNamesList = Banlist.getBannedNames();
            if (args.length == 0)
            {
                return bannedNamesList;
            }
            else
            {
                final List<String> tmpMatchList = new ArrayList<>();
                final String completingParam = args[args.length - 1];
                for (String bannedName : bannedNamesList)
                {
                    if (bannedName.toLowerCase().startsWith(completingParam.toLowerCase()))
                    {
                        tmpMatchList.add(bannedName);
                    }
                }
                return tmpMatchList;
            }
        }
        return Collections.emptyList();
    }
}
