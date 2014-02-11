package com.thevoxelbox.voxelguest.modules.regions.snapshotting;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @author monofraps
 */
public class SnapshotCreationTask implements Runnable
{
    private final static int CHUNK_SIZE = 16;
    private final static int REPORT_INTERVAL = 500;

    private final ChunkCollectionTask chunkCollector;
    private final Region              region;
    private final CommandSender       sender;

    private FileOutputStream fileStream;
    private GZIPOutputStream gzipStream;
    private DataOutputStream dataStream;

    public SnapshotCreationTask(final ChunkCollectionTask chunkCollector, final Region region, final CommandSender sender)
    {
        this.chunkCollector = chunkCollector;
        this.region = region;
        this.sender = sender;

        final File snapshotDirectory = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s", region.getWorldName()));

        if (!snapshotDirectory.mkdirs() && !snapshotDirectory.isDirectory())
        {
            sender.sendMessage("Failed to create snapshot: Failed to create snapshot directory.");
            Bukkit.getLogger().severe(String.format("Failed to create snapshot: Failed to create snapshot directory %s.", snapshotDirectory.getAbsolutePath()));
            return;
        }

        int snapshotNumber = 0;
        File snapshotFile = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s/%s-%d.snps", region.getWorldName(), region.getName(), snapshotNumber));

        while (snapshotFile.exists())
        {
            snapshotFile = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s/%s-%d.snps", region.getWorldName(), region.getName(), ++snapshotNumber));
        }

        try
        {
            fileStream = new FileOutputStream(snapshotFile);
            gzipStream = new GZIPOutputStream(fileStream);
            dataStream = new DataOutputStream(gzipStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void finish()
    {
        try
        {
            dataStream.close();
            gzipStream.close();
            fileStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        sender.sendMessage(String.format("Region Snapshot: Snapshot of region %s in world %s done.", region.getName(), region.getWorldName()));
    }

    /**
     * SNPS v0.1
     *
     * Snapshot format (files are GZIPed):
     * +------------------------------------+
     * | CHUNK 0                            |
     * +------------------------------------+
     * | CHUNK HEADER header0                |
     * | CHUNK DATA data0                   |
     * +------------------------------------+
     * | CHUNK n                            |
     * +------------------------------------+
     * | CHUNK HEADER headerN               |
     * | CHUNK DATA dataN                   |
     * +------------------------------------+
     *
     *
     * +------------------------------------+
     * | CHUNK HEADER                       |
     * +------------------------------------+
     * | INT chunkX                         |
     * | INT chunkZ                         |
     * +------------------------------------+
     * | CHUNK DATA                         |
     * +------------------------------------+
     * | INT blockId @ 0, 0, 0              |
     * | INT blockId @ 0, 1, 0              |
     * | INT blockId @ 0, 2, 0              |
     * | INT blockId @ 0, ..., 0            |
     * | INT blockId @ 0, worldHeight, 0    |
     * | INT blockId @ 0, 0, 1              |
     * | INT blockId @ 0, 1, 1              |
     * | INT blockId @ 0, ..., 1            |
     * | INT blockId @ 0, worldHeight, 1    |
     * | INT blockId @ ...                  |
     * | INT blockId @ 15, worldHeight, 15  |
     * +------------------------------------+
     *
     * Chunk data block coordinate iteration: x { z { y[0-worldHeight] }[0-15] }[0-15]
     *
     * All blocks in an affected chunk are written to file (this include blocks which don't actually belong to the region)
     */
    @Override
    public void run()
    {
        try
        {
            final World world = Bukkit.getWorld(region.getWorldName());

            int chunksWritten = 0;
            for (final ChunkSnapshot cs : chunkCollector.getChunks())
            {
                dataStream.writeInt(cs.getX());
                dataStream.writeInt(cs.getZ());

                chunksWritten++;
                if(chunksWritten % REPORT_INTERVAL == 0){
                    sender.sendMessage(String.format("%d chunks written", chunksWritten));
                }

                for (int x = 0; x < CHUNK_SIZE; x++)
                {
                    for (int z = 0; z < CHUNK_SIZE; z++)
                    {
                        for (int y = 0; y < world.getMaxHeight(); y++)
                        {
                            final int globalX = CHUNK_SIZE * cs.getX() + x;
                            final int globalZ = CHUNK_SIZE * cs.getZ() + z;

                            final Vector currentGlobalLocation = new Vector(globalX, y, globalZ);
                            if (!currentGlobalLocation.isInAABB(region.getLowerBound(), region.getUpperBound()))
                            {
                                dataStream.writeInt(0);
                                continue;
                            }

                            dataStream.writeInt(cs.getBlockTypeId(x, y, z));
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        chunkCollector.proceed();
    }
}
