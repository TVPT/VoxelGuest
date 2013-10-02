package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * @author Monofraps
 */
public class DisableLavaFlow extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof BlockFromToEvent)
        {
            final BlockFromToEvent blockFromToEvent = (BlockFromToEvent) event;
            return blockFromToEvent.getBlock().getType().equals(Material.LAVA) || blockFromToEvent.getBlock().getType().equals(Material.STATIONARY_LAVA);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableLavaFlow", "DLF"};
    }
}
