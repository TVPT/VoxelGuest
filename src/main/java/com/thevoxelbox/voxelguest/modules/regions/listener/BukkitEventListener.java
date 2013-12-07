package com.thevoxelbox.voxelguest.modules.regions.listener;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RegionProvider;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.modules.regions.RuleIndex;
import com.thevoxelbox.voxelguest.modules.regions.settings.RuleSettingsDAO;
import com.thevoxelbox.voxelguest.utils.Pair;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Monofraps
 */
public final class BukkitEventListener implements Listener
{
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockCanBuild(final BlockCanBuildEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockDispense(final BlockDispenseEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockFade(final BlockFadeEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockFromTo(final BlockFromToEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockGrow(final BlockGrowEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockIgnite(final BlockIgniteEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockPhysics(final BlockPhysicsEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockPistonExtend(final BlockPistonExtendEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockPistonRetract(final BlockPistonRetractEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onBlockRedstone(final BlockRedstoneEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onNotePlay(final NotePlayEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onLeavesDacay(final LeavesDecayEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onSignChange(final SignChangeEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onEnchantItem(final EnchantItemEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onHangingBreakEvent(final HangingBreakEvent event)
    {
        handleIncomingEvent(event);
    }

    @EventHandler
    public void onEntityDamageEvent(final EntityDamageEvent event)
    {
        handleIncomingEvent(event);
    }


    private void handleIncomingEvent(final Event event)
    {
        if (!(event instanceof Cancellable))
        {
            return;
        }

        final List<Region> affectedRegions = getAffectedRegions(event);
        if (affectedRegions == null) return;

        // stores the rules that have to be checked (created based on priorities)
        final Map<String, Pair<Region, RuleExecutionSettings>> rulesToCheck = new HashMap<>();
        final Map<String, Integer> currentPriorityMap = new HashMap<>();
        for (final Region region : affectedRegions)
        {
            for (final String ruleId : region.getActiveRules())
            {
                final RuleExecutionSettings settings = RuleSettingsDAO.getSettings(region, ruleId);

                // calculate priority
                int computedPriority = settings.getPriority();
                // Integer.MIN_VALUE is defined as "Inherit from region"
                if (computedPriority == RegionRule.INHERIT_PRIORITY_FROM_REGION)
                {
                    computedPriority = region.getPriority();
                }

                // check if current rule's priority is lower than the one we had before
                // we don't need to execute the rule then
                if (!currentPriorityMap.containsKey(ruleId) || (currentPriorityMap.get(ruleId) < computedPriority))
                {
                    currentPriorityMap.put(ruleId, computedPriority);
                    rulesToCheck.put(ruleId, new Pair<>(region, settings));
                }
            }
        }

        // go through the list of rules we have to check
        for (final String ruleId : rulesToCheck.keySet())
        {
            // TODO: we could optimize things by moving this check up before SettingsDAO.get
            final RegionRule rule = RuleIndex.getRule(ruleId);
            if (!rule.handles(event))
            {
                continue;
            }

            final Region region = rulesToCheck.get(ruleId).getFirst();
            final RuleExecutionSettings settings = rulesToCheck.get(ruleId).getSecond();
            final boolean cancellationSuggested = rule.check(event, region, settings);
            final Cancellable cancellableEvent = (Cancellable) event;

            // don't override if event is already cancelled
            // TODO: implement a way to force cancellation override
            if (!cancellableEvent.isCancelled())
            {
                cancellableEvent.setCancelled(cancellationSuggested);
            }
        }
    }

    private List<Region> getAffectedRegions(final Event event)
    {
        final RegionModule module = (RegionModule) VoxelGuest.getModuleManagerInstance().getModuleInstance(RegionModule.class);
        // extremely unlikely that module becomes null
        final RegionProvider provider = module.getRegionProvider();

        final List<Region> unfilteredAffectedRegions;

        // try to get the event location
        if (event instanceof PlayerInteractEvent)
        {
            unfilteredAffectedRegions = provider.byLocation(((PlayerInteractEvent) event).getClickedBlock().getLocation());
        }
        else if (event instanceof BlockEvent)
        {
            unfilteredAffectedRegions = provider.byLocation(((BlockEvent) event).getBlock().getLocation());
        }
        else if (event instanceof EnchantItemEvent)
        {
            unfilteredAffectedRegions = provider.byLocation(((EnchantItemEvent) event).getEnchantBlock().getLocation());
        }
        else if (event instanceof EntityEvent)
        {
            unfilteredAffectedRegions = provider.byLocation(((EntityEvent) event).getEntity().getLocation());
        }
        else if (event instanceof HangingEvent)
        {
            unfilteredAffectedRegions = provider.byLocation(((HangingEvent) event).getEntity().getLocation());
        }
        else
        {
            return null;
        }

        // filter our regions based on priority
        // same region priorities -> rules will be merged
        // A has lower region priority than B -> A will be completely ignored
        final List<Region> affectedRegions = new ArrayList<>();
        int currentHighestRegionPriority = Integer.MIN_VALUE;
        for (final Region region : unfilteredAffectedRegions)
        {
            if (region.getPriority() > currentHighestRegionPriority)
            {
                affectedRegions.clear();
                currentHighestRegionPriority = region.getPriority();
            }

            if (region.getPriority() == currentHighestRegionPriority)
            {
                affectedRegions.add(region);
            }
        }
        return affectedRegions;
    }
}
