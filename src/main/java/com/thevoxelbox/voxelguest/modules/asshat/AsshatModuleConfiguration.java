package com.thevoxelbox.voxelguest.modules.asshat;

import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationProperty;

/**
 * Represents the asshat module configuration.
 */
public final class AsshatModuleConfiguration
{
    @ConfigurationProperty("default-asshat-reason")
    private String defaultAsshatReason = "asshat";
    @ConfigurationProperty("ban-message")
    private String banBroadcastMsg     = "§8Player §c${playername}§8 has been banned by §c${admin}§8 for: §9${reason}";
    @ConfigurationProperty("unban-message")
    private String unbanBroadcastMsg   = "§8Player §c${playername}§8 has been unbanned by §c${reason}";
    @ConfigurationProperty("kick-message")
    private String kickBroadcastMsg    = "§8Player §c${playername}§8 has been kicked by §c%admin%§8 for: §9${reason}";
    @ConfigurationProperty("gag-message")
    private String gagBroadcastMsg     = "§8Player §c${playername}§8 has been gagged by §c%admin%§8 for: §9${reason}";
    @ConfigurationProperty("ungag-message")
    private String ungagBroadcastMsg   = "§8Player §c${playername}§8 has been ungagged by §c${reason}";

    public String getDefaultAsshatReason()
    {
        return defaultAsshatReason;
    }

    public void setDefaultAsshatReason(final String defaultAsshatReason)
    {
        this.defaultAsshatReason = defaultAsshatReason;
    }

    public String getBanBroadcastMsg()
    {
        return banBroadcastMsg;
    }

    public void setBanBroadcastMsg(final String banBroadcastMsg)
    {
        this.banBroadcastMsg = banBroadcastMsg;
    }

    public String getUnbanBroadcastMsg()
    {
        return unbanBroadcastMsg;
    }

    public void setUnbanBroadcastMsg(final String unbanBroadcastMsg)
    {
        this.unbanBroadcastMsg = unbanBroadcastMsg;
    }

    public String getKickBroadcastMsg()
    {
        return kickBroadcastMsg;
    }

    public void setKickBroadcastMsg(final String kickBroadcastMsg)
    {
        this.kickBroadcastMsg = kickBroadcastMsg;
    }

    public String getGagBroadcastMsg()
    {
        return gagBroadcastMsg;
    }

    public void setGagBroadcastMsg(final String gagBroadcastMsg)
    {
        this.gagBroadcastMsg = gagBroadcastMsg;
    }

    public String getUngagBroadcastMsg()
    {
        return ungagBroadcastMsg;
    }

    public void setUngagBroadcastMsg(final String ungagBroadcastMsg)
    {
        this.ungagBroadcastMsg = ungagBroadcastMsg;
    }
}
