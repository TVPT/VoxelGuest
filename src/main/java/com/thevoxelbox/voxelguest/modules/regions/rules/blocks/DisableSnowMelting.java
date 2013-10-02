package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockFadeEvent;

/**
 * @author Monofraps
 */
public class DisableSnowMelting extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof BlockFadeEvent)
        {
            final BlockFadeEvent blockFadeEvent = (BlockFadeEvent) event;
            return blockFadeEvent.getBlock().getType().equals(Material.SNOW_BLOCK);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableSnowMelting", "DSM"};
    }
}
