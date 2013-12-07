package com.thevoxelbox.voxelguest.modules.regions.command.subcommand.debug;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.exceptions.NoSuchRegionException;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Command;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author Monofraps
 */
public final class RegionInfo implements Command
{
    @Argument(index = 0, metaVar = "<region-name>", required = true)
    private String regionName;
    @Option(name = "-world", aliases = {"-w"})
    private String worldName = "";

    @Override
    public boolean execute(final CommandSender sender)
    {
        // Check if we can get a world name (either through provided parameter or by using a players location)
        if (worldName.isEmpty() && !(sender instanceof Player))
        {
            sender.sendMessage("Failed to create region. Please specify a word when using 'resize' in console.");
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

        final Region region;
        try
        {
            region = module.getRegionProvider().byName(regionName, worldName);
        }
        catch (NoSuchRegionException e)
        {
            sender.sendMessage(String.format("Failed to modify region. Region %s does not exist in world %s", regionName, worldName));
            return true;
        }

        sender.sendMessage(region.toString());
        sender.sendMessage("Active Rules:");
        for (final String ruleId : region.getActiveRules())
        {
            sender.sendMessage(ruleId);
        }

        return true;
    }
}
