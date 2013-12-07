package com.thevoxelbox.voxelguest.modules.regions.rules.player.damage;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableBlockExplosionDamage extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final EntityDamageEvent entityDamageEvent = (EntityDamageEvent) event;
        return ((EntityDamageEvent) event).getEntityType().equals(EntityType.PLAYER) && entityDamageEvent.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION);

    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableBlockExplosionDamage", "DBED"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof EntityDamageEvent;
    }
}
