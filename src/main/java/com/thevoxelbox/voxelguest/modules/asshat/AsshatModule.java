package com.thevoxelbox.voxelguest.modules.asshat;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.asshat.command.BanCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.BanreasonCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.FreezeCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.KickCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.MuteCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.SoapboxCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.UnbanCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.command.UnmuteCommandExecutor;
import com.thevoxelbox.voxelguest.modules.asshat.listener.PlayerListener;
import com.thevoxelbox.voxelguest.modules.asshat.mute.Mutelist;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Monofraps
 */
public class AsshatModule extends GuestModule
{
    public static final String SILENCE_BYPASS_PERM = "voxelguest.asshat.bypass.silence";
    public static final String FREEZE_BYPASS_PERM = "voxelguest.asshat.bypass.freeze";
    private final AsshatModuleConfiguration config = new AsshatModuleConfiguration();
    private PlayerListener playerListener;
    private BanCommandExecutor banCommandExecutor;
    private UnbanCommandExecutor unbanCommandExecutor;
    private BanreasonCommandExecutor banreasonCommandExecutor;
    private MuteCommandExecutor muteCommandExecutor;
    private UnmuteCommandExecutor unmuteCommandExecutor;
    private KickCommandExecutor kickCommandExecutor;
    private SoapboxCommandExecutor soapboxCommandExecutor;
    private FreezeCommandExecutor freezeCommandExecutor;
    private Mutelist mutelist = new Mutelist();
    private boolean silenceEnabled = false;
    private boolean freezeEnabled = false;

    /**
     *
     */
    public AsshatModule()
    {
        setName("Asshat Module");

        playerListener = new PlayerListener(this);

        banCommandExecutor = new BanCommandExecutor(this);
        unbanCommandExecutor = new UnbanCommandExecutor(this);
        banreasonCommandExecutor = new BanreasonCommandExecutor();
        muteCommandExecutor = new MuteCommandExecutor(this);
        unmuteCommandExecutor = new UnmuteCommandExecutor(this);
        kickCommandExecutor = new KickCommandExecutor(this);
        soapboxCommandExecutor = new SoapboxCommandExecutor(this);
        freezeCommandExecutor = new FreezeCommandExecutor(this);
    }

    @Override
    public final void onEnable()
    {
        super.onEnable();
    }

    @Override
    public final void onDisable()
    {
        super.onDisable();
    }

    @Override
    public final Object getConfiguration()
    {
        return config;
    }

    @Override
    public final Set<Listener> getListeners()
    {
        final HashSet<Listener> listeners = new HashSet<>();
        listeners.add(playerListener);
        return listeners;
    }

    @Override
    public final Map<String, CommandExecutor> getCommandMappings()
    {
        HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
        commandMappings.put("ban", banCommandExecutor);
        commandMappings.put("unban", unbanCommandExecutor);
        commandMappings.put("banreason", banreasonCommandExecutor);
        commandMappings.put("gag", muteCommandExecutor);
        commandMappings.put("ungag", unmuteCommandExecutor);
        commandMappings.put("kick", kickCommandExecutor);
        commandMappings.put("soapbox", soapboxCommandExecutor);
        commandMappings.put("freeze", freezeCommandExecutor);

        return commandMappings;
    }

    /**
     * @return Returns the mutelist instance.
     */
    public final Mutelist getMutelist()
    {
        return mutelist;
    }

    /**
     * @return Returns true if silence is enabled, false otherwise.
     */
    public final boolean isSilenceEnabled()
    {
        return silenceEnabled;
    }

    /**
     * Enables or disables the silence.
     *
     * @param silenceEnabled true to enable silence, false to disable
     */
    public final void setSilenceEnabled(final boolean silenceEnabled)
    {
        this.silenceEnabled = silenceEnabled;
    }

    /**
     * @return Returns true if freeze is enabled.
     */
    public final boolean isFreezeEnabled()
    {
        return freezeEnabled;
    }

    /**
     * Enables or disables freeze.
     *
     * @param freezeEnabled true to enable freeze, false to disable
     */
    public final void setFreezeEnabled(final boolean freezeEnabled)
    {
        this.freezeEnabled = freezeEnabled;
    }

    /**
     * Formats a broadcast message based on a given format string.
     *
     * @param msg       The format string.
     * @param target    Will replace all occurrences of %playername% in the format string.
     * @param adminName Will replace all occurrences of %admin% in the format string.
     * @param reason    Will replace all occurrences of %reason% in the format string.
     *
     * @return Returns the formatted and replaced string.
     */
    public static String formatBroadcastMessage(final String msg, final String target, final String adminName, final String reason)
    {
        return msg.replace("%playername%", target).replace("%admin%", adminName)
                .replace("%reason%", reason);
    }
}
