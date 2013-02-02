package com.thevoxelbox.voxelguest;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import com.thevoxelbox.voxelguest.persistence.Persistence;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author MikeMatrix
 * @author Monofraps
 */
public class VoxelGuest extends JavaPlugin
{
    private static VoxelGuest pluginInstance = null;
    private static ModuleManager moduleManagerInstance = null;
	
	private static Permission perms = null; //vault perms

    public static VoxelGuest getPluginInstance()
    {
        return VoxelGuest.pluginInstance;
    }

    private static void setPluginInstance(VoxelGuest pluginInstance)
    {
        if (VoxelGuest.pluginInstance != null)
        {
            throw new RuntimeException("Guest Plugin Instance already set.");
        }

        VoxelGuest.pluginInstance = pluginInstance;
    }

    public static ModuleManager getModuleManagerInstance()
    {
        return moduleManagerInstance;
    }

    public static void setModuleManagerInstance(final ModuleManager moduleManagerInstance)
    {
        if (VoxelGuest.moduleManagerInstance != null)
        {
            throw new RuntimeException("Guest Module Manger Instance already set.");
        }

        VoxelGuest.moduleManagerInstance = moduleManagerInstance;
    }

    @Override
    public void onLoad()
    {
        Persistence.getInstance().initialize(new File(getDataFolder(), "persistence.db"));
    }

    @Override
    public void onDisable()
    {
        VoxelGuest.getModuleManagerInstance().shutdown();
    }

    @Override
    public void onEnable()
    {
	    //if(!setupPermissions()) {
		//    Bukkit.getLogger().severe("Failed to setup Vault, due to no dependency found!"); //Should stop?
	    //}

        VoxelGuest.setPluginInstance(this);
        VoxelGuest.setModuleManagerInstance(new ModuleManager());

        VoxelGuest.getModuleManagerInstance().registerGuestModule(new RegionModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new AsshatModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new GreylistModule(), false);

	    Persistence.getInstance().rebuildSessionFactory();

	    VoxelGuest.getModuleManagerInstance().enableModuleByType(RegionModule.class);
	    VoxelGuest.getModuleManagerInstance().enableModuleByType(AsshatModule.class);
	    VoxelGuest.getModuleManagerInstance().enableModuleByType(GreylistModule.class);
    }

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

    static
    {
        Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
    }
}
