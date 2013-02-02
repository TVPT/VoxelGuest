package com.thevoxelbox.voxelguest.modules.general.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thevoxelbox.voxelguest.modules.general.GeneralModule;

public class FakequitCommandExecutor implements CommandExecutor {
	private GeneralModule module;
	
	public FakequitCommandExecutor(final GeneralModule generalModule) {
		this.module = generalModule;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		module.fakequitPlayer(sender);
		return true;
	}
}
