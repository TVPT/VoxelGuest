package com.thevoxelbox.voxelguest.modules.greylist;

import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationProperty;

/**
 * Represents the greylist module configuration.
 */
public final class GreylistConfiguration
{
    @ConfigurationProperty("stream-password")
    private String streamPasswordHash = "changeme";
    @ConfigurationProperty("stream-port")
    private int streamPort = 8080;
    @ConfigurationProperty("not-greylisted-kick-message")
    private String notGreylistedKickMessage = "You are not greylisted.";
    @ConfigurationProperty("wl-group-name")
    private String whitelistGroupName = "Member";
    @ConfigurationProperty("gl-group-name")
    private String greylistGroupName = "Guest";
    @ConfigurationProperty("exploration-mode")
    private boolean explorationMode = false;
    @ConfigurationProperty("stream-enable")
    private boolean streamGreylisting = false;
    @ConfigurationProperty("set-group-on-graylist")
    private boolean setGroupOnGreylist = true;
    @ConfigurationProperty("broadcast-greylists")
    private boolean broadcastGreylists = true;

    public String getStreamPasswordHash()
    {
        return streamPasswordHash;
    }

    public void setStreamPasswordHash(final String streamPasswordHash)
    {
        this.streamPasswordHash = streamPasswordHash;
    }

    public int getStreamPort()
    {
        return streamPort;
    }

    public void setStreamPort(final int streamPort)
    {
        this.streamPort = streamPort;
    }

    public String getNotGreylistedKickMessage()
    {
        return notGreylistedKickMessage;
    }

    public void setNotGreylistedKickMessage(final String notGreylistedKickMessage)
    {
        this.notGreylistedKickMessage = notGreylistedKickMessage;
    }

    public String getWhitelistGroupName()
    {
        return whitelistGroupName;
    }

    public void setWhitelistGroupName(final String whitelistGroupName)
    {
        this.whitelistGroupName = whitelistGroupName;
    }

    public String getGreylistGroupName()
    {
        return greylistGroupName;
    }

    public void setGreylistGroupName(final String greylistGroupName)
    {
        this.greylistGroupName = greylistGroupName;
    }

    public boolean isExplorationMode()
    {
        return explorationMode;
    }

    public void setExplorationMode(final boolean explorationMode)
    {
        this.explorationMode = explorationMode;
    }

    public boolean isStreamGreylisting()
    {
        return streamGreylisting;
    }

    public void setStreamGreylisting(final boolean streamGreylisting)
    {
        this.streamGreylisting = streamGreylisting;
    }

    public boolean isSetGroupOnGreylist()
    {
        return setGroupOnGreylist;
    }

    public void setSetGroupOnGreylist(final boolean setGroupOnGreylist)
    {
        this.setGroupOnGreylist = setGroupOnGreylist;
    }

    public boolean isBroadcastGreylists()
    {
        return broadcastGreylists;
    }

    public void setBroadcastGreylists(final boolean broadcastGreylists)
    {
        this.broadcastGreylists = broadcastGreylists;
    }
}
