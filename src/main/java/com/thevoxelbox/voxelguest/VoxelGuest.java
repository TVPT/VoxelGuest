package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.commands.ImportCommandExecutor;
import com.thevoxelbox.voxelguest.commands.ModulesCommandExecutor;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.ban.Banlist;
import com.thevoxelbox.voxelguest.modules.asshat.mute.Mutelist;
import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import com.thevoxelbox.voxelguest.modules.helper.HelperModule;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author MikeMatrix
 * @author Monofraps
 */
public class VoxelGuest extends JavaPlugin
{
    private static VoxelGuest pluginInstance = null;
    private static ModuleManager moduleManagerInstance = null;
    private static Permission perms = null;

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
            throw new RuntimeException("Guest Plugin Instance already set.");
        }

        VoxelGuest.pluginInstance = pluginInstance;
    }

    /**
     * Returns the module manager instance.
     *
     * @return Returns the module manager instance.
     */
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

    @Override
    public final void onDisable()
    {
        try
        {
            Persistence.getInstance().shutdown();
        }
        catch (SQLException e)
        {
            Bukkit.getLogger().severe("Failed to finalize persistence system.");
            e.printStackTrace();
        }

        getCommand("vmodules").setExecutor(null);
        getCommand("vgimport").setExecutor(null);
        VoxelGuest.getModuleManagerInstance().shutdown();

    }

    @Override
    public final void onEnable()
    {
        try
        {
            Metrics metrics = new Metrics(this);

            Metrics.Graph asshatGraph = metrics.createGraph("Asshat Statistics");
            asshatGraph.addPlotter(new Metrics.Plotter("Banned Players")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (!(module instanceof AsshatModule))
                        {
                            continue;
                        }

                        final AsshatModule asshatModule = (AsshatModule) module;
                        if (!asshatModule.isEnabled())
                        {
                            return 0;
                        }

                        final Banlist banlist = asshatModule.getBanlist();
                        return banlist.getBanCount();
                    }

                    return 0;
                }
            });

            asshatGraph.addPlotter(new Metrics.Plotter("Muted Players")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (!(module instanceof AsshatModule))
                        {
                            continue;
                        }

                        final AsshatModule asshatModule = (AsshatModule) module;
                        if (!asshatModule.isEnabled())
                        {
                            return 0;
                        }

                        final Mutelist mutelist = asshatModule.getMutelist();
                        return mutelist.getMuteCount();
                    }

                    return 0;
                }
            });

            Metrics.Graph moduleGraph = metrics.createGraph("Enabled Modules");
            moduleGraph.addPlotter(new Metrics.Plotter("Asshat Module")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (module instanceof AsshatModule)
                        {
                            return 1;
                        }
                    }
                    return 0;
                }
            });

            moduleGraph.addPlotter(new Metrics.Plotter("General Module")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (module instanceof GeneralModule)
                        {
                            return 1;
                        }
                    }
                    return 0;
                }
            });

            moduleGraph.addPlotter(new Metrics.Plotter("Greylist Module")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (module instanceof GreylistModule)
                        {
                            return 1;
                        }
                    }
                    return 0;
                }
            });

            moduleGraph.addPlotter(new Metrics.Plotter("Helper Module")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (module instanceof HelperModule)
                        {
                            return 1;
                        }
                    }
                    return 0;
                }
            });

            moduleGraph.addPlotter(new Metrics.Plotter("Region Module")
            {
                @Override
                public int getValue()
                {
                    final HashMap<Module, HashSet<Listener>> modules = getModuleManagerInstance().getRegisteredModules();
                    for (Module module : modules.keySet())
                    {
                        if (module instanceof RegionModule)
                        {
                            return 1;
                        }
                    }
                    return 0;
                }
            });

            metrics.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


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

        VoxelGuest.setPluginInstance(this);
        VoxelGuest.setModuleManagerInstance(new ModuleManager());

        VoxelGuest.getModuleManagerInstance().registerGuestModule(new RegionModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new AsshatModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new GreylistModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new GeneralModule(), false);
        VoxelGuest.getModuleManagerInstance().registerGuestModule(new HelperModule(), false);

        getCommand("vmodules").setExecutor(new ModulesCommandExecutor());
        getCommand("vgimport").setExecutor(new ImportCommandExecutor());

        VoxelGuest.getModuleManagerInstance().enableModuleByType(RegionModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(AsshatModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(GreylistModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(GeneralModule.class);
        VoxelGuest.getModuleManagerInstance().enableModuleByType(HelperModule.class);
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
        return VoxelGuest.getPerms() != null;
    }
}
