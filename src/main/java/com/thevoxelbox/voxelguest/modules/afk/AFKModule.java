package com.thevoxelbox.voxelguest.modules.afk;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author keto23
 */
public class AFKModule extends GuestModule 
{
    private AFKCommand afkCommand;
    private PlayerListener afkListener;
    private Set<Player> afkPlayers = new HashSet<>();
    
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
	final HashSet<Listener> listeners = new HashSet<>();
	listeners.add(afkListener);

	return listeners;
    }
    
    public final void toggleAFK(Player player) {
	if (isAFK(player))
	{
	    afkPlayers.remove(player);
	}
	else
	{
	    afkPlayers.add(player);
	}
    }

    public final boolean isAFK(Player player) {
	return afkPlayers.contains(player);
    }
    
    public final void broadcastAFKMessage(Player player, String message)
    {
	String name = player.getName();
	String reason = message;
	
	if (reason.equalsIgnoreCase(""))
	{
	    reason = "has gone AFK";
	}
	
	Bukkit.broadcastMessage(String.format(ChatColor.DARK_AQUA + "%s" + ChatColor.DARK_GRAY + "%s", name, reason));
    }
}
