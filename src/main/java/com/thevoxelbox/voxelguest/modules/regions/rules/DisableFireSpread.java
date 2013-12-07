package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockIgniteEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableFireSpread extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return true;
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockIgniteEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableFireSpread", "DFS"};
    }
}
