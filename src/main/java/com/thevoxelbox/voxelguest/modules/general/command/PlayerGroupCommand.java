package com.thevoxelbox.voxelguest.modules.general.command;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.general.PlayerGroup;
import com.thevoxelbox.voxelguest.modules.general.PlayerGroupDAO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @author Monofraps
 */
public final class PlayerGroupCommand implements TabExecutor
{

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (!VoxelGuest.hasPermissionProvider())
        {
            sender.sendMessage(ChatColor.RED + "No permission provider available. Please make sure Vault and a vault-compatible permission system are installed.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("colors"))
        {
            sender.sendMessage("Available colors are:");
            for (ChatColor color : ChatColor.values())
            {
                sender.sendMessage(color + color.name());
            }
            return true;
        }

        if (args.length < 2)
        {
            sender.sendMessage("Not enough arguments.");
            return false;
        }

        final String groupName = args[0];
        final ChatColor groupColor;

        try
        {
            groupColor = ChatColor.valueOf(args[1].toUpperCase());
        }
        catch (final IllegalArgumentException ex)
        {
            ex.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not parse color " + args[1] + ". Use /vgpg colors to get a list of all available colors.");
            return true;
        }

        PlayerGroupDAO.save(new PlayerGroup(groupName, groupColor.toString()));
        sender.sendMessage(ChatColor.GREEN + "Saved group " + groupColor + groupName + ChatColor.GREEN + " to persistence database.");
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        return null;
    }
}
