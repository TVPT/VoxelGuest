package com.thevoxelbox.voxelguest.modules.regions;

import com.google.common.base.Preconditions;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "regionsx")
public class Region implements Comparable<Region>, com.thevoxelbox.voxelguest.api.modules.regions.Region
{
    private long id;
    private int lowerX;
    private int lowerY;
    private int lowerZ;
    private int upperX;
    private int upperY;
    private int upperZ;
    private String worldName;
    private String name;
    /**
     * Lower priority means earlier processing of rules.
     * If priorities are equal the alphabetical order of region names will decide.
     */
    private int priority;
    private List<String> activeRules = new ArrayList<>();

    public Region() {

    }

    public Region(final String name, final Location firstBound, final Location secondBound)
    {
        this.name = name;

        Preconditions.checkArgument(firstBound.getWorld().equals(secondBound.getWorld()));

        priority = 0;

        lowerX = (firstBound.toVector().getBlockX() < secondBound.toVector().getBlockX()) ? firstBound.toVector().getBlockX() : secondBound.toVector().getBlockX();
        lowerY = (firstBound.toVector().getBlockY() < secondBound.toVector().getBlockY()) ? firstBound.toVector().getBlockY() : secondBound.toVector().getBlockY();
        lowerZ = (firstBound.toVector().getBlockZ() < secondBound.toVector().getBlockZ()) ? firstBound.toVector().getBlockZ() : secondBound.toVector().getBlockZ();

        upperX = (firstBound.toVector().getBlockX() > secondBound.toVector().getBlockX()) ? firstBound.toVector().getBlockX() : secondBound.toVector().getBlockX();
        upperY = (firstBound.toVector().getBlockY() > secondBound.toVector().getBlockY()) ? firstBound.toVector().getBlockY() : secondBound.toVector().getBlockY();
        upperZ = (firstBound.toVector().getBlockZ() > secondBound.toVector().getBlockZ()) ? firstBound.toVector().getBlockZ() : secondBound.toVector().getBlockZ();

        worldName = firstBound.getWorld().getName();
    }

    public Region(final String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public Vector getLowerBound()
    {
        return new Vector(lowerX, lowerY, lowerZ);
    }

    public Vector getUpperBound()
    {
        return new Vector(upperX, upperY, upperZ);
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getActiveRules()
    {
        return activeRules;
    }

    public String getWorldName()
    {
        return worldName;
    }

    public void setWorldName(final String worldName)
    {
        this.worldName = worldName;
    }

    @Override
    public int compareTo(final Region region)
    {
        if (this.priority == region.getPriority())
        {
            return this.getName().compareTo(region.getName());
        }

        return (this.priority < region.getPriority()) ? -1 : 1;
    }
}
