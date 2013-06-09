package com.thevoxelbox.voxelguest.modules.greylist.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author MikeMatrix
 */
@DatabaseTable(tableName = "greylist")
public final class Greylistee
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String name;

    /**
     * Default constructor used be ORM system.
     */
    public Greylistee()
    {
    }

    /**
     * @param name The name of the player to greylist.
     */
    public Greylistee(final String name)
    {
        this.name = name;
    }

    /**
     * @return Returns the name of the greylistee.
     */
    public String getName()
    {
        return name;
    }
}
