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
public class DisableHungerDamage extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final EntityDamageEvent entityDamageEvent = (EntityDamageEvent) event;
        if (((EntityDamageEvent) event).getEntityType().equals(EntityType.PLAYER))
        {
            return entityDamageEvent.getCause().equals(EntityDamageEvent.DamageCause.STARVATION);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableHungerDamage", "DHD"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof EntityDamageEvent;
    }
}
