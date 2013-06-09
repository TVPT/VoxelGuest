package com.thevoxelbox.voxelguest.modules.regions.listener;

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
import org.bukkit.event.block.StructureGrowEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * @author Butters
 */
public final class BlockEventListener implements Listener
{

    private static final String CANT_BUILD_HERE = "§cYou may not modify this area.";
    private RegionModule regionModule;

    /**
     * Creates a new block event listener.
     *
     * @param regionModule The owning module.
     */
    public BlockEventListener(final RegionModule regionModule)
    {
        this.regionModule = regionModule;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event)
    {
        Preconditions.checkNotNull(event.getPlayer());
        Preconditions.checkNotNull(event.getBlock());
        if (!this.regionModule.getRegionManager().canPlayerModify(event.getPlayer(), event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(CANT_BUILD_HERE);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDrop(final BlockBreakEvent event)
    {
        Preconditions.checkNotNull(event);
        Block eventLoc = event.getBlock();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc.getLocation());

        for (final Region region : regions)
        {
            if (!region.isBlockDropAllowed())
            {
                event.getBlock().setTypeId(Material.AIR.getId(), false);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
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
    public void onPlayerInteract(final PlayerInteractEvent event)
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
    public void onLeafDecay(final LeavesDecayEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isBlockSpreadAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockGrow(final BlockGrowEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isBlockGrowthAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onFromTo(final BlockFromToEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isBlockSpreadAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockFade(final BlockFadeEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (event.getBlock().getType().equals(Material.SNOW))
            {
                if (!region.isSnowMeltingAllowed())
                {
                    event.setCancelled(true);
                    break;
                }
            }
            else if (event.getBlock().getType().equals(Material.ICE))
            {
                if (!region.isIceMeltingAllowed())
                {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockForm(final BlockFormEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (event.getBlock().getType().equals(Material.SNOW))
            {
                if (!region.isSnowFormationAllowed())
                {
                    event.setCancelled(true);
                    break;
                }
            }
            else if (event.getBlock().getType().equals(Material.ICE))
            {
                if (!region.isIceFormationAllowed())
                {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(final BlockIgniteEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isFireSpreadAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event)
    {
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isBlockSpreadAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }
    
    @EventHandler
    public void onStructureGrow(final ScructureGrowEvent event)
    {
        Preconditions.checkNotNull(event.getBlock());
        final Location eventLoc = event.getBlock().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isStructureGrowAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }
    


    @EventHandler
    public void onBlockPhysics(final BlockPhysicsEvent event)
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

        final Block block = event.getBlock();

        for (final Region region : regions)
        {
            if (!region.isPhysicsAllowed())
            {
                event.setCancelled(true);
                break;
            }

            if (block.getType().equals(Material.SOIL) && !region.isSoilDehydrationAllowed() && (block.getData() != 0))
            {
                event.getBlock().setData((byte) 7);
            }
        }
    }

    @EventHandler
    public void onEnchant(final EnchantItemEvent event)
    {
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(event.getEnchantBlock().getLocation());

        for (final Region region : regions)
        {
            if (!region.isEnchantingAllowed())
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event)
    {
        final Location eventLoc = event.getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isCreeperExplosionAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event)
    {
        final Location eventLoc = event.getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);
        if (regions.isEmpty())
        {
            event.setCancelled(true);
        }

        for (final Region region : regions)
        {
            if (!region.isCreatureSpawnAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPaintingBreak(final HangingBreakByEntityEvent event)
    {
        final Location eventLoc = event.getEntity().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);

        for (final Region region : regions)
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
        }
    }

}
