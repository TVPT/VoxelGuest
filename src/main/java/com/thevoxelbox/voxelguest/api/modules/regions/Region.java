package com.thevoxelbox.voxelguest.api.modules.regions;

import org.bukkit.util.Vector;

import java.util.List;

/**
 * @author monofraps
 */
public interface Region
{
    long getId();
    Vector getLowerBound();
    Vector getUpperBound();
    int getPriority();
    void setPriority(int priority);
    String getName();
    void setName(String name);
    List<String> getActiveRules();
    String getWorldName();
    void setWorldName(final String worldName);
}
