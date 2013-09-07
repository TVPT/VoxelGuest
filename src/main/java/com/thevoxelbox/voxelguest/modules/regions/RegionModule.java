package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.regions.command.RegionCommand;
import com.thevoxelbox.voxelguest.modules.regions.listener.BlockEventListener;
import com.thevoxelbox.voxelguest.modules.regions.listener.DoopListener;
import com.thevoxelbox.voxelguest.modules.regions.listener.PlayerEventListener;
import com.thevoxelbox.voxelguest.util.FloodProtection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Core Region module class
 *
 * @author Butters
 * @author TheCryoknight
 */
public final class RegionModule extends GuestModule
{
    private final BlockEventListener blockEventListener;
    private final PlayerEventListener playerEventListener;
    private final RegionCommand regionCommand;
    private final RegionManager regionManager;
    private final FloodProtection floodProtector;

    /**
     * Creates a new RegionModule instance.
     */
    public RegionModule()
    {
        this.setName("Region Module");

        this.blockEventListener = new BlockEventListener(this);
        this.playerEventListener = new PlayerEventListener(this);
        this.regionCommand = new RegionCommand(this);
        this.regionManager = new RegionManager();
        this.floodProtector = new FloodProtection();
    }

    @Override
    public void onEnable()
    {
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
        listeners.add(this.blockEventListener);
        listeners.add(this.playerEventListener);
        if (Bukkit.getPluginManager().isPluginEnabled("VoxelDoop"))
        {
            listeners.add(new DoopListener(this));
        }
        return listeners;
    }

    @Override
    public HashMap<String, CommandExecutor> getCommandMappings()
    {
        HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
        commandMappings.put("vgregion", this.regionCommand);
        return commandMappings;
    }

    /**
     * @return The region manager
     */
    public RegionManager getRegionManager()
    {
        return this.regionManager;
    }

    public FloodProtection getFloodProtector()
    {
        return floodProtector;
    }

}
