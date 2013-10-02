package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Monofraps
 */
public class DisableBlockDrop extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof BlockBreakEvent)
        {
            final BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            blockBreakEvent.getBlock().setTypeId(Material.AIR.getId(), false);
            return true;
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableBlockDrop", "DBD"};
    }
}
