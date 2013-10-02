package com.thevoxelbox.voxelguest.modules.regions.rules.explosion;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Monofraps
 */
public class DisableTntExplosion extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof EntityExplodeEvent)
        {
            final EntityExplodeEvent entityExplodeEvent = (EntityExplodeEvent) event;
            return entityExplodeEvent.getEntityType().equals(EntityType.PRIMED_TNT) || entityExplodeEvent.getEntityType().equals(EntityType.MINECART_TNT);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableTntExplosion", "DTE"};
    }
}
