package com.thevoxelbox.voxelguest.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author monofraps
 */
public final class EventInformationProvider
{
    private EventInformationProvider()
    {

    }

    /**
     * Extracts the player from Bukkit events.
     *
     * @param event The event to extract a player instance from.
     *
     * @return Returns a player instance if the event carries a player argument.
     */
    public static Player getPlayerFromEvent(final Event event)
    {
        final Player player;

        if (event instanceof BlockBreakEvent)
        {
            player = ((BlockBreakEvent) event).getPlayer();
        }
        else if (event instanceof BlockPlaceEvent)
        {
            player = ((BlockPlaceEvent) event).getPlayer();
        }
        else if (event instanceof PlayerInteractEvent)
        {
            player = ((PlayerInteractEvent) event).getPlayer();
        }
        else if (event instanceof HangingBreakByEntityEvent)
        {
            if (((HangingBreakByEntityEvent) event).getRemover() instanceof Player)
            {
                player = (Player) ((HangingBreakByEntityEvent) event).getRemover();
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }

        return player;
    }
}
