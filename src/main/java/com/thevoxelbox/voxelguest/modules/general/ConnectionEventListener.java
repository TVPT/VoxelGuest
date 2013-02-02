package com.thevoxelbox.voxelguest.modules.general;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionEventListener implements Listener {

	private GeneralModule module;
	
	public ConnectionEventListener(final GeneralModule generalModule) {
		this.module = generalModule;
	}
	
	@EventHandler
	public final void onPlayerJoin(final PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		if(module.ovanished.contains(p.getName())) {
			module.vanished.add(p.getName());
			module.ovanished.remove(p.getName());
			module.hidePlayerForAll(p);
		}
		
		module.hideAllForPlayer(p);
		
		if(module.ofakequit.contains(p.getName())) {
			module.fakequit.add(p.getName());
			module.ofakequit.remove(p.getName());
			event.setJoinMessage("");
		}
	}
	
	@EventHandler
	public final void onPlayerQuit(final PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		if(module.vanished.contains(p.getName())) {
			module.ovanished.add(p.getName());
			module.vanished.remove(p.getName());
		}
		
		if(module.fakequit.contains(p.getName())) {
			module.ofakequit.add(p.getName());
			module.fakequit.remove(p.getName());
			event.setQuitMessage("");
		}
	}
	
	@EventHandler
	public final void onPlayerKick(final PlayerKickEvent event) {
		Player p = event.getPlayer();
		
		if(module.vanished.contains(p.getName())) {
			module.ovanished.add(p.getName());
			module.vanished.remove(p.getName());
		}
		
		if(module.fakequit.contains(p.getName())) {
			module.ofakequit.add(p.getName());
			module.fakequit.remove(p.getName());
			event.setLeaveMessage("");
		}
	}
}
