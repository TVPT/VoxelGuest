package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Monofraps
 */
@Rule
public class DisableBlockDrop extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
        blockBreakEvent.getBlock().setType(Material.AIR);
        return true;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableBlockDrop", "DBD"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockBreakEvent;
    }
}
