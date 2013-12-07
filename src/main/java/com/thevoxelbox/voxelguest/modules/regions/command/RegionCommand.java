package com.thevoxelbox.voxelguest.modules.regions.command;

import com.thevoxelbox.voxelguest.utils.A4JUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @author monofraps
 */
public final class RegionCommand implements TabExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        final RegionCommandArguments rootArguments = A4JUtils.parse(RegionCommandArguments.class, args, sender);
        if (rootArguments != null)
        {
            rootArguments.getCmd().execute(sender);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        return null;
    }
}
