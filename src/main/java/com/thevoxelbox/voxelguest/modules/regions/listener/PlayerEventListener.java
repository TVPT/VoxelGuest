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

/**
 * @author Butters
 */
public class PlayerEventListener implements Listener
{
   	private RegionModule regionModule;

	public PlayerEventListener(RegionModule regionModule)
	{
		this.regionModule = regionModule;
	}

	@EventHandler
	public final void onDamageByBlock(EntityDamageByBlockEvent event)
	{
		if (!(event.getEntityType() == EntityType.PLAYER))
		{
			return;
		}

		Region region = regionModule.getRegionAtLocation(event.getEntity().getLocation());
		if (region == null)
		{
			return;
		}

		DamageCause cause = event.getCause();

		if (cause == DamageCause.CONTACT)
		{
			if (!region.isAllowCactusDamage())
			{
				event.setCancelled(true);
			}
		}

		if (cause == DamageCause.LAVA)
		{
			if (!region.isAllowLavaDamage())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onDamageByEntity(EntityDamageByEntityEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getEntity().getLocation());
		if (region == null)
		{
			return;
		}

		Entity entity = event.getEntity();
		DamageCause cause = event.getCause();

		if (entity instanceof Player)
		{
			if (cause == DamageCause.ENTITY_ATTACK)
			{
				if (!region.isAllowPvPDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.ENTITY_EXPLOSION)
			{
				if (!region.isAllowExplosiveDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.PROJECTILE)
			{
				if (!region.isAllowProjectileDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.BLOCK_EXPLOSION)
			{
				if (!region.isAllowTnTDamage())
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

	@EventHandler
	public final void onEntityDamage(EntityDamageEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getEntity().getLocation());
		if (region == null)
		{
			return;
		}

		Entity entity = event.getEntity();
		DamageCause cause = event.getCause();

		if (entity instanceof Player)
		{
			if (cause == DamageCause.DROWNING)
			{
				if (!region.isAllowDrowningDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.FALL)
			{
				if (!region.isAllowFallDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.FIRE)
			{
				if (!region.isAllowFireDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.FIRE_TICK)
			{
				if (!region.isAllowFireTickDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.LIGHTNING)
			{
				if (!region.isAllowLightningDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.MAGIC)
			{
				if (!region.isAllowMagicDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.POISON)
			{
				if (!region.isAllowPoisonDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.STARVATION)
			{
				if (!region.isAllowHungerDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.SUFFOCATION)
			{
				if (!region.isAllowSuffocationDamage())
				{
					event.setCancelled(true);
				}
			}
			else if (cause == DamageCause.VOID)
			{
				if (!region.isAllowVoidDamage())
				{
					event.setCancelled(true);
				}
			}

		}
	}

	@EventHandler
	public final void onFoodChange(FoodLevelChangeEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getEntity().getLocation());
		if (region == null)
		{
			return;
		}

		if (event.getFoodLevel() < 20)
		{
			event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

}
