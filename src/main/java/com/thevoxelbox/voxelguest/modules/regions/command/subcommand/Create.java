package com.thevoxelbox.voxelguest.modules.regions.command.subcommand;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.modules.regions.GuestRegion;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author monofraps
 */
public final class Create implements Command
{
    @Argument(index = 0, metaVar = "<region-name>", required = true)
    private String regionName;
    @Argument(index = 1, metaVar = "x1")
    private int x1;
    @Argument(index = 2, metaVar = "z1")
    private int z1;
    @Argument(index = 3, metaVar = "x2")
    private int x2;
    @Argument(index = 4, metaVar = "z2")
    private int z2;
    @Option(name = "-world", aliases = {"-w"})
    private String worldName = "";
    @Option(name = "-priority", aliases = {"-p", "-pri", "-prio"})
    private Integer regionPriority = 0;

    @Override
    public boolean execute(final CommandSender sender)
    {
        // Check if we can get a world name (either through provided parameter or by using a players location)
        if (worldName.isEmpty() && !(sender instanceof Player))
        {
            sender.sendMessage("Failed to create region. Please specify a word when using 'create' in console.");
            return false;
        }
        else if (worldName.isEmpty())
        {
            worldName = ((Player) sender).getWorld().getName();
        }

        // Translate world name into world instance
        final World world = Bukkit.getWorld(worldName);
        if (world == null)
        {
            sender.sendMessage(String.format("Failed to create region. World %s does not exist.", worldName));
            return true;
        }

        final RegionModule module = (RegionModule) VoxelGuest.getModuleManagerInstance().getModuleInstance(RegionModule.class);
        // extremely unlikely that module becomes null

        if (module.getRegionProvider().regionExists(regionName, worldName))
        {
            sender.sendMessage(String.format("Failed to create region. Region %s already exists in world %s", regionName, worldName));
            return true;
        }

        final Region region = new GuestRegion(regionName, new Location(world, x1, 0, z1), new Location(world, x2, world.getMaxHeight(), z2));
        region.setPriority(regionPriority);
        module.getRegionProvider().saveRegion(region);

        sender.sendMessage(String.format("Successfully created region %s in world %s.", regionName, worldName));

        return true;
    }
}
