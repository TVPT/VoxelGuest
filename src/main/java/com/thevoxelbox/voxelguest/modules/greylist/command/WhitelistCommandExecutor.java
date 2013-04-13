package com.thevoxelbox.voxelguest.modules.greylist.command;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistConfiguration;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles /whitelist commands.
 */
public final class WhitelistCommandExecutor implements TabExecutor
{
    private final GreylistConfiguration moduleConfiguration;

    /**
     * Creats a new whitelist command executor instance.
     *
     * @param module The owning module.
     */
    public WhitelistCommandExecutor(final GreylistModule module)
    {
        this.moduleConfiguration = (GreylistConfiguration) module.getConfiguration();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (args.length == 1)
        {
            final List<Player> matchPlayer = Bukkit.matchPlayer(args[0]);
            if (matchPlayer.size() == 1)
            {
                final Player match = matchPlayer.get(0);
                for (final String groupName : VoxelGuest.getPerms().getGroups())
                {
                    if (groupName.equalsIgnoreCase(moduleConfiguration.getWhitelistGroupName()))
                    {
                        for (final String oldGroupName : VoxelGuest.getPerms().getPlayerGroups(match))
                        {
                            VoxelGuest.getPerms().playerRemoveGroup(match, oldGroupName);
                        }

                        VoxelGuest.getPerms().playerAddGroup(match, groupName);
                        Bukkit.broadcastMessage("§aWhitelisted: §6" + match.getName());
                        break;
                    }
                }
            }
            else if (matchPlayer.size() > 1)
            {
                sender.sendMessage("Multiple Player matches for \"" + args[0] + "\"");
                return true;
            }
            else
            {
                sender.sendMessage("No player matches found for \"" + args[0] + "\"");
                return true;
            }

            String header = "";
            final HashMap<String, List<String>> groups = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers())
            {
                final String group = VoxelGuest.getPerms().getPrimaryGroup(player);
                final List<String> names = new ArrayList<>();

                if (groups.containsKey(group))
                {
                    names.addAll(groups.get(group));
                }

                names.add(player.getDisplayName());
                groups.put(group, names);

            }
            for (final String groupName : groups.keySet())
            {
                header += ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + groupName.substring(0, 1).toUpperCase() + ":" + groups.get(groupName).size() + ChatColor.DARK_GRAY + "] ";
            }

            final int numOnlinePlayers = Bukkit.getOnlinePlayers().length;
            header += ChatColor.DARK_GRAY + "(" + ChatColor.WHITE + "O:" + String.valueOf(numOnlinePlayers) + ChatColor.DARK_GRAY + ")";
            Bukkit.broadcastMessage(header);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        return null;
    }
}
