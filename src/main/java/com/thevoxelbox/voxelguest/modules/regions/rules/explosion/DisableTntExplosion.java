package com.thevoxelbox.voxelguest.modules.regions.rules.explosion;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableTntExplosion extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final EntityExplodeEvent entityExplodeEvent = (EntityExplodeEvent) event;
        return entityExplodeEvent.getEntityType().equals(EntityType.PRIMED_TNT) || entityExplodeEvent.getEntityType().equals(EntityType.MINECART_TNT);
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableTntExplosion", "DTE"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof EntityExplodeEvent;
    }
}
