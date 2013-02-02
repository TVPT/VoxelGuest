package com.thevoxelbox.voxelguest.modules.greylist.command;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author MikeMatrix
 */
public class UngreylistCommandExecutor implements CommandExecutor
{
    private GreylistModule greylistModule;

    public UngreylistCommandExecutor(final GreylistModule greylistModule)
    {
        this.greylistModule = greylistModule;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        Preconditions.checkNotNull(sender);
        Preconditions.checkNotNull(command);
        Preconditions.checkNotNull(args);

        if (sender.hasPermission("voxelguest.greylist.ungreylist"))
        {
            if (args.length == 1)
            {
                String greylistee = Strings.nullToEmpty(args[0]);
                greylistModule.ungreylist(greylistee);
                sender.sendMessage(String.format("Removed %s from greylist.", greylistee));
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }
}
