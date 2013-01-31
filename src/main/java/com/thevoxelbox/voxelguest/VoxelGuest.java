package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import org.bukkit.plugin.java.JavaPlugin;

public class VoxelGuest extends JavaPlugin
{
	private static VoxelGuest pluginInstance = null;
	private static ModuleManager moduleManagerInstance = null;

	@Override
	public void onEnable() {
		VoxelGuest.setPluginInstance(this);
		VoxelGuest.setModuleManagerInstance(new ModuleManager());

                VoxelGuest.getModuleManagerInstance().registerGuestModule(new RegionModule(), false);
	}

	@Override
	public void onDisable() {
		VoxelGuest.getModuleManagerInstance().shutdown();
	}

	private static void setPluginInstance(VoxelGuest pluginInstance) {
		if(VoxelGuest.pluginInstance != null) {
			throw new RuntimeException("Guest Plugin Instance already set.");
		}

		VoxelGuest.pluginInstance = pluginInstance;
	}

	public static VoxelGuest getPluginInstance() {
		return VoxelGuest.pluginInstance;
	}

	public static ModuleManager getModuleManagerInstance()
	{
		return moduleManagerInstance;
	}

	public static void setModuleManagerInstance(final ModuleManager moduleManagerInstance)
	{
		if(VoxelGuest.moduleManagerInstance != null) {
			throw new RuntimeException("Guest Module Manger Instance already set.");
		}

		VoxelGuest.moduleManagerInstance = moduleManagerInstance;
	}
}
