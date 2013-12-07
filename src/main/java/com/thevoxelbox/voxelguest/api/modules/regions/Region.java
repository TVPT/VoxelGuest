package com.thevoxelbox.voxelguest.api.modules.regions;

import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * @author monofraps
 */
public interface Region
{
    /**
     * Returns the unique ID of the region.
     *
     * @return Returns the unique ID of the region.
     */
    long getId();

    /**
     * Returns the lower vector that defines the region area.
     *
     * @return Returns the lower vector that defines the region area.
     */
    Vector getLowerBound();

    /**
     * Returns the upper vector that defines the region area.
     *
     * @return Returns the upper vector that defines the region area.
     */
    Vector getUpperBound();

    /**
     * Sets the boundaries of the region.
     *
     * @param firstBound  A vector defining the first point of the new area.
     * @param secondBound A vector defining the second point of the new area.
     */
    void setBounds(final Vector firstBound, final Vector secondBound);

    /**
     * Returns the region's priority.
     *
     * @return Returns the region's priority.
     */
    int getPriority();

    /**
     * Sets the region's priority.
     *
     * @param priority Sets the region's priority.
     */
    void setPriority(final int priority);

    /**
     * Returns the region's name.
     *
     * @return Returns the region's name.
     */
    String getName();

    /**
     * Sets the region's name.
     *
     * @param name Sets the region's name.
     */
    void setName(final String name);

    /**
     * Returns a list of all currently active rules.
     *
     * @return Returns a lost of all currently active rules.
     */
    Collection<String> getActiveRules();

    /**
     * Returns the name of the world the region is in.
     *
     * @return Returns the name of the world the region is in.
     */
    String getWorldName();

    /**
     * Sets the name of the world the region is in.
     *
     * @param worldName Sets the name of the world the region is in.
     */
    void setWorldName(final String worldName);
}
