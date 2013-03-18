package com.thevoxelbox.voxelguest.modules.general.listener;

import com.thevoxelbox.voxelguest.modules.general.GeneralModule;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author TheCryoknight
 */
public final class PlayerEventListener implements Listener
{
    private final GeneralModule module;

    /**
     * Creates a new player event listener instance.
     *
     * @param generalModule The owning module.
     */

    public PlayerEventListener(final GeneralModule generalModule)
    {
        this.module = generalModule;
    }

    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event)
    {
        if (event.getTarget() != null)
        {
            if (event.getTarget() instanceof Player)
            {
                final Player player = (Player) event.getTarget();
                if (this.module.getVanishFakequitHandler().isPlayerVanished(player))
                {
                    event.setCancelled(true);
                    return;
                }
                if (this.module.getVanishFakequitHandler().isPlayerFakequit(player))
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event)
    {
        if (event.getPlayer() != null)
        {
            final Player player = event.getPlayer();
            if (this.module.getVanishFakequitHandler().isPlayerVanished(player))
            {
                event.setCancelled(true);
                return;
            }
            if (this.module.getVanishFakequitHandler().isPlayerFakequit(player))
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event)
    {
        if (event.getPlayer() != null)
        {
            this.module.getAfkManager().processPotentialReturnEvent(event.getPlayer());
        }
    }
}
