package com.thevoxelbox.voxelguest.modules.general;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author TheCryoknight
 */
@DatabaseTable(tableName = "afkMessages")
public final class AfkMessage
{
    @DatabaseField(generatedId = true)
    private long   id;
    @DatabaseField
    private String message;

    /**
     * Creates a new AFK message. (Constructor used by ORM system)
     */
    public AfkMessage()
    {
    }

    /**
     * Creates a new AFK message.
     *
     * @param message The message
     */
    public AfkMessage(final String message)
    {
        this.message = message;
    }

    /**
     * @return the afk message
     */
    public String getMessage()
    {
        return message;
    }
}
