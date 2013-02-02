package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Monofraps
 */
public class UnbanCommandExecutor implements CommandExecutor
{
	private AsshatModule module;

	/**
	 *
	 * @param module The owning module.
	 */
	public UnbanCommandExecutor(final AsshatModule module)
	{
		this.module = module;
	}

	@Override
	public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
	{
		if(!commandSender.hasPermission("voxelguest.asshat.unban")) {
			commandSender.sendMessage("You don't have permissions.");
			return true;
		}

		if (args.length < 1)
		{
			commandSender.sendMessage("You need to specify the name of the player to unban.");
			return false;
		}

		final String playerName = args[0].toLowerCase();
		boolean silentFlag = false;

		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si") || arg.equalsIgnoreCase("-s"))
			{
				silentFlag = true;
			}
		}

		safeUnban(playerName, commandSender, silentFlag);

		return true;
	}

	private void safeUnban(final String playerName, final CommandSender commandSender, final boolean silentFlag)
	{
		try
		{
			module.getBanlist().unban(playerName);
			Bukkit.getLogger().info(String.format("%s got unbanned by %s", playerName, commandSender.getName()));
			if (!silentFlag)
			{
				Bukkit.broadcastMessage(String.format("%s got unbanned by %s", playerName, commandSender.getName()));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			commandSender.sendMessage(String.format("Something went wrong: %s", ex.getMessage()));
		}
	}
}
