package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockIgniteEvent;

/**
 * @author Monofraps
 */
public class DisableFireSpread extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings) {
        return event instanceof BlockIgniteEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[] {"DisableFireSpread", "DFS"};
    }
}
