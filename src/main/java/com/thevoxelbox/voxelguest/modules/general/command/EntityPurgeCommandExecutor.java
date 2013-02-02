package com.thevoxelbox.voxelguest.modules.general.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.thevoxelbox.voxelguest.modules.general.GeneralModule;

public class EntityPurgeCommandExecutor implements CommandExecutor {
	private GeneralModule module;
	
	public EntityPurgeCommandExecutor(final GeneralModule generalModule) {
		this.module = generalModule;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(module.ENTITY_PURGE_PERM)) {
			return false;
		}
		for(String s: args) {
			String worldName = s;
			World world = Bukkit.getWorld(worldName);
			if(world != null) {
				sender.sendMessage(ChatColor.RED + "Purging entities from " + world.getName());
				for(Entity e: world.getEntities()) {
					if(e.getType().equals(EntityType.ITEM_FRAME) || e.getType().equals(EntityType.PAINTING) || e.getType().equals(EntityType.PLAYER)) {
						//skip
					} else {
						e.remove();
					}
				}
			}
		}
		
		return true;
	}
}
