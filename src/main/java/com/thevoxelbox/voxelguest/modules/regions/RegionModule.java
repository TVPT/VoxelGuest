package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.RegionProvider;
import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.regions.command.RegionCommand;
import com.thevoxelbox.voxelguest.modules.regions.listener.BukkitEventListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Butters
 */
public final class RegionModule extends GuestModule
{
    private final BukkitEventListener bukkitEventListener;
    private RegionProvider regionProvider = new GuestRegionDAO();

    /**
     * Creates a new RegionModule instance.
     */
    public RegionModule()
    {
        this.setName("Region Module");

        bukkitEventListener = new BukkitEventListener();
    }

    @Override
    public void onEnable()
    {
        RuleIndex.discoverRulesInPackage("com.thevoxelbox.voxelguest.modules.regions.rules");

        // check if each world has a global region, create if not
        for (final World world : Bukkit.getWorlds())
        {
            if (!regionProvider.regionExists("global", world.getName()))
            {
                regionProvider.saveRegion(new GuestRegion("global", world.getName()));
            }
        }

        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        RuleIndex.flipTables();
    }

    @Override
    public HashSet<Listener> getListeners()
    {
        final HashSet<Listener> listeners = new HashSet<>();
        listeners.add(this.bukkitEventListener);
        return listeners;
    }

    @Override
    public HashMap<String, CommandExecutor> getCommandMappings()
    {
        final HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
        commandMappings.put("vgregion", new RegionCommand());
        return commandMappings;
    }

    /**
     * Returns the currently active default region provider.
     *
     * @return Returns the currently active default region provider.
     */
    public RegionProvider getRegionProvider()
    {
        return regionProvider;
    }
}
