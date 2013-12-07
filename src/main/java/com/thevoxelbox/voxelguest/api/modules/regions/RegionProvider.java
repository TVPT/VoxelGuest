package com.thevoxelbox.voxelguest.api.modules.regions;


import com.thevoxelbox.voxelguest.api.modules.regions.exceptions.NoSuchRegionException;
import org.bukkit.Location;

import java.util.List;

/**
 * @author monofraps
 */
public interface RegionProvider
{
    /**
     * Saves a GuestRegion instance to database.
     *
     * @param region The region to save.
     */
    void saveRegion(final Region region);

    /**
     * Queries the database for all regions <code>location</code> is in.
     *
     * @param location The location in question.
     *
     * @return Returns a list of all GuestRegions containing location.
     */
    List<Region> byLocation(final Location location);

    /**
     * Queries a region by name and world.
     *
     * @param name  the name of the region
     * @param world the world to search
     *
     * @return Returns a GuestRegion instance if the region was found, throws an exception otherwise.
     *
     * @throws NoSuchRegionException Thrown when the requested region could not be found.
     */
    Region byName(final String name, final String world) throws NoSuchRegionException;

    /**
     * Checks if a region exists in a specified world.
     *
     * @param regionName the region to check for
     * @param worldName  the world to search in
     *
     * @return Returns a boolean indicating if region regionName exists in world worldName
     */
    boolean regionExists(final String regionName, final String worldName);
}
