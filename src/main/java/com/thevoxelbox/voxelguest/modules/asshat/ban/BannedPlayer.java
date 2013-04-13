package com.thevoxelbox.voxelguest.modules.asshat.ban;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "bans")
public class BannedPlayer
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String playerName;
    @DatabaseField
    private String banReason;

    /**
     * ORM constructor.
     */
    public BannedPlayer()
    {
    }

    /**
     * @param playerName The name of the banned player.
     * @param banReason  The reason the player is banned for.
     */
    public BannedPlayer(final String playerName, final String banReason)
    {
        this.playerName = playerName;
        this.banReason = banReason;
    }

    /**
     * @return Returns the name of the banned player.
     */
    public final String getPlayerName()
    {
        return playerName;
    }

    /**
     * @return Returns the reason the player is banned for.
     */
    public final String getBanReason()
    {
        return banReason;
    }
}
