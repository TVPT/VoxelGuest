package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import org.bukkit.entity.Monster;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableMonsterSpawn extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return ((CreatureSpawnEvent) event).getEntity() instanceof Monster;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableMonsterSpawn", "DMS"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof CreatureSpawnEvent;
    }
}
