package com.thevoxelbox.voxelguest.modules.afk;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

/**
 *
 * @author keto23
 */
public class AFKModule extends GuestModule 
{
    private AFKCommand afkCommand;
    private PlayerListener afkListener;
    private static Set<String> afkPlayers = new HashSet<>();
    
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
    public Map<String, CommandExecutor> getCommandMappings()
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
     * Toggles a users AFK status
     * @param name Name of the player to check
     * @param message AFK message
     */
    public final void toggleAFK(String name, String message) 
    {
	if (isAFK(name))
	{
	    afkPlayers.remove(name);
	}
	else
	{
	    afkPlayers.add(name);
	}
	
	broadcastAFKMessage(name, message);
    }

    /**
     * Checks if a player is AFK
     * @param name Name of the player to check
     * @return Whether the player is AFK
     */
    public final boolean isAFK(String name) 
    {
	return afkPlayers.contains(name);
    }
    
    /**
     * Broadcasts a formatted AFK message
     * @param name Name of the player
     * @param message Message to broadcast, if empty, defaults to "has gone AFK"
     */
    public final void broadcastAFKMessage(String name, String message)
    {
	String reason = message;
	
	if (reason.equalsIgnoreCase("") || reason.equalsIgnoreCase(" "))
	{
	    reason = "has gone AFK";
	}
	
	Bukkit.broadcastMessage(String.format(ChatColor.DARK_AQUA + "%s" + ChatColor.DARK_GRAY + "%s", name, reason));
    }
}
