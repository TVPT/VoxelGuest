package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Monofraps
 */
public class BanCommandExecutor implements CommandExecutor
{
	private AsshatModule module;

	/**
	 *
	 * @param module The owning module.
	 */
	public BanCommandExecutor(final AsshatModule module)
	{
		this.module = module;
	}

	@Override
	public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
	{
		if(!commandSender.hasPermission("voxelguest.asshat.ban")) {
			commandSender.sendMessage("You don't have permissions.");
			return true;
		}

		if (args.length < 1)
		{
			commandSender.sendMessage("You must at least specify the name of the player to ban.");
			return false;
		}

		final String playerName = args[0].toLowerCase();
		boolean forceNameFlag = false;
		boolean silentFlag = false;
		String banReason = "TODO!";

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
			safeBan(playerName, banReason, commandSender, silentFlag);
			return true;
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

		players.get(0).kickPlayer(banReason);
		safeBan(players.get(0).getName(), banReason, commandSender, silentFlag);

		return true;
	}

	private void safeBan(final String playerName, final String banReason, final CommandSender commandSender, final boolean silentFlag)
	{
		try
		{
			module.getBanlist().ban(playerName, banReason);
			Bukkit.getLogger().info(String.format("%s got banned for %s by %s", playerName, banReason, commandSender.getName()));
			if (!silentFlag)
			{
				Bukkit.broadcastMessage(String.format("%s got banned for %s by %s", playerName, banReason, commandSender.getName()));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			commandSender.sendMessage(String.format("Something went wrong: %s", ex.getMessage()));
		}
	}
}
