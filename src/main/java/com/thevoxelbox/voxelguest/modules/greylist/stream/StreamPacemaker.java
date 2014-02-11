package com.thevoxelbox.voxelguest.modules.greylist.stream;

import org.bukkit.Bukkit;

/**
 * @author Monofraps
 */
@Deprecated
public final class StreamPacemaker extends Thread
{
    private StreamThread streamThread;

    public StreamPacemaker(final StreamThread streamThread)
    {
        this.streamThread = streamThread;
    }

    public void kill()
    {
        if (streamThread.isAlive())
        {
            streamThread.killProcesses();
            streamThread.interrupt();
        }
    }

    @Override
    public void run()
    {
        if (!streamThread.isAlive())
        {
            Bukkit.getLogger().warning("Greylist Stream Thread died. Gonna restart it.");
            streamThread.start();
        }
    }
}
