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

import com.thevoxelbox.voxelguest.commands.engine.Command;
import com.thevoxelbox.voxelguest.commands.engine.CommandPermission;
import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.Setting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@MetaData(name="AFK", description="Handles all AFK players and whatnot")
public class AFKModule extends Module {
    
    protected HashMap<Player, Long> timeMap = new HashMap<Player, Long>();
    protected List<Player> afkList = new ArrayList<Player>();
    
    private int afkTaskID = -1;
    
    public AFKModule() {
        super(AFKModule.class.getAnnotation(MetaData.class));
    }
    
    class AFKConfiguration extends ModuleConfiguration {
        @Setting("afk-timeout-enabled") public boolean timeoutEnabled = false;
        @Setting("afk-timeout-minutes") public int timeoutMinutes = 5;
        
        public AFKConfiguration(AFKModule parent) {
            super(parent);
        }
    }

    @Override
    public void enable() {
        setConfiguration(new AFKConfiguration(this));
        timeMap.clear();
        
        for (Player player : Bukkit.getOnlinePlayers())
            timeMap.put(player, System.currentTimeMillis());
        
        if (getConfiguration().getBoolean("afk-timeout-enabled")) {
            if (getConfiguration().getInt("afk-timeout-minutes") >= 0) {
                final long timeout = 60000L * getConfiguration().getInt("afk-timeout-minutes");
                
                afkTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(VoxelGuest.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        for (Map.Entry<Player, Long> entry : timeMap.entrySet()) {
                            Player player = entry.getKey();
                            
                            if ((System.currentTimeMillis() - entry.getValue() > timeout) && !isAFK(player)) {
                                cycleAFK(player);
                                broadcastAFKMessage(player);
                            }
                        }
                    }
                    
                }, 0L, 1800L);
            }
        }
    }
    
    @Override
    public void disable() {
        timeMap.clear();
        afkList.clear();
        
        if (afkTaskID != -1)
            Bukkit.getScheduler().cancelTask(afkTaskID);
    }

    @Override
    public void explorationToggle(boolean explorationSetting) {
        
    }
    
    @Override
    public String getLoadMessage() {
        return "AFK module enabled - Auto-AFK timeout is " + (VoxelGuest.getConfigData().getBoolean("afk-timeout-enabled") ? "enabled" : "disabled");
    }
    
    public boolean isAFK(Player player) {
        return afkList.contains(player);
    }
    
    public void cycleAFK(Player player) {
        if (!isAFK(player))
            afkList.add(player);
        else
            afkList.remove(player);
    }
    
    @Command(aliases={"afk", "vafk"},
            bounds={0, -1},
            help="To go AFK, type §c/afk (message)",
            playerOnly=true)
    @CommandPermission(permission="voxelguest.afk.afk")
    public void afk(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        
        if (args.length == 0 && !isAFK(p)) {
            cycleAFK(p);
            broadcastAFKMessage(p);
            return;
        } else if (!isAFK(p)) {
            String concat = "";
            
            for (int i = 0; i < args.length; i++) {
                concat = concat + args[i] + " ";
            }
            
            concat = concat.trim();
            
            cycleAFK(p);
            broadcastAFKMessage(p, concat);
            return;
        }
    }
    
    @ModuleEvent(event=PlayerJoinEvent.class)
    public void onPlayerJoin(BukkitEventWrapper wrapper) {
        PlayerJoinEvent event = (PlayerJoinEvent) wrapper.getEvent();
        updateTimeEntry(event.getPlayer());
    }
    
    @ModuleEvent(event=PlayerQuitEvent.class)
    public void onPlayerQuit(BukkitEventWrapper wrapper) {
        PlayerQuitEvent event = (PlayerQuitEvent) wrapper.getEvent();
        timeMap.remove(event.getPlayer());
        if(isAFK(event.getPlayer())){
            afkList.remove(event.getPlayer());
        }
    }
    
    @ModuleEvent(event=PlayerKickEvent.class)
    public void onPlayerKick(BukkitEventWrapper wrapper) {
        PlayerKickEvent event = (PlayerKickEvent) wrapper.getEvent();
        timeMap.remove(event.getPlayer());
        if(isAFK(event.getPlayer())){
            afkList.remove(event.getPlayer());
        }
    }
    
    @ModuleEvent(event=PlayerChatEvent.class)
    public void onPlayerChat(BukkitEventWrapper wrapper) {
        PlayerChatEvent event = (PlayerChatEvent) wrapper.getEvent();
        Player p = event.getPlayer();
        
        if (isAFK(p)) {
            cycleAFK(p);
            broadcastAFKMessage(p);
        }
        
        updateTimeEntry(p);
    }
    
    @ModuleEvent(event=PlayerMoveEvent.class)
    public void onPlayerMove(BukkitEventWrapper wrapper) {
        PlayerMoveEvent event = (PlayerMoveEvent) wrapper.getEvent();
        Player p = event.getPlayer();
        
        if (isAFK(p)) {
            cycleAFK(p);
            broadcastAFKMessage(p);
        }
        
        updateTimeEntry(p);
    }
    
    private void updateTimeEntry(Player player) {
        timeMap.put(player, System.currentTimeMillis());
    }
    
    private void broadcastAFKMessage(Player player) {
        broadcastAFKMessage(player, null);
    }
    
    private void broadcastAFKMessage(Player player, String message) {
        if (message == null) {
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.DARK_GRAY + ((isAFK(player)) ? " has gone AFK" : " has returned"));
        } else {
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.DARK_GRAY + ((isAFK(player)) ? " " + message : " has returned"));
        }
    }
}
