package com.thevoxelbox.voxelguest.modules.spawn.command;

import com.thevoxelbox.voxelguest.modules.spawn.SpawnModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Random;

/**
 *
 * @author keto23
 */
public class SpawnCommand implements CommandExecutor
{
	private SpawnModule spawnModule;

	public SpawnCommand(final SpawnModule spawnModule)
	{
		this.spawnModule = spawnModule;
	}

	@Override
	public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
	{
		if (!(commandSender instanceof Player))
		{
			commandSender.sendMessage("You can't teleport to spawn from the console! Derp!");
			return true;
		}

		Player player = (Player) commandSender;

		if (spawnModule.getSpawnLocation() == null)
		{
			player.sendMessage(ChatColor.RED + "Spawn has not been set yet!");
			return true;
		}

		player.teleport((Location) spawnModule.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);


		String[] messages = spawnModule.getRandomMessages().split(",");
		if (messages.length > 0 && spawnModule.areRandomMessagesEnabled())
		{
			String message = messages[new Random().nextInt((messages.length - 1))];
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}

		return true;

	}
}
