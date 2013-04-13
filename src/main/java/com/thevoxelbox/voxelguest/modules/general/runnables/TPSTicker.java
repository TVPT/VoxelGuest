package com.thevoxelbox.voxelguest.modules.general.runnables;

/**
 * Helps determine the number of ticks in a second by polling the system time at select intervals.
 *
 * @author TheCryoknight
 */
public final class TPSTicker implements Runnable
{
    public static final long POLL_INTERVAL = 60L;
    private static long lastTimestamp = System.currentTimeMillis() - (POLL_INTERVAL * 50);
    private static long lastDifference = POLL_INTERVAL;

    /**
     * Calculates the number of ticks in a second
     *
     * @return Number of ticks in a second
     */
    public static double calculateTPS()
    {
        if (lastDifference == 0)
        {
            lastDifference = 1;
        }

        return (double) POLL_INTERVAL / lastDifference;
    }

    @Override
    public void run()
    {
        lastDifference = (System.currentTimeMillis() - lastTimestamp) / 1000;
        lastTimestamp = System.currentTimeMillis();
    }
}
