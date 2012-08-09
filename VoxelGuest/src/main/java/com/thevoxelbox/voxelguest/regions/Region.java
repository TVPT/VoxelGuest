/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of The VoxelPlugineering Team nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.thevoxelbox.voxelguest.regions;

import com.patrickanker.lib.config.PropertyConfiguration;
import com.patrickanker.lib.util.JavaPropertiesFileManager;
import java.io.File;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Region {

    private final String name;
    private final World world;
    private final Vector3D pos1;
    private final Vector3D pos2;
    private boolean disableGeneralBuildOverride = false;

    public Region(String name)
    {
        this.name = name;
        PropertyConfiguration config = new PropertyConfiguration(name, "/VoxelGuest/regions");
        this.world = Bukkit.getWorld(config.getString("world"));
        this.pos1 = new Vector3D(config.getDouble("x1"), config.getDouble("y1"), config.getDouble("z1"));
        this.pos2 = new Vector3D(config.getDouble("x2"), config.getDouble("y2"), config.getDouble("z2"));
        this.disableGeneralBuildOverride = config.getBoolean("disable-general-build-override");
    }

    public Region(String name, World world, Vector3D vec1, Vector3D vec2)
    {
        this.name = name;
        this.world = world;
        this.pos1 = vec1;
        this.pos2 = vec2;
    }

    public Region(String name, World world, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.name = name;
        this.world = world;
        Vector3D pos1 = new Vector3D(x1, y1, z1);
        Vector3D pos2 = new Vector3D(x1, y2, z2);
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Region(String name, World world, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.name = name;
        this.world = world;
        Vector3D min = new Vector3D(minX, minY, minZ);
        Vector3D max = new Vector3D(maxX, maxY, maxZ);
        this.pos1 = min;
        this.pos2 = max;
    }

    public void save()
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("world", world.getName());
        map.put("x1", pos1.getX());
        map.put("y1", pos1.getY());
        map.put("z1", pos1.getZ());
        map.put("x2", pos2.getX());
        map.put("y2", pos2.getY());
        map.put("z2", pos2.getZ());
        map.put("disable-general-build-override", disableGeneralBuildOverride);
        JavaPropertiesFileManager.save(name, map, "/VoxelGuest/regions");
    }

    public void delete()
    {
        File f = new File(JavaPropertiesFileManager.BASE + "/VoxelGuest/regions/" + name + ".properties");
        f.delete();
    }

    public String getName()
    {
        return name;
    }

    public World getWorld()
    {
        return this.world;
    }

    public boolean isGeneralBuildOverrideDisabled()
    {
        return disableGeneralBuildOverride;
    }

    public void setGeneralBuildOverrideDisable(boolean bool)
    {
        disableGeneralBuildOverride = bool;
    }

    public Vector3D getMaximumPoint()
    {
        return Vector3D.getMaximum(pos1, pos2);
    }

    public Vector3D getMinimumPoint()
    {
        return Vector3D.getMinimum(pos1, pos2);
    }

    public boolean inBounds(Location loc)
    {
        Vector3D vec = new Vector3D(loc);

        if (loc.getWorld() != null && !loc.getWorld().equals(world)) {
            return false;
        }

        Vector3D max = getMaximumPoint();
        Vector3D min = getMinimumPoint();

        if (vec.getX() >= min.getX() && vec.getX() <= max.getX()) {
            if (vec.getY() >= min.getY() && vec.getY() <= max.getY()) {
                if (vec.getZ() >= min.getZ() && vec.getZ() <= max.getZ()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean inBounds(Region region)
    {
        if (world == null || region == null || region.world == null) {
            return false;
        }

        if (!world.equals(region.world)) {
            return false;
        }

        Vector3D max = getMaximumPoint();
        Vector3D min = getMinimumPoint();

        Vector3D regMax = region.getMaximumPoint();
        Vector3D regMin = region.getMinimumPoint();

        if (regMin.getX() >= min.getX() && regMax.getX() <= max.getX()) {
            if (regMin.getY() >= min.getY() && regMax.getY() <= max.getY()) {
                if (regMin.getZ() >= min.getZ() && regMax.getZ() <= max.getZ()) {
                    return true;
                }
            }
        }

        return false;
    }

    public double getLength()
    {
        return getMaximumPoint().getX() - getMinimumPoint().getX();
    }

    public double getWidth()
    {
        return getMaximumPoint().getY() - getMinimumPoint().getY();
    }

    public double getHeight()
    {
        return getMaximumPoint().getZ() - getMinimumPoint().getZ();
    }

    public double getVolume()
    {
        return getLength() * getWidth() * getHeight();
    }

    public boolean matches(Region region)
    {
        if (world == null || region == null || region.world == null) {
            return false;
        }

        if (!world.equals(region.world)) {
            return false;
        }

        Vector3D max = getMaximumPoint();
        Vector3D min = getMinimumPoint();

        Vector3D regMax = region.getMaximumPoint();
        Vector3D regMin = region.getMinimumPoint();

        if ((max.getX() != regMax.getX()) || (min.getX() != regMin.getX())) {
            return false;
        }
        if ((max.getY() != regMax.getY()) || (min.getY() != regMin.getY())) {
            return false;
        }
        if ((max.getZ() != regMax.getZ()) || (min.getZ() != regMin.getZ())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj.getClass() != Region.class) {
            return false;
        }

        Region test = (Region) obj;

        if (!name.equals(test.name)) {
            return false;
        }

        return matches(test);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 97 * hash + (this.pos1 != null ? this.pos1.hashCode() : 0);
        hash = 97 * hash + (this.pos2 != null ? this.pos2.hashCode() : 0);
        return hash;
    }
}
