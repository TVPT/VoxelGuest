package com.thevoxelbox.voxelguest.modules.regions.snapshotting;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all chunks that should be included in the snapshot.
 *
 * @author monofraps
 */
public class ChunkCollectionTask implements Runnable
{
    private final List<ChunkSnapshot> chunks = new ArrayList<>();
    private final World         world;
    private final CommandSender sender;
    private final Region        region;

    private final ChunkSnapshot startChunk;
    private final ChunkSnapshot endChunk;
    private       int           currentChunkX;
    private       int           currentChunkZ;
    private long    gatheringStartTime = 0;
    private int     chunksGathered     = 0;
    private boolean gatheringDone      = false;
    private boolean clear = false;

    private final SnapshotCreationTask snapshotCreator;


    public ChunkCollectionTask(final Region region, final CommandSender sender)
    {
        this.region = region;
        this.sender = sender;
        world = Bukkit.getWorld(region.getWorldName());

        startChunk = world.getChunkAt(new Location(world, region.getLowerBound().getBlockX(), 0, region.getLowerBound().getBlockZ())).getChunkSnapshot();
        endChunk = world.getChunkAt(new Location(world, region.getUpperBound().getBlockX(), 0, region.getUpperBound().getBlockZ())).getChunkSnapshot();
        currentChunkX = startChunk.getX();
        currentChunkZ = startChunk.getZ();

        snapshotCreator = new SnapshotCreationTask(this, region, sender);
    }

    @Override
    public void run()
    {
        if (gatheringStartTime == 0)
        {
            gatheringStartTime = System.currentTimeMillis();
        }

        if(clear) {
            chunks.clear();
            gatheringStartTime = System.currentTimeMillis();
            chunksGathered = 0;
            clear = false;
        }

        final long startTime = System.currentTimeMillis();
        for (; currentChunkX <= endChunk.getX(); currentChunkX++)
        {
            for (; currentChunkZ <= endChunk.getZ(); currentChunkZ++)
            {
                chunks.add(world.getChunkAt(currentChunkX, currentChunkZ).getChunkSnapshot());
                chunksGathered++;

                if (System.currentTimeMillis() - startTime >= 1000)
                {
                    scheduleNextCollectionStep();
                    return;
                }

                if (chunks.size() >= 5000)
                {
                    scheduleSnapshotCreation();
                    return;
                }
            }
            currentChunkZ = startChunk.getZ();
        }

        scheduleSnapshotCreation();
        gatheringDone = true;
    }

    private void scheduleNextCollectionStep()
    {
        final int chunksToGather = (endChunk.getX() - startChunk.getX() + 1) * (endChunk.getZ() - startChunk.getZ() + 1);
        final int chunksLeft = chunksToGather - chunksGathered;
        sender.sendMessage(String.format("Region Snapshot: Gathered %d/%d chunks. (%d left, %d chunks per second)", chunksGathered, chunksToGather, chunksLeft, chunksGathered / ((System.currentTimeMillis() - gatheringStartTime) / 1000)));
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoxelGuest.getPluginInstance(), this, 40);
    }

    private void scheduleSnapshotCreation()
    {
        sender.sendMessage(String.format("Region Snapshot: Gathered %d chunks.", chunksGathered));
        sender.sendMessage(String.format("Region Snapshot: Starting chunk file write of region %s in world %s...", region.getName(), region.getWorldName()));
        Bukkit.getScheduler().runTaskAsynchronously(VoxelGuest.getPluginInstance(), snapshotCreator);
    }

    public List<ChunkSnapshot> getChunks()
    {
        return chunks;
    }

    public void proceed()
    {
        clear = true;
        if (gatheringDone)
        {
            snapshotCreator.finish();
        }
        else
        {
            scheduleNextCollectionStep();
            sender.sendMessage("Region Snapshot: File write done, proceeding with chunk gathering...");
        }
    }
}
