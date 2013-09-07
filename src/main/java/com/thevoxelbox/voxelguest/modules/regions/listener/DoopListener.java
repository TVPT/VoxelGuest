package com.thevoxelbox.voxelguest.modules.regions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxeldoop.events.DoopDestroyEvent;
import com.thevoxelbox.voxeldoop.events.DoopPaintEvent;
import com.thevoxelbox.voxeldoop.events.DoopSpreadEvent;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;

/**
 * The Doop listener controls usage of VoxelDoop in VoxelGuest regions.
 * 
 * @author TheCryoknight
 */
public class DoopListener implements Listener
{
    private final RegionModule regionModule;

    /**
     * Creates a new VoxelDoop event listener.
     *
     * @param regionModule The owning module.
     */
    public DoopListener(final RegionModule regionModule)
    {
        this.regionModule = regionModule;
    }

    @EventHandler
    public void onDoopPaint(final DoopPaintEvent event)
    {
        Preconditions.checkNotNull(event.getPlayer());
        Preconditions.checkNotNull(event.getBlock());
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            this.regionModule.getFloodProtector().sendMessage(event.getPlayer(), BlockEventListener.CANT_BUILD_HERE);
        }
    }

    @EventHandler
    public void onDoopDestroy(final DoopDestroyEvent event)
    {
        Preconditions.checkNotNull(event.getPlayer());
        Preconditions.checkNotNull(event.getBlock());
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            this.regionModule.getFloodProtector().sendMessage(event.getPlayer(), BlockEventListener.CANT_BUILD_HERE);
        }
    }
    @EventHandler
    public void onDoopSpread(final DoopSpreadEvent event)
    {
        Preconditions.checkNotNull(event.getPlayer());
        Preconditions.checkNotNull(event.getBlock());
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BlockEventListener.CANT_BUILD_HERE);
        }
    }
}
