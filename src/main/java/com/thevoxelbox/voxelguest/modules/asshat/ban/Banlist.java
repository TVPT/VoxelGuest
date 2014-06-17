package com.thevoxelbox.voxelguest.modules.asshat.ban;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.persistence.Persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    @Deprecated
    public static boolean ban(final String playerName, final String banReason)
    {
        if (isPlayerBanned(playerName))
        {
            return false;
        }

        Persistence.getInstance().save(new BannedPlayer(playerName.toLowerCase(), banReason));
        return true;
    }

    public static boolean ban(final UUID playerUid, final String banReason)
    {
        if (isPlayerBanned(playerUid))
        {
            return false;
        }

        Persistence.getInstance().save(new BannedUUIDPlayer(playerUid, banReason));
        return true;
    }

    /**
     * Unbans the player named playerName.
     *
     * @param playerName The name of the player to unban.
     *
     * @return Returns true if the unban operation was successful. False indicates that the player is not banned.
     */
    @Deprecated
    public static boolean unban(final String playerName)
    {
        if (!isPlayerBanned(playerName))
        {
            return false;
        }

        Persistence.getInstance().delete(getBannedPlayer(playerName));
        return true;
    }

    public static boolean unban(final UUID playerUid)
    {
        if (!isPlayerBanned(playerUid))
        {
            return false;
        }

        Persistence.getInstance().delete(getBannedPlayer(playerUid));
        return true;
    }

    /**
     * Loads all banned players from database and returns a String list of the player names.
     *
     * @return Returns a list of the names of all banned players.
     */
    @Deprecated
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

    public static List<UUID> getBannedUUIDs()
    {
        final List<BannedUUIDPlayer> bannedPlayers = Persistence.getInstance().loadAll(BannedUUIDPlayer.class);
        final List<UUID> bannedUUIDs = new ArrayList<>();
        for (final BannedUUIDPlayer bannedPlayer : bannedPlayers)
        {
            bannedUUIDs.add(bannedPlayer.getPlayerUUID());
        }
        return bannedUUIDs;
    }

    /**
     * Returns the number of banned players.
     *
     * @return Returns the number of banned players.
     */
    public static int getBanCount()
    {
        return getBannedNames().size() + getBannedUUIDs().size();
    }

    @Deprecated
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

    private static BannedUUIDPlayer getBannedPlayer(final UUID playerUUID)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("playerUUID", playerUUID);

        final List<BannedUUIDPlayer> bannedPlayers = Persistence.getInstance().loadAll(BannedUUIDPlayer.class, selectRestrictions);

        for (final BannedUUIDPlayer bannedPlayer : bannedPlayers)
        {
            if (bannedPlayer.getPlayerUUID().equals(playerUUID))
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
    @Deprecated
    public static boolean isPlayerBanned(final String playerName)
    {
        return getBannedPlayer(playerName) != null;
    }

    public static boolean isPlayerBanned(final UUID playerUUID)
    {
        return getBannedPlayer(playerUUID) != null;
    }

    /**
     * Gets the reason why a player is banned.
     *
     * @param playerName The name of the player to check.
     *
     * @return Returns the reason the player is banned for.
     */
    @Deprecated
    public static String getPlayerBanreason(final String playerName)
    {
        Preconditions.checkState(isPlayerBanned(playerName), "Player %s must be banned in order to get the ban reason.", playerName);
        return getBannedPlayer(playerName).getBanReason();
    }

    public static String getPlayerBanreason(final UUID playerUUID)
    {
        Preconditions.checkState(isPlayerBanned(playerUUID), "Player %s must be banned in order to get the ban reason.", playerUUID);
        return getBannedPlayer(playerUUID).getBanReason();
    }
}
