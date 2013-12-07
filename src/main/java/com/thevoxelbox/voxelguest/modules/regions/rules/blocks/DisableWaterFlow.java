package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableWaterFlow extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final BlockFromToEvent blockFromToEvent = (BlockFromToEvent) event;
        return blockFromToEvent.getBlock().getType().equals(Material.WATER) || blockFromToEvent.getBlock().getType().equals(Material.STATIONARY_WATER);
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableWaterFlow", "DWF"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockFromToEvent;
    }
}
