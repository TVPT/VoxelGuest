package com.thevoxelbox.voxelguest.modules.general.runnables;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TheCryoknight
 */
public final class LagMeterHelperThread extends Thread
{
    private final Set<Player> activePlayers = Collections.synchronizedSet(new HashSet<Player>());

    public LagMeterHelperThread()
    {

    }

    /**
     * Sets a players watch state.
     *
     * @param player The name of the player.
     * @param state  A boolean to indicate whether or not you want to set or unset the watch state.
     */
    public void setPlayerWatchState(final Player player, final boolean state)
    {
        if (state)
        {
            this.activePlayers.add(player);
        }
        else
        {
            this.activePlayers.remove(player);
        }
    }

    /**
     * Toggles whether or not the players is watching TPS.
     *
     * @param player player to toggle
     */
    public void togglePlayer(final Player player)
    {
        if (this.activePlayers.contains(player))
        {
            this.setPlayerWatchState(player, false);
            player.sendMessage(ChatColor.GRAY + "Your experience bar will no longer represent the server's TPS.");
        }
        else
        {
            this.setPlayerWatchState(player, true);
            player.sendMessage(ChatColor.GRAY + "Your experience bar will now represent the server's TPS.");
        }
    }

    /**
     * Checks if a player is on the TPS watch list.
     *
     * @param player The player name.
     *
     * @return Returns a boolean indicating if the given player name is on the tps watch list.
     */
    public boolean isPlayerOnTpsWatch(final Player player)
    {
        return this.activePlayers.contains(player);
    }

    @Override
    public void run()
    {
        float tps;
        try
        {
            for (final Player player : this.activePlayers)
            {
                tps = (float) TPSTicker.calculateTPS();
                if (tps > 20)
                {
                    tps = 20;
                }
                if (player.isOnline())
                {
                    player.setExp(tps / 20);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
