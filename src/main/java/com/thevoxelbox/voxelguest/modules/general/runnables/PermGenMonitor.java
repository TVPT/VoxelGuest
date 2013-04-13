package com.thevoxelbox.voxelguest.modules.general.runnables;

import com.thevoxelbox.voxelguest.modules.general.GeneralModuleConfiguration;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author Monofraps
 */
public final class PermGenMonitor implements Runnable
{
    private final GeneralModuleConfiguration configuration;
    private boolean sentBroadcastWarning = false;

    /**
     * @param configuration THe general module configuration
     */
    public PermGenMonitor(final GeneralModuleConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void run()
    {
        for (final MemoryPoolMXBean item : ManagementFactory.getMemoryPoolMXBeans())
        {
            String name = item.getName();
            MemoryUsage usage = item.getUsage();

            if (name != null && name.contains("Perm Gen") && !name.contains("ro") && !name.contains("rw"))
            {
                double permGenUsage = (double) usage.getUsed() / (double) usage.getMax();
                if (permGenUsage > ((double) configuration.getPermGenShutdownThreshold() / 100f))
                {
                    Bukkit.broadcastMessage(String.format("Perm Gen space exceeded %d%% usage! Forcing server shutdown to prevent data loss.", configuration.getPermGenShutdownThreshold()));
                    Bukkit.getLogger().severe("Perm Gen threshold exceeded. Forcing shutdown to prevent data loss.");
                    Bukkit.shutdown();
                    break;
                }

                if ((permGenUsage > ((double) configuration.getPermGenWarningThreshold() / 100f)) && !sentBroadcastWarning)
                {
                    Bukkit.getLogger().warning(String.format("WARNING: Perm Gen space exceeded %d%% usage! A server restart is recommended.", configuration.getPermGenWarningThreshold()));
                    Bukkit.broadcastMessage(String.format("WARNING: Perm Gen space exceeded %d%% usage! A server restart is recommended.", configuration.getPermGenWarningThreshold()));
                    sentBroadcastWarning = true;
                    break;
                }
            }
        }
    }
}
