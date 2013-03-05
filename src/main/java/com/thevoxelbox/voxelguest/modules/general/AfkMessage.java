package com.thevoxelbox.voxelguest.modules.general;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author TheCryoknight
 */
@DatabaseTable(tableName = "afkMessages")
public class AfkMessage
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String message;

    public AfkMessage() {}

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
