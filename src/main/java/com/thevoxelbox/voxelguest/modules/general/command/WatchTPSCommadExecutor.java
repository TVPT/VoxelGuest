package com.thevoxelbox.voxelguest.modules.general.command;

import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 */
public final class WatchTPSCommadExecutor implements CommandExecutor
{
    private final GeneralModule module;

    /**
     * Creates a new watch tps command executor instance.
     *
     * @param module The owning module.
     */
    public WatchTPSCommadExecutor(final GeneralModule module)
    {
        this.module = module;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        if (sender instanceof Player)
        {
            this.module.getLagmeter().togglePlayer((Player) sender);
            return true;
        }
        return false;
    }

}
