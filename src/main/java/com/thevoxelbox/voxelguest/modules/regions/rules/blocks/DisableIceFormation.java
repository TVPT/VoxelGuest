package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockFormEvent;

/**
 * @author Monofraps
 */
public class DisableIceFormation extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof BlockFormEvent)
        {
            final BlockFormEvent blockFormEvent = (BlockFormEvent) event;
            return blockFormEvent.getBlock().getType().equals(Material.ICE);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableIceFormation", "DIF"};
    }
}
