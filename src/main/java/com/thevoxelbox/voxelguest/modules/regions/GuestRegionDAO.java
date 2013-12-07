package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RegionProvider;
import com.thevoxelbox.voxelguest.api.modules.regions.exceptions.NoSuchRegionException;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Monofraps
 */
public final class GuestRegionDAO implements RegionProvider
{

    @Override
    public void saveRegion(final Region region)
    {
        Persistence.getInstance().save(region);
    }

    @Override
    public List<Region> byLocation(final Location location)
    {
        final HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("worldName", location.getWorld().getName());

        final List<GuestRegion> regionsInWorld = Persistence.getInstance().loadAll(GuestRegion.class, selectRestrictions);
        final List<Region> returnList = new ArrayList<>();
        for (GuestRegion region : regionsInWorld)
        {
            if (location.toVector().isInAABB(region.getLowerBound(), region.getUpperBound()))
            {
                returnList.add(region);
            }
            else if (region.getName().equalsIgnoreCase("global"))
            {
                returnList.add(region);
            }
        }

        return returnList;
    }


    @Override
    public GuestRegion byName(final String name, final String world) throws NoSuchRegionException
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("name", name);
        selectRestrictions.put("worldName", world);

        final List<GuestRegion> returnList = Persistence.getInstance().loadAll(GuestRegion.class, selectRestrictions);

        if (returnList.size() == 0)
        {
            throw new NoSuchRegionException(name, world);
        }
        else
        {
            return returnList.get(0);
        }
    }


    @Override
    public boolean regionExists(final String regionName, final String worldName)
    {
        try
        {
            byName(regionName, worldName);
            return true;
        }
        catch (NoSuchRegionException e)
        {
            return false;
        }
    }
}
