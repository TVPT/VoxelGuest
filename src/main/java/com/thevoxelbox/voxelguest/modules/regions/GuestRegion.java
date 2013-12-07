package com.thevoxelbox.voxelguest.modules.regions;

import com.google.common.base.Preconditions;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "regions")
public final class GuestRegion implements Region
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private int lowerX;
    @DatabaseField
    private int lowerY;
    @DatabaseField
    private int lowerZ;
    @DatabaseField
    private int upperX;
    @DatabaseField
    private int upperY;
    @DatabaseField
    private int upperZ;
    @DatabaseField
    private String worldName;
    @DatabaseField
    private String name;
    @DatabaseField
    private int priority;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> activeRules = new ArrayList<>();

    /**
     * Default constructor for ORM system use ony.
     */
    public GuestRegion()
    {

    }

    /**
     * Creates a new region based on a name and two locations.
     * The location objects don't need to be sorted. Lower/Upper bound sorting will be performed by the constructor.
     *
     * @param name        The region name.
     * @param firstBound  A location object defining one corner of the region.
     * @param secondBound A location object defining the opposite corner of the region.
     */
    public GuestRegion(final String name, final Location firstBound, final Location secondBound)
    {
        this.name = name;

        Preconditions.checkArgument(firstBound.getWorld().equals(secondBound.getWorld()));

        setBounds(firstBound.toVector(), secondBound.toVector());

        priority = 0;


        worldName = firstBound.getWorld().getName();
    }

    /**
     * Creates a proto region using a name and a world.
     * NOTE: This method doesn't define any region boundaries.
     *
     * @param name      The region name.
     * @param worldName The name of the world to place the region in.
     */
    public GuestRegion(final String name, final String worldName)
    {
        this.name = name;
        this.worldName = worldName;
    }

    @Override
    public long getId()
    {
        return id;
    }

    @Override
    public Vector getLowerBound()
    {
        return new Vector(lowerX, lowerY, lowerZ);
    }

    @Override
    public Vector getUpperBound()
    {
        return new Vector(upperX, upperY, upperZ);
    }

    @Override
    public void setBounds(final Vector firstBound, final Vector secondBound)
    {
        lowerX = (firstBound.getBlockX() < secondBound.getBlockX()) ? firstBound.getBlockX() : secondBound.getBlockX();
        lowerY = (firstBound.getBlockY() < secondBound.getBlockY()) ? firstBound.getBlockY() : secondBound.getBlockY();
        lowerZ = (firstBound.getBlockZ() < secondBound.getBlockZ()) ? firstBound.getBlockZ() : secondBound.getBlockZ();

        upperX = (firstBound.getBlockX() > secondBound.getBlockX()) ? firstBound.getBlockX() : secondBound.getBlockX();
        upperY = (firstBound.getBlockY() > secondBound.getBlockY()) ? firstBound.getBlockY() : secondBound.getBlockY();
        upperZ = (firstBound.getBlockZ() > secondBound.getBlockZ()) ? firstBound.getBlockZ() : secondBound.getBlockZ();
    }

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public void setPriority(final int priority)
    {
        this.priority = priority;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public ArrayList<String> getActiveRules()
    {
        if (activeRules == null)
        {
            activeRules = new ArrayList<>();
        }
        return activeRules;
    }

    @Override
    public String getWorldName()
    {
        return worldName;
    }

    @Override
    public void setWorldName(final String worldName)
    {
        this.worldName = worldName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("lowerX", lowerX)
                .append("lowerY", lowerY)
                .append("lowerZ", lowerZ)
                .append("upperX", upperX)
                .append("upperY", upperY)
                .append("upperZ", upperZ)
                .append("worldName", worldName)
                .append("name", name)
                .append("priority", priority)
                .toString();
    }
}
