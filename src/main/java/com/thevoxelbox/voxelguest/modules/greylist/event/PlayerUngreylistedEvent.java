package com.thevoxelbox.voxelguest.modules.greylist.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Monofraps
 */
public final class PlayerUngreylistedEvent extends Event
{
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private String playerName;

    /**
     * Creates a new player ungreylisy event instance.
     *
     * @param playerName The player who has been ungreylisted.
     */
    public PlayerUngreylistedEvent(final String playerName)
    {

        this.playerName = playerName;
    }

    /**
     * Does some dark magic stuff.
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
     *
     * @return Returns the name of the player ungreylisted.
     */
    public String getPlayerName()
    {
        return playerName;
    }
}
