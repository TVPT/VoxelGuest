package com.thevoxelbox.voxelguest.modules.regions.snapshotting;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author monofraps
 */
public class RollbackTask implements Runnable
{
    private final int           version;
    private final Region        region;
    private final CommandSender sender;

    public RollbackTask(final int version, final Region region, final CommandSender sender)
    {
        this.version = version;
        this.region = region;
        this.sender = sender;
    }

    @Override
    public void run()
    {
        final File snapshotFile = new File(VoxelGuest.getPluginInstance().getDataFolder() + String.format("/snapshots/%s/%s-%d.snps", region.getWorldName(), region.getName(), version));
        if (!snapshotFile.exists())
        {
            sender.sendMessage("Couldn't find snapshot version " + version);
        }

        FileInputStream fileStream = null;
        GZIPInputStream gzipStream = null;
        DataInputStream dataStream = null;

        try
        {
            fileStream = new FileInputStream(snapshotFile);
            gzipStream = new GZIPInputStream(fileStream);
            dataStream = new DataInputStream(gzipStream);

            while (dataStream.available() > 0)
            {
                final int chunkX = dataStream.readInt();
                final int chunkZ = dataStream.readInt();

                final World world = Bukkit.getWorld(region.getWorldName());
                final Chunk chunk = world.getChunkAt(chunkX, chunkZ);

                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        for (int y = 0; y < world.getMaxHeight(); y++)
                        {
                            final int globalX = 16 * chunkX + x;
                            final int globalZ = 16 * chunkZ + z;

                            final Vector currentGlobalLocation = new Vector(globalX, y, globalZ);
                            if (!currentGlobalLocation.isInAABB(region.getLowerBound(), region.getUpperBound()))
                            {
                                dataStream.readInt();
                                continue;
                            }

                            chunk.getBlock(x, y, z).setTypeId(dataStream.readInt());
                        }
                    }
                }
            }
        }
        catch (EOFException ignored) {
            try {
                if(dataStream != null) {
                dataStream.close();
                }
                if (gzipStream != null) {
                    gzipStream.close();
                }
                fileStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        sender.sendMessage("Region rollback successful.");
    }
}
