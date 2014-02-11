package com.thevoxelbox.voxelguest.modules.helper.command;

import com.thevoxelbox.voxelguest.modules.helper.HelperModule;
import com.thevoxelbox.voxelguest.modules.helper.model.ReviewRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Handles /review commands.
 *
 * @author TheCryoknight
 */
public final class HelperReviewCommand implements TabExecutor
{
    private final HelperModule module;

    /**
     * Create a new review command executor.
     *
     * @param module The owning module.
     */
    public HelperReviewCommand(final HelperModule module)
    {
        this.module = module;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (sender instanceof Player)
        {
            final Player player = (Player) sender;
            if (this.module.getManager().isHelper(player))
            {
                if (args.length >= 1)
                {
                    if (args[0].equalsIgnoreCase("-c"))
                    {
                        String comment = "";
                        for (int i = 1; i < args.length; i++)
                        {
                            comment += args[i];
                            comment += " ";
                        }
                        comment = comment.trim();
                        this.module.getManager().addComment(player, comment);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("-h"))
                    {
                        if (args.length == 2)
                        {
                            final List<Player> matches = Bukkit.matchPlayer(args[1]);
                            if (matches.size() == 1)
                            {
                                this.module.getManager().sendHelperGuestHistory(player, matches.get(0).getName());
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
                                    sender.sendMessage(ChatColor.DARK_RED + "No matches found for \"" + args[1] + "\"");
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            sender.sendMessage("Please specify a player!");
                            return true;
                        }
                    }
                    final List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.size() == 1)
                    {
                        final Player guest = matches.get(0);
                        final ReviewRequest review = this.module.getManager().getReview(guest);
                        if (review != null)
                        {
                            if (args.length > 2)
                            {
                                if (args[1].equalsIgnoreCase("-f"))
                                {
                                    player.teleport(review.getLocation());
                                    if (guest.isOnline())
                                    {
                                        guest.teleport(player);
                                    }
                                    this.module.getManager().closeReview(player, review);
                                    this.module.getManager().sendHelperGuestHistory(player, guest.getName());
                                    return true;
                                }
                            }
                            if (guest.isOnline())
                            {
                                player.teleport(review.getLocation());
                                guest.teleport(player);
                                this.module.getManager().closeReview(player, review);
                                this.module.getManager().sendHelperGuestHistory(player, guest.getName());
                            }
                            else
                            {
                                player.sendMessage(ChatColor.GRAY + "Guest is not online, please append a -f to force close the ticket.");
                            }
                            return true;
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "There is no open review from \"" + guest.getName() + "\"");
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
                else
                {
                    final String reviews = this.module.getManager().getActiveRequests();
                    if (reviews != null)
                    {
                        player.sendMessage(reviews);
                        return true;
                    }
                    else
                    {
                        player.sendMessage("There are currenly no WL reviews");
                        return true;
                    }
                }
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You are not a helper you can not do this command");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        return null;
    }
}
