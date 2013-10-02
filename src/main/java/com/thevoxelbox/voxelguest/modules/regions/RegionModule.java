package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.modules.GuestModule;
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
        // check if each world has a global region and create it if not
        for (final World world : Bukkit.getWorlds())
        {
            if (RegionDAO.byName("global", world.getName()) == null)
            {
                RegionDAO.saveRegion(new Region("global"));
            }
        }

        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
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
        HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
        return commandMappings;
    }

}
