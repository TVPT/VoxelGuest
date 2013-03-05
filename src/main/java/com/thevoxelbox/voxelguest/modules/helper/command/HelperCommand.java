package com.thevoxelbox.voxelguest.modules.helper.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelguest.modules.helper.Helper;
import com.thevoxelbox.voxelguest.modules.helper.HelperModule;

/**
 *
 * @author TheCryoknight
 */
public class HelperCommand implements TabExecutor
{
    private final HelperModule module;

    public HelperCommand(final HelperModule module)
    {
        this.module = module;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command,
            final String label, final String[] args)
    {
        if (args.length >= 2)
        {
            if (args[0].equalsIgnoreCase("-add"))
            {
                List<Player> matches = Bukkit.matchPlayer(args[1]);
                if (matches.size() == 1)
                {
                    this.module.getManager().addHelper(matches.get(0).getName());
                    sender.sendMessage(ChatColor.GRAY + "Sucessfully added!");
                    return true;
                }
                else
                {
                    if (matches.size() > 1)
                    {
                        sender.sendMessage(ChatColor.DARK_RED + "Multiple matches found for \"" + args[1] + "\"");
                        return true;
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.DARK_RED + "No online matches found for \"" + args[1] + "\"");
                        this.module.getManager().addHelper(args[1]);
                        sender.sendMessage(ChatColor.GRAY + "Sucessfully added offline player!");
                        return true;
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("-remove"))
            {
                List<Player> matches = Bukkit.matchPlayer(args[1]);
                if (matches.size() == 1)
                {
                    Helper oldHelper = this.module.getManager().getHelper(matches.get(0));
                    if (oldHelper != null)
                    {
                        this.module.getManager().removeHelper(oldHelper);
                        sender.sendMessage(ChatColor.GRAY + "Sucessfully removed!");
                        return true;
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.DARK_RED + "Player called \"" + matches.get(0).getName() + "\" is not a helper");
                        return true;
                    }
                }
                else
                {
                    if (matches.size() > 1)
                    {
                        sender.sendMessage(ChatColor.DARK_RED + "Multiple matches found for \"" + args[1] + "\"");
                        return true;
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.DARK_RED + "No matches found for \"" + args[1] + "\"");
                        return true;
                    }
                }
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Invalid Syntax!");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
            final String alias, final String[] args)
    {
        return null;
    }
}
