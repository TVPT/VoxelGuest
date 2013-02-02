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
	
	if (!afkModule.isAFK(player))
	{
	    if (args.length == 0)
	    {
		afkModule.broadcastAFKMessage(player, "");
	    }
	    else
	    {
		afkModule.broadcastAFKMessage(player, StringUtils.join(args, " "));
	    }
	}
	else
	{
	    afkModule.broadcastAFKMessage(player, "has returned");
	}
	
	afkModule.toggleAFK(player);
	
	return true;

    }
    
}
