package com.thevoxelbox.voxelguest.modules.general.command;

import com.thevoxelbox.voxelguest.VoxelGuest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 *
 */
public final class VpgCommandExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        if (args.length >= 2)
        {
            List<Player> matchPlayer = Bukkit.matchPlayer(args[0]);
            if (matchPlayer.size() == 1)
            {
                Player match = matchPlayer.get(0);
                for (final String groupName : VoxelGuest.getPerms().getGroups())
                {
                    if (groupName.equalsIgnoreCase(args[1]))
                    {
                        for (final String oldGroupName : VoxelGuest.getPerms().getPlayerGroups(match))
                        {
                            VoxelGuest.getPerms().playerRemoveGroup(match, oldGroupName);
                        }

                        VoxelGuest.getPerms().playerAddGroup(match, groupName);
                        sender.sendMessage(ChatColor.GREEN + "Group Sucessfully Changed!");
                        return true;
                    }
                }
                sender.sendMessage("No group found by name \"" + args[1] + "\"");
                return true;
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
        }
        else
        {
            sender.sendMessage("Incorect syntax proper syntax is /vpg <Player name> <New rank>");
            return true;
        }
    }
}
