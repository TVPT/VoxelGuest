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

package com.thevoxelbox.voxelguest.commands;

import com.thevoxelbox.voxelguest.commands.engine.Command;
import com.thevoxelbox.voxelguest.commands.engine.CommandPermission;
import com.thevoxelbox.voxelguest.commands.engine.Subcommands;
import com.thevoxelbox.voxelguest.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.AFKModule;
import com.thevoxelbox.voxelguest.VanishModule;
import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.ModuleException;
import com.thevoxelbox.voxelguest.modules.ModuleManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MiscellaneousCommands {
    private final String AFK = "§8[§9AFK§8]";
    private final String FAKEQUIT = "§8[§cFQ§8]";
    private final String COMMA = "§6,";
    
    private HashMap<String, Location> teleportHistory = new HashMap<String, Location>();
    
    
    @Command(aliases={"who", "online", "list", "readlist", "playerlist"},
            bounds={0,1},
            help="To list all online players, type §c/who")
    @Subcommands(arguments={"-f"},
            permission={"voxelguest.miscellaneous.list.admin"})
    @CommandPermission(permission="voxelguest.miscellaneous.list.list")
    public void who(CommandSender cs, String[] args) {
        if (args != null && args.length > 0 && args[0].equalsIgnoreCase("-f")) {
            adminWho(cs);
            return;
        }
        
        normalWho(cs);
    }
    
    @Command(aliases={"vteleport", "vtp"},
            bounds={1, 2},
            help="Teleport to other people with /vtp [player]\n"
            + "Teleport to other people at an offset with /vtp [player] [x#,y#,z#][num]\n"
            + "Teleport others to you with /vtp [player] me",
            playerOnly=true)
    @CommandPermission(permission="voxelguest.miscellaneous.vtp")
    public void vteleport(CommandSender cs, String[] args) {
        if (args != null && args.length > 0) {
            Player p = (Player) cs;

            List<Player> l = Bukkit.matchPlayer(args[0]);
            if (l.size() > 1) {
                p.sendMessage(ChatColor.RED + "Partial match");
            } else if (l.isEmpty()) {
                p.sendMessage(ChatColor.RED + "No player to match");
            } else {
                Player pl = l.get(0);
                Location loc = pl.getLocation();

                p.sendMessage(ChatColor.AQUA + "Woosh!");

                if (args.length < 2) {
                    p.teleport(loc);
                } else {
                    if (args[1].matches("me")) {
                        insertHistoryEntry(pl, pl.getLocation());
                        pl.sendMessage(ChatColor.DARK_AQUA + "Woosh!");
                        pl.teleport(p.getLocation());
                        return;
                    }

                    for (int i = 1; i < args.length; i++) {
                        try {
                            if (args[i].startsWith("x")) {
                                loc.setX(loc.getX() + Double.parseDouble(args[i].replace("x", "")));
                                continue;
                            } else if (args[i].startsWith("y")) {
                                loc.setY(loc.getY() + Double.parseDouble(args[i].replace("y", "")));
                                continue;
                            } else if (args[i].startsWith("z")) {
                                loc.setZ(loc.getZ() + Double.parseDouble(args[i].replace("z", "")));
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage(ChatColor.RED + "Error parsing argument \"" + args[i] + "\"");
                            return;
                        }
                    }

                    insertHistoryEntry(p, p.getLocation());
                    p.teleport(loc);
                }
            }
            return;
        } else {
            cs.sendMessage(ChatColor.RED + "Please specify the target player.");
            return;
        }
    }
    
    @Command(aliases={"vback"},
            bounds={0, 0},
            help="Go back to your previous location with §c/vback",
            playerOnly=true)
    @CommandPermission(permission="voxelguest.miscellaneous.vback")
    public void vback(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        
        Location back = getHistoryEntry(p);
        if (back == null) {
            p.sendMessage("§cYou have no previous location.");
        } else {
            p.sendMessage(ChatColor.DARK_AQUA + "Woosh!");
            p.teleport(back);
        }
    }
    
    private void insertHistoryEntry(Player p, Location last) {
        teleportHistory.put(p.getName(), last);
    }
    
    private Location getHistoryEntry(Player p) {
        if (!teleportHistory.containsKey(p.getName())) {
            return null;
        } else {
            Location back = teleportHistory.get(p.getName());
            teleportHistory.remove(p.getName());
            return back;
        }
    }
    
    private void adminWho(CommandSender sender) {
        LinkedHashMap<String, List<String>> storage = new LinkedHashMap<String, List<String>>();
        String defaultGroupId = VoxelGuest.getGroupManager().getDefaultConfiguration().getString("group-id");
        boolean colorSwitch = false;
        
        String header = "";
        
        storage.put("A", null);
        storage.put("C", null);
        storage.put("S", null);
        storage.put("L", null);
        storage.put("M", null);
        storage.put("G", null);
        storage.put("V", null);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            String groupId;
            
            String[] groups = PermissionsManager.getHandler().getGroups(p.getName());
            
            if (groups == null || groups.length == 0) {
                groupId = defaultGroupId;
            } else {
                groupId = VoxelGuest.getGroupManager().getGroupConfiguration(groups[0]).getString("group-id");
            }
            
            boolean afk = isAFK(p);
            boolean fq  = isInFakeQuit(p);
            
            String user = ((fq) ? FAKEQUIT : "") + ((afk) ? AFK : "") + ((colorSwitch) ? "§7" : "§f") + p.getName();
            groupId = "§8[" + groupId + "§8]";
            
            if (!storage.containsKey(groupId)) {
                List<String> l = new ArrayList<String>();
                l.add(user);
                storage.put(groupId, l);
            } else {
                List<String> l = storage.get(groupId);
                l.add(user);
                storage.put(groupId, l);
            }
            
            colorSwitch = !colorSwitch;
        }
        
        header = writeHeader(storage, Bukkit.getOnlinePlayers().length);
        
        sender.sendMessage("§8------------------------------");
        sender.sendMessage(header.trim());
        
        for (Map.Entry<String, List<String>> entry : storage.entrySet()) {
            sendGroupStrings(sender, entry.getValue(), entry.getKey());
        }
        
        sender.sendMessage("§8------------------------------");
    }
    
    private void normalWho(CommandSender sender) {
        LinkedHashMap<String, List<String>> storage = new LinkedHashMap<String, List<String>>();
        String defaultGroupId = VoxelGuest.getGroupManager().getDefaultConfiguration().getString("group-id");
        boolean colorSwitch = false;
        
        String header = "";
        
        storage.put("A", null);
        storage.put("C", null);
        storage.put("S", null);
        storage.put("L", null);
        storage.put("M", null);
        storage.put("G", null);
        storage.put("V", null);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isInFakeQuit(p))
                continue;
            
            String groupId;
            
            String[] groups = PermissionsManager.getHandler().getGroups(p.getName());
            
            if (groups == null || groups.length == 0) {
                groupId = defaultGroupId;
            } else {
                groupId = VoxelGuest.getGroupManager().getGroupConfiguration(groups[0]).getString("group-id");
            }
            
            boolean afk = isAFK(p);
            
            String user = ((afk) ? AFK : "") + ((colorSwitch) ? "§7" : "§f") + p.getName();
            groupId = "§8[" + groupId + "§8]";
            
            if (!storage.containsKey(groupId)) {
                List<String> l = new ArrayList<String>();
                l.add(user);
                storage.put(groupId, l);
            } else {
                List<String> l = storage.get(groupId);
                l.add(user);
                storage.put(groupId, l);
            }
            
            colorSwitch = !colorSwitch;
        }
        
        
        header = writeHeader(storage, Bukkit.getOnlinePlayers().length - getFakequitSize());
        
        sender.sendMessage("§8------------------------------");
        sender.sendMessage(header.trim());
        
      for (Map.Entry<String, List<String>> entry : storage.entrySet()) {
          sendGroupStrings(sender, entry.getValue(), entry.getKey());
      }
        
        sender.sendMessage("§8------------------------------");
    }
    
    private String writeHeader(HashMap<String, List<String>> storage, int onlineNumber) {
        String header = "";
        String defaultGroupId = VoxelGuest.getGroupManager().getDefaultConfiguration().getString("group-id");
        
        for (String group : VoxelGuest.getGroupManager().getRegisteredGroups()) {
            String groupId = VoxelGuest.getGroupManager().getGroupConfiguration(group).getString("group-id");
            
            if (groupId == null)
                groupId = defaultGroupId;
            
            String groupTest = "§8[" + groupId + "§8]";
            
            if (storage.containsKey(groupTest)) {
                header = header + "§8[" + groupId + ":" + storage.get(groupTest).size() + "§8] ";
            } else {
                header = header + "§8[" + groupId + ":0§8] ";
            }
        }
        
        return (header.trim() + (" §8(§fO:" + onlineNumber + "§8)"));
    }
    
    private void sendGroupStrings(CommandSender cs, List<String> list, String groupHeader) {
        if (list == null || list.isEmpty()) {
            return;
        } else {
            boolean groupStart = false;
            String line = " ";
            Iterator<String> it = list.listIterator();
            Collections.sort(list);
            
            while (it.hasNext()) {
                String str = it.next();
                
                if (line.length() + (str.length() + COMMA.length() + 1) > 70) {
                    if (!groupStart) {
                        groupStart = true;
                        cs.sendMessage(groupHeader + line.substring(0, line.length() - 2));
                        line = " ";
                    } else {
                        cs.sendMessage(line.substring(0, line.length() - 2));
                        line = " ";
                    }
                }
                
                line += (str + COMMA + " ");
            }
            
            if (!line.isEmpty()) {
                if (!groupStart) {
                    cs.sendMessage(groupHeader + line.substring(0, line.length() - 2));
                } else {
                    cs.sendMessage(line.substring(0, line.length() - 2));
                }
            }
        }
    }
    
    
       
    private boolean isAFK(Player p) {
        try {
            AFKModule module = (AFKModule) ModuleManager.getManager().getModule(AFKModule.class);
            return module.isAFK(p);
        } catch (ModuleException ex) {
            return false;
        }
    }
    
    private boolean isInFakeQuit(Player p) {
        try {
            VanishModule module = (VanishModule) ModuleManager.getManager().getModule(VanishModule.class);
            return module.isInFakequit(p);
        } catch (ModuleException ex) {
            return false;
        }
    }
    
    private int getFakequitSize() {
        try {
            VanishModule module = (VanishModule) ModuleManager.getManager().getModule(VanishModule.class);
            return module.getFakequitSize();
        } catch (ModuleException ex) {
            return 0;
        }
    }
}
