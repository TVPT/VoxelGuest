package com.thevoxelbox.voxelguest.modules.greylist;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.greylist.command.GreylistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.command.UngreylistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.command.WhitelistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerGreylistEvent;
import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerGreylistedEvent;
import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerUngreylistedEvent;
import com.thevoxelbox.voxelguest.modules.greylist.listener.GreylistListener;
import com.thevoxelbox.voxelguest.modules.greylist.model.Greylistee;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author MikeMatrix
 */
public final class GreylistModule extends GuestModule
{
    private final GreylistHelper greylistHelper;

    private GreylistListener greylistListener;
    private GreylistCommandExecutor greylistCommandExecutor;
    private UngreylistCommandExecutor ungreylistCommandExecutor;
    private WhitelistCommandExecutor whitelistCommandExecutor;
    private GreylistConfiguration config;
    private StreamThread streamTask;

    /**
     *
     */
    public GreylistModule()
    {
        this.setName("Greylist Module");
        greylistHelper = new GreylistHelper();
        config = new GreylistConfiguration();
        greylistListener = new GreylistListener(this);
        greylistCommandExecutor = new GreylistCommandExecutor(this);
        ungreylistCommandExecutor = new UngreylistCommandExecutor(this);
        whitelistCommandExecutor = new WhitelistCommandExecutor(this);
    }

    @Override
    public void onEnable()
    {
        if (config.isStreamGreylisting())
        {
            this.streamTask = new StreamThread(this);
            this.streamTask.start();
        }
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        if (this.streamTask != null)
        {
            this.streamTask.killProcesses();
        }
        super.onDisable();
    }

    @Override
    public Object getConfiguration()
    {
        return this.config;
    }

    @Override
    public HashSet<Listener> getListeners()
    {
        final HashSet<Listener> listeners = new HashSet<>();
        listeners.add(greylistListener);
        return listeners;
    }

    @Override
    public HashMap<String, CommandExecutor> getCommandMappings()
    {
        HashMap<String, CommandExecutor> commandMapping = new HashMap<>();
        commandMapping.put("greylist", greylistCommandExecutor);
        commandMapping.put("ungreylist", ungreylistCommandExecutor);
        commandMapping.put("whitelist", whitelistCommandExecutor);

        return commandMapping;
    }

    /**
     * Gets the separate object that helps in handling the greylist.
     *
     * @return the greylist helper
     */
    public GreylistHelper getGreylistHelper()
    {
        return greylistHelper;
    }
}
