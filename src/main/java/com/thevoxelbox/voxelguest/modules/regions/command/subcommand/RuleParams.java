package com.thevoxelbox.voxelguest.modules.regions.command.subcommand;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.exceptions.NoSuchRegionException;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import com.thevoxelbox.voxelguest.modules.regions.RuleIndex;
import com.thevoxelbox.voxelguest.modules.regions.command.handler.SettingsValuesHandler;
import com.thevoxelbox.voxelguest.modules.regions.settings.RuleSettingsDAO;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author Monofraps
 */
public final class RuleParams implements Command
{
    @Argument(index = 0, metaVar = "<region-name>", required = true)
    private String regionName;
    @Argument(index = 1, metaVar = "<rule-handle>", required = true)
    private String ruleHandle;
    @Argument(index = 2, metaVar = "[<settings>]", handler = SettingsValuesHandler.class)
    private String  settingsParams = "";
    @Option(name = "-priority", aliases = {"-p", "-pri", "-prio"})
    private Integer rulePriority   = null;
    @Option(name = "-world", aliases = {"-w"})
    private String  worldName      = "";

    @Override
    public boolean execute(final CommandSender sender)
    {
        // Check if we can get a world name (either through provided parameter or by using a players location)
        if (worldName.isEmpty() && !(sender instanceof Player))
        {
            sender.sendMessage("Failed to modify settings. Please specify a world when using 'ruleparams' in console.");
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
            sender.sendMessage(String.format("Failed to modify settings. World %s does not exist.", worldName));
            return true;
        }

        final RegionModule module = (RegionModule) VoxelGuest.getModuleManagerInstance().findStateContainer(RegionModule.class).getModule();
        // extremely unlikely that module becomes null

        final Region region;
        try
        {
            region = module.getRegionProvider().byName(regionName, worldName);
        }
        catch (NoSuchRegionException e)
        {
            sender.sendMessage(String.format("Failed to modify settings. Region %s does not exist in world %s", regionName, worldName));
            return true;
        }

        final String ruleId = RuleIndex.translateHandle(ruleHandle);
        if (ruleId.isEmpty())
        {
            sender.sendMessage("No such rule ID: " + ruleHandle);
            return true;
        }

        if (!region.getActiveRules().contains(ruleId))
        {
            sender.sendMessage(String.format("Region %s in world %s does not have rule %s enabled.", regionName, worldName, ruleHandle));
            return true;
        }

        final RuleExecutionSettings settings = RuleSettingsDAO.getSettings(region, ruleId);
        settings.handleChange(settingsParams);
        RuleSettingsDAO.save(region, settings, ruleId);

        if (rulePriority != null)
        {
            settings.setPriority(rulePriority);
        }

        return true;
    }
}
