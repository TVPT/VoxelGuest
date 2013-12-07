package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockGrowEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableBlockGrowth extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return true;
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockGrowEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableBlockGrowth", "DBG"};
    }
}
