package com.thevoxelbox.voxelguest.modules.general.runnables;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.v1_4_R1.Packet43SetExperience;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;


/**
 *
 * @author TheCryoknight
 */
public class LagMeterHelperThread extends Thread
{
    private final Set<Player> activePlayers = Collections.synchronizedSet(new HashSet<Player>());
    private volatile boolean isStoped = false;


    public LagMeterHelperThread()
    {
        
    }

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
     * Toggles whether or not the players is watching TPS
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
    
    public boolean isPlayerOnTpsWatch(final Player player)
    {
        return this.activePlayers.contains(player);
    }

    public void setStopped(final boolean stop)
    {
        this.isStoped = stop;
    }

    @Override
    public void run()
    {
        float tps = 0;
        try {
            while (!this.isStoped)
            {
                for (Player player : this.activePlayers.toArray(new Player[0]))
                {
                    final CraftPlayer cPlayer = (CraftPlayer) player;
                    tps = (float) TPSTicker.calculateTPS();
                    if (tps > 20)
                    {
                        tps = 20;
                    }
                    if (cPlayer.isOnline())
                    {
                        final Packet43SetExperience packet = new Packet43SetExperience(tps / 20.0F, 0, (int) tps);
                        cPlayer.getHandle().playerConnection.sendPacket(packet); 
                    }
                }
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
