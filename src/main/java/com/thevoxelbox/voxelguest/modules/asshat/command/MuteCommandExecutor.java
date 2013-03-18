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
 * Executes /mute and /gag commands.
 *
 * @author Monofraps
 */
public class MuteCommandExecutor implements TabExecutor
{
    private final AsshatModuleConfiguration configuration;
    private final AsshatModule module;

    /**
     * Create a new mute/gag command executor.
     *
     * @param module The owning module.
     */
    public MuteCommandExecutor(final AsshatModule module)
    {
        this.module = module;
        configuration = (AsshatModuleConfiguration) module.getConfiguration();
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if (args.length < 1)
        {
            commandSender.sendMessage("You must at least specify the name of the player to mute.");
            return false;
        }

        final String playerName = args[0].toLowerCase();
        boolean forceNameFlag = false;
        boolean silentFlag = false;
        String muteReason = "";

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

            muteReason += arg + " ";
        }

        if (muteReason.isEmpty())
        {
            muteReason = configuration.getDefaultAsshatReason();
        }

        if (forceNameFlag)
        {
            safeMute(playerName, muteReason, commandSender, silentFlag);
            return true;
        }

        final List<Player> players = Bukkit.matchPlayer(playerName);
        if (players.size() < 1)
        {
            commandSender.sendMessage(String.format("Could not find any player named like %s. Append the -force parameter to mute offline players.", playerName));
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

        safeMute(players.get(0).getName(), muteReason, commandSender, silentFlag);

        return true;
    }

    private void safeMute(final String playerName, final String muteReason, final CommandSender commandSender, final boolean silentFlag)
    {
        if (module.getMutelist().isPlayerMuted(playerName))
        {
            commandSender.sendMessage(String.format("Player %s is already gagged.", playerName));
            return;
        }

        try
        {
            module.getMutelist().mute(playerName, muteReason);
            Bukkit.getLogger().info(String.format("%s got gagged for %s by %s", playerName, muteReason, commandSender.getName()));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(this.module.formatBroadcastMessage(configuration.getGagBroadcastMsg(), playerName, commandSender.getName(), muteReason));
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
