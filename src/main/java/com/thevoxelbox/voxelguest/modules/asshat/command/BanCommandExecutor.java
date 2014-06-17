package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.asshat.ban.Banlist;
import com.thevoxelbox.voxelguest.modules.asshat.command.argument.AsshatCommandArguments;
import com.thevoxelbox.voxelguest.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Executes /ban commands.
 *
 * @author Monofraps
 */
public class BanCommandExecutor implements TabExecutor
{
    private final AsshatModuleConfiguration configuration;

    /**
     * Creates a new ban command executor.
     *
     * @param module The owning module.
     */
    public BanCommandExecutor(final AsshatModule module)
    {
        configuration = (AsshatModuleConfiguration) module.getConfiguration();
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        final AsshatCommandArguments arguments = new AsshatCommandArguments();
        final CmdLineParser parser = new CmdLineParser(arguments);
        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + e.getMessage());
            return false;
        }

        final String banReason = arguments.getReason().isEmpty() ? configuration.getDefaultAsshatReason() : arguments.getReason();

        if (arguments.isForceName())
        {
            safeBan(arguments.getPlayerName(), banReason, commandSender, arguments.isSilent());
            return true;
        }

        final List<Player> players = Bukkit.matchPlayer(arguments.getPlayerName());
        if (players.size() < 1)
        {
            commandSender.sendMessage(String.format("Could not find any online player named like %s. Append the -force parameter to the command to ban offline players.", arguments.getPlayerName()));
            return true;
        }

        if (players.size() > 1)
        {
            commandSender.sendMessage("Found multiple players matching the name (use the -force flag if you entered the exact player name) " + arguments.getPlayerName());
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
        safeBan(players.get(0).getName(), banReason, commandSender, arguments.isSilent());

        return true;
    }

    private void safeBan(final String playerName, final String banReason, final CommandSender commandSender, final boolean silentFlag)
    {
        // Resolve player name to UUID
        final UUID playerUUID;
        try
        {
            playerUUID = UUIDFetcher.getUUIDOf(playerName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            commandSender.sendMessage("An error occurred resolving the player's UUID - check your input or file a bug report.");
            return;
        }

        if (Banlist.isPlayerBanned(playerUUID))
        {
            commandSender.sendMessage(String.format("Player %s is already banned.", playerName));
            return;
        }

        try
        {
            Banlist.ban(playerUUID, banReason);
            Bukkit.getLogger().info(String.format("%s banned by %s for %s", playerName, commandSender.getName(), banReason));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(AsshatModule.formatBroadcastMessage(configuration.getBanBroadcastMsg(), playerName, commandSender.getName(), banReason));
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
        return Collections.emptyList();
    }
}
