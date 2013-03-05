package com.thevoxelbox.voxelguest.modules.helper;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelguest.persistence.Persistence;

/**
 *
 * @author TheCryoknight
 */
public final class HelperManager
{
    private final Set<ReviewRequest> activeReviews = new HashSet<>();
    private final Map<String, Helper> helpers = new HashMap<>();
    private final Map<Player, GuestHistoryEntry> lastReview = new HashMap<>();
    private static final long minTimeBetween = 1200000;

    /**
     * Opens up a new review request for the player provided.
     *
     * @param player Player to create review for.
     */
    public void newReview(final Player player)
    {
        if (this.canMakeNewReview(player))
        {
            final ReviewRequest review = new ReviewRequest(player, player.getLocation());
            this.activeReviews.add(review);
            this.notifyForNewReview(review);
            player.sendMessage(ChatColor.GRAY + "Please wait for a helper to come and review your build");
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You can not currently make a new review.");
        }
    }

    /**
     * Gets a review that matches the player provided
     * or null if no match exists.
     *
     * @param guest Player to query a review for
     * @return Review for the specified guest
     */
    public ReviewRequest getReview(final Player guest)
    {
        for (ReviewRequest review : this.activeReviews)
        {
            if (review.getGuest().getName().equals(guest.getName()))
            {
                return review;
            }
        }
        return null;
    }

    /**
     * Checks to see if a guest can make a new review.
     *
     * @param guest Player to check if they can create a new review
     * @return true if player can create a review
     */
    public boolean canMakeNewReview(final Player guest)
    {
        final ReviewRequest review = this.getReview(guest);
        if (review == null)
        {
            for (ReviewRequest request : this.activeReviews)
            {
                if (request.getGuest().getName().equalsIgnoreCase(guest.getName()))
                {
                    return false;
                }
            }
            final List<GuestHistoryEntry> tmpList = this.getGuestHistory(guest.getName());
            if (tmpList.size() != 0)
            {
                Collections.sort(tmpList);
                final long lastReview = tmpList.get(tmpList.size() - 1).getReviewTime();
                final long timeSinceLastReview = System.currentTimeMillis() - lastReview;
                if (timeSinceLastReview >= HelperManager.minTimeBetween)
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
            guest.sendMessage(ChatColor.RED + "You can not currently submit a review");
            return false;
        }
        return false;
    }

    /**
     * Closes a review request and stores a record of it to the database.
     *
     * @param helper The helper closing the review
     * @param review The review to be closed
     */
    public void closeReview(final Player helper, final ReviewRequest review)
    {
        this.activeReviews.remove(review);
        final GuestHistoryEntry reviewHistorical = new GuestHistoryEntry(review.getGuest().getName(), helper.getName());
        Persistence.getInstance().save(reviewHistorical);
        this.lastReview.put(helper, reviewHistorical);
        final Helper helperObj = this.getHelper(helper);
        if (helperObj != null)
        {
            helperObj.review();
        }
        Persistence.getInstance().save(helperObj);
    }

    /**
     * Adds a comment to the last review performed by the specified helper.
     *
     * @param helper Helper adding a comment
     * @param comment The comment being added
     */
    public void addComment(final Player helper, final String comment)
    {
        final GuestHistoryEntry historyEntry = this.lastReview.get(helper);
        if (historyEntry != null)
        {
            Persistence.getInstance().delete(historyEntry);
            historyEntry.setComment(comment);
            Persistence.getInstance().save(historyEntry);
            helper.sendMessage(ChatColor.GRAY + "Comment successfuly added");
        }
        else
        {
            helper.sendMessage(ChatColor.RED + "You havent reviewed anyone yet!");
        }
    }

    /**
     * Adds a helper to the non-administrator list of helpers and
     * saves them to the database (administrator list is permission based).
     *
     * @param newHelper Player name to add as a helper
     */
    public void addHelper(final String newHelper)
    {
        final Helper newHelperObj = new Helper(newHelper);
        Persistence.getInstance().save(newHelperObj);
        this.helpers.put(newHelperObj.getName(), newHelperObj);
    }

    /**
     * Removes a helper from the non-administrator list of helpers and deletes
     * them to the database (administrator list is permission based).
     *
     * @param oldHelper Helper to remove
     * @return True if successfully removed region
     */
    public boolean removeHelper(final Helper oldHelper)
    {
        if (this.helpers.containsKey(oldHelper.getName()))
        {
            return false;
        }
        Persistence.getInstance().delete(oldHelper);
        this.helpers.remove(oldHelper.getName());
        return true;
    }

    /**
     * Initializes the list of non-administrator helpers to ram.
     */
    public void initHelperList()
    {
        final List<Helper> tmpHelperList = Persistence.getInstance().loadAll(Helper.class);
        for (Helper helper : tmpHelperList)
        {
            this.helpers.put(helper.getName(), helper);
        }
    }

    /**
     * Gets the helper object corresponding with the player provided
     * or returns null if no player on list.
     *
     * @param helper Player to find corresponding helper object
     * @return helper object corresponding with the player
     */
    public Helper getHelper(final Player helper)
    {
        return this.helpers.get(helper.getName());
    }

    /**
     * Creates a highly formated string containing all information related to the
     * current list of active reviews OR returns null if there are no currently active reviews.
     * <br />
     * <strong>Note:</strong> Will not show reviews for people who are currently not online.
     *
     * @return A formatted string of all active reviews
     */
    public String getActiveRequests()
    {
        if (this.activeReviews.isEmpty())
        {
            return null;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ChatColor.DARK_GRAY + "========================\n");
        stringBuilder.append(ChatColor.GOLD + "Whitelist Requests\n");
        for (ReviewRequest request : this.activeReviews)
        {
            if (!request.getGuest().isOnline())
            {
                stringBuilder.append(ChatColor.STRIKETHROUGH);
            }
            stringBuilder.append(ChatColor.DARK_AQUA + "Name" + ChatColor.WHITE + ": "
                    + ChatColor.DARK_AQUA + request.getGuest().getName()
                    + ChatColor.DARK_GRAY + "(" + ChatColor.GOLD
                    + this.getGuestHistory(request.getGuest().getName()).size()
                    + ChatColor.DARK_GRAY + ")\n");
        }
        stringBuilder.append(ChatColor.DARK_GRAY + "========================\n");
        return stringBuilder.toString();
    }

    /**
     * Notifies all of the currently online helpers that there is a new whitelist review.
     *
     * @param review
     */
    public void notifyForNewReview(final ReviewRequest review)
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ChatColor.DARK_GRAY + "========================\n");
        stringBuilder.append(ChatColor.DARK_AQUA + "New Whitelist Review\n");
        stringBuilder.append(ChatColor.DARK_AQUA + "Name" + ChatColor.GRAY + ": " + ChatColor.GOLD + review.getGuest().getName() + "\n");
        stringBuilder.append(ChatColor.DARK_GRAY + "========================\n");

        this.notifyHelpers(stringBuilder.toString());
    }

    /**
     * Core notify method sends specified string to all online helpers.
     *
     * @param messageForHelpers Message to send
     */
    public void notifyHelpers(final String messageForHelpers)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (this.isHelper(player))
            {
                player.sendMessage(messageForHelpers);
            }
        }
    }

    /**
     * Searches through the database and finds all historical
     * guest reviews for the name provided.
     *
     * @param playerName Name of the guest to search for history
     * @return List of all review history
     */
    public List<GuestHistoryEntry> getGuestHistory(final String playerName)
    {
        final HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("guestName", playerName);
        return Persistence.getInstance().loadAll(GuestHistoryEntry.class, selectRestrictions);
    }

    /**
     * Creates, formats, and sends a message to the specified helper,
     * informing them of review history of a specified guest.
     *
     * @param helper The helper requesting the history
     * @param guestName The guest
     */
    public void sendHelperGuestHistory(final Player helper, final String guestName)
    {
        final List<GuestHistoryEntry> history = this.getGuestHistory(guestName); 
        if (history.isEmpty())
        {
            helper.sendMessage("Player has not yet been reviewed");
            return;
        }
        Collections.sort(history);
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ChatColor.DARK_GRAY + "=====================================\n");
        stringBuilder.append(ChatColor.DARK_AQUA + "Whitelist Review History for" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + guestName + "\n");
        stringBuilder.append(ChatColor.DARK_GRAY + "=====================================\n");

        final ListIterator<GuestHistoryEntry> reviewListItr = history.listIterator();
        while (reviewListItr.hasNext())
        {
            //TODO : Time zone stuffs
            final GuestHistoryEntry entry = reviewListItr.next();
            final Calendar date = new GregorianCalendar();
            date.setTimeInMillis(entry.getReviewTime());

            final String dateStr = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + ". "
                    + date.get(Calendar.DAY_OF_MONTH) + ", " + date.get(Calendar.YEAR) + " at "
                    + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE);

            stringBuilder.append(ChatColor.DARK_AQUA + "(" + ChatColor.GOLD + (reviewListItr.previousIndex() + 1) + ChatColor.DARK_AQUA + ")"
                    + ChatColor.GRAY + " by " + ChatColor.GOLD + entry.getReviewerName() + ChatColor.GRAY + " on "
                    + ChatColor.DARK_AQUA + dateStr);

            if (!entry.getComment().equals(""))
            {
                stringBuilder.append(ChatColor.GRAY.toString() + ChatColor.ITALIC + " - " + entry.getComment() + "\n");
            }
            else
            {
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append(ChatColor.DARK_GRAY + "=====================================\n");

        helper.sendMessage(stringBuilder.toString());
    }

    /**
     * Checks to see if a player is a helper.
     *
     * @param player player to check
     * @return true if player is a helper
     */
    public boolean isHelper(final Player player)
    {
        if (this.helpers.containsKey(player.getName()))
        {
            return true;
        }
        if (player.hasPermission("voxelguest.helper.adminhelper"))
        {
            return true;
        }
        return false;
    }

    public boolean isNonAdminHelper(final Player player)
    {
        if (this.helpers.containsKey(player.getName()))
        {
            return true;
        }
        return false;
    }

    public void handleLogin(final Player player)
    {
        if (this.isHelper(player))
        {
            final String msg = this.getActiveRequests();
            if (msg != null)
            {
                player.sendMessage(msg);
            }
        }
        if (this.isNonAdminHelper(player))
        {
            player.setMetadata("isHelper", this.getHelper(player));
        }
    }
}
