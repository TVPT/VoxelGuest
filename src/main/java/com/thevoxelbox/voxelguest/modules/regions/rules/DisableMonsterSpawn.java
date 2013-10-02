package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import org.bukkit.entity.Monster;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * @author Monofraps
 */
public class DisableMonsterSpawn extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return event instanceof CreatureSpawnEvent && ((CreatureSpawnEvent) event).getEntity() instanceof Monster;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableMonsterSpawn", "DMS"};
    }
}
