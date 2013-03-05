package com.thevoxelbox.voxelguest.modules.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelguest.persistence.Persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author TheCryoknight
 */
public class AfkManager
{
    private final GeneralModule module;
    private final Set<String> playersAfk = Collections.synchronizedSet(new HashSet<String>());

    public AfkManager(final GeneralModule module)
    {
        this.module = module;
    }

    /**
     * Sets the players afk state
     *
     * @param player Player who gets their AFK state set.
     * @param isAfk the state to set the afk state of the player provided
     */
    public synchronized void setPlayerAfk(final Player player, final boolean isAfk)
    {
        if (isAfk)
        {
            this.playersAfk.add(player.getName());
            return;
        }
        this.playersAfk.remove(player.getName());
    }

    /**
     * Toggles the specified players afk state.
     *
     * @param player Player toggleing their state
     * @param message the message to provide if player is going afk
     */
    public void toggleAfk(final Player player, final String message)
    {
        if (this.playersAfk.contains(player.getName()))
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
     * @return true if player is afk
     */
    public boolean isPlayerAfk(final Player player)
    {
        return this.playersAfk.contains(player.getName());
    }

    public void broadcastAfk(final String pName, final String message, final boolean isAfk)
    {
        if(isAfk)
        {
            if (!message.isEmpty())
            {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + pName + ChatColor.DARK_GRAY + message);
                return;
            }
            if (((GeneralModuleConfiguration) this.module.getConfiguration()).isRandomAfkMsgs())
            {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + pName + ChatColor.DARK_GRAY + " " + this.getAfkMsg());
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

    private String getAfkMsg()
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
