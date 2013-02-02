package com.thevoxelbox.voxelguest.modules.afk;

import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationGetter;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationSetter;
import com.thevoxelbox.voxelguest.modules.GuestModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author keto23
 */
public class AFKModule extends GuestModule 
{
    private AFKCommand afkCommand;
    private PlayerListener afkListener;
    private static Set<String> afkPlayers = new HashSet<>();
    private String enterAfkFormat = ChatColor.DARK_AQUA + "%PLAYER%" + ChatColor.DARK_GRAY + " %REASON%";
    private String leaveAfkFormat = ChatColor.DARK_AQUA + "%PLAYER%" + ChatColor.DARK_GRAY + " has returned";

    /**
     *
     */
    public AFKModule()
    {
	    setName("AFK");
    }
    
    @Override
    public final void onEnable()
    {
		afkCommand = new AFKCommand(this);
		afkListener = new PlayerListener(this);
	
		super.onEnable();
    }
    
    @Override
    public final void onDisable()
    {
		afkPlayers.clear();
	
		super.onDisable();
    }
   
    @Override
    public final Object getConfiguration()
    {
        return this;
    }
    
    @Override
    public final Map<String, CommandExecutor> getCommandMappings()
    {
		final HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
		commandMappings.put("afk", afkCommand);
	
		return commandMappings;
    }
    
    @Override
    public final Set<Listener> getListeners()
    {
		final Set<Listener> listeners = new HashSet<>();
		listeners.add(afkListener);

		return listeners;
    }

	/**
	 *
	 * @return Enter afk format
	 */
    @ConfigurationGetter("enter-afk-format")
    public final String getEnterAfkFormat()
    {
        return enterAfkFormat;
    }

	/**
	 *
	 * @return Leave afk format    .
	 */
    @ConfigurationGetter("leave-afk-format")
    public final String getLeaveAfkFormat()
    {
        return leaveAfkFormat;
    }

	/**
	 *
	 * @param format Format for enter afk message.
	 */
    @ConfigurationSetter("enter-afk-format")
    public final void setEnterAfkFormat(final String format)
    {
		this.enterAfkFormat = ChatColor.translateAlternateColorCodes('&', format);
    }

	/**
	 *
	 * @param format Format for leave afk message.
	 */
    @ConfigurationSetter("leave-afk-format")
    public final void setLeaveAfkFormat(final String format)
    {
		this.leaveAfkFormat = ChatColor.translateAlternateColorCodes('&', format);
    }

    /**
     * Toggles a users AFK status
     * @param name Name of the player to toggle AFK status on
     * @param message AFK message
     */
    public final void toggleAFK(final String name, final boolean broadcast, final String message)
    {
		if (isAFK(name))
		{
	        afkPlayers.remove(name);
	        if (broadcast)
	        {
				this.broadcastAfkReturn(name);
	        }
		}
		else
		{
	        afkPlayers.add(name);
	        if (broadcast)
	        {
				this.broadcastAFKEnter(name, message);
	        }
			
		}
    }
    
    
    /**
     * Checks if a player is AFK
     * @param name Name of the player to check
     * @return Whether the player is AFK
     */
    public final boolean isAFK(final String name)
    {
		return afkPlayers.contains(name);
    }
    
	/**
     *
     * Broadcasts a formatted AFK message
     * @param name Name of the player
     * @param message Message to broadcast, if empty, defaults to "has gone AFK"
     */
    public final void broadcastAFKEnter(final String name, final String message)
    {
		String reason = message;
	
		if (reason.equalsIgnoreCase("") || reason.equalsIgnoreCase(" "))
		{
	        reason = "has gone AFK";
		}

		Bukkit.broadcastMessage(this.enterAfkFormat.replaceAll("%PLAYER%", name).replaceAll("%REASON%", reason));
    }


	/**
	 *
	 * @param name Name of player
	 */
    public final void broadcastAfkReturn(final String name)
    {
		Bukkit.broadcastMessage(this.leaveAfkFormat.replaceAll("%PLAYER%", name));
    }
}
