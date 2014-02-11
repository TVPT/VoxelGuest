package com.thevoxelbox.voxelguest.modules.general;

import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.api.AutoRegisterModule;
import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.general.command.AddAfkMessageCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.AfkCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.EntityPurgeCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.FakequitCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.PlayerGroupCommand;
import com.thevoxelbox.voxelguest.modules.general.command.SystemCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.VanishCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.VpgCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.VtpCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.WatchTPSCommadExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.WhoCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.listener.ConnectionEventListener;
import com.thevoxelbox.voxelguest.modules.general.listener.PlayerEventListener;
import com.thevoxelbox.voxelguest.modules.general.runnables.LagMeterHelperThread;
import com.thevoxelbox.voxelguest.modules.general.runnables.PermGenMonitor;
import com.thevoxelbox.voxelguest.modules.general.runnables.TPSTicker;
import com.thevoxelbox.voxelguest.utils.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Core class for general module, manages all
 * subcomponents of the general module.
 *
 * @author TheCryoknight
 * @author Deamon5550
 */
@AutoRegisterModule
public final class GeneralModule extends GuestModule
{
    public static final String FAKEQUIT_PERM = "voxelguest.general.fakequit";
    //CommandExecuters
    private final EntityPurgeCommandExecutor   entityPurgeCommandExecutor;
    private final VanishCommandExecutor        vanishCommandExecutor;
    private final FakequitCommandExecutor      fakequitCommandExecutor;
    private final WhoCommandExecutor           whoCommandExecutor;
    private final AfkCommandExecutor           afkCommandExecutor;
    private final WatchTPSCommadExecutor       watchTPSCommadExecutor;
    private final SystemCommandExecutor        systemCommandExecutor;
    private final VpgCommandExecutor           vpgCommandExecutor;
    private final VtpCommandExecutor           vtpCommandExecutor;
    private final AddAfkMessageCommandExecutor addAfkMessageCommandExecutor;
    private final PlayerGroupCommand           playerGroupCommandExecutor;
    //Listeners
    private final ConnectionEventListener      connectionEventListener;
    private final PlayerEventListener          playerEventListener;
    //TPS ticker
    private final TPSTicker            ticker   = new TPSTicker();
    //Lag Meter thread and helper
    private final LagMeterHelperThread lagmeter = new LagMeterHelperThread();
    //Handlers
    private final AfkManager                 afkManager;
    private final VanishFakequitHandler      vanishFakequitHandler;
    private       BukkitTask                 lagmeterTask;
    private       GeneralModuleConfiguration configuration;
    private int tpsTickerTaskId      = -1;
    private int permGenMonitorTaskId = -1;

    /**
     * Creates a new general module instance.
     */
    public GeneralModule()
    {
        this.setName("General Module");

        this.configuration = new GeneralModuleConfiguration();

        this.entityPurgeCommandExecutor = new EntityPurgeCommandExecutor();
        this.vanishCommandExecutor = new VanishCommandExecutor(this);
        this.connectionEventListener = new ConnectionEventListener(this);
        this.playerEventListener = new PlayerEventListener(this);
        this.fakequitCommandExecutor = new FakequitCommandExecutor(this);
        this.whoCommandExecutor = new WhoCommandExecutor(this);
        this.afkCommandExecutor = new AfkCommandExecutor(this);
        this.watchTPSCommadExecutor = new WatchTPSCommadExecutor(this);
        this.systemCommandExecutor = new SystemCommandExecutor();
        this.vpgCommandExecutor = new VpgCommandExecutor();
        this.vtpCommandExecutor = new VtpCommandExecutor();
        this.addAfkMessageCommandExecutor = new AddAfkMessageCommandExecutor();
        this.playerGroupCommandExecutor = new PlayerGroupCommand();

        this.afkManager = new AfkManager(this);
        this.vanishFakequitHandler = new VanishFakequitHandler(this);
    }

    @Override
    public void onEnable()
    {
        final PermGenMonitor permGenMonitor = new PermGenMonitor(configuration);

        tpsTickerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(VoxelGuest.getPluginInstance(), ticker, 0, TPSTicker.POLL_INTERVAL);
        permGenMonitorTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(VoxelGuest.getPluginInstance(), permGenMonitor, 20, 20 * 5);

        lagmeterTask = Bukkit.getScheduler().runTaskTimer(VoxelGuest.getPluginInstance(), lagmeter, 20, 20 * 3);

        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        this.lagmeterTask.cancel();
        Bukkit.getScheduler().cancelTask(tpsTickerTaskId);
        Bukkit.getScheduler().cancelTask(permGenMonitorTaskId);

        tpsTickerTaskId = -1;
        permGenMonitorTaskId = -1;
        if (lagmeter.isAlive())
        {
            try
            {
                lagmeter.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        super.onDisable();
    }

    @Override
    public Object getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public HashSet<Listener> getListeners()
    {
        final HashSet<Listener> listeners = new HashSet<>();
        listeners.add(this.connectionEventListener);
        listeners.add(this.playerEventListener);

        return listeners;
    }

    @Override
    public HashMap<String, CommandExecutor> getCommandMappings()
    {
        HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
        commandMappings.put("ep", this.entityPurgeCommandExecutor);
        commandMappings.put("vanish", this.vanishCommandExecutor);
        commandMappings.put("fakequit", this.fakequitCommandExecutor);
        commandMappings.put("who", this.whoCommandExecutor);
        commandMappings.put("afk", this.afkCommandExecutor);
        commandMappings.put("sys", this.systemCommandExecutor);
        commandMappings.put("vpg", this.vpgCommandExecutor);
        commandMappings.put("vtp", this.vtpCommandExecutor);
        commandMappings.put("watchtps", this.watchTPSCommadExecutor);
        commandMappings.put("addafkmessage", this.addAfkMessageCommandExecutor);
        commandMappings.put("vgpg", this.playerGroupCommandExecutor);


        return commandMappings;
    }

    /**
     * @return Returns the AFK manager.
     */
    public AfkManager getAfkManager()
    {
        return afkManager;
    }

    /**
     * @return Returns the fakequit handler.
     */
    public VanishFakequitHandler getVanishFakequitHandler()
    {
        return vanishFakequitHandler;
    }

    /**
     * Replaces all occurrences of $no to the number of online players
     * and replaces all $n with a given players name.
     *
     * @param msg        The format string.
     * @param playerName The player name to replace all occurrences of $n
     *
     * @return Returns the formatted and replaced string.
     */
    public String formatJoinLeaveMessage(final String msg, final String playerName)
    {
        int onlinePlayers = Bukkit.getOnlinePlayers().length;
        onlinePlayers -= this.getVanishFakequitHandler().getFakequitSize();
        final Map<String, String> vals = new HashMap<>();
        vals.put("no", String.valueOf(onlinePlayers));
        vals.put("n", playerName);

        return MessageFormatter.format(msg, vals);
    }

    /**
     * @return Returns the lagmeter instance.
     */
    public LagMeterHelperThread getLagmeter()
    {
        return lagmeter;
    }
}
