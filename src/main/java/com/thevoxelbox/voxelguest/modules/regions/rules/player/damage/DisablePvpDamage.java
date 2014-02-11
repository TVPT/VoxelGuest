package com.thevoxelbox.voxelguest.modules.regions.rules.player.damage;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {"playerDamage"})
public class DisablePvpDamage extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
        return entityDamageByEntityEvent.getEntityType().equals(EntityType.PLAYER);
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisablePvpDamage", "DPvD"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof EntityDamageByEntityEvent;
    }
}
