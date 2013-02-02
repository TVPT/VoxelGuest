package com.thevoxelbox.voxelguest.modules.afk;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.;

/**
 *
 * @author keto23
 */
public class PlayerListener implements Listener
{
    private AFKModule afkModule;

	/**
	 *
	 * @param afkModule AFKModule
	 */
    public PlayerListener(final AFKModule afkModule)
    {
	    this.afkModule = afkModule;
    }

	/**
	 *
	 * @param event PlayerQuitEvent
	 */
    @EventHandler
    public final void onPlayerQuit(final PlayerQuitEvent event)
    {
        checkAFK(event.getPlayer().getName(), false);
    }

	/**
	 *
	 * @param event PlayerKickEvent
	 */
    @EventHandler
    public final void onPlayerKick(final PlayerKickEvent event)
    {
		checkAFK(event.getPlayer().getName(), false);
    }

	/**
	 *
	 * @param event AsyncPlayerChatEvent
	 */
    @EventHandler(priority = EventPriority.LOWEST)
    public final void onPlayerChat(final AsyncPlayerChatEvent event)
    {
		checkAFK(event.getPlayer().getName(), true);
    }

	/**
	 *
	 * @param event PlayerMoveEvent
	 */
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerMove(final PlayerMoveEvent event)
    {
		checkAFK(event.getPlayer().getName(), true);
    }

	/**
	 *
	 * @param event PlayerInteractEvent
	 */
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerInteract(final PlayerInteractEvent event)
    {
		checkAFK(event.getPlayer().getName(), true);
    }

	/**
	 *
	 * @param event PlayerDropItemEvent
	 */
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerDropEvent(final PlayerDropItemEvent event)
    {
		checkAFK(event.getPlayer().getName(), true);
    }

	/**
	 *
	 * @param event PlayerEggThrowEvent
	 */
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEggThrowEvent(final PlayerEggThrowEvent event)
    {
		checkAFK(event.getPlayer().getName(), true);
    }

	/**
	 *
	 * @param event PlayerRespawnEvent
	 */
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerRespawnEvent(final PlayerRespawnEvent event)
    {
		checkAFK(event.getPlayer().getName(), true);
    }

	/**
	 *
	 * @param name Name of the player
	 * @param broadcast Whether to broadcast the user leaving afk
	 */
    private void checkAFK(final String name, final boolean broadcast)
    {
		if (afkModule.isAFK(name))
		{
	        afkModule.toggleAFK(name, broadcast, null);
		}
    }	  
}
