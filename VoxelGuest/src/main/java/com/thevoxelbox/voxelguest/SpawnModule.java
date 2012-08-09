/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of The VoxelPlugineering Team nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.thevoxelbox.voxelguest;

import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.thevoxelbox.voxelguest.modules.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
@MetaData(name = "Spawn", description = "Handles the spawn command")
public class SpawnModule extends Module {

    protected Location spawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
    protected String[] spawnMessages = {"Woosh!", "Weeee!"};

    public SpawnModule()
    {
        super(SpawnModule.class.getAnnotation(MetaData.class));
    }

    class SpawnConfiguration extends ModuleConfiguration {

        @Setting("use-configuration-location") public boolean defaultWorld = false;
        @Setting("use-ender-pearl-teleportation-cause") public boolean enderTeleport = false;
        @Setting("world") public String worldName = "";
        @Setting("world-x") public double x = 0;
        @Setting("world-y") public double y = 0;
        @Setting("world-z") public double z = 0;
        @Setting("world-yaw") public double yaw = 0;
        @Setting("world-pitch") public double pitch = 0;
        @Setting("use-random-spawn-messages") public boolean randomMessage = false;
        @Setting("random-spawn-messages") public String messages = "Your butt hurts,Woosh!,Weeee!,Buy Now! Ganz Ganz!,Huzzah!,*Blip*,*Pop*,Eat your veggies,Shake-Shake-Shake";

        public SpawnConfiguration(SpawnModule parent)
        {
            super(parent);
        }
    }

    @Override
    public void enable() throws ModuleException
    {
        setConfiguration(new SpawnConfiguration(this));

        if (getConfiguration().getBoolean("use-configuration-location")) {
            Location loc = new Location(
                    Bukkit.getWorld(getConfiguration().getString("world")),
                    getConfiguration().getDouble("world-x"),
                    getConfiguration().getDouble("world-y"),
                    getConfiguration().getDouble("world-z"),
                    (float) getConfiguration().getDouble("world-yaw"),
                    (float) getConfiguration().getDouble("world-pitch"));
            spawnLocation = loc;
        }

        if (getConfiguration().getBoolean("use-random-spawn-messages")) {
            spawnMessages = getConfiguration().getString("random-spawn-messages").split(",");
        }
    }

    @Override
    public String getLoadMessage()
    {
        return "Spawn module enabled - using " + (getConfiguration().getBoolean("use-configuration-location") ? "configuration spawn" : "default spawn");
    }

    @Override
    public void disable()
    {
    }

    @Command(aliases = {"setspawn"},
        bounds = {0, -1},
        help = "To set the spawn location type §c/setspawn",
        playerOnly = true)
    @CommandPermission("voxelguest.spawn.setspawn")
    public void setSpawn(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;

        spawnLocation = p.getLocation();
        getConfiguration().setBoolean("use-configuration-location", true);
        getConfiguration().setString("world", spawnLocation.getWorld().getName());
        getConfiguration().setDouble("world-x", spawnLocation.getX());
        getConfiguration().setDouble("world-y", spawnLocation.getY());
        getConfiguration().setDouble("world-z", spawnLocation.getZ());
        getConfiguration().setDouble("world-yaw", spawnLocation.getYaw());
        getConfiguration().setDouble("world-pitch", spawnLocation.getPitch());
        getConfiguration().save();

        p.sendMessage(ChatColor.GOLD + "Spawn location has been set to your current location.");
    }

    @Command(aliases = {"spawn"},
        bounds = {0, -1},
        help = "To travel to spawn type §c/spawn",
        playerOnly = true)
    @CommandPermission("voxelguest.spawn.spawn")
    public void spawn(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        p.teleport(spawnLocation,
                getConfiguration().getBoolean("use-ender-pearl-teleportation-cause")
                ? PlayerTeleportEvent.TeleportCause.ENDER_PEARL : PlayerTeleportEvent.TeleportCause.COMMAND);
        if (getConfiguration().getBoolean("use-random-spawn-messages")) {
            p.sendMessage(ChatColor.AQUA + (spawnMessages[(int) (Math.random() * (spawnMessages.length - 1))]));
        }
    }
}
