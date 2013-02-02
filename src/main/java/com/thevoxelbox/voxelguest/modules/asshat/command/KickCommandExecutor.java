package com.thevoxelbox.voxelguest.modules.asshat.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Monofraps
 */
public class KickCommandExecutor implements CommandExecutor
{
	@Override
	public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
	{
		if(!commandSender.hasPermission("voxelguest.asshat.kick")) {
			commandSender.sendMessage("You don't have permissions.");
			return true;
		}

		if (args.length < 1)
		{
			commandSender.sendMessage("You must at least specify the name of the player to kick.");
			return false;
		}

		final String playerName = args[0].toLowerCase();
		boolean forceNameFlag = false;
		boolean silentFlag = false;
		String kickReason = "TODO!";

		for (String arg : args)
		{

			if (arg.equalsIgnoreCase("-force") || arg.equalsIgnoreCase("-f"))
			{
				forceNameFlag = true;
			}

			if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si") || arg.equalsIgnoreCase("-s"))
			{
				silentFlag = true;
			}

		}

		if (forceNameFlag)
		{
			if (Bukkit.getPlayerExact(playerName) != null)
			{
				safeKick(Bukkit.getPlayerExact(playerName), kickReason, commandSender, silentFlag);
				return true;
			}
			else
			{
				commandSender.sendMessage("Could not find any player named like " + playerName);
				return true;
			}
		}

		final List<Player> players = Bukkit.matchPlayer(playerName);
		if (players.size() < 0)
		{
			commandSender.sendMessage("Could not find any player named like " + playerName);
			return true;
		}

		if (players.size() > 1)
		{
			commandSender.sendMessage("Found multiple player matching the name (use the -force flag if you entered the exact player name)" + playerName);
			String list = "";
			for (Player player : players)
			{
				list += player.getName() + ", ";
			}
			list = list.substring(0, list.length() - 1);

			commandSender.sendMessage(list);
			return true;
		}

		safeKick(players.get(0), kickReason, commandSender, silentFlag);

		return true;
	}

	private void safeKick(final Player player, final String reason, final CommandSender sender, final boolean silentFlag)
	{
		player.kickPlayer(reason);

		Bukkit.getLogger().info(String.format("%s got kicked by %s for %s", player.getName(), sender.getName(), reason));
		if (!silentFlag)
		{
			Bukkit.broadcastMessage(String.format("%s got kicked by %s for %s", player.getName(), sender.getName(), reason));
		}
	}
}
