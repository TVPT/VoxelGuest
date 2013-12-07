package com.thevoxelbox.voxelguest.api.modules.regions.exceptions;

/**
 * Raised when the GuestRegionDAO is queried for a region that does not exist.
 *
 * @author monofraps
 */
public class NoSuchRegionException extends Exception
{
    /**
     * Instantiates a NoSuchRegionException using region name and world name
     *
     * @param regioNname the region the called was looking for
     * @param worldName  the world the caller was searching
     */
    public NoSuchRegionException(final String regioNname, final String worldName)
    {
        super(String.format("Couldn't find region \"%s\" in world \"%s\"", regioNname, worldName));
    }
}
