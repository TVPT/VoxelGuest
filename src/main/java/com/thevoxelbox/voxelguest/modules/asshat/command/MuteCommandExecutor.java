package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.asshat.command.argument.AsshatCommandArguments;
import com.thevoxelbox.voxelguest.modules.asshat.mute.Mutelist;
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
 * Executes /mute and /gag commands.
 *
 * @author Monofraps
 */
public class MuteCommandExecutor implements TabExecutor
{
    private final AsshatModuleConfiguration configuration;

    /**
     * Create a new mute/gag command executor.
     *
     * @param module The owning module.
     */
    public MuteCommandExecutor(final AsshatModule module)
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

        final String muteReason = arguments.getReason().isEmpty() ? configuration.getDefaultAsshatReason() : arguments.getReason();

        if (arguments.isForceName())
        {
            safeMute(arguments.getPlayerName(), muteReason, commandSender, arguments.isSilent());
            return true;
        }

        final List<Player> players = Bukkit.matchPlayer(arguments.getPlayerName());
        if (players.size() < 1)
        {
            commandSender.sendMessage(String.format("Could not find any player named like %s. Append the -force parameter to mute offline players.", arguments.getPlayerName()));
            return true;
        }

        if (players.size() > 1)
        {
            commandSender.sendMessage("Found multiple players matching the name (use the -force flag if you entered the exact player name)" + arguments.getPlayerName());
            String list = "";
            for (Player player : players)
            {
                list += player.getName() + ", ";
            }
            list = list.substring(0, list.length() - 1);

            commandSender.sendMessage(list);
            return true;
        }

        safeMute(players.get(0).getName(), muteReason, commandSender, arguments.isSilent());
        return true;
    }

    private void safeMute(final String playerName, final String muteReason, final CommandSender commandSender, final boolean silentFlag)
    {
        if (Mutelist.isPlayerMuted(playerName))
        {
            commandSender.sendMessage(String.format("Player %s is already gagged.", playerName));
            return;
        }

        try
        {
            Mutelist.mute(playerName, muteReason);
            Bukkit.getLogger().info(String.format("%s gagged by %s for %s", playerName, commandSender.getName(), muteReason));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(AsshatModule.formatBroadcastMessage(configuration.getGagBroadcastMsg(), playerName, commandSender.getName(), muteReason));
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
