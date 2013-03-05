package com.thevoxelbox.voxelguest.modules.general.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thevoxelbox.voxelguest.modules.general.AfkMessage;
import com.thevoxelbox.voxelguest.persistence.Persistence;

/**
 * @author TheCryoknight
 */
public class AddAfkMessageCommandExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (args.length >= 1)
        {
            Persistence.getInstance().save(new AfkMessage(this.compileArgs(args)));
            sender.sendMessage(ChatColor.GRAY + "Message sucessfully added!");
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Please provide an Afk message to add.");
        }
        return false;
    }

    public String compileArgs(final String[] args)
    {
        String newStr = args[0];
        for (int i = 1; i < args.length; i++)
        {
            newStr += " ";
            newStr += args[i];
        }
        return newStr;
    }
}
