package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Monofraps
 */
public class FreezeCommandExecutor implements CommandExecutor
{
	private AsshatModule module;

	/**
	 *
	 * @param module The owning module.
	 */
	public FreezeCommandExecutor(final AsshatModule module)
	{
		this.module = module;
	}

	@Override
	public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings)
	{
		if(!commandSender.hasPermission("voxelguest.asshat.freeze")) {
			commandSender.sendMessage("You don't have permissions.");
			return true;
		}

		module.setFreezeEnabled(!module.isFreezeEnabled());

		return true;
	}
}
