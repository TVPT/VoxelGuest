package com.thevoxelbox.voxelguest.modules.asshat.ban;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "bans_uuid")
public class BannedUUIDPlayer
{
    @DatabaseField(generatedId = true)
    private long   id;
    @DatabaseField
    private UUID   playerUUID;
    @DatabaseField
    private String banReason;

    /**
     * ORM constructor.
     */
    public BannedUUIDPlayer()
    {
    }

    /**
     * @param playerName The name of the banned player.
     * @param banReason  The reason the player is banned for.
     */
    public BannedUUIDPlayer(final UUID playerUUID, final String banReason)
    {
        this.playerUUID = playerUUID;
        this.banReason = banReason;
    }

    public final UUID getPlayerUUID()
    {
        return playerUUID;
    }

    /**
     * @return Returns the reason the player is banned for.
     */
    public final String getBanReason()
    {
        return banReason;
    }
}
