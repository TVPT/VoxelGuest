package com.thevoxelbox.voxelguest.modules.regions.rules.player.damage;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Monofraps
 */
public class DisablePvpDamage extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings) {
        if(event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
            return entityDamageByEntityEvent.getEntityType().equals(EntityType.PLAYER);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[] {"DisablePvpDamage", "DPvD"};
    }
}
