package com.thevoxelbox.voxelguest;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import com.thevoxelbox.voxelguest.modules.helper.HelperModule;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

/**
 * @author MikeMatrix
 * @author Monofraps
 */
public class VoxelGuest extends JavaPlugin
{
    private static VoxelGuest pluginInstance = null;
    private static ModuleManager moduleManagerInstance = null;
    private static Permission perms = null;

    public static VoxelGuest getPluginInstance()
    {
        return VoxelGuest.pluginInstance;
    }

    private static void setPluginInstance(final VoxelGuest pluginInstance)
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

    private static void setModuleManagerInstance(final ModuleManager moduleManagerInstance)
    {
        if (VoxelGuest.moduleManagerInstance != null)
        {
            throw new RuntimeException("Guest Module Manger Instance already set.");
        }

        VoxelGuest.moduleManagerInstance = moduleManagerInstance;
    }

    /**
     * Gets the permission manager provided by vault.
     *
     * @return permission manager
     */
    public static Permission getPerms()
    {
        return perms;
    }

    private static void setPerms(final Permission perms)
    {
        VoxelGuest.perms = perms;
    }

    @Override
    public void onDisable()
    {
        try
        {
            Persistence.getInstance().shutdown();
        } catch (SQLException e)
        {
            Bukkit.getLogger().severe("Failed to finalize persistence system.");
            e.printStackTrace();
        }

        VoxelGuest.getModuleManagerInstance().shutdown();

    }

    @Override
    public void onEnable()
    {
        try
        {
            Persistence.getInstance().initialize(new File(getDataFolder(), "persistence2.db"));
        } catch (SQLException e)
        {
            Bukkit.getLogger().severe("Failed to initialize persistence system.");
            e.printStackTrace();
        }

        if (!setupPermissions())
        {
            Bukkit.getLogger().severe("Failed to setup Vault, due to no dependency found!"); //Should stop?
        }

        VoxelGuest.setPluginInstance(this);
        VoxelGuest.setModuleManagerInstance(new ModuleManager());

        VoxelGuest.getModuleManagerInstance().registerGuestModule(new RegionModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new AsshatModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new GreylistModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new GeneralModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new HelperModule(), false);

        VoxelGuest.getModuleManagerInstance().enableModuleByType(RegionModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(AsshatModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(GreylistModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(GeneralModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(HelperModule.class);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command,
            final String label, final String[] args)
    {
        Preconditions.checkNotNull(sender);
        sender.sendMessage("Command disabled or unimplemented!");
        return true;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        setPerms(rsp.getProvider());
        return VoxelGuest.getPerms() != null;
    }
}
