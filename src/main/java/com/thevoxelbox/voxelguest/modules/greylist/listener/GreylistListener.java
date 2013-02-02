package com.thevoxelbox.voxelguest.modules.greylist.listener;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author MikeMatrix
 */
public class GreylistListener implements Listener
{
    GreylistModule greylistModule;

    public GreylistListener(final GreylistModule greylistModule)
    {
        this.greylistModule = greylistModule;
    }

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event)
    {
        Preconditions.checkNotNull(event);

        if (greylistModule.isExplorationMode())
        {
            return;
        }

        final Player player = event.getPlayer();
        if(!player.hasPermission("voxelguest.greylist.override") && !greylistModule.isOnPersistentGreylist(player.getName()))
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, greylistModule.getNotGreylistedKickMessage());
        }
    }
}
