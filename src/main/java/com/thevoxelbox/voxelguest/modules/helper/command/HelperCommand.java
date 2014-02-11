package com.thevoxelbox.voxelguest.modules.helper.command;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.helper.HelperModule;
import com.thevoxelbox.voxelguest.modules.helper.model.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * @author TheCryoknight
 */
public final class HelperCommand implements TabExecutor
{
    private final HelperModule module;

    private static final String[] COMMAND_FLAGS = {"-add", "-remove", "-hist"};

    /**
     * Creates a new helper command executor.
     *
     * @param module The owning module.
     */
    public HelperCommand(final HelperModule module)
    {
        this.module = module;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (args.length >= 1 && args.length <= 3)
        {
            if (args[0].equalsIgnoreCase("-add"))
            {
                List<Player> matches = Bukkit.matchPlayer(args[1]);
                if (matches.size() == 1)
                {
                    Player matchedPlayer = matches.get(0);
                    this.module.getManager().addHelper(matchedPlayer.getName());
                    if (!matchedPlayer.hasMetadata("isHelper"))
                    {
                        matchedPlayer.setMetadata("isHelper", this.module.getManager().getHelper(matchedPlayer));
                    }
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
                        if (args.length == 3)
                        {
                            if (args[2].equalsIgnoreCase("-f"))
                            {
                                this.module.getManager().addHelper(args[1]);
                                sender.sendMessage(ChatColor.GRAY + "Sucessfully added offline player!");
                                return true;
                            }
                        }
                        sender.sendMessage(ChatColor.DARK_RED + "No online matches found for \"" + args[1] + "\" to add an offline player append the flag -f after their name");
                        return true;
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("-remove"))
            {
                List<Player> matches = Bukkit.matchPlayer(args[1]);
                if (matches.size() == 1)
                {
                    final Player matchedPlayer = matches.get(0);
                    final Helper oldHelper = this.module.getManager().getHelper(matchedPlayer);
                    if (oldHelper != null)
                    {
                        if (matchedPlayer.hasMetadata("isHelper"))
                        {
                            matchedPlayer.removeMetadata("isHelper", VoxelGuest.getPluginInstance());
                        }
                        this.module.getManager().removeHelper(oldHelper);
                        sender.sendMessage(ChatColor.GRAY + "Sucessfully removed!");
                        return true;
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.DARK_RED + "Player called \"" + matchedPlayer.getName() + "\" is not a helper");
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
            else if (args[0].equalsIgnoreCase("-hist"))
            {
                List<Player> matches = Bukkit.matchPlayer(args[1]);
                if (matches.size() == 1)
                {
                    final Player matchedPlayer = matches.get(0);
                    final Helper helper = this.module.getManager().getHelper(matchedPlayer);
                    if (helper != null)
                    {
                        final Calendar date = new GregorianCalendar();
                        date.setTimeInMillis(helper.getTimeOfLastReview());

                        final String dateStr = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + ". "
                                               + date.get(Calendar.DAY_OF_MONTH) + ", " + date.get(Calendar.YEAR) + " at "
                                               + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE);

                        final String helperHistMsg = ChatColor.GOLD + helper.getName()
                                                     + ChatColor.GRAY + " has reviewed "
                                                     + ChatColor.GOLD + helper.getReviews()
                                                     + ChatColor.GRAY + (helper.getReviews() == 1 ? " time" : " times")
                                                     + " and last reviewed on " + dateStr;

                        sender.sendMessage(helperHistMsg);
                        return true;
                    }
                    else
                    {
                        if (matchedPlayer.hasPermission("voxelguest.helper.adminhelper"))
                        {
                            sender.sendMessage(ChatColor.DARK_RED + "Player called \"" + matchedPlayer.getName()
                                               + "\" is an administrative helper no statistics are taken from administrative helpers.");
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.DARK_RED + "Player called \"" + matchedPlayer.getName() + "\" is not a helper");
                        }
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
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        if (sender.hasPermission("voxelguest.helper.control"))
        {
            if (args.length == 1)
            {
                final List<String> matches = new ArrayList<>();
                for (String flag : HelperCommand.COMMAND_FLAGS)
                {
                    if (flag.startsWith(args[0]))
                    {
                        matches.add(flag);
                    }
                }
                return matches;
            }
            else if (args.length == 0)
            {
                return Arrays.asList(HelperCommand.COMMAND_FLAGS);
            }
        }
        return null;
    }
}
