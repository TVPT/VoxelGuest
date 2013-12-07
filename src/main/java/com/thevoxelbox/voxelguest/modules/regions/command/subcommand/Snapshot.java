package com.thevoxelbox.voxelguest.modules.regions.command.subcommand;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.exceptions.NoSuchRegionException;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author monofraps
 */
public final class Snapshot implements Command
{
    @Argument(index = 0, metaVar = "<region-name>", required = true)
    private String regionName;
    @Option(name = "-world", aliases = {"-w"})
    private String worldName = "";

    @Override
    public boolean execute(final CommandSender sender)
    {
        // Get world name by either parameter or player location
        if (worldName.isEmpty() && !(sender instanceof Player))
        {
            sender.sendMessage("Failed to snapshot region. Please specify a world when using 'resize' in console.");
            return false;
        }
        else if (worldName.isEmpty())
        {
            worldName = ((Player) sender).getWorld().getName();
        }

        // Translate world name into a world instance
        final World world = Bukkit.getWorld(worldName);
        if (world == null)
        {
            sender.sendMessage(String.format("Failed to snapshot region. World %s does not exist.", worldName));
            return true;
        }

        final RegionModule module = (RegionModule) VoxelGuest.getModuleManagerInstance().getModuleInstance(RegionModule.class);
        // extremely unlikely that module becomes null

        // Get the region
        final Region region;
        try
        {
            region = module.getRegionProvider().byName(regionName, worldName);
        }
        catch (NoSuchRegionException e)
        {
            sender.sendMessage(String.format("Failed to snapshot region. Region %s does not exist in world %s", regionName, worldName));
            return true;
        }

        // Initiate snapshot creation
        final com.thevoxelbox.voxelguest.modules.regions.snapshotting.Snapshot snapshot = new com.thevoxelbox.voxelguest.modules.regions.snapshotting.Snapshot(region, sender);
        snapshot.create();

        sender.sendMessage("Successfully started snapshot creation...");
        return true;
    }
}
