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
public class AFKListener implements Listener
{
    private AFKModule afkModule; 
	    
    public AFKListener(AFKModule afkModule)
    {
	this.afkModule = afkModule;
    }
    
    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent event)
    {
        if (afkModule.afkList.isAFK(event.getPlayer()))
	{
	    afkModule.afkList.toggleAFK(event.getPlayer());
	}
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public final void onPlayerChat(AsyncPlayerChatEvent event)
    {
	if (afkModule.afkList.isAFK(event.getPlayer()))
	{
	    afkModule.afkList.toggleAFK(event.getPlayer());
	    
	    Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.DARK_GRAY + " has returned");
	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerMove(PlayerMoveEvent event)
    {
	if (afkModule.afkList.isAFK(event.getPlayer()))
	{
	    afkModule.afkList.toggleAFK(event.getPlayer());
	    
	    Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.DARK_GRAY + " has returned");
	}
    }
    
    
}
