package com.thevoxelbox.voxelguest.modules.greylist.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called before a player gets added to the greylist.
 *
 * @author Monofraps
 */
public final class PlayerGreylistEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private String playerName;
    private boolean cancelled = false;

    /**
     * Creates a new player (pre) greylist event instance.
     *
     * @param playerName The name of the player to greylist.
     */
    public PlayerGreylistEvent(final String playerName)
    {

        this.playerName = playerName;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLER_LIST;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(final String playerName)
    {
        this.playerName = playerName;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled)
    {
        this.cancelled = cancelled;
    }
}
