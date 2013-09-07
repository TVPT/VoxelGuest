package com.thevoxelbox.voxelguest.modules.general.listener;

import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import com.thevoxelbox.voxelguest.modules.general.GeneralModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.general.command.WhoCommandExecutor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles messages on player join/quit/kick.
 */
public final class ConnectionEventListener implements Listener
{
    private final GeneralModule module;
    private final GeneralModuleConfiguration configuration;

    /**
     * Creates a new ConnectionEventListener instance.
     *
     * @param generalModule The parent module.
     */
    public ConnectionEventListener(final GeneralModule generalModule)
    {
        this.module = generalModule;
        this.configuration = (GeneralModuleConfiguration) generalModule.getConfiguration();
    }

    /**
     * Handles player join events.
     * @param event The Bukkit event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        WhoCommandExecutor.updatePGM();
        final Player player = event.getPlayer();

        if (this.configuration.isDefaultWatchTPSState())
        {
            this.module.getLagmeter().setPlayerWatchState(player, true);
        }

        event.setJoinMessage(this.module.formatJoinLeaveMessage(configuration.getJoinFormat(), player.getName()));
        this.module.getVanishFakequitHandler().handleConnect(player);
        if (this.module.getVanishFakequitHandler().isPlayerFakequit(player))
        {
            event.setJoinMessage("");
        }
    }

    /**
     * Handles player quit events.
     * @param event The Bukkit event.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        WhoCommandExecutor.updatePGM();
        Player player = event.getPlayer();

        if (this.module.getLagmeter().isPlayerOnTpsWatch(player))
        {
            this.module.getLagmeter().setPlayerWatchState(player, false);
        }

        event.setQuitMessage(this.module.formatJoinLeaveMessage(configuration.getLeaveFormat(), player.getName()));

        if (this.module.getVanishFakequitHandler().handleDisconnect(player))
        {
            event.setQuitMessage("");
        }
    }

    /**
     * Handles player kick events.
     * @param event The Bukkit event.
     */
    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event)
    {
        WhoCommandExecutor.updatePGM();
        Player player = event.getPlayer();

        if (this.module.getLagmeter().isPlayerOnTpsWatch(player))
        {
            this.module.getLagmeter().setPlayerWatchState(player, false);
        }

        event.setLeaveMessage(this.module.formatJoinLeaveMessage(configuration.getKickFormat(), player.getName()));

        if (this.module.getVanishFakequitHandler().handleDisconnect(player))
        {
            event.setLeaveMessage("");
        }
    }
}
