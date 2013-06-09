package com.thevoxelbox.voxelguest.modules.greylist;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.greylist.command.GreylistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.command.UngreylistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.command.WhitelistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.listener.GreylistListener;
import com.thevoxelbox.voxelguest.modules.greylist.stream.StreamPacemaker;
import com.thevoxelbox.voxelguest.modules.greylist.stream.StreamThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author MikeMatrix
 */
public final class GreylistModule extends GuestModule
{
    private GreylistListener greylistListener;
    private GreylistCommandExecutor greylistCommandExecutor;
    private UngreylistCommandExecutor ungreylistCommandExecutor;
    private WhitelistCommandExecutor whitelistCommandExecutor;
    private GreylistConfiguration config;
    private StreamPacemaker streamPacemakerThread;
    private BukkitTask streamPacemaker;

    /**
     *
     */
    public GreylistModule()
    {
        this.setName("Greylist Module");
        config = new GreylistConfiguration();
        greylistListener = new GreylistListener(this);
        greylistCommandExecutor = new GreylistCommandExecutor();
        ungreylistCommandExecutor = new UngreylistCommandExecutor();
        whitelistCommandExecutor = new WhitelistCommandExecutor(this);
    }

    @Override
    public void onEnable()
    {
        if (config.isStreamGreylisting())
        {
            StreamThread streamThread = new StreamThread(this);
            streamPacemakerThread = new StreamPacemaker(streamThread);
            streamPacemaker = Bukkit.getScheduler().runTaskTimer(VoxelGuest.getPluginInstance(), streamPacemakerThread, 200, 20 * 30);
        }
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        if (streamPacemaker != null)
        {
            streamPacemaker.cancel();
            streamPacemakerThread.kill();
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
}
