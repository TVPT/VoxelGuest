package com.thevoxelbox.voxelguest.modules.greylist.command;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistDAO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author MikeMatrix
 */
public final class UngreylistCommandExecutor implements CommandExecutor
{
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
                final String greylistee = Strings.nullToEmpty(args[0]);
                if (!GreylistDAO.isOnPersistentGreylist(greylistee))
                {
                    sender.sendMessage(String.format("%s is not already on the greylist.", greylistee));
                    return true;
                }
                GreylistDAO.ungreylist(greylistee);
                sender.sendMessage(String.format("Removed %s from greylist.", greylistee));
                return true;
            }
            else
            {
                sender.sendMessage("Please supply a player name to be ungraylisted.");
                return false;
            }
        }
        return false;
    }
}
