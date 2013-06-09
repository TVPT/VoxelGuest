package com.thevoxelbox.voxelguest.modules.general;

import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationProperty;
import org.bukkit.ChatColor;

/**
 * @author Monofraps
 */
public final class GeneralModuleConfiguration
{
    @ConfigurationProperty("random-afk-messages-enabled")
    private boolean randomAfkMsgs = true;
    @ConfigurationProperty("force-watch-tps")
    private boolean defaultWatchTPSState = true;
    @ConfigurationProperty("kick-format")
    private String kickFormat = ChatColor.DARK_GRAY
            + "(" + ChatColor.GOLD + "$no" + ChatColor.DARK_GRAY + ") "
            + ChatColor.DARK_AQUA + "$n" + ChatColor.DARK_RED + " was kicked out";
    @ConfigurationProperty("leave-format")
    private String leaveFormat = ChatColor.DARK_GRAY
            + "(" + ChatColor.GOLD + "$no" + ChatColor.DARK_GRAY + ") "
            + ChatColor.DARK_AQUA + "$n" + ChatColor.GRAY + " left";
    @ConfigurationProperty("join-format")
    private String joinFormat = ChatColor.DARK_GRAY
            + "(" + ChatColor.GOLD + "$no" + ChatColor.DARK_GRAY + ") "
            + ChatColor.DARK_AQUA + "$n" + ChatColor.GRAY + " joined";
    @ConfigurationProperty("fakequit-prefix")
    private String fakequitPrefix = ChatColor.DARK_GRAY
            + "[" + ChatColor.RED + "FQ" + ChatColor.DARK_GRAY + "]";
    @ConfigurationProperty("permgen-shutdown-threshold")
    private int permGenShutdownThreshold = 80;
    @ConfigurationProperty("permgen-warning-threshold")
    private int permGenWarningThreshold = 65;

    public boolean isRandomAfkMsgs()
    {
        return randomAfkMsgs;
    }

    public void setRandomAfkMsgs(final boolean randomAfkMsgs)
    {
        this.randomAfkMsgs = randomAfkMsgs;
    }

    public boolean isDefaultWatchTPSState()
    {
        return defaultWatchTPSState;
    }

    public void setDefaultWatchTPSState(final boolean defaultWatchTPSState)
    {
        this.defaultWatchTPSState = defaultWatchTPSState;
    }

    public String getKickFormat()
    {
        return kickFormat;
    }

    public void setKickFormat(final String kickFormat)
    {
        this.kickFormat = kickFormat;
    }

    public String getLeaveFormat()
    {
        return leaveFormat;
    }

    public void setLeaveFormat(final String leaveFormat)
    {
        this.leaveFormat = leaveFormat;
    }

    public String getJoinFormat()
    {
        return joinFormat;
    }

    public void setJoinFormat(final String joinFormat)
    {
        this.joinFormat = joinFormat;
    }

    public String getFakequitPrefix()
    {
        return fakequitPrefix;
    }

    public void setFakequitPrefix(final String fakequitPrefix)
    {
        this.fakequitPrefix = fakequitPrefix;
    }

    public int getPermGenShutdownThreshold()
    {
        return permGenShutdownThreshold;
    }

    public void setPermGenShutdownThreshold(final int permGenShutdownThreshold)
    {
        this.permGenShutdownThreshold = permGenShutdownThreshold;
    }

    public int getPermGenWarningThreshold()
    {
        return permGenWarningThreshold;
    }

    public void setPermGenWarningThreshold(final int permGenWarningThreshold)
    {
        this.permGenWarningThreshold = permGenWarningThreshold;
    }
}
