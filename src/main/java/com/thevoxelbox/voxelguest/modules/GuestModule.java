package com.thevoxelbox.voxelguest.modules;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements all primitive basic functions to simplify module creation.
 *
 * @author MikeMatrix
 * @author Monofraps
 */
public abstract class GuestModule implements Module
{
    private String name = "Default Module Name (Yell at the developer if you see this!)";
    private boolean enabled = false;

    @Override
    public void onEnable()
    {
        enabled = true;
    }

    @Override
    public void onDisable()
    {
        enabled = false;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public String getName()
    {
        return name;
    }

    protected void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public String getConfigFileName()
    {
        return getName().replace(" ", "");
    }

    @Override
    public Object getConfiguration()
    {
        return null;
    }

    @Override
    public Set<Listener> getListeners()
    {
        return new HashSet<>();
    }

    @Override
    public Map<String, CommandExecutor> getCommandMappings()
    {
        return new HashMap<>();
    }
}
