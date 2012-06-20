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
import com.thevoxelbox.voxelguest.commands.engine.Subcommands;
import com.thevoxelbox.voxelguest.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.ModuleEventPriority;
import com.thevoxelbox.voxelguest.modules.ModuleException;
import com.thevoxelbox.voxelguest.modules.Setting;
import com.thevoxelbox.voxelguest.players.GroupNotFoundException;
import com.thevoxelbox.voxelguest.players.GuestPlayer;
import com.thevoxelbox.voxelguest.util.FlatFileManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@MetaData(name="Greylist", description="Allows for the setup of a greylist system!")
public class GreylistModule extends Module {
    
    private static final List<String> greylist = new ArrayList<String>();
    private final List<String> onlineGreys = new ArrayList<String>();
    
    private StreamThread streamTask = null;
    private String streamPasswordHash;
    private int streamPort;
    
    private int onlineGreylistLimit = -1;
    private boolean explorationMode = false;
    
    public GreylistModule() {
        super(GreylistModule.class.getAnnotation(MetaData.class));
    }
    
    class GreylistConfiguration extends ModuleConfiguration {
        @Setting("enable-greylist") public boolean enableGreylist = false;
        @Setting("enable-greylist-stream") public boolean enableGreylistStream = false;
        @Setting("greylist-stream-password") public String streamPassword = "changeme";
        @Setting("greylist-stream-port") public int streamPort = 8080;
        @Setting("exploration-mode") public boolean explorationMode = false;
        @Setting("greylist-online-limit") public int onlineLimit = 10;
        @Setting("greylist-not-greylisted-kick-message") public String notGreylistedKickMessage = "You are not greylisted on this server.";
        @Setting("greylist-over-capacity-kick-message") public String overCapacityKickMessage = "The server is temporarily over guest capacity. Check back later.";
        @Setting("save-on-player-greylist") public boolean saveOnPlayerGreylist = false;
        @Setting("backup-greylist-entries") public boolean backupGreylistEntries = false;
        
        public GreylistConfiguration(GreylistModule parent) {
            super(parent);
        }
    }
    
    @Override
    public void enable() throws ModuleException {
        setConfiguration(new GreylistConfiguration(this));
        String[] list = FlatFileManager.load("greylist");
        
        if (list == null) {
            throw new ModuleException("Empty greylist");
        } else if (!getConfiguration().getBoolean("enable-greylist")) {
            throw new ModuleException("Greylist is disabled in config");
        }
        
        injectGreylist(list);
        
        if (getConfiguration().getBoolean("enable-greylist-stream") && 
                getConfiguration().getString("greylist-stream-password") != null &&
                getConfiguration().getInt("greylist-stream-port") != -1) {
            
            streamPort = getConfiguration().getInt("greylist-stream-port");
            streamPasswordHash = getConfiguration().getString("greylist-stream-password");
            streamTask = new StreamThread(this);
            streamTask.start();
            
            explorationMode = getConfiguration().getBoolean("exploration-mode");
            onlineGreylistLimit = getConfiguration().getInt("greylist-online-limit");
        }
    }
    
    @Override
    public void disable() {
        if (streamTask != null)
            streamTask.killProcesses();
            
        saveGreylist();
        
        if (getConfiguration().getBoolean("backup-greylist-entries")) {
            backupEntries();
        }
    }

    @Override
    public void explorationToggle(boolean explorationSetting) {
        
    }
    
    @Override
    public String getLoadMessage() {
        return "Greylist module loaded";
    }
    
    @Command(aliases={"greylist", "gl", "graylist"},
            bounds={1, -1})
    @CommandPermission(permission="voxelguest.greylist.admin.add")
    @Subcommands(arguments={"limit", "password"},
            permission={"voxelguest.greylist.admin.limit", "voxelguest.greylist.admin.password"})
    public void greylist(CommandSender cs, String[] args) {
        if (args[0].equalsIgnoreCase("limit")) {
            try {
                int newLimit = Integer.parseInt(args[1]);
                onlineGreylistLimit = newLimit;
                getConfiguration().setInt("greylist-online-limit", onlineGreylistLimit);
                cs.sendMessage(ChatColor.GREEN + "Reset the online greylist limit to " + onlineGreylistLimit);
                return;
            } catch (NumberFormatException ex) {
                cs.sendMessage("Incorrect format. Try /gl limit [number]");
                return;
            }
        } else if (args[0].equalsIgnoreCase("password")) {
            String concat = "";
            
            for (int i = 1; i < args.length; i++) {
                if (i == (args.length - 1))
                    concat = concat + args[i];
                else
                    concat = concat + args[i] + " ";
            }
            
            String reverse = (new StringBuilder(concat)).reverse().toString();
            
            try {
                setPassword(name, reverse);
                cs.sendMessage(ChatColor.GREEN + "Set the greylist stream password to \"" + concat + "\"");
                return;
            } catch (CouldNotStoreEncryptedPasswordException ex) {
                cs.sendMessage(ChatColor.RED + "Could not store the greylist stream password");
            }
        }
        
        String user = args[0];
        injectGreylist(user);
        announceGreylist(user);
        
        if (getConfiguration().getBoolean("save-on-player-greylist")) {
            saveGreylist();
        }
        
        if (getConfiguration().getBoolean("backup-greylist-entries")) {
            backupEntries();
        }
    }
    
    @Command(aliases={"whitelist", "wl"},
            bounds={1, 1},
            help="Whitelist someone to your server\n"
            + "by typing §c/whitelist [player]")
    @CommandPermission(permission="voxelguest.greylist.whitelist")
    public void whitelist(CommandSender cs, String[] args) {
        List<Player> l = Bukkit.matchPlayer(args[0]);
        
        if (l.isEmpty()) {
            cs.sendMessage("§cNo player found with that name.");
            return;
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple players found with that name.");
            return;
        } else {
            Player p = l.get(0);
            
            try {
                String group = VoxelGuest.getGroupManager().findGroup("whitelist", true);
                
                if (PermissionsManager.hasMultiGroupSupport()) {
                    PermissionsManager.getHandler().addGroup(p.getName(), group);
                } else {
                    for (String nixGroup : PermissionsManager.getHandler().getGroups(p.getName()))
                        PermissionsManager.getHandler().removeGroup(p.getName(), nixGroup);
                    
                    PermissionsManager.getHandler().addGroup(p.getName(), group);
                }
                
                if (!PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.bypass")) {
                    PermissionsManager.getHandler().giveGroupPermission(group, "voxelguest.greylist.bypass");
                }
                
            } catch (GroupNotFoundException ex) {
                PermissionsManager.getHandler().givePermission(p.getName(), "voxelguest.greylist.bypass");
            }
            
            String header = "";
            for (String group : VoxelGuest.getGroupManager().getRegisteredGroups()) {
                List<String> players = VoxelGuest.getGroupManager().getPlayerListForGroup(group);
                String groupId = VoxelGuest.getGroupManager().getGroupConfiguration(group).getString("group-id");
                
                if (groupId == null)
                    groupId = "§fG";
                
                if (players != null) {
                    header = header + "§8[" + groupId + ":" + players.size() + "§8] ";
                    continue;
                }
                
                header = header + "§8[" + groupId + ":0§8] ";
            }
            
            Bukkit.broadcastMessage(header);
            Bukkit.broadcastMessage("§aWhitelisted: §6" + p.getName());
        }
    }
    
    @Command(aliases={"unwhitelist", "unwl"},
            bounds={1,1},
            help="Unwhitelist someone to your server\n"
            + "by typing §c/unwhitelist [player]")
    @CommandPermission(permission="voxelguest.greylist.unwhitelist")
    public void unwhitelist(CommandSender cs, String[] args) {
        List<Player> l = Bukkit.matchPlayer(args[0]);
        
        if (l.isEmpty()) {
            cs.sendMessage("§cNo player found with that name.");
            return;
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple players found with that name.");
            return;
        } else {
            Player p = l.get(0);
            
            try {
                String whitelistGroup = VoxelGuest.getGroupManager().findGroup("whitelist", true);
                String greylistGroup = VoxelGuest.getGroupManager().findGroup("greylist", true);
                
                if (PermissionsManager.hasMultiGroupSupport()) {
                    PermissionsManager.getHandler().addGroup(p.getName(), greylistGroup);
                    PermissionsManager.getHandler().removeGroup(p.getName(), whitelistGroup);
                } else {
                    for (String nixGroup : PermissionsManager.getHandler().getGroups(p.getName()))
                        PermissionsManager.getHandler().removeGroup(p.getName(), nixGroup);
                    
                    PermissionsManager.getHandler().addGroup(p.getName(), greylistGroup);
                }
                
                if (PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.bypass")) {
                    PermissionsManager.getHandler().removeGroupPermission(greylistGroup, "voxelguest.greylist.bypass");
                }
                
            } catch (GroupNotFoundException ex) {
                PermissionsManager.getHandler().removePermission(p.getName(), "voxelguest.greylist.bypass");
            }
            
            String header = "";
            for (String group : VoxelGuest.getGroupManager().getRegisteredGroups()) {
                List<String> players = VoxelGuest.getGroupManager().getPlayerListForGroup(group);
                String groupId = VoxelGuest.getGroupManager().getGroupConfiguration(group).getString("group-id");
                
                if (groupId == null)
                    groupId = "§fG";
                
                if (players != null) {
                    header = header + "§8[" + groupId + ":" + players.size() + "§8] ";
                    continue;
                }
                
                header = header + "§8[" + groupId + ":0§8] ";
            }
            
            Bukkit.broadcastMessage(header);
            Bukkit.broadcastMessage("§4Unwhitelisted: §6" + p.getName());
        }
    }
    
    @Command(aliases={"explorationmode"},
            bounds={0,0},
            help="Toggle your server's floodgates on and off")
    @CommandPermission(permission="voxelguest.greylist.admin.exploration")
    public void explorationMode(CommandSender cs, String[] args) {
        explorationMode = !explorationMode;
        getConfiguration().setBoolean("exploration-mode", explorationMode);
        cs.sendMessage(ChatColor.GREEN + "Exploration mode has been " + ((explorationMode) ? "enabled" : "disabled"));
    }
    
    @ModuleEvent(event=PlayerPreLoginEvent.class, priority= ModuleEventPriority.HIGHEST)
    public void onPlayerPreLogin(BukkitEventWrapper wrapper) {
        PlayerPreLoginEvent event = (PlayerPreLoginEvent) wrapper.getEvent();
        
        if (PermissionsManager.getHandler().hasPermission(event.getName(), "voxelguest.greylist.bypass")) {
            return;
        }
        
        VoxelGuest.log(event.getName());
        
        if (!explorationMode) {
            if (!greylist.contains(event.getName().toLowerCase())) {
                event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, (getConfiguration().getString("greylist-not-greylisted-kick-message") != null) ? getConfiguration().getString("greylist-not-greylisted-kick-message") : "You are not greylisted on this server.");
                return;
            } else if (greylist.contains(event.getName().toLowerCase()) && !PermissionsManager.getHandler().hasPermission(event.getName(), "voxelguest.greylist.bypass")) {
                if (onlineGreylistLimit > -1 && onlineGreys.size() >= onlineGreylistLimit) {
                    String str = getConfiguration().getString("greylist-over-capacity-kick-message");
                    event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, (str != null) ? str : "The server is temporarily over guest capacity. Check back later.");
                    return;
                }
            }
        }
    }
    
    @ModuleEvent(event=PlayerJoinEvent.class, priority=ModuleEventPriority.HIGHEST)
    public void onPlayerJoin(BukkitEventWrapper wrapper) {
        PlayerJoinEvent event = (PlayerJoinEvent) wrapper.getEvent();
        GuestPlayer gp = VoxelGuest.getGuestPlayer(event.getPlayer());
        
        if (PermissionsManager.getHandler().hasPermission(gp.getPlayer().getName(), "voxelguest.greylist.bypass")) {
            return;
        }
        
        if (!explorationMode) {
            if (!greylist.contains(gp.getPlayer().getName().toLowerCase())) {
                gp.getPlayer().kickPlayer((getConfiguration().getString("greylist-not-greylisted-kick-message") != null) ? getConfiguration().getString("greylist-not-greylisted-kick-message") : "You are not greylisted on this server.");
                event.setJoinMessage("");
                return;
            } else if (greylist.contains(gp.getPlayer().getName().toLowerCase()) && !PermissionsManager.getHandler().hasPermission(gp.getPlayer().getName(), "voxelguest.greylist.bypass")) {
                if (onlineGreylistLimit > -1 && onlineGreys.size() >= onlineGreylistLimit) {
                    String str = getConfiguration().getString("greylist-over-capacity-kick-message");
                    gp.getPlayer().kickPlayer((str != null) ? str : "The server is temporarily over guest capacity. Check back later.");
                    return;
                }
                
                if (!onlineGreys.contains(gp.getPlayer().getName())) {
                    onlineGreys.add(gp.getPlayer().getName());
                    
                    try {
                        String user = gp.getPlayer().getName();
                        
                        String[] groups = PermissionsManager.getHandler().getGroups(user);
                        String group = VoxelGuest.getGroupManager().findGroup("greylist", true);

                        if (groups == null || groups.length == 0 || !Arrays.asList(groups).contains(group)) {
                            if (!PermissionsManager.hasMultiGroupSupport()) {
                                for (String _group : groups) {
                                    PermissionsManager.getHandler().removeGroup(user, _group);
                                }

                                PermissionsManager.getHandler().addGroup(user, group);
                            } else {
                                PermissionsManager.getHandler().addGroup(user, group);
                            }
                        }
                    } catch (GroupNotFoundException ex) {
                        // Just leave in greylist ... no group defined
                    }
                }
            }
        }
    }
    
    @ModuleEvent(event=PlayerQuitEvent.class)
    public void onPlayerQuit(BukkitEventWrapper wrapper) {
        PlayerQuitEvent event = (PlayerQuitEvent) wrapper.getEvent();
        
        if (!explorationMode && onlineGreys.contains(event.getPlayer().getName()))
            onlineGreys.remove(event.getPlayer().getName());
    }
    
    @ModuleEvent(event=PlayerKickEvent.class)
    public void onPlayerKick(BukkitEventWrapper wrapper) {
        PlayerKickEvent event = (PlayerKickEvent) wrapper.getEvent();
        
        if (!explorationMode && onlineGreys.contains(event.getPlayer().getName()))
            onlineGreys.remove(event.getPlayer().getName());
    }
    
    private void saveGreylist() {
        if (!greylist.isEmpty()) {
            Iterator<String> it = greylist.listIterator();
            String[] toSave = new String[greylist.size()];
            int i = 0;

            while (it.hasNext()) {
                String entry = it.next();
                
                if (entry == null)
                    continue;
                
                toSave[i] = entry.toLowerCase();
                ++i;
            }

            FlatFileManager.save(toSave, "greylist");
        }
    }
    
    private void backupEntries() {
       if (!greylist.isEmpty()) {
            Iterator<String> it = greylist.listIterator();
            String[] toSave = new String[greylist.size()];
            int i = 0;

            while (it.hasNext()) {
                String entry = it.next();
                
                if (entry == null)
                    continue;
                
                toSave[i] = entry.toLowerCase();
                ++i;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            FlatFileManager.save(toSave, dateFormat.format(new Date()), "/greylist-backups");
        } 
    }
    
    private void announceGreylist(String user) {
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + user + ChatColor.DARK_GRAY + " was added to the greylist.");
    }
    
    private void announceGreylist(List<String> users) {
        Iterator<String> it = users.listIterator();
        
        while (it.hasNext()) {
            String user = it.next();
            announceGreylist(user);
        }
    }
    
    private void injectGreylist(String str) {
        if (str == null) {
            VoxelGuest.log("DERP");
        }
        
        if (!greylist.contains(str.toLowerCase()))
            greylist.add(str.toLowerCase());
        
        Iterator<String> it = greylist.listIterator();
    }
    
    private void injectGreylist(String[] strs) {
        if (strs == null)
            return;
        
        for (String str : strs) {
            if (!greylist.contains(str.toLowerCase()))
                greylist.add(str.toLowerCase());
        }
    }
    
    private void injectGreylist(List<String> list) {
        if (list.isEmpty() || list == null)
            return;
        
       Iterator<String> it = list.listIterator();
       
       while (it.hasNext()) {
           String next = it.next();
           
           if (!greylist.contains(next.toLowerCase()))
               greylist.add(next.toLowerCase());
       }
    }
    
    private String interpretStreamInput(String input) {
        String[] args = input.split("\\:");
        
        if (args[0].equals(streamPasswordHash)) {
            String user = args[1];
            boolean accepted = Boolean.parseBoolean(args[2]);
            
            if (accepted) {
                return user;
            }
        }
        
        return null;
    }
    
    class StreamThread extends Thread {
        private ServerSocket serverSocket;
        private StreamReader reader;
        
        public StreamThread(GreylistModule module) {
            try {    
                this.serverSocket = new ServerSocket(streamPort);
            } catch (IOException ex) {
                this.serverSocket = null;
                VoxelGuest.log(name, "Could not bind to port " + streamPort + ". Perhaps it is already in use?", 2);
            }
        }
        
        public void killProcesses() {
            if (reader != null && reader.getStatus() == 100) {
                reader.interrupt();
            }
            
            this.interrupt();
            
            try {
                serverSocket.close();
            } catch (IOException ex) {
                VoxelGuest.log(name, "Could not release port " + streamPort, 2);
            }
        }
        
        @Override
        public void run() {
            if (serverSocket == null)
                return;
            
            try {
                while (true) {
                    reader = new StreamReader(serverSocket.accept());
                    reader.start();
                }
                
            } catch (IOException ex) {
                // Shutting down...
            }
        }
    }
    
    class StreamReader extends Thread {
        private final Socket socket;
        private int status = -1;
        
        public StreamReader(Socket s) {
            socket = s;
        }
        
        public int getStatus() {
            // -1 : Not yet called
            // 100: In process
            // 200: Exited with no error
            // 201: Exited for no greylist to add
            // 202: Exited with socket being null
            // 222: Exited with error
            
            return status;
        }
        
        @Override
        public void run() {
            status = 100;
            try {
                VoxelGuest.log(name, "Accepted client on port " + streamPort, 0);
                List<String> list = readSocket(socket);
                socket.close();

                if (list == null || list.isEmpty()) {
                    status = 201;
                    return;
                }

                injectGreylist(list);
                announceGreylist(list);
                
                if (getConfiguration().getBoolean("save-on-player-greylist")) {
                    saveGreylist();
                }
                
                if (getConfiguration().getBoolean("backup-greylist-entries")) {
                    backupEntries();
                }
            } catch (IOException ex) {
                VoxelGuest.log(name, "Could not close client stream socket", 2);
                status = 222;
                return;
            }
        }
        
        private synchronized List<String> readSocket(Socket socket) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                
                List<String> list = new ArrayList<String>();
                String line = null;
                
                while ((line = in.readLine()) != null) {
                    String toAdd = interpretStreamInput(line);
                    
                    if (toAdd != null) {
                        if (!list.contains(toAdd))
                            list.add(toAdd);
                    }
                    
                    out.println(line);
                }
                
                in.close();
                out.close();
                socket.close();
                return list;
            } catch (SocketException ex) {
                VoxelGuest.log(name, "Stream closed while reading stream", 1);
                return null;
            } catch (IOException ex) {
                return null;
            }
        }
    }
    
    public void setPassword(String name, String input) throws CouldNotStoreEncryptedPasswordException {
        byte[] shhash = new byte[40];
        String store = "";
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(input.getBytes("iso-8859-1"), 0, input.length());
            shhash = md.digest();
            store = convertToHex(shhash);
            
            getConfiguration().setString("greylist-stream-password", store);
        } catch (NoSuchAlgorithmException e) {
            throw new CouldNotStoreEncryptedPasswordException("Fatal error in storage - NoSuchAlgorithmException");
        } catch (UnsupportedEncodingException e) {
            throw new CouldNotStoreEncryptedPasswordException("Fatal error in storage - UnsupportedEncodingException");
        }
    }
    
    private String convertToHex(byte[] data) { 
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    }
}
