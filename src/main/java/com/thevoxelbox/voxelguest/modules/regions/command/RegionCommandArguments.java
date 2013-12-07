package com.thevoxelbox.voxelguest.modules.regions.command;

import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Command;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Create;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Help;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Modify;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Resize;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.RuleParams;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Snapshot;
import com.thevoxelbox.voxelguest.modules.regions.command.subcommand.debug.Debug;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

/**
 * @author Monofraps
 */
public final class RegionCommandArguments
{
    @Argument(index = 0, metaVar = "<sub-command>", required = true, handler = SubCommandHandler.class)
    @SubCommands({
            @SubCommand(name = "modify", impl = Modify.class),
            @SubCommand(name = "create", impl = Create.class),
            @SubCommand(name = "resize", impl = Resize.class),
            @SubCommand(name = "help", impl = Help.class),
            @SubCommand(name = "snapshot", impl = Snapshot.class),
            @SubCommand(name = "ruleparams", impl = RuleParams.class),
            @SubCommand(name = "debug", impl = Debug.class)
    })
    private Command cmd;

    /**
     * Returns the sub command instance.
     *
     * @return An instance of a class implementing com.thevoxelbox.voxelguest.modules.regions.command.subcommand.Command.
     */
    public Command getCmd()
    {
        return cmd;
    }
}
