package com.thevoxelbox.voxelguest.modules.general.command;

import com.google.common.base.Joiner;
import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author TheCryoknight
 */
public final class AfkCommandExecutor implements CommandExecutor
{
    private final GeneralModule module;

    /**
     * Creates a new /afk command executor.
     *
     * @param generalModule The owning parent module.
     */
    public AfkCommandExecutor(final GeneralModule generalModule)
    {
        this.module = generalModule;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            if (args.length != 0)
            {
                final String afkMsg = Joiner.on(" ").join(args);
                this.module.getAfkManager().toggleAfk(player, afkMsg);
                return true;
            }
            this.module.getAfkManager().toggleAfk(player, "");
            return true;
        }
        return false;
    }

}
