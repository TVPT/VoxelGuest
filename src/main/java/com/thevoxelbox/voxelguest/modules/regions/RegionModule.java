package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.regions.command.RegionCommand;
import com.thevoxelbox.voxelguest.modules.regions.listener.BlockEventListener;
import com.thevoxelbox.voxelguest.modules.regions.listener.PlayerEventListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Butters
 */
public final class RegionModule extends GuestModule
{
    private final BlockEventListener blockEventListener;
    private final PlayerEventListener playerEventListener;
    private final RegionCommand regionCommand;
    private final RegionManager regionManager;

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

}
