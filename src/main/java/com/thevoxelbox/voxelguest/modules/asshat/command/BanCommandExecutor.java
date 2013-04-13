package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Executes /ban commands.
 *
 * @author Monofraps
 */
public class BanCommandExecutor implements TabExecutor
{
    private final AsshatModule module;
    private final AsshatModuleConfiguration configuration;

    /**
     * Creates a new ban command executor.
     *
     * @param module The owning module.
     */
    public BanCommandExecutor(final AsshatModule module)
    {
        this.module = module;
        configuration = (AsshatModuleConfiguration) module.getConfiguration();
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if (args.length < 1)
        {
            commandSender.sendMessage("You must at least specify the name of the player to ban.");
            return false;
        }

        final String playerName = args[0].toLowerCase();
        boolean forceNameFlag = false;
        boolean silentFlag = false;
        String banReason = "";

        for (int i = 1; i < args.length; i++)
        {
            final String arg = args[i];

            if (arg.equalsIgnoreCase("-force") || arg.equalsIgnoreCase("-f"))
            {
                forceNameFlag = true;
                continue;
            }

            if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si") || arg.equalsIgnoreCase("-s"))
            {
                silentFlag = true;
                continue;
            }

            banReason += arg + " ";
        }

        if (banReason.isEmpty())
        {
            banReason = configuration.getDefaultAsshatReason();
        }

        if (forceNameFlag)
        {
            safeBan(playerName, banReason, commandSender, silentFlag);
            return true;
        }

        final List<Player> players = Bukkit.matchPlayer(playerName);
        if (players.size() < 1)
        {
            commandSender.sendMessage(String.format("Could not find any online player named like %s. Append the -force parameter to the command to ban offline players.", playerName));
            return true;
        }

        if (players.size() > 1)
        {
            commandSender.sendMessage("Found multiple players matching the name (use the -force flag if you entered the exact player name)" + playerName);
            String list = "";
            for (Player player : players)
            {
                list += player.getName() + ", ";
            }
            list = list.substring(0, list.length() - 1);

            commandSender.sendMessage(list);
            return true;
        }

        players.get(0).kickPlayer(banReason);
        safeBan(players.get(0).getName(), banReason, commandSender, silentFlag);

        return true;
    }

    private void safeBan(final String playerName, final String banReason, final CommandSender commandSender, final boolean silentFlag)
    {
        if (module.getBanlist().isPlayerBanned(playerName))
        {
            commandSender.sendMessage(String.format("Player %s is already banned.", playerName));
            return;
        }

        try
        {
            module.getBanlist().ban(playerName, banReason);
            Bukkit.getLogger().info(String.format("%s got banned for %s by %s", playerName, banReason, commandSender.getName()));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(this.module.formatBroadcastMessage(configuration.getBanBroadcastMsg(), playerName, commandSender.getName(), banReason));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            commandSender.sendMessage(String.format("Something went wrong: %s", ex.getMessage()));
        }
    }

    @Override
    public final List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] strings)
    {
        return null;
    }
}
