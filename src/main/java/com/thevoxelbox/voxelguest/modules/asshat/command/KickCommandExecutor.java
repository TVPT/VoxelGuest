package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.asshat.command.argument.AsshatCommandArguments;
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

/**
 * Executes /kick commands.
 *
 * @author Monofraps
 */
public class KickCommandExecutor implements TabExecutor
{
    private final AsshatModuleConfiguration configuration;

    /**
     * Creates a new kick command executor.
     *
     * @param module The parent module.
     */
    public KickCommandExecutor(final AsshatModule module)
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

        final String kickReason = arguments.getReason().isEmpty() ? configuration.getDefaultAsshatReason() : arguments.getReason();

        if (arguments.isForceName())
        {
            if (Bukkit.getPlayerExact(arguments.getPlayerName()) != null)
            {
                safeKick(Bukkit.getPlayerExact(arguments.getPlayerName()), kickReason, commandSender, arguments.isSilent());
                return true;
            }
            else
            {
                commandSender.sendMessage("Could not find any player named like " + arguments.getPlayerName());
                return true;
            }
        }

        final List<Player> players = Bukkit.matchPlayer(arguments.getPlayerName());
        if (players.size() < 1)
        {
            commandSender.sendMessage("Could not find any player named like " + arguments.getPlayerName());
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

        safeKick(players.get(0), kickReason, commandSender, arguments.isSilent());
        return true;
    }

    private void safeKick(final Player player, final String reason, final CommandSender sender, final boolean silentFlag)
    {
        player.kickPlayer(reason);

        Bukkit.getLogger().info(String.format("%s kicked by %s for %s", player.getName(), sender.getName(), reason));
        if (!silentFlag)
        {
            Bukkit.broadcastMessage(AsshatModule.formatBroadcastMessage(configuration.getKickBroadcastMsg(), player.getName(), sender.getName(), reason));
        }
    }

    @Override
    public final List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] strings)
    {
        return Collections.emptyList();
    }
}
