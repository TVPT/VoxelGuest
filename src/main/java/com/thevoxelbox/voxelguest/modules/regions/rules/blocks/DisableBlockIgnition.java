package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockIgniteEvent;

/**
 * @author Monofraps
 */
@Rule
public class DisableBlockIgnition extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return !((BlockIgniteEvent) event).getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL);
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableBlockIgnition", "DBI"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockIgniteEvent;
    }
}
