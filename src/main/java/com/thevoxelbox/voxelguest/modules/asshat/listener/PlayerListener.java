package com.thevoxelbox.voxelguest.modules.asshat.listener;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import com.thevoxelbox.voxelguest.modules.asshat.ban.Banlist;
import com.thevoxelbox.voxelguest.modules.asshat.mute.Mutelist;
import com.thevoxelbox.voxelguest.utils.UUIDFetcher;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

/**
 * @author Monofraps
 */
public class PlayerListener implements Listener
{
    private final AsshatModule module;

    /**
     * @param module The parent module.
     */
    public PlayerListener(final AsshatModule module)
    {
        this.module = module;
    }

    /**
     * Handles muted players and global silence.
     *
     * @param event The event.
     */
    @EventHandler
    public final void onChatEvent(final AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();

        if (Mutelist.isPlayerMuted(playerUUID))
        {
            event.setCancelled(true);

            // TODO: Make that phrase configurable
            if (Mutelist.isSelfUngaggable(playerUUID) && event.getMessage().equals("I agree. Allow me to chat."))
            {
                Mutelist.unmute(playerUUID);
            }
            else
            {
                player.sendMessage("You are muted for: ");
                player.sendMessage(Mutelist.getPlayerMutereason(playerUUID));
                player.sendMessage("You cannot chat until you say &6the ungag key phrase.");
            }
        }

        if (module.isSilenceEnabled())
        {
            if (!player.hasPermission(AsshatModule.SILENCE_BYPASS_PERM))
            {
                event.setCancelled(true);
                player.sendMessage("Be quiet! Global silence is enabled.");
            }
        }
    }

    /**
     * Handles banned players.
     *
     * @param event The event.
     */
    @EventHandler
    public final void onPlayerLogin(final AsyncPlayerPreLoginEvent event)
    {
        final String playerName = event.getName();

        final UUID playerUUID;
        try
        {
            playerUUID = UUIDFetcher.getUUIDOf(playerName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "We're having trouble determining your UUID. Please get in touch with an admin.");
            return;
        }

        if (Banlist.isPlayerBanned(playerUUID))
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Banlist.getPlayerBanreason(playerUUID));
        }
    }

    /**
     * Handles global freeze.
     *
     * @param event The event.
     */
    @EventHandler
    public final void onPlayerMove(final PlayerMoveEvent event)
    {
        final Player player = event.getPlayer();

        if (module.isFreezeEnabled())
        {
            if (!player.hasPermission(AsshatModule.FREEZE_BYPASS_PERM))
            {
                event.setCancelled(true);
            }
        }
    }
}
