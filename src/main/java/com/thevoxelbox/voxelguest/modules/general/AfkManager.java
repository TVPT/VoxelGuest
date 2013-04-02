package com.thevoxelbox.voxelguest.modules.general;

import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author TheCryoknight
 */
public final class AfkManager
{
    private static final long MAX_AFK_THRESHOLD = 2500L;
    private final GeneralModule module;
    private final Map<String, Long> playersAfk = Collections.synchronizedMap(new HashMap<String, Long>());

    /**
     * Creates a new instance of the AfkManager.
     *
     * @param module The owning module.
     */
    public AfkManager(final GeneralModule module)
    {
        this.module = module;
    }

    /**
     * Sets the players afk state.
     *
     * @param player Player who gets their AFK state set.
     * @param isAfk  The state to set the afk state of the player provided
     */
    public void setPlayerAfk(final Player player, final boolean isAfk)
    {
        if (isAfk)
        {
            this.playersAfk.put(player.getName(), System.currentTimeMillis());
            return;
        }
        this.playersAfk.remove(player.getName());
    }

    /**
     * Toggles the specified players afk state.
     *
     * @param player  Player toggleing their state
     * @param message The message to provide if player is going afk
     */
    public void toggleAfk(final Player player, final String message)
    {
        if (this.playersAfk.containsKey(player.getName()))
        {
            this.setPlayerAfk(player, false);
            this.broadcastAfk(player.getName(), "", false);
            return;
        }
        this.setPlayerAfk(player, true);
        this.broadcastAfk(player.getName(), message, true);
    }

    /**
     * Checks to see if Player is afk.
     *
     * @param player Player to check afk state
     *
     * @return true if player is afk
     */
    public boolean isPlayerAfk(final Player player)
    {
        return this.playersAfk.containsKey(player.getName());
    }

    /**
     * Handles all events used to signify that the player may have returned.
     *
     * @param player The player involved in the event
     */
    public void processPotentialReturnEvent(final Player player)
    {
        if (this.isPlayerAfk(player))
        {
            final long timeDiff = System.currentTimeMillis() - this.playersAfk.get(player.getName());
            if (timeDiff > MAX_AFK_THRESHOLD)
            {
                this.toggleAfk(player, "");
            }
        }
    }

    /**
     * Broadcasts an AFK message.
     *
     * @param pName   The name of the player who has gone AFK/returned.
     * @param message The message to display.
     * @param isAfk   A boolean indicating if the player has gone AFK or returned.
     */
    public void broadcastAfk(final String pName, final String message, final boolean isAfk)
    {
        if (isAfk)
        {
            if (!message.isEmpty())
            {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + pName + " " + ChatColor.DARK_GRAY + message);
                return;
            }
            if (((GeneralModuleConfiguration) this.module.getConfiguration()).isRandomAfkMsgs())
            {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + pName + ChatColor.DARK_GRAY + " " + this.getRandAfkMsg());
            }
            else
            {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + pName + ChatColor.DARK_GRAY + " has gone AFK");
            }
        }
        else
        {
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + pName + ChatColor.DARK_GRAY + " has returned");
        }
    }

    /**
     * Generates a new random afk message based on afk messages stored in the database.
     * If there are no afk messages in the database it returns: "<code>has gone AFK</code>"
     *
     * @return Random afk message
     */
    private String getRandAfkMsg()
    {
        final Random rand = new Random();
        final List<AfkMessage> afkMessages = Persistence.getInstance().loadAll(AfkMessage.class);
        if (afkMessages.isEmpty())
        {
            return "has gone AFK";
        }
        return afkMessages.get(rand.nextInt(afkMessages.size())).getMessage();
    }
}
