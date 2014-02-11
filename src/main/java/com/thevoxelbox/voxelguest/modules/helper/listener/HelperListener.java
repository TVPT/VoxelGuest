package com.thevoxelbox.voxelguest.modules.helper.listener;

import com.thevoxelbox.voxelguest.modules.helper.HelperManager;
import com.thevoxelbox.voxelguest.modules.helper.HelperModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author TheCryoknight
 */
public final class HelperListener implements Listener
{
    private final HelperManager manager;

    /**
     * Creates a new helper listener instance.
     *
     * @param module The owning module.
     */
    public HelperListener(final HelperModule module)
    {
        manager = module.getManager();
    }

    /**
     * Handles player join events.
     *
     * @param event The Bukkit event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (player != null)
        {
            if (manager.isHelper(player))
            {
                final String msg = manager.getActiveRequests();
                if (msg != null)
                {
                    player.sendMessage(msg);
                }
            }
            if (manager.isNonAdminHelper(player))
            {
                player.setMetadata("isHelper", manager.getHelper(player));
            }
        }
    }
}
