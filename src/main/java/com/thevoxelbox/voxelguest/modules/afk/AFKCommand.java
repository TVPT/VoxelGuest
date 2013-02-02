package com.thevoxelbox.voxelguest.modules.afk;

import org.apache.commons.lang.StringUtils;
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
    
    /**
     *
     * @param afkModule
     */
    public AFKCommand(AFKModule afkModule) 
    {
	this.afkModule = afkModule;
    }

    /**
     * @see org.bukkit.command.CommandExecutor
     */
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
	if (!(sender instanceof Player))
	{
	    sender.sendMessage("You can't go afk in the console!");
	    return true;
	}
	
	String name = sender.getName();
	
	afkModule.toggleAFK(name, (afkModule.isAFK(name) ? "has returned" : StringUtils.join(args, " ")));
	
	return true;

    }
    
}
