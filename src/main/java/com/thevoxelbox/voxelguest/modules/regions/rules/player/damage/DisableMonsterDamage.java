package com.thevoxelbox.voxelguest.modules.regions.rules.player.damage;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.entity.Monster;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableMonsterDamage extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
        return entityDamageByEntityEvent.getDamager() instanceof Monster;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableMonsterDamage", "DMoD"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof EntityDamageByEntityEvent;
    }
}
