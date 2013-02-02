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
     * @param afkModule AFKModule
     */
    public AFKCommand(final AFKModule afkModule) 
    {
		this.afkModule = afkModule;
    }

    /**
     * @see CommandExecutor
     */
    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) 
    {
		if (!(sender instanceof Player))
		{
	        sender.sendMessage("You can't go afk in the console!");
	        return true;
		}
	
		String name = sender.getName();
	
		afkModule.toggleAFK(name, true, StringUtils.join(args, " "));
	
		return true;
    }
    
}
