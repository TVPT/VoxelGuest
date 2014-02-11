package com.thevoxelbox.voxelguest.modules.regions.snapshotting;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Rollback;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import sun.print.resources.serviceui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author monofraps
 */
public class Snapshot
{
    private final Region        region;
    private final CommandSender commandSender;

    public Snapshot(final Region region, final CommandSender commandSender)
    {
        this.region = region;
        this.commandSender = commandSender;
    }

    public void create()
    {
        commandSender.sendMessage(String.format("Region Snapshot: Starting chunk gathering for region %s in world %s...", region.getName(), region.getWorldName()));
        final ChunkCollectionTask chunkCollector = new ChunkCollectionTask(region, commandSender);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoxelGuest.getPluginInstance(), chunkCollector);
    }

    public void rollback(final int snapshotNumber) {
        commandSender.sendMessage(String.format("Rolling back region %s to version %s", region.getName(), snapshotNumber));
        final RollbackTask rollback = new RollbackTask(snapshotNumber, region, commandSender);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoxelGuest.getPluginInstance(), rollback);
    }

}
