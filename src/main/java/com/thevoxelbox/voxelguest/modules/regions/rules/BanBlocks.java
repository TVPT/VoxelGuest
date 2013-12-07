package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.settings.BanBlocksSettings;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockCanBuildEvent;

import java.util.List;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class BanBlocks extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (settings instanceof BanBlocksSettings)
        {
            final int materialIdToPlace = ((BlockCanBuildEvent) event).getMaterialId();
            final List<Integer> bannedMaterials = ((BanBlocksSettings) settings).getBlockedBlocks();

            return bannedMaterials.contains(materialIdToPlace);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"BanBlocks", "BannedBlocks", "BB"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof BlockCanBuildEvent;
    }

    @Override
    public GuestRuleExecutionSettings instantiateExecutorSettingsClass()
    {
        return new BanBlocksSettings();
    }
}
