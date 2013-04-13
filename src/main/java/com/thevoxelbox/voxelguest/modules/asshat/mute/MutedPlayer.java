package com.thevoxelbox.voxelguest.modules.asshat.mute;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "mutes")
public class MutedPlayer
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String playerName;
    @DatabaseField
    private String muteReason;

    /**
     * ORM constructor.
     */
    public MutedPlayer()
    {
    }

    /**
     * @param playerName The name of the muted player.
     * @param muteReason The reason the player is muted for.
     */
    public MutedPlayer(final String playerName, final String muteReason)
    {
        this.playerName = playerName;
        this.muteReason = muteReason;
    }

    /**
     * @return Returns the name of the muted player.
     */
    public final String getPlayerName()
    {
        return playerName;
    }

    /**
     * @return Returns the reason the player is muted for.
     */
    public final String getMuteReason()
    {
        return muteReason;
    }
}
