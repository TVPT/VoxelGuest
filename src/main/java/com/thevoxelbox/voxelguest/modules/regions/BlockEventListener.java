/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelguest.modules.regions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Joe
 */
public class BlockEventListener implements Listener
{

	private final String CANT_BUILD_HERE = "&4You cannot build here";
	private RegionModule regionModule;

	public BlockEventListener(final RegionModule regionModule)
	{
		this.regionModule = regionModule;
	}

	@EventHandler
	public final void onBlockBreak(final BlockBreakEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.getBuildPermission().equalsIgnoreCase(""))
		{
			if (!event.getPlayer().hasPermission(region.getBuildPermission()))
			{
				event.getPlayer().sendMessage(CANT_BUILD_HERE);
				event.setCancelled(true);
			}
		}

	}

	@EventHandler
	public final void onBlockPlace(final BlockPlaceEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.getBuildPermission().equalsIgnoreCase(""))
		{
			if (!event.getPlayer().hasPermission(region.getBuildPermission()))
			{
				event.getPlayer().sendMessage(CANT_BUILD_HERE);
				event.setCancelled(true);
			}
		}

		if (region.getBannedBlocks().contains(event.getBlockPlaced()))
		{
			event.getPlayer().sendMessage(CANT_BUILD_HERE);
			event.setCancelled(true);
		}
	}
        
	@EventHandler
	public final void onPlayerInteract(final PlayerInteractEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getClickedBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.getBuildPermission().equalsIgnoreCase(""))
		{
			if (!event.getPlayer().hasPermission(region.getBuildPermission()))
			{
				event.getPlayer().sendMessage(CANT_BUILD_HERE);
				event.setCancelled(true);
			}
		}

		if (region.getBannedItems().contains(event.getItem()))
		{
			event.getPlayer().sendMessage(CANT_BUILD_HERE);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void onLeafDecay(final LeavesDecayEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.isLeafDecayAllowed())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void onBlowGrow(final BlockGrowEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.isBlowGrowthAllowed())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void onFromTo(final BlockFromToEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		final Block movedBlock = event.getBlock();
		if ((movedBlock.getType() == Material.STATIONARY_WATER) || (movedBlock.getType() == Material.WATER))
		{
			if (!region.isWaterFlowAllowed())
			{
				event.setCancelled(true);
			}
		}

		if ((movedBlock.getType() == Material.STATIONARY_LAVA) || (movedBlock.getType() == Material.LAVA))
		{
			if (!region.isLavaFlowAllowed())
			{
				event.setCancelled(true);
			}
		}

		if (movedBlock.getType() == Material.DRAGON_EGG)
		{
			if (!region.isDragonEggMovementAllowed())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onBlockFade(final BlockFadeEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		final Block fadedBlock = event.getBlock();
		if (fadedBlock.getType() == Material.ICE)
		{
			if (!region.isIceMeltingAllowed())
			{
				event.setCancelled(true);
			}
		}

		if (fadedBlock.getType() == Material.SNOW)
		{
			if (!region.isSnowMeltingAllowed())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onBlockForm(final BlockFormEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		final Block formedBlock = event.getBlock();
		if ((formedBlock.getType() == Material.WATER) || (formedBlock.getType() == Material.STATIONARY_WATER))
		{
			if (!region.isIceFormationAllowed())
			{
				event.setCancelled(true);
			}
		}

		if (formedBlock.getType() == Material.AIR)
		{
			if (!region.isSnowFormationAllowed())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onBlockIgnite(final BlockIgniteEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if ((event.getCause() == IgniteCause.SPREAD) || (event.getCause() == IgniteCause.LAVA) || (event.getCause() == IgniteCause.LIGHTNING))
		{
			if (!region.isFireSpreadAllowed())
			{
				event.setCancelled(true);
			}
		}

	}

	@EventHandler
	public final void onBlockSpread(final BlockSpreadEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getBlock().getLocation());
		if (region == null)
		{
			return;
		}

		if (event.getNewState().getType() == Material.FIRE)
		{
			if (!region.isFireSpreadAllowed())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onEnchant(final EnchantItemEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getEnchanter().getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.isEnchantingAllowed())
		{
			event.setCancelled(true);
		}

		if (region.getBannedItems().contains(event.getItem()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void onEntityExplode(final EntityExplodeEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getLocation());
		if (region == null)
		{
			return;
		}

		if (!region.isCreeperExplosionsAllowed())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void onPaintingBreak(final HangingBreakByEntityEvent event)
	{
		Region region = regionModule.getRegionAtLocation(event.getEntity().getLocation());
		if (region == null)
		{
			return;
		}

		if (event.getEntity() instanceof Painting)
		{
			if (!region.isTntBreakingPaintingsAllowed())
			{
				event.setCancelled(true);
			}
		}
	}

}
