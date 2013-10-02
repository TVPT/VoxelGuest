package com.thevoxelbox.voxelguest.modules.regions.rules.blocks;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * @author Monofraps
 */
public class DisableSoilDehydration extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof BlockPhysicsEvent)
        {
            final BlockPhysicsEvent blockPhysicsEvent = (BlockPhysicsEvent) event;
            final Block block = blockPhysicsEvent.getBlock();
            if (block.getType().equals(Material.SOIL) && (block.getData() != 0))
            {
                block.setData((byte) 7);
            }
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableSoilDehydration", "DSD"};
    }
}
