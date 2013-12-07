package com.thevoxelbox.voxelguest.modules.regions.snapshotting;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * @author monofraps
 */
public class Snapshot
{
    private final Region region;
    private final CommandSender commandSender;

    public Snapshot(final Region region, final CommandSender commandSender)
    {
        this.region = region;
        this.commandSender = commandSender;
    }

    public void create()
    {
        commandSender.sendMessage(String.format("Region Snapshot: Starting chunk gathering for region %s in world %s...", region.getName(), region.getWorldName()));
        final ChunkCollector chunkCollector = new ChunkCollector();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoxelGuest.getPluginInstance(), chunkCollector);
    }

    /**
     * Collects all chunks that should be included in the snapshot.
     */
    private class ChunkCollector implements Runnable
    {
        private final List<ChunkSnapshot> chunks = new ArrayList<>();
        private final World world = Bukkit.getWorld(region.getWorldName());

        private final Chunk startChunk = world.getChunkAt(region.getLowerBound().getBlockX(), region.getLowerBound().getBlockZ());
        private final Chunk endChunk = world.getChunkAt(region.getUpperBound().getBlockX(), region.getUpperBound().getBlockZ());
        private int currentChunkX = startChunk.getX() - 1;
        private int currentChunkZ = startChunk.getZ() - 1;

        @Override
        public void run()
        {
            final long startTime = System.currentTimeMillis();
            for (currentChunkX++; currentChunkX <= endChunk.getX(); currentChunkX++)
            {
                for (currentChunkZ++; currentChunkZ <= endChunk.getZ(); currentChunkZ++)
                {
                    chunks.add(world.getChunkAt(currentChunkX, currentChunkZ).getChunkSnapshot());

                    if (System.currentTimeMillis() - startTime > 1000)
                    {
                        scheduleNextCollectionStep();
                        return;
                    }
                }
            }

            scheduleSnapshotCreation();
        }

        private void scheduleNextCollectionStep()
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoxelGuest.getPluginInstance(), this, 40);
        }

        private void scheduleSnapshotCreation()
        {
            commandSender.sendMessage(String.format("Region Snapshot: Chunks gathered. - Gathered %d chunks.", chunks.size()));
            commandSender.sendMessage(String.format("Region Snapshot: Starting actual snapshot operation of region %s in world %s...", region.getName(), region.getWorldName()));
            final SnapshotCreator snapshotCreator = new SnapshotCreator(this);
            Bukkit.getScheduler().runTaskAsynchronously(VoxelGuest.getPluginInstance(), snapshotCreator);
        }
    }

    private class SnapshotCreator implements Runnable
    {
        private final ChunkCollector chunkCollector;

        private SnapshotCreator(final ChunkCollector chunkCollector)
        {
            this.chunkCollector = chunkCollector;
        }

        @Override
        public void run()
        {
            try
            {
                final File snapshotDirectory = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s", region.getWorldName()));

                if (!snapshotDirectory.mkdirs() && !snapshotDirectory.isDirectory())
                {
                    commandSender.sendMessage("Failed to create snapshot: Failed to create snapshot directory.");
                    Bukkit.getLogger().severe(String.format("Failed to create snapshot: Failed to create snapshot directory %s.", snapshotDirectory.getAbsolutePath()));
                    return;
                }

                int snapshotNumber = 0;
                File snapshotFile = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s/%s-%d.snps", region.getWorldName(), region.getName(), snapshotNumber));

                while (!snapshotFile.createNewFile())
                {
                    snapshotFile = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s/%s-%d.snps", region.getWorldName(), region.getName(), ++snapshotNumber));
                }

                final FileOutputStream fileStream = new FileOutputStream(snapshotFile);
                final GZIPOutputStream gzipStream = new GZIPOutputStream(fileStream);
                final DataOutputStream dataStream = new DataOutputStream(gzipStream);

                final World world = Bukkit.getWorld(region.getWorldName());

                for (final ChunkSnapshot cs : chunkCollector.chunks)
                {
                    for (int x = 0; x < 16; x++)
                    {
                        for (int z = 0; z < 16; z++)
                        {
                            for (int y = 0; y < world.getMaxHeight(); y++)
                            {
                                int currentGlobalX = x + (cs.getX() - 1) * 16;
                                int currentGLobalZ = z + (cs.getZ() - 1) * 16;

                                if (currentGlobalX < region.getLowerBound().getBlockX() || currentGlobalX > region.getUpperBound().getBlockX())
                                {
                                    Bukkit.getLogger().info("Skipping block");
                                    continue;
                                }
                                if (currentGLobalZ < region.getLowerBound().getBlockZ() || currentGLobalZ > region.getUpperBound().getBlockZ())
                                {
                                    Bukkit.getLogger().info("Skipping block");
                                    continue;
                                }
                                if (y < region.getLowerBound().getBlockY() || y > region.getUpperBound().getBlockY())
                                {
                                    Bukkit.getLogger().info("Skipping block");
                                    continue;
                                }

                                dataStream.writeInt(cs.getBlockTypeId(x, y, z));
                            }
                        }
                    }
                }

                dataStream.close();
                gzipStream.close();
                fileStream.close();

                commandSender.sendMessage(String.format("Region Snapshot: Snapshot of region %s in world %s done.", region.getName(), region.getWorldName()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
