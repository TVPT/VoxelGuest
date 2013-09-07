package com.thevoxelbox.voxelguest.modules.regions.listener;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.VoxelGuest;
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
import org.bukkit.event.block.BlockBurnEvent;
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
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * @author Butters
 */
public final class BlockEventListener implements Listener
{

    public static final String CANT_BUILD_HERE = "§cYou may not modify this area.";
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
            this.regionModule.getFloodProtector().sendMessage(event.getPlayer(), BlockEventListener.CANT_BUILD_HERE);
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
                event.getBlock().setTypeId(Material.AIR.getId(), true);
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
            this.regionModule.getFloodProtector().sendMessage(event.getPlayer(), BlockEventListener.CANT_BUILD_HERE);
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
            this.regionModule.getFloodProtector().sendMessage(event.getPlayer(), BlockEventListener.CANT_BUILD_HERE);
        }
    }

    @EventHandler(ignoreCancelled = true)
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
            if (!region.isLeafDecayAllowed())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(final BlockBurnEvent event)
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
            if (!region.isLeafDecayAllowed()) // Temp Fix as permanent fix would need to change db schema
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
    public void onBlockPhysics(final BlockPhysicsEvent event)
    {
        final Material mat = event.getChangedType();
        switch (mat)
        {
            case COMMAND:
            case DETECTOR_RAIL:
            case DIODE:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case DISPENSER:
            case GLOWING_REDSTONE_ORE:
            case IRON_DOOR:
            case IRON_DOOR_BLOCK:
            case MINECART:
            case NOTE_BLOCK:
            case PISTON_BASE:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case PISTON_STICKY_BASE:
            case POWERED_RAIL:
            case REDSTONE:
            case REDSTONE_LAMP_OFF:
            case REDSTONE_LAMP_ON:
            case REDSTONE_ORE:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case REDSTONE_WIRE:
            case STONE_PLATE:
            case WOOD_PLATE:
            case STORAGE_MINECART:
            case STRING:
            case TNT:
            case TRAP_DOOR:
            case TRIPWIRE:
            case TRIPWIRE_HOOK:
            case WOODEN_DOOR:
            case WOOD_BUTTON:
            case WOOD_DOOR:
            case LEVER:
            case STONE_BUTTON:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case REDSTONE_COMPARATOR:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
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
        if (
            event.getSpawnReason().equals(SpawnReason.CUSTOM) ||
            event.getSpawnReason().equals(SpawnReason.EGG) ||
            event.getSpawnReason().equals(SpawnReason.SPAWNER_EGG)
           )
        {
            return;
        }
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
    public void onPaintingBreakByEntity(final HangingBreakByEntityEvent event)
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

    @EventHandler
    public void onPaintingBreak(final HangingBreakEvent event)
    {
        final Location eventLoc = event.getEntity().getLocation();
        final List<Region> regions = this.regionModule.getRegionManager().getRegionsAtLoc(eventLoc);

        for (final Region region : regions)
        {
            if (!region.isBuildingRestricted() && event.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION))
            {
                if (!region.isTntBreakingPaintingsAllowed())
                {
                    event.setCancelled(true);
                }
            }
        }
    }
}
