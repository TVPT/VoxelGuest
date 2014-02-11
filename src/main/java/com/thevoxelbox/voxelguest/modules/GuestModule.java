package com.thevoxelbox.voxelguest.modules;

import com.thevoxelbox.voxelguest.api.modules.Module;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.Collections;
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
    private String name = "Default Module Name";

    @Override
    public void onEnable()
    {
    }

    @Override
    public void onDisable()
    {
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
        return StringUtils.deleteWhitespace(getName());
    }

    @Override
    public Object getConfiguration()
    {
        return null;
    }

    @Override
    public Set<Listener> getListeners()
    {
        return Collections.emptySet();
    }

    @Override
    public Map<String, CommandExecutor> getCommandMappings()
    {
        return Collections.emptyMap();
    }
}
