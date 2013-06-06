package com.thevoxelbox.voxelguest.modules.asshat.ban;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.persistence.Persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a list of banned players.
 *
 * @author Monofraps
 */
public final class Banlist
{
    private Banlist()
    {

    }

    /**
     * Bans the player named playerName and stores the ban reason.
     *
     * @param playerName The name of the player to ban.
     * @param banReason  The reason the player is banned for.
     *
     * @return Returns true if the ban operation was successful. False indicates that the player is already banned.
     */
    public static boolean ban(final String playerName, final String banReason)
    {
        if (isPlayerBanned(playerName))
        {
            return false;
        }

        Persistence.getInstance().save(new BannedPlayer(playerName.toLowerCase(), banReason));
        return true;
    }

    /**
     * Unbans the player named playerName.
     *
     * @param playerName The name of the player to unban.
     *
     * @return Returns true if the unban operation was successful. False indicates that the player is not banned.
     */
    public static boolean unban(final String playerName)
    {
        if (!isPlayerBanned(playerName))
        {
            return false;
        }

        Persistence.getInstance().delete(getBannedPlayer(playerName));
        return true;
    }

    /**
     * Loads all banned players from database and returns a String list of the player names.
     *
     * @return Returns a list of the names of all banned players.
     */
    public static List<String> getBannedNames()
    {
        final List<BannedPlayer> bannedPlayers = Persistence.getInstance().loadAll(BannedPlayer.class);
        final List<String> bannedNames = new ArrayList<>();
        for (final BannedPlayer bannedPlayer : bannedPlayers)
        {
            bannedNames.add(bannedPlayer.getPlayerName());
        }
        return bannedNames;
    }

    /**
     * Returns the number of banned players.
     *
     * @return Returns the number of banned players.
     */
    public static int getBanCount()
    {
        return getBannedNames().size();
    }

    private static BannedPlayer getBannedPlayer(final String playerName)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("playerName", playerName.toLowerCase());

        final List<BannedPlayer> bannedPlayers = Persistence.getInstance().loadAll(BannedPlayer.class, selectRestrictions);

        for (final BannedPlayer bannedPlayer : bannedPlayers)
        {
            if (bannedPlayer.getPlayerName().equalsIgnoreCase(playerName))
            {
                return bannedPlayer;
            }
        }

        return null;
    }

    /**
     * Checks if a player is banned.
     *
     * @param playerName The name of the player to check.
     *
     * @return Returns true if the player is banned, otherwise false.
     */
    public static boolean isPlayerBanned(final String playerName)
    {
        return getBannedPlayer(playerName) != null;
    }

    /**
     * Gets the reason why a player is banned.
     *
     * @param playerName The name of the player to check.
     *
     * @return Returns the reason the player is banned for.
     */
    public static String getPlayerBanreason(final String playerName)
    {
        Preconditions.checkState(isPlayerBanned(playerName), "Player %s must be banned in order to get the ban reason.", playerName);
        return getBannedPlayer(playerName).getBanReason();
    }
}
