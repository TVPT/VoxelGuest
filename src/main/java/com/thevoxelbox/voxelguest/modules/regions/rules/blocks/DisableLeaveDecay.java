package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.event.Event;
import org.bukkit.event.block.LeavesDecayEvent;

/**
 * @author Monofraps
 */
public class DisableLeaveDecay extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return event instanceof LeavesDecayEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableLeaveDecay", "DLD"};
    }
}
