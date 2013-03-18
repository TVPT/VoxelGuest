package com.thevoxelbox.voxelguest.modules.asshat.command;

import com.thevoxelbox.voxelguest.modules.asshat.AsshatModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Executes /banreason commands.
 *
 * @author Monofraps
 */
public class BanreasonCommandExecutor implements TabExecutor
{
    private final AsshatModule module;

    /**
     * Creates a new banreason command executor.
     *
     * @param module The owning module.
     */
    public BanreasonCommandExecutor(final AsshatModule module)
    {
        this.module = module;
    }

    @Override
    public final boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if (args.length < 1)
        {
            commandSender.sendMessage("Invalid number of parameters.");
            return false;
        }

        final String playerName = args[0];

        if (!module.getBanlist().isPlayerBanned(playerName))
        {
            commandSender.sendMessage(String.format("Player %s is not even banned.", playerName));
            return true;
        }

        commandSender.sendMessage(String.format("%s is banned for %s", playerName, module.getBanlist().whyIsPlayerBanned(playerName)));
        return true;
    }

    @Override
    public final List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] args)
    {
        if (commandSender.hasPermission("voxelguest.asshat.banreason"))
        {
            final List<String> bannedNamesList = this.module.getBanlist().getBannedNames();
            if (args.length == 0)
            {
                return bannedNamesList;
            }
            else
            {
                final List<String> matches = new ArrayList<>();
                final String completingParam = args[args.length - 1];
                for (String bannedName : bannedNamesList)
                {
                    if (bannedName.toLowerCase().startsWith(completingParam.toLowerCase()))
                    {
                        matches.add(bannedName);
                    }
                }
                return matches;
            }
        }

        return Collections.emptyList();
    }
}
