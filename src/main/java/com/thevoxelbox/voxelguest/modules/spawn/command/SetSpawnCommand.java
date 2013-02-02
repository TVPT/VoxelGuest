package com.thevoxelbox.voxelguest.modules.spawn.command;

import com.thevoxelbox.voxelguest.modules.spawn.SpawnModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 * @author keto23
 */
public class SetSpawnCommand implements CommandExecutor
{
	private SpawnModule spawnModule;

	public SetSpawnCommand(final SpawnModule spawnModule)
	{
		this.spawnModule = spawnModule;
	}

	@Override 
	public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
	{
		if (!(commandSender instanceof Player))
		{
			commandSender.sendMessage("You can't set the spawn location from the console! Silly :P");
			return true;
		}

		Player player = (Player) commandSender;


		spawnModule.setSpawnLocation(player.getLocation());

		player.sendMessage(ChatColor.AQUA + "Spawn location set");
		return false;
	}
}
