package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.commands.ImportCommandExecutor;
import com.thevoxelbox.voxelguest.commands.ModulesCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.ban.Banlist;
import com.thevoxelbox.voxelguest.modules.asshat.mute.Mutelist;
import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import com.thevoxelbox.voxelguest.modules.helper.HelperModule;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import net.milkbowl.vault.permission.Permission;
import org.apache.log4j.PropertyConfigurator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author MikeMatrix
 * @author Monofraps
 */
public class VoxelGuest extends JavaPlugin
{
    private static VoxelGuest         pluginInstance        = null;
    private static GuestModuleManager moduleManagerInstance = null;
    private static Permission         perms                 = null;

    /**
     * Returns the VoxelGuest plugin instance.
     *
     * @return Returns the VoxelGuest plugin class instance.
     */
    public static VoxelGuest getPluginInstance()
    {
        return VoxelGuest.pluginInstance;
    }

    private static void setPluginInstance(final VoxelGuest pluginInstance)
    {
        if (VoxelGuest.pluginInstance != null)
        {
            throw new RuntimeException("VoxelGuest plugin instance already set.");
        }

        VoxelGuest.pluginInstance = pluginInstance;
    }

    /**
     * Returns the module manager instance.
     *
     * @return Returns the module manager instance.
     */
    public static GuestModuleManager getModuleManagerInstance()
    {
        return moduleManagerInstance;
    }

    private static void setModuleManagerInstance(final GuestModuleManager moduleManagerInstance)
    {
        if (VoxelGuest.moduleManagerInstance != null)
        {
            throw new RuntimeException("VoxelGuest module manger instance already set.");
        }

        VoxelGuest.moduleManagerInstance = moduleManagerInstance;
    }

    /**
     * Gets the permission manager provided by Vault.
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

    /**
     * @return Returns true if a permission provider is available.
     */
    public static boolean hasPermissionProvider()
    {
        return VoxelGuest.perms != null;
    }

    @Override
    public final void onDisable()
    {
        getCommand("vmodules").setExecutor(null);
        getCommand("vgimport").setExecutor(null);
        VoxelGuest.getModuleManagerInstance().shutdown();

        try
        {
            Persistence.getInstance().shutdown();
        }
        catch (SQLException e)
        {
            Bukkit.getLogger().severe("Failed to finalize persistence system.");
            e.printStackTrace();
        }
    }

    /**
     * Enables the plugin.
     */
    @Override
    public final void onEnable()
    {
        Properties logProps = new Properties();
        logProps.put("log4j.rootLogger", "ERROR, stdout");
        logProps.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        logProps.put("log4j.appender.stdout.layout", "org.apache.log4j.SimpleLayout");
        logProps.put("log4j.logger.com.j256.ormlite", "ERROR");
        PropertyConfigurator.configure(logProps);

        VoxelGuest.setPluginInstance(this);
        VoxelGuest.setModuleManagerInstance(new GuestModuleManager());

        try
        {
            Persistence.getInstance().initialize(new File(getDataFolder(), "persistence.db"));
        }
        catch (SQLException e)
        {
            Bukkit.getLogger().severe("Failed to initialize persistence system.");
            e.printStackTrace();
        }

        if (!setupPermissions())
        {
            Bukkit.getLogger().severe("Failed to setup Vault, due to no dependency found!"); //Should stop?
        }

        VoxelGuest.getModuleManagerInstance().autoRegisterModulesInPackage("com.thevoxelbox.voxelguest.modules");

        getCommand("vmodules").setExecutor(new ModulesCommandExecutor());
        getCommand("vgimport").setExecutor(new ImportCommandExecutor());

        try
        {
            startMetrics();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void startMetrics() throws IOException
    {
        Metrics metrics = new Metrics(this);

        Metrics.Graph asshatGraph = metrics.createGraph("Asshat Statistics");
        asshatGraph.addPlotter(new Metrics.Plotter("Banned Players")
        {
            @Override
            public int getValue()
            {
                return Banlist.getBanCount();
            }
        });

        asshatGraph.addPlotter(new Metrics.Plotter("Muted Players")
        {
            @Override
            public int getValue()
            {
                return Mutelist.getMuteCount();
            }
        });

        Metrics.Graph moduleGraph = metrics.createGraph("Enabled Modules");
        moduleGraph.addPlotter(new Metrics.Plotter("Asshat Module")
        {
            @Override
            public int getValue()
            {
                try {
                    return getModuleManagerInstance().findStateContainer(AsshatModule.class).isEnabled() ? 1 : 0;
                } catch (final IllegalStateException ignored) {
                    return 0;
                }
            }
        });

        moduleGraph.addPlotter(new Metrics.Plotter("General Module")
        {
            @Override
            public int getValue()
            {
                try {
                    return getModuleManagerInstance().findStateContainer(GeneralModule.class).isEnabled() ? 1 : 0;
                } catch (final IllegalStateException ignored) {
                    return 0;
                }
            }
        });

        moduleGraph.addPlotter(new Metrics.Plotter("Greylist Module")
        {
            @Override
            public int getValue()
            {
                try {
                    return getModuleManagerInstance().findStateContainer(GreylistModule.class).isEnabled() ? 1 : 0;
                } catch (final IllegalStateException ignored) {
                    return 0;
                }
            }
        });

        moduleGraph.addPlotter(new Metrics.Plotter("Helper Module")
        {
            @Override
            public int getValue()
            {
                try {
                    return getModuleManagerInstance().findStateContainer(HelperModule.class).isEnabled() ? 1 : 0;
                } catch (final IllegalStateException ignored) {
                    return 0;
                }
            }
        });

        metrics.start();
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null)
        {
            Bukkit.getLogger().severe("Cannot find permission service provider. Check that a permission system and Vault are installed.");
            return false;
        }

        setPerms(rsp.getProvider());
        return VoxelGuest.hasPermissionProvider();
    }
}
