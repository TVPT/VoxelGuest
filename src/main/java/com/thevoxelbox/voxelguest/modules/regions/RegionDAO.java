package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Monofraps
 */
public class RegionDAO
{
    public static void saveRegion(final Region region)
    {
        Persistence.getInstance().save(region);
    }

    public static List<Region> byLocation(final Location location)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("worldName", location.getWorld());

        final List<Region> regionsInWorld = Persistence.getInstance().loadAll(Region.class, selectRestrictions);
        final List<Region> returnList = new ArrayList<>();
        for (Region region : regionsInWorld)
        {
            if (location.toVector().isInAABB(region.getLowerBound(), region.getUpperBound()))
            {
                returnList.add(region);
            } else if(region.getName().equalsIgnoreCase("global")) {
                returnList.add(region);
            }
        }

        Collections.sort(returnList); // lower priorities first

        return returnList;
    }

    public static Region byName(final String name, final String world)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("name", name);
        selectRestrictions.put("worldName", world);

        final List<Region> returnList = Persistence.getInstance().loadAll(Region.class, selectRestrictions);

        if (returnList.size() == 0)
        {
            return null;
        }
        else
        {
            return returnList.get(0);
        }
    }
}
