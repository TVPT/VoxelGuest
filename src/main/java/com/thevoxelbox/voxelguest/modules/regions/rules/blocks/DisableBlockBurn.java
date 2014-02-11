package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBurnEvent;

/**
 * @author Monofraps
 */
@Rule
public class DisableBlockBurn extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return true;
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockBurnEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableBlockBurn", "DBB"};
    }
}
