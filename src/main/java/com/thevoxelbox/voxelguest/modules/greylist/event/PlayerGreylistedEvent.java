package com.thevoxelbox.voxelguest.modules.greylist.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event gets called right after a player was successfully added to the greylist.
 *
 * @author Monofraps
 */
public final class PlayerGreylistedEvent extends Event
{
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private String playerName;

    /**
     * Creates a new greylist event instance.
     *
     * @param playerName The name of the player who has been greylisted.
     */
    public PlayerGreylistedEvent(final String playerName)
    {

        this.playerName = playerName;
    }

    /**
     * Does some dark magic stuff.
     *
     * @return Returns a mystic list of event handlers.
     */
    public static HandlerList getHandlerList()
    {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLER_LIST;
    }

    /**
     * Returns the player name.
     *
     * @return Returns the player name.
     */
    public String getPlayerName()
    {
        return playerName;
    }
}
