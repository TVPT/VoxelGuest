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
import com.thevoxelbox.voxelguest.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.ModuleEventPriority;
import com.thevoxelbox.voxelguest.util.FlatFileManager;
import com.thevoxelbox.voxelguest.util.Formatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@MetaData(name="Vanish", description="Vanish in front of your peers!")
public class VanishModule extends Module {
    
    protected static List<String> vanished = new ArrayList<String>();
    protected static List<String> safeList = new ArrayList<String>();
    
    protected static List<String> fakequit = new ArrayList<String>();
    protected static List<String> ofakequit= new ArrayList<String>();
    
    private String[] reloadVanishedList;
    private String[] reloadFakequitList;
    private String[] reloadOfflineFQList;
    
    public VanishModule() {
        super(VanishModule.class.getAnnotation(MetaData.class));
    }
    
    class VanishConfiguration extends ModuleConfiguration {
        
        public VanishConfiguration(VanishModule parent) {
            super(parent);
        }
    }
    
    @Override
    public void enable() {
        setConfiguration(new VanishConfiguration(this));
        reloadVanishedList  = FlatFileManager.load("tmpvanished", "", true);
        reloadFakequitList  = FlatFileManager.load("tmpfakequit", "", true);
        reloadOfflineFQList = FlatFileManager.load("tmpofflinefakequit", "", true);
        
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.vanish.safelist")) {
                addMemberToSafeList(p);
            }
        }
        
        if (reloadVanishedList != null) {
            for (String str : reloadVanishedList) {
                hidePlayer(Bukkit.getPlayer(str));
            }
        }
        
        if (reloadFakequitList != null) {
            for (String str : reloadFakequitList) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(str);
                Player p = op.getPlayer();
                
                if (p != null) {
                    if (!fakequit.contains(str)) {
                        fakequit.add(str);
                    }
                } else {
                    ofakequit.add(str);
                }      
            }
        }
        
        if (reloadOfflineFQList != null) {
            for (String str : reloadOfflineFQList)
                if (!ofakequit.contains(str) && !fakequit.contains(str))
                    ofakequit.add(str);
        }
    }
    
    @Override
    public void disable() {
        String[] saveVanished = new String[vanished.size()];
        String[] saveFakequit = new String[fakequit.size()];
        String[] saveOfakequit = new String[ofakequit.size()];
        
        saveVanished = vanished.toArray(saveVanished);
        saveFakequit = fakequit.toArray(saveFakequit);
        saveOfakequit = ofakequit.toArray(saveOfakequit);
        
        FlatFileManager.save(saveVanished, "tmpvanished");
        FlatFileManager.save(saveFakequit, "tmpfakequit");
        FlatFileManager.save(saveOfakequit, "tmpofflinefakequit");
    }

    @Override
    public void explorationToggle(boolean explorationSetting) {
        
    }
    
    @Override
    public String getLoadMessage() {
        return "Vanish module loaded";
    }
    
    @Command(aliases="vanish",
            bounds={0,0},
            help="To toggle your vanish setting, type:\n"
            + "§c/vanish",
            playerOnly=true)
    @CommandPermission(permission="voxelguest.vanish.vanish")
    public void vanish(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        
        if (!isVanished(p))
            hidePlayer(p);
        else
            revealPlayer(p);
    }
    
    @Command(aliases={"fakequit", "fq"},
            bounds={0,0},
            playerOnly=true)
    @CommandPermission(permission="voxelguest.vanish.fakequit")
    public void fakequit(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        
        if (!isInFakequit(p))
            fakequitMember(p);
        else
            unFakequitMember(p);
    }
    
    @ModuleEvent(event=PlayerJoinEvent.class, priority=ModuleEventPriority.LOW)
    public void onPlayerJoin(BukkitEventWrapper wrapper) {
        PlayerJoinEvent event = (PlayerJoinEvent) wrapper.getEvent();
        
        if (PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.vanish.safelist")) {
            addMemberToSafeList(event.getPlayer());
            revealVanishedToPlayer(event.getPlayer());
        }
        
        if (ofakequit.contains(event.getPlayer().getName())) {
            ofakequit.remove(event.getPlayer().getName());
            hidePlayer(event.getPlayer());
            
            event.setJoinMessage("");
        }
    }
    
    @ModuleEvent(event=PlayerQuitEvent.class, priority=ModuleEventPriority.LOW)
    public void onPlayerQuit(BukkitEventWrapper wrapper) {
        PlayerQuitEvent event = (PlayerQuitEvent) wrapper.getEvent();
        
        if (fakequit.contains(event.getPlayer().getName())) {
            ofakequit.add(event.getPlayer().getName());
            fakequit.remove(event.getPlayer().getName());
            event.setQuitMessage("");
        }
    }
    
    @ModuleEvent(event=PlayerKickEvent.class, priority=ModuleEventPriority.LOW)
    public void onPlayerKick(BukkitEventWrapper wrapper) {
        PlayerKickEvent event = (PlayerKickEvent) wrapper.getEvent();
        
        if (fakequit.contains(event.getPlayer().getName())) {
            ofakequit.add(event.getPlayer().getName());
            fakequit.remove(event.getPlayer().getName());
            event.setLeaveMessage("");
        }
    }
    
    @ModuleEvent(event=PlayerChatEvent.class)
    public void onPlayerChat(BukkitEventWrapper wrapper) {
        PlayerChatEvent event = (PlayerChatEvent) wrapper.getEvent();
        
        if (isInFakequit(event.getPlayer())) {
            event.getPlayer().sendMessage("§cYou cannot chat while in FakeQuit");
            event.setCancelled(true);
            return;
        }
    }
    
    public void hidePlayer(Player hidden) {
        if (hidden == null)
            return;
        
        if (!vanished.contains(hidden.getName())) {
            vanished.add(hidden.getName());
            
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!safeList.contains(p.getName()))
                    p.hidePlayer(hidden);
            }
            
            hidden.sendMessage("§bYou have vanished.");
        }
    }
    
    public void revealPlayer(Player hidden) {
        if (vanished.contains(hidden.getName())) {
            vanished.remove(hidden.getName());
            VoxelGuest.log(Boolean.valueOf(isVanished(hidden)).toString());
            
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!safeList.contains(p.getName()))
                    p.showPlayer(hidden);
            }
            
            hidden.sendMessage("§bYou have reappeared.");
        }
    }
    
    public void revealVanishedToPlayer(Player p) {
        Iterator<String> it = vanished.listIterator();
        
        while (it.hasNext()) {
            String vanishedName = it.next();
            Player vanishedPlayer = Bukkit.getPlayer(vanishedName);
            
            if (vanishedPlayer == null)
                continue;
            
            p.showPlayer(vanishedPlayer);
        }
    }
    
    public void fakequitMember(Player p) {
        if (!fakequit.contains(p.getName()))
            fakequit.add(p.getName());
        else
            return;
        
        VoxelGuest.log(name, p.getName() + " has gone into FakeQuit.", 0);
        String leaveMessageFormat = VoxelGuest.getConfigData().getString("leave-message-format");
        String leaveMessage = "";
        
        if (leaveMessageFormat == null) {
            leaveMessage = "§e" + p.getName() + " left";
        } else { 
            String[] messages = Formatter.selectFormatter(SimpleFormatter.class).format(leaveMessageFormat, VoxelGuest.getGuestPlayer(p));
            leaveMessage = messages[0];
        }
        
        Bukkit.broadcastMessage(leaveMessage);
        
        if (!isVanished(p))
            hidePlayer(p);
    }
    
    public void unFakequitMember(Player p) {
        if (fakequit.contains(p.getName()))
            fakequit.remove(p.getName());
        else
            return;
        
        VoxelGuest.log(name, p.getName() + " has left FakeQuit.", 0);
        String leaveMessageFormat = VoxelGuest.getConfigData().getString("join-message-format");
        String leaveMessage = "";
        
        if (leaveMessageFormat == null) {
            leaveMessage = "§e" + p.getName() + " joined";
        } else { 
            String[] messages = Formatter.selectFormatter(SimpleFormatter.class).format(leaveMessageFormat, VoxelGuest.getGuestPlayer(p));
            leaveMessage = messages[0];
        }
        
        Bukkit.broadcastMessage(leaveMessage);
        
        if (isVanished(p))
            revealPlayer(p);
    }
    
    public int getFakequitSize() {
        return fakequit.size();
    }
    
    public void addMemberToSafeList(Player p) {
        if (!safeList.contains(p.getName()))
            safeList.add(p.getName());
    }
    
    public boolean isOnSafeList(Player p) {
        return safeList.contains(p.getName());
    }
    
    public boolean isVanished(Player p) {
        return vanished.contains(p.getName());
    }
    
    public boolean isInFakequit(Player p) {
        return fakequit.contains(p.getName());
    }
}
