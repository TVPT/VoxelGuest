package com.thevoxelbox.voxelguest.modules.afk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author keto23
 */
public class PlayerListener implements Listener
{
    private AFKModule afkModule; 
	    
    public PlayerListener(AFKModule afkModule)
    {
	this.afkModule = afkModule;
    }
    
    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent event)
    {
        checkAFK(event.getPlayer().getName(), false);
    }
    
    @EventHandler
    public final void onPlayerKick(PlayerKickEvent event)
    {
	checkAFK(event.getPlayer().getName(), false);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public final void onPlayerChat(AsyncPlayerChatEvent event)
    {
	checkAFK(event.getPlayer().getName(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerMove(PlayerMoveEvent event)
    {
	checkAFK(event.getPlayer().getName(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerInteract(PlayerInteractEvent event)
    {
	checkAFK(event.getPlayer().getName(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerDropEvent(PlayerDropItemEvent event)
    {
	checkAFK(event.getPlayer().getName(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEggThrowEvent(PlayerEggThrowEvent event)
    {
	checkAFK(event.getPlayer().getName(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerRespawnEvent(PlayerRespawnEvent event)
    {
	checkAFK(event.getPlayer().getName(), true);
    }
    
    private void checkAFK(String name, boolean broadcast)
    {
	if (afkModule.isAFK(name))
	{
	    if (broadcast)
	    {
		afkModule.toggleAFK(name, "has returned");
	    }
	    else
	    {
		afkModule.silentToggleAFK(name);
	    }
	    
	}
    }	  
}
