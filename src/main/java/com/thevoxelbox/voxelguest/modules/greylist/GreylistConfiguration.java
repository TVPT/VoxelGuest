package com.thevoxelbox.voxelguest.modules.greylist;

import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationGetter;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationSetter;

/**
 * Represents the greylist module configuration.
 */
public final class GreylistConfiguration
{
    private String streamPasswordHash = "changeme";
    private int streamPort = 8080;
    private String notGreylistedKickMessage = "You are not greylisted.";
    private String whitelistGroupName = "Member";
    private String greylistGroupName = "Guest";
    private boolean explorationMode = false;
    private boolean streamGreylisting = false;
    private boolean setGroupOnGreylist = true;
    private boolean broadcastGreylists = true;

    @ConfigurationGetter("broadcast-greylists")
    public boolean isBroadcastGreylists()
    {
        return broadcastGreylists;
    }

    @ConfigurationSetter("broadcast-greylists")
    public void setBroadcastGreylists(final boolean broadcastGreylists)
    {
        this.broadcastGreylists = broadcastGreylists;
    }

    @ConfigurationGetter("exploration-mode")
    public boolean isExplorationMode()
    {
        return explorationMode;
    }

    @ConfigurationSetter("exploration-mode")
    public void setExplorationMode(final boolean explorationMode)
    {
        this.explorationMode = explorationMode;
    }

    @ConfigurationGetter("not-greylisted-kick-message")
    public String getNotGreylistedKickMessage()
    {
        return notGreylistedKickMessage;
    }

    @ConfigurationSetter("not-greylisted-kick-message")
    public void setNotGreylistedKickMessage(final String notGreylistedKickMessage)
    {
        this.notGreylistedKickMessage = notGreylistedKickMessage;
    }

    @ConfigurationGetter("stream-port")
    public int getStreamPort()
    {
        return streamPort;
    }

    @ConfigurationSetter("stream-port")
    public void setStreamPort(final int streamPort)
    {
        this.streamPort = streamPort;
    }

    @ConfigurationGetter("stream-password")
    public String getStreamPasswordHash()
    {
        return streamPasswordHash;
    }

    @ConfigurationSetter("stream-password")
    public void setStreamPasswordHash(final String streamPasswordHash)
    {
        this.streamPasswordHash = streamPasswordHash;
    }

    @ConfigurationGetter("stream-enable")
    public boolean isStreamGreylisting()
    {
        return streamGreylisting;
    }

    @ConfigurationSetter("stream-enable")
    public void setStreamGreylisting(final boolean streamGraylisting)
    {
        this.streamGreylisting = streamGraylisting;
    }

    @ConfigurationGetter("wl-group-name")
    public String getWhitelistGroupName()
    {
        return whitelistGroupName;
    }

    @ConfigurationSetter("wl-group-name")
    public void setWhitelistGroupName(final String whitelistGroupName)
    {
        this.whitelistGroupName = whitelistGroupName;
    }

    @ConfigurationGetter("set-group-on-graylist")
    public boolean isSetGroupOnGreylist()
    {
        return setGroupOnGreylist;
    }

    @ConfigurationSetter("set-group-on-graylist")
    public void setSetGroupOnGreylist(final boolean setGroupOnGraylist)
    {
        this.setGroupOnGreylist = setGroupOnGraylist;
    }

    @ConfigurationGetter("gl-group-name")
    public String getGreylistGroupName()
    {
        return greylistGroupName;
    }

    @ConfigurationSetter("gl-group-name")
    public void setGreylistGroupName(final String graylistGroupName)
    {
        this.greylistGroupName = graylistGroupName;
    }
}
