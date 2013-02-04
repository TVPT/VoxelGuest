package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModuleConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Monofraps
 */
public class UnmuteCommandExecutor implements CommandExecutor
{
    private AsshatModule module;
    private final AsshatModuleConfiguration configuration;

    /**
     *
     * @param module The owning module.
     */
    public UnmuteCommandExecutor(final AsshatModule module)
    {
        this.module = module;

        Preconditions.checkState(module.getConfiguration() instanceof AsshatModuleConfiguration);
        configuration = (AsshatModuleConfiguration)module.getConfiguration();
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if(!commandSender.hasPermission("voxelguest.asshat.unmute")) {
            commandSender.sendMessage("You don't have permissions.");
            return true;
        }

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

        if(!module.getMutelist().isPlayerMuted(playerName)) {
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
            module.getMutelist().unmute(playerName);
            Bukkit.getLogger().info(String.format("%s got unmuted by %s", playerName, commandSender.getName()));
            if (!silentFlag)
            {
                Bukkit.broadcastMessage(this.module.formatBroadcastMessage(configuration.getUngagBroadcastMsg(), playerName, commandSender.getName(), "", false));
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
            commandSender.sendMessage(String.format("Something went wrong: %s", ex.getMessage()));
        }
    }
}
