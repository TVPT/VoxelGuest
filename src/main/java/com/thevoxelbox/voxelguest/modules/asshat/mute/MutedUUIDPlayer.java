package com.thevoxelbox.voxelguest.modules.asshat.mute;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "mutes_uuid")
public class MutedUUIDPlayer
{
    @DatabaseField(generatedId = true)
    private long   id;
    @DatabaseField
    private UUID   playerUUID;
    @DatabaseField
    private String  muteReason;
    @DatabaseField
    private boolean selfUngag;

    /**
     * ORM constructor.
     */
    public MutedUUIDPlayer()
    {
    }

    /**
     * @param muteReason  The reason the player is banned for.
     * @param selfUngag
     */
    public MutedUUIDPlayer(final UUID playerUUID, final String muteReason, final boolean selfUngag)
    {
        this.playerUUID = playerUUID;
        this.muteReason = muteReason;
        this.selfUngag = selfUngag;
    }

    public final UUID getPlayerUUID()
    {
        return playerUUID;
    }

    public final String getMuteReason()
    {
        return muteReason;
    }

    public final boolean isSelfUngag()
    {
        return selfUngag;
    }
}
