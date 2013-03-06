package com.thevoxelbox.voxelguest.modules.regions.listener;

import java.util.List;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.modules.regions.Region;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Butters
 */
public class BlockEventListener implements Listener
{

    private final String CANT_BUILD_HERE = "§cYou cannot build here";
    private RegionModule regionModule;

    public BlockEventListener(final RegionModule regionModule)
    {
        this.regionModule = regionModule;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockBreak(final BlockBreakEvent event)
    {
        Preconditions.checkNotNull(event.getPlayer());
        Preconditions.checkNotNull(event.getBlock());
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CANT_BUILD_HERE);
        }
    }

    /**
     * Prevents block drops.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public final void onBlockDrop(final BlockBreakEvent event)
    {
        Preconditions.checkNotNull(event);
        Block eventLoc = event.getBlock();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc.getLocation());
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isBlockDropAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
        if (eventLoc != null)
        {
            eventLoc.setType(Material.AIR);
        }
    }

    @EventHandler
    public final void onBlockPlace(final BlockPlaceEvent event)
    {
        Preconditions.checkNotNull(event.getPlayer());
        Preconditions.checkNotNull(event.getBlock());
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CANT_BUILD_HERE);
        }
    }

    @EventHandler
    public final void onPlayerInteract(final PlayerInteractEvent event)
    {
        if (event.getClickedBlock() == null)
        {
            return;
        }
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getClickedBlock().getLocation()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CANT_BUILD_HERE);
        }
    }

    @EventHandler
    public final void onLeafDecay(final LeavesDecayEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isBlockSpreadAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockGrow(final BlockGrowEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isBlockGrowthAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onFromTo(final BlockFromToEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isBlockSpreadAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockFade(final BlockFadeEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (event.getBlock().getType().equals(Material.SNOW))
            {
                if (!region.isSnowMeltingAllowed())
                {
                    isNotAllowed = true;
                    break;
                }
            }
            else if (event.getBlock().getType().equals(Material.ICE))
            {
                if (!region.isIceMeltingAllowed())
                {
                    isNotAllowed = true;
                    break;
                }
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockForm(final BlockFormEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (event.getBlock().getType().equals(Material.SNOW))
            {
                if (!region.isSnowFormationAllowed())
                {
                    isNotAllowed = true;
                    break;
                }
            }
            else if (event.getBlock().getType().equals(Material.ICE))
            {
                if (!region.isIceFormationAllowed())
                {
                    isNotAllowed = true;
                    break;
                }
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockIgnite(final BlockIgniteEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isFireSpreadAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockSpread(final BlockSpreadEvent event)
    {
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isBlockSpreadAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public final void onBlockPhysics(final BlockPhysicsEvent event)
    {
        final Material mat = event.getChangedType();
        switch (mat)
        {
        case COMMAND:
            return;
        case DETECTOR_RAIL:
            return;
        case DIODE:
            return;
        case DIODE_BLOCK_OFF:
            return;
        case DIODE_BLOCK_ON:
            return;
        case DISPENSER:
            return;
        case DRAGON_EGG:
            return;
        case GLOWING_REDSTONE_ORE:
            return;
        case IRON_DOOR:
            return;
        case IRON_DOOR_BLOCK:
            return;
        case MINECART:
            return;
        case NOTE_BLOCK:
            return;
        case PISTON_BASE:
            return;
        case PISTON_EXTENSION:
            return;
        case PISTON_MOVING_PIECE:
            return;
        case PISTON_STICKY_BASE:
            return;
        case POWERED_RAIL:
            return;
        case REDSTONE:
            return;
        case REDSTONE_LAMP_OFF:
            return;
        case REDSTONE_LAMP_ON:
            return;
        case REDSTONE_ORE:
            return;
        case REDSTONE_TORCH_OFF:
            return;
        case REDSTONE_TORCH_ON:
            return;
        case REDSTONE_WIRE:
            return;
        case STONE_PLATE:
            return;
        case WOOD_PLATE:
            return;
        case STORAGE_MINECART:
            return;
        case STRING:
            return;
        case TNT:
            return;
        case TRAP_DOOR:
            return;
        case TRIPWIRE:
            return;
        case TRIPWIRE_HOOK:
            return;
        case WOODEN_DOOR:
            return;
        case WOOD_BUTTON:
            return;
        case WOOD_DOOR:
            return;
        case LEVER:
            return;
        case STONE_BUTTON:
            return;
        case WATER:
            return;
        case STATIONARY_WATER:
            return;
        case LAVA:
            return;
        case STATIONARY_LAVA:
            return;
        default:
            break;
        }

        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isPhysicsAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onEnchant(final EnchantItemEvent event)
    {
        final Region region = this.regionModule.getRegionManager().getRegionAtLoc(event.getEnchantBlock().getLocation());

        if (region != null)
        {
            if (!region.isEnchantingAllowed())
            {
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        return;
    }

    @EventHandler
    public final void onEntityExplode(final EntityExplodeEvent event)
    {
        final Location eventLoc = event.getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isCreeperExplosionAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
        return;
    }

    @EventHandler
    public final void onCreatureSpawn(final CreatureSpawnEvent event)
    {
        final Location eventLoc = event.getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }
        boolean isNotAllowed = false;
        for (Region region : regions)
        {
            if (!region.isCreatureSpawnAllowed())
            {
                isNotAllowed = true;
                break;
            }
        }
        if (isNotAllowed)
        {
            event.setCancelled(true);
        }
        return;
    }
    
    @EventHandler
    public final void onPlayerInteractEvent(PlayerInteractEvent event)
    {
    final Location eventLoc = event.getLocation();
    final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
    
    if (regions.isEmpty())
    Player p = event.getPlayer();
    }
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_BLOCK))
    {
      if ((event.getClickedBlock().getType() != null) && (event.getClickedBlock().getType() == Material.DRAGON_EGG))
        {
        event.setCancelled(true);
        return;
        }
    }

    @EventHandler
    public final void onPaintingBreak(final HangingBreakByEntityEvent event)
    {
        final Location eventLoc = event.getEntity().getLocation();
        final Region region = this.regionModule.getRegionManager().getRegionAtLoc(eventLoc);

        if (region != null)
        {
            if (!region.isBuildingRestricted())
            {
                if (event.getRemover() instanceof Player)
                {
                    if (!this.regionModule.getRegionManager().canPlayerModify((Player) event.getRemover(), event.getEntity().getLocation()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        return;
        }
    }    
        
    @EventHandler
    public final void onPaintingBreak(final HangingBreakEvent event)
    {
        final Location eventLoc = event.getEntity().getLocation();
        final Region region = this.regionModule.getRegionManager().getRegionAtLoc(eventLoc);

        if (region != null)
        {
            if (!region.isBuildingRestricted())
            {
                if (event.getRemover() instanceof Player)
                {
                    if (!this.regionModule.getRegionManager().canPlayerModify((Player) event.getRemover(), event.getEntity().getLocation()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        return;
        }
        event.setCancelled(true);
        return;
    }

}
