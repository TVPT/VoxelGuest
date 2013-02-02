package com.thevoxelbox.voxelguest.modules.afk;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author keto23
 */
public class AFKCommand implements CommandExecutor
{
    private AFKModule afkModule;
    
    public AFKCommand(AFKModule afkModule) {
	this.afkModule = afkModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (!(sender instanceof Player))
	{
	    sender.sendMessage("You can't go afk in the console!");
	    return true;
	}
	
	Player player = (Player) sender;
	
	if (!afkModule.afkList.isAFK(player))
	{
	    if (args.length == 0)
	    {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.DARK_GRAY + " has gone AFK");
	    }
	    else
	    {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.DARK_GRAY + " " + ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, " ")));
	    }
	}
	else
	{
	    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.DARK_GRAY + " has returned");
	}
	
	afkModule.afkList.toggleAFK(player);
	
	return true;

    }
    
}
