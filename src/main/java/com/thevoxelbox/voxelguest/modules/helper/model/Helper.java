package com.thevoxelbox.voxelguest.modules.helper.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thevoxelbox.voxelguest.VoxelGuest;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * Represents a helper.
 *
 * @author TheCryoknight
 */
@DatabaseTable(tableName = "helpers")
public final class Helper implements MetadataValue
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String name = "";
    @DatabaseField
    private int reviews = 0;
    @DatabaseField
    private long lastReview = 0;

    /**
     * Default constructor for ORM system.
     */
    public Helper()
    {
    }

    public Helper(final String name)
    {
        this.name = name;
    }

    /**
     * Sets stats for reviews done by this helper
     */
    public void review()
    {
        this.reviews++;
        this.lastReview = System.currentTimeMillis();
    }

    /**
     * @return The number of reviews this helper has performed.
     */
    public int getReviews()
    {
        return this.reviews;
    }

    /**
     * @return The in game name of the helper.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The time (as a long) of the last review from this helper
     */
    public long getTimeOfLastReview()
    {
        return this.lastReview;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof Helper))
        {
            return false;
        }

        final Helper otherHelper = (Helper) other;
        return this.name.equalsIgnoreCase(otherHelper.getName()) && this.getTimeOfLastReview() == otherHelper.getTimeOfLastReview();
    }

    @Override
    public Object value()
    {
        return null;
    }

    @Override
    public int asInt()
    {
        return 0;
    }

    @Override
    public float asFloat()
    {
        return 0;
    }

    @Override
    public double asDouble()
    {
        return 0;
    }

    @Override
    public long asLong()
    {
        return 0;
    }

    @Override
    public short asShort()
    {
        return 0;
    }

    @Override
    public byte asByte()
    {
        return 0;
    }

    @Override
    public boolean asBoolean()
    {
        return true;
    }

    @Override
    public String asString()
    {
        return "This player is a helper";
    }

    @Override
    public Plugin getOwningPlugin()
    {
        return VoxelGuest.getPluginInstance();
    }

    @Override
    public void invalidate()
    {
    }
}

