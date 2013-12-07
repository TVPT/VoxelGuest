package com.thevoxelbox.voxelguest.modules.regions.command.subcommand;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.exceptions.NoSuchRegionException;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.modules.regions.RuleIndex;
import com.thevoxelbox.voxelguest.modules.regions.command.RuleModification;
import com.thevoxelbox.voxelguest.modules.regions.command.handler.RuleModificationListHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Monofraps
 */
public final class Modify implements Command
{
    @Argument(index = 0, metaVar = "<region-name>", required = true)
    private String regionName;
    @Option(name = "-rule", metaVar = "<handle>:<on|off>", handler = RuleModificationListHandler.class)
    private List<RuleModification> ruleModifications = new ArrayList<>();
    @Option(name = "-priority", aliases = {"-p", "-pri", "-prio"})
    private Integer regionPriority = null;
    @Option(name = "-world", aliases = {"-w"})
    private String worldName = "";

    @Override
    public boolean execute(final CommandSender sender)
    {
        // Check if we can get a world name (either through provided parameter or by using a players location)
        if (worldName.isEmpty() && !(sender instanceof Player))
        {
            sender.sendMessage("Failed to modify region. Please specify a world when using 'modify' in console.");
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
            sender.sendMessage(String.format("Failed to modify region. World %s does not exist.", worldName));
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

        for (final RuleModification modification : ruleModifications)
        {
            Bukkit.getLogger().fine(modification.toString());

            // Translate region handle to rule ID
            final String ruleId = RuleIndex.translateHandle(modification.getRulId());
            if (ruleId.isEmpty())
            {
                sender.sendMessage("No such rule ID: " + modification.getRulId());
                continue;
            }

            // Add/Remove rule to/from region
            if (region.getActiveRules().contains(ruleId) && !modification.isEnable())
            {
                region.getActiveRules().remove(ruleId);
            }
            else if ((!region.getActiveRules().contains(ruleId)) && modification.isEnable())
            {
                region.getActiveRules().add(ruleId);
            }
        }

        if (regionPriority != null)
        {
            region.setPriority(regionPriority);
        }

        module.getRegionProvider().saveRegion(region);


        return true;
    }
}
