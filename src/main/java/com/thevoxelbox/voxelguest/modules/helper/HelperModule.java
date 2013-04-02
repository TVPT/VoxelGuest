package com.thevoxelbox.voxelguest.modules.helper;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.helper.command.HelperCommand;
import com.thevoxelbox.voxelguest.modules.helper.command.HelperReviewCommand;
import com.thevoxelbox.voxelguest.modules.helper.command.WLReviewCommand;
import com.thevoxelbox.voxelguest.modules.helper.listener.HelperListener;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TheCryoknight
 */
public final class HelperModule extends GuestModule
{
    private final WLReviewCommand wLReviewCommand;
    private final HelperCommand helperCommand;
    private final HelperReviewCommand helperReviewCommand;
    private final HelperListener connectionListener;
    private final HelperManager manager;

    /**
     * Creates a new helper module instance.
     */
    public HelperModule()
    {
        this.setName("Helper Module");
        this.wLReviewCommand = new WLReviewCommand(this);
        this.helperCommand = new HelperCommand(this);
        this.helperReviewCommand = new HelperReviewCommand(this);
        this.manager = new HelperManager();
        this.connectionListener = new HelperListener(this);
    }

    @Override
    public void onEnable()
    {
        this.getManager().initHelperList();
        super.onEnable();
    }

    @Override
    public Set<Listener> getListeners()
    {
        final Set<Listener> listeners = new HashSet<>();
        listeners.add(this.connectionListener);
        return listeners;
    }

    @Override
    public Map<String, CommandExecutor> getCommandMappings()
    {
        final Map<String, CommandExecutor> command = new HashMap<>();
        command.put("wlreview", this.wLReviewCommand);
        command.put("helper", this.helperCommand);
        command.put("helperreview", this.helperReviewCommand);
        return command;
    }

    /**
     * Returns the helper manager.
     *
     * @return Returns the helper manager.
     */
    public HelperManager getManager()
    {
        return manager;
    }

}
