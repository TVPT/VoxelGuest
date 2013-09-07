package com.thevoxelbox.voxelguest.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

/**
 * Utility that prevents a chat message from flooding users chat log.
 *
 * @author TheCryoknight
 */
public class FloodProtection
{
    private final Map<Player, Long> playerTimeMap = new HashMap<>();
    private final long sendThreshold;

    /**
     * Constructs a FloodProtection object with the default <code>sendThreshold</code> of 1 second.
     */
    public FloodProtection()
    {
        this(1000L);
    }

    /**
     * Constructs a FloodProtection object with the provided <code>sendThreshold</code> in milliseconds.
     *
     * @param sendThreshold Millisecond length of the <code>sendThreshold</code>
     */
    public FloodProtection(final long sendThreshold)
    {
        this.sendThreshold = sendThreshold;
    }

    /**
     * Safely sends the provided message to the provided player.
     * This will not send the message if there was another attempt
     * to send a message to this user within the <code>sendThreshold</code>.
     *
     * @param target Player to send the message to
     * @param msg Message to send
     */
    public void sendMessage(final Player target, final String msg)
    {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(msg);

        if(playerTimeMap.containsKey(target))
        {
            final long timeDiff = System.currentTimeMillis() - playerTimeMap.get(target);
            if (timeDiff > sendThreshold)
            {
                target.sendMessage(msg);
            }
            playerTimeMap.put(target, System.currentTimeMillis());
        }
        else
        {
            target.sendMessage(msg);
            playerTimeMap.put(target, System.currentTimeMillis());
        }
    }
    public void sendMessage(final Player target, final String msg, final Object... arg)
    {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(msg);
        Preconditions.checkNotNull(arg);

        if(playerTimeMap.containsKey(target))
        {
            final long timeDiff = System.currentTimeMillis() - playerTimeMap.get(target);
            if (timeDiff > sendThreshold)
            {
                target.sendMessage(String.format(msg, arg));
            }
            playerTimeMap.put(target, System.currentTimeMillis());
        }
        else
        {
            target.sendMessage(String.format(msg, arg));
            playerTimeMap.put(target, System.currentTimeMillis());
        }
    }
}
