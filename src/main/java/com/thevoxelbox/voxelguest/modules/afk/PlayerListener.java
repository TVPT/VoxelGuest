package com.thevoxelbox.voxelguest.modules.afk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        if (afkModule.isAFK(event.getPlayer()))
	{
	    afkModule.toggleAFK(event.getPlayer());
	}
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public final void onPlayerChat(AsyncPlayerChatEvent event)
    {
	if (afkModule.isAFK(event.getPlayer()))
	{
	    afkModule.toggleAFK(event.getPlayer());
	    
	    Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.DARK_GRAY + " has returned");
	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerMove(PlayerMoveEvent event)
    {
	if (afkModule.isAFK(event.getPlayer()))
	{
	    afkModule.toggleAFK(event.getPlayer());
	    
	    Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.DARK_GRAY + " has returned");
	}
    }
    
    
}
