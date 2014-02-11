package com.thevoxelbox.voxelguest.modules.regions.command.subcommand.debug;

import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Command;
import org.bukkit.command.CommandSender;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

/**
 * @author monofraps
 */
public final class Debug implements Command
{
    @Argument(index = 0, metaVar = "<sub-command>", required = true, handler = SubCommandHandler.class)
    @SubCommands({
                         @SubCommand(name = "rinfo", impl = RegionInfo.class)
                 })
    private Command cmd;

    @Override
    public boolean execute(final CommandSender sender)
    {
        return cmd.execute(sender);
    }
}
