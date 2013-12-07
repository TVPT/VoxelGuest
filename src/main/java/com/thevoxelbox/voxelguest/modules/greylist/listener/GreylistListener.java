package com.thevoxelbox.voxelguest.modules.greylist.listener;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistConfiguration;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistDAO;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerGreylistedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author MikeMatrix
 */
public final class GreylistListener implements Listener
{
    private final GreylistConfiguration moduleConfiguration;

    /**
     * Creates a new greylist listener instance.
     *
     * @param greylistModule The owning module.
     */
    public GreylistListener(final GreylistModule greylistModule)
    {
        this.moduleConfiguration = (GreylistConfiguration) greylistModule.getConfiguration();
    }

    /**
     * Handles login events.
     *
     * @param event The Bukkit event.
     */
    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event)
    {
        Preconditions.checkNotNull(event);

        if (moduleConfiguration.isExplorationMode())
        {
            return;
        }

        final Player player = event.getPlayer();
        if (!player.hasPermission("voxelguest.greylist.override") && !GreylistDAO.isOnPersistentGreylist(player.getName()))
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, moduleConfiguration.getNotGreylistedKickMessage());
        }
    }

    /**
     * Handles greylist events, does broadcasts and sets the players group.
     *
     * @param event The Bukkit event.
     */
    @EventHandler
    public void onPlayerGreylisted(final PlayerGreylistedEvent event)
    {
        if (moduleConfiguration.isBroadcastGreylists())
        {
            Bukkit.broadcastMessage(ChatColor.GRAY + event.getPlayerName() + ChatColor.DARK_GRAY + " was added to the greylist.");
        }

        if (moduleConfiguration.isSetGroupOnGreylist() && VoxelGuest.hasPermissionProvider())
        {
            if (VoxelGuest.getPerms().playerAddGroup(Bukkit.getWorlds().get(0), event.getPlayerName(), moduleConfiguration.getGreylistGroupName()))
            {
                VoxelGuest.getPluginInstance().getLogger().warning("Error: Could not set new greylisted player to group.");
            }
        }
    }
}
