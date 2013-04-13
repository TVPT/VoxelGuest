package com.thevoxelbox.voxelguest.modules.regions.listener;

import com.thevoxelbox.voxelguest.modules.regions.Region;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.List;

/**
 * @author Butters
 */
public final class PlayerEventListener implements Listener
{
    private final RegionModule regionModule;

    /**
     * Creates a new player event listener.
     *
     * @param regionModule The owning module.
     */
    public PlayerEventListener(final RegionModule regionModule)
    {
        this.regionModule = regionModule;
    }

    @EventHandler
    public void onDamageByBlock(final EntityDamageByBlockEvent event)
    {
        if (!(event.getEntityType() == EntityType.PLAYER))
        {
            return;
        }

        List<Region> regions = regionModule.getRegionManager().getRegionsAtLoc(event.getEntity().getLocation());

        DamageCause cause = event.getCause();

        for (Region region : regions)
        {
            if (cause == DamageCause.CONTACT)
            {
                if (!region.isCactusDamageAllowed())
                {
                    event.setCancelled(true);
                }
            }

            if (cause == DamageCause.LAVA)
            {
                if (!region.isLavaDamageAllowed())
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(final EntityDamageByEntityEvent event)
    {
        List<Region> regions = regionModule.getRegionManager().getRegionsAtLoc(event.getEntity().getLocation());

        Entity entity = event.getEntity();
        DamageCause cause = event.getCause();

        for (Region region : regions)
        {
            if (entity instanceof Player)
            {
                if (cause == DamageCause.ENTITY_ATTACK)
                {
                    if (!region.isPvpDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.ENTITY_EXPLOSION)
                {
                    if (!region.isExplosiveDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.PROJECTILE)
                {
                    if (!region.isProjectileDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.BLOCK_EXPLOSION)
                {
                    if (!region.isTntDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
            }
            else if (entity instanceof Painting)
            {
                if (cause == DamageCause.BLOCK_EXPLOSION)
                {
                    if (!region.isTntBreakingPaintingsAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event)
    {
        List<Region> regions = regionModule.getRegionManager().getRegionsAtLoc(event.getEntity().getLocation());

        Entity entity = event.getEntity();
        DamageCause cause = event.getCause();

        for (Region region : regions)
        {
            if (entity instanceof Player)
            {
                if (cause == DamageCause.DROWNING)
                {
                    if (!region.isDrowningDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.FALL)
                {
                    if (!region.isFallDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.FIRE)
                {
                    if (!region.isFireDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.FIRE_TICK)
                {
                    if (!region.isFireTickDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.LIGHTNING)
                {
                    if (!region.isLightningDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.MAGIC)
                {
                    if (!region.isMagicDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.POISON)
                {
                    if (!region.isPoisonDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.STARVATION)
                {
                    if (!region.isHungerDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.SUFFOCATION)
                {
                    if (!region.isSuffocationDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.VOID)
                {
                    if (!region.isVoidDamageAllowed())
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event)
    {
        List<Region> regions = regionModule.getRegionManager().getRegionsAtLoc(event.getEntity().getLocation());

        for (Region region : regions)
        {
            if (!region.isFoodChangeAllowed())
            {
                if (event.getFoodLevel() < 20)
                {
                    event.setFoodLevel(20);
                    event.setCancelled(true);
                }
            }
        }
    }

}
