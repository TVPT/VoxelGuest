package com.thevoxelbox.voxelguest.modules.general.command;

import com.google.common.base.Joiner;
import com.thevoxelbox.voxelguest.modules.general.AfkMessage;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author TheCryoknight
 */
public final class AddAfkMessageCommandExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (args.length >= 1)
        {
            Persistence.getInstance().save(new AfkMessage(Joiner.on(" ").join(args)));
            sender.sendMessage(ChatColor.GRAY + "Message sucessfully added!");
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Please provide an Afk message to add.");
        }
        return false;
    }
}
