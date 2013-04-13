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

    public Greylistee(final String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setId(final long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }


}
