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

import com.patrickanker.lib.bukkit.LibraryPlugin;
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.patrickanker.lib.commands.Subcommands;
import com.patrickanker.lib.notifications.Notification;
import com.patrickanker.lib.notifications.NotificationCentre;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.persist.MySQLDriver;
import com.patrickanker.lib.persist.SQLDriver;
import com.patrickanker.lib.persist.SQLiteDriver;
import com.patrickanker.lib.util.FlatFileManager;
import com.thevoxelbox.voxelguest.modules.*;
import com.thevoxelbox.voxelguest.players.GroupNotFoundException;
import com.thevoxelbox.voxelguest.players.GuestPlayer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@MetaData(name = "Greylist", description = "Allows for the setup of a greylist system!")
public class GreylistModule extends Module {

    private static final List<String> greylist = new ArrayList<String>();
    private final List<String> onlineGreys = new ArrayList<String>();
    private StreamThread streamTask = null;
    private String streamPasswordHash;
    private int streamPort;
    private int onlineGreylistLimit = -1;
    private boolean explorationMode = false;
    
    // -- Gatekeeper portion --
    private boolean gatekeeperIsEnabled;
    private final String gatekeeperDatabase = "gatekeeper";
    private final File gatekeeperSqliteDir = new File("plugins/VoxelGuest/");
    private SQLDriver driver;
    
    private final String HELPER_TABLE      = "helpers";
    private final String OPEN_REVIEW_TABLE = "open_reviews";
    private final String REVIEW_HISTORY_TABLE    = "review_history";
    
    private final String NEW_REVIEW_UUID   = "VGGKNewReview";
    private final Notification.NotificationProperty[] NEW_REVIEW_PROPERTIES = new Notification.NotificationProperty[] {Notification.NotificationProperty.SINGLE, Notification.NotificationProperty.STRONG};
    
    private final List<String> helpers = new ArrayList<String>();
    private final HashMap<String, String> openReviewTickets = new HashMap<String, String>();
    private final List<String> reviewBlacklist = new ArrayList<String>();

    public GreylistModule()
    {
        super(GreylistModule.class.getAnnotation(MetaData.class));
    }

    class GreylistConfiguration extends ModuleConfiguration {

        @Setting("enable-greylist") public boolean enableGreylist = false;
        @Setting("enable-greylist-stream") public boolean enableGreylistStream = false;
        @Setting("greylist-stream-password") public String streamPassword = "changeme";
        @Setting("greylist-stream-port") public int streamPort = 8080;
        @Setting("exploration-mode") public boolean explorationMode = false;
        @Setting("announce-visitor-logins") public boolean announceVisitorLogins = false;
        @Setting("greylist-online-limit") public int onlineLimit = 10;
        @Setting("greylist-not-greylisted-kick-message") public String notGreylistedKickMessage = "You are not greylisted on this server.";
        @Setting("greylist-over-capacity-kick-message") public String overCapacityKickMessage = "The server is temporarily over guest capacity. Check back later.";
        @Setting("save-on-player-greylist") public boolean saveOnPlayerGreylist = false;
        @Setting("backup-greylist-entries") public boolean backupGreylistEntries = false;
        @Setting("enable-gatekeeper") public boolean enableGatekeeper = false;
        @Setting("gatekeeper-storage-type") public String gatekeeperStorageType = "sqlite";

        public GreylistConfiguration(GreylistModule parent)
        {
            super(parent);
        }
    }

    @Override
    public void enable() throws ModuleException
    {
        setConfiguration(new GreylistConfiguration(this));
        String[] list = FlatFileManager.load("greylist", "/VoxelGuest");

        if (list == null) {
            throw new ModuleException("Empty greylist");
        } else if (!getConfiguration().getBoolean("enable-greylist")) {
            throw new ModuleException("Greylist is disabled in config");
        }

        injectGreylist(list);

        if (getConfiguration().getBoolean("enable-greylist-stream")
                && getConfiguration().getString("greylist-stream-password") != null
                && getConfiguration().getInt("greylist-stream-port") != -1) {

            streamPort = getConfiguration().getInt("greylist-stream-port");
            streamPasswordHash = getConfiguration().getString("greylist-stream-password");
            streamTask = new StreamThread(this);
            streamTask.start();

            explorationMode = getConfiguration().getBoolean("exploration-mode");
            onlineGreylistLimit = getConfiguration().getInt("greylist-online-limit");
        }
        
        if (getConfiguration().getBoolean("enable-gatekeeper")) {
            gatekeeperIsEnabled = true;
            
            // Gatekeeper loading logic
            
            if (getConfiguration().getString("gatekeeper-storage-type").equalsIgnoreCase("mysql")) {
                driver = new MySQLDriver(LibraryPlugin.getConfigData().getString("mysql-username"), 
                            LibraryPlugin.getConfigData().getString("mysql-password"), 
                            LibraryPlugin.getConfigData().getString("mysql-hostname"), 
                            LibraryPlugin.getConfigData().getString("mysql-port"), 
                            LibraryPlugin.getConfigData().getString("mysql-database"));
            } else {
                driver = new SQLiteDriver(gatekeeperDatabase, gatekeeperSqliteDir.getAbsolutePath());
            }
            
            if (!driver.checkTable(HELPER_TABLE)) {
                driver.createTable("CREATE TABLE " + HELPER_TABLE + "(Name varchar(255))");
            } else {
                try {
                    Statement statement;
                    ResultSet result;

                    try {
                        driver.getConnection();
                        statement = driver.getStatement();
                        result = statement.executeQuery("SELECT * FROM " + HELPER_TABLE);

                        while (result.next()) {
                            helpers.add(result.getString("Name"));
                        }

                    } catch (SQLException ex) {
                        VoxelGuest.log("SQLException caught in GreylistModule.enable(): " + ex.getMessage());
                    } finally {
                        driver.release();
                    }
                } catch (SQLException ex) {
                    VoxelGuest.log("SQLException caught in GreylistModule.enable()(): " + ex.getMessage());
                }
            }
            
            if (!driver.checkTable(OPEN_REVIEW_TABLE)) {
                driver.createTable("CREATE TABLE " + OPEN_REVIEW_TABLE + "(Name varchar(255), Session varchar(255))");
            } else {
                try {
                    
                    Statement statement;
                    ResultSet result;

                    try {
                        driver.getConnection();
                        statement = driver.getStatement();
                        result = statement.executeQuery("SELECT * FROM " + OPEN_REVIEW_TABLE);

                        while (result.next()) {
                            openReviewTickets.put(result.getString("Name"), result.getString("Session"));
                        }

                    } catch (SQLException ex) {
                        VoxelGuest.log("SQLException caught in GreylistModule.load(): " + ex.getMessage());
                    } finally {
                        driver.release();
                    }
                } catch (SQLException ex) {
                    VoxelGuest.log("SQLException caught in GreylistModule.load()(): " + ex.getMessage());
                }
            }
            
            for (Map.Entry<String, String> entry : openReviewTickets.entrySet()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey());
                
                if (!op.isOnline()) {
                    closeReviewNoUpdate(op.getName());
                }
            }
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (helpers.contains(p.getName()) || PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.whitelist")) {
                    NotificationCentre.sharedCentre().addObserver(p.getName(), NEW_REVIEW_UUID);
                }
            }
            
        } else {
            gatekeeperIsEnabled = false;
        }
    }

    @Override
    public void disable()
    {
        if (streamTask != null) {
            streamTask.killProcesses();
        }

        saveGreylist();

        if (getConfiguration().getBoolean("backup-greylist-entries")) {
            backupEntries();
        }
        
        // -- Gatekeeper portion --
        
        if (gatekeeperIsEnabled) {
            driver.clearTable(HELPER_TABLE);
            driver.clearTable(OPEN_REVIEW_TABLE);
            
            try {
                

                try {
                    driver.getConnection();
                    
                    for (String helper : helpers) {
                        Statement statement = driver.getStatement();
                        statement.execute("INSERT INTO " + HELPER_TABLE + " VALUES ('" + helper + "')");
                        statement.close();
                    }
                    
                    for (Map.Entry<String, String> entry : openReviewTickets.entrySet()) {
                        Statement statement = driver.getStatement();
                        statement.execute("INSERT INTO " + OPEN_REVIEW_TABLE + " VALUES ('" + entry.getKey() + "', '" + entry.getValue() + "')");
                        statement.close();
                    }

                } catch (SQLException ex) {
                    VoxelGuest.log("SQLException caught in GreylistModule.disable(): " + ex.getMessage());
                } finally {
                    driver.release();
                }
            } catch (SQLException ex) {
                VoxelGuest.log("SQLException caught in GreylistModule.disable(): " + ex.getMessage());
            }
        }
    }

    @Override
    public String getLoadMessage()
    {
        return "Greylist module loaded";
    }

    @Command(aliases = {"greylist", "gl", "graylist"},
        bounds = {1, -1})
    @CommandPermission("voxelguest.greylist.admin.add")
    @Subcommands(arguments = {"limit", "password"},
    permission = {"voxelguest.greylist.admin.limit", "voxelguest.greylist.admin.password"})
    public void greylist(CommandSender cs, String[] args)
    {
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
                if (i == (args.length - 1)) {
                    concat = concat + args[i];
                } else {
                    concat = concat + args[i] + " ";
                }
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

    @Command(aliases = {"whitelist", "wl"},
        bounds = {1, 1},
        help = "Whitelist someone to your server\n"
        + "by typing §c/whitelist [player]")
    @CommandPermission("voxelguest.greylist.whitelist")
    public void whitelist(CommandSender cs, String[] args)
    {
        List<Player> l = Bukkit.matchPlayer(args[0]);

        if (l.isEmpty()) {
            cs.sendMessage("§cNo player found with that name.");
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple players found with that name.");
        } else {
            Player p = l.get(0);

            try {
                String group = VoxelGuest.getGroupManager().findGroup("whitelist", true);

                if (PermissionsManager.hasMultiGroupSupport()) {
                    PermissionsManager.getHandler().addGroup(p.getName(), group);
                } else {
                    for (String nixGroup : PermissionsManager.getHandler().getGroups(p.getName())) {
                        PermissionsManager.getHandler().removeGroup(p.getName(), nixGroup);
                    }

                    PermissionsManager.getHandler().addGroup(p.getName(), group);
                }

                if (!PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.bypass")) {
                    PermissionsManager.getHandler().giveGroupPermission(group, "voxelguest.greylist.bypass");
                }

            } catch (GroupNotFoundException ex) {
                PermissionsManager.getHandler().givePermission(p.getName(), "voxelguest.greylist.bypass");
            }

            
            Bukkit.broadcastMessage(getHeader());
            Bukkit.broadcastMessage("§aWhitelisted: §6" + p.getName());
        }
    }
    
    @Command(aliases= {"whitelistreview", "wlreview"},
            bounds={0,2},
            help="§c/whitelistreview §fwill open a new whitelist review request for a greylistee\n"
            + "§c/whitelistreview <player> §fwill close a review request by a helper/whitelisting figure\n"
            + "and will teleport the reviewer to the requesting party\n"
            + "§c/whitelistreview [history, -h] <player> §f will show the review request count\n"
            + "to the command sender. <player> must be spelled completely.\n"
            + "§c/whitelistreview [flag, -f] <player> §f will blacklist a greylistee\n"
            + "in case the greylistee spams the Gatekeeper service.\n"
            + "<player> must be spelled completely. The flag will only last\n"
            + "until a stop or reload.",
            playerOnly=true)
    @CommandPermission("voxelguest.greylist.whitelistreview.whitelistreview")
    public void whitelistReview(CommandSender cs, String[] args)
    {
        if (!gatekeeperIsEnabled) {
            cs.sendMessage("§cGatekeeper is not enabled.");
            return;
        }
        
        Player p = (Player) cs;
        
        if ((args == null || args.length == 0) && !PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.bypass")) {
            
            if (reviewBlacklist.contains(p.getName())) {
                p.sendMessage("§cYou cannot open a whitelist review request at this time.");
                return;
            }
            
            if (openReview(p.getName())) {
                p.sendMessage("§6You have opened a whitelist review request.");
                p.sendMessage("§6A helper or whitelisting figure will be with you shortly.");
            } else {
                p.sendMessage("§cYou cannot open multiple requests.");
            }
            
            return;
        }
        
        if (args.length > 0 && (args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("-f")) && (helpers.contains(p.getName()) || PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.whitelist"))) {
            if (args.length == 2) {
                String greylistee = args[1];
                reviewBlacklist.add(greylistee);
                
                if (openReviewTickets.containsKey(greylistee))
                    closeReviewNoUpdate(greylistee);
                
                p.sendMessage("§aFlagged §7greylistee \"§a" + greylistee + "§7\" for Gatekeeper blacklist");
                return;
            }
            
            showBlacklistedGreylistees(p);
            return;
        }
        
        if (args.length == 1 && (helpers.contains(p.getName()) || PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.whitelist"))) {
            List<Player> l = Bukkit.matchPlayer(args[0]);

            if (l.isEmpty()) {
                p.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                p.sendMessage("§cMultiple players found with that name.");
            } else {
                Player greylistee = l.get(0);
                
                if (!openReviewTickets.containsKey(greylistee.getName())) {
                    p.sendMessage("§cNo opened ticket found with that name.");
                    return;
                }
                
                closeReview(greylistee.getName());
                p.teleport(greylistee.getLocation());
                showHistory(greylistee.getName(), p);
            }
            
            return;
        }
        
        if (args.length == 2  && (args[0].equalsIgnoreCase("history") || args[0].equalsIgnoreCase("-h")) && (helpers.contains(p.getName()) || PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.whitelist"))) {
            String greylistee = args[1];
            
            showHistory(greylistee, p);
            return;
        }
        
        p.sendMessage("§cIncorrect format. See /whitelistreview help");
    }
    
    @Command(aliases = {"helper"},
            bounds = {2,2},
            help = "§c/helper [add, -a] <name> §fadds a helper with <name>.\n"
            + "<name> must be spelled correctly.\n"
            + "§c/helper [remove, -r] <name> §fremoves a helper with <name>.\n"
            + "<name> must be spelled correctly.")
    @CommandPermission("voxelguest.greylist.whitelistreview.helpermanagement")
    public void helperManagement(CommandSender cs, String[] args)
    {
        if (!gatekeeperIsEnabled) {
            cs.sendMessage("§cGatekeeper is not enabled.");
            return;
        }
        
        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("-a")) {
            if (!helpers.contains(args[1])) {
                helpers.add(args[1]);
                cs.sendMessage("§7Added new helper \"§a" + args[1] + "§7\"");
                
                return;
            }
        } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-r")) {
            if (helpers.contains(args[1])) {
                helpers.remove(args[1]);
                cs.sendMessage("§Removed helper \"§a" + args[1] + "§7\"");
                
                return;
            }
        }
        
        cs.sendMessage("§cIncorrect format. See /helper help");
    }
    
    @Command(aliases = {"listhelpers", "helpers"},
            bounds = {0,0},
            help = "Lists all helpers and indicates if online or offline")
    @CommandPermission("voxelguest.greylist.whitelistreview.listhelpers")
    public void listHelpers(CommandSender cs, String[] args)
    {
        showHelpers(cs);
    }

    @Command(aliases = {"unwhitelist", "unwl"},
        bounds = {1, 1},
        help = "Unwhitelist someone to your server\n"
        + "by typing §c/unwhitelist [player]")
    @CommandPermission("voxelguest.greylist.unwhitelist")
    public void unwhitelist(CommandSender cs, String[] args)
    {
        List<Player> l = Bukkit.matchPlayer(args[0]);

        if (l.isEmpty()) {
            cs.sendMessage("§cNo player found with that name.");
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple players found with that name.");
        } else {
            Player p = l.get(0);

            try {
                String whitelistGroup = VoxelGuest.getGroupManager().findGroup("whitelist", true);
                String greylistGroup = VoxelGuest.getGroupManager().findGroup("greylist", true);

                if (PermissionsManager.hasMultiGroupSupport()) {
                    PermissionsManager.getHandler().addGroup(p.getName(), greylistGroup);
                    PermissionsManager.getHandler().removeGroup(p.getName(), whitelistGroup);
                } else {
                    for (String nixGroup : PermissionsManager.getHandler().getGroups(p.getName())) {
                        PermissionsManager.getHandler().removeGroup(p.getName(), nixGroup);
                    }

                    PermissionsManager.getHandler().addGroup(p.getName(), greylistGroup);
                }

                if (PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.greylist.bypass")) {
                    PermissionsManager.getHandler().removeGroupPermission(greylistGroup, "voxelguest.greylist.bypass");
                }

            } catch (GroupNotFoundException ex) {
                PermissionsManager.getHandler().removePermission(p.getName(), "voxelguest.greylist.bypass");
            }
            
            Bukkit.broadcastMessage(getHeader());
            Bukkit.broadcastMessage("§4Unwhitelisted: §6" + p.getName());
        }
    }

    @Command(aliases = {"explorationmode"},
        bounds = {0, 0},
        help = "Toggle your server's floodgates on and off")
    @CommandPermission("voxelguest.greylist.admin.exploration")
    public void explorationMode(CommandSender cs, String[] args)
    {
        explorationMode = !explorationMode;
        getConfiguration().setBoolean("exploration-mode", explorationMode);
        cs.sendMessage(ChatColor.GREEN + "Exploration mode has been " + ((explorationMode) ? "enabled" : "disabled"));
    }

    @ModuleEvent(event = PlayerPreLoginEvent.class, priority = ModuleEventPriority.HIGHEST)
    public void onPlayerPreLogin(BukkitEventWrapper wrapper)
    {
        PlayerPreLoginEvent event = (PlayerPreLoginEvent) wrapper.getEvent();

        if (PermissionsManager.getHandler().hasPermission(event.getName(), "voxelguest.greylist.bypass")) {
            return;
        }

        if (!explorationMode) {
            if (!greylist.contains(event.getName().toLowerCase())) {
                event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, (getConfiguration().getString("greylist-not-greylisted-kick-message") != null) ? getConfiguration().getString("greylist-not-greylisted-kick-message") : "You are not greylisted on this server.");
            } else if (greylist.contains(event.getName().toLowerCase()) && !PermissionsManager.getHandler().hasPermission(event.getName(), "voxelguest.greylist.bypass")) {
                if (onlineGreylistLimit > -1 && onlineGreys.size() >= onlineGreylistLimit) {
                    String str = getConfiguration().getString("greylist-over-capacity-kick-message");
                    event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, (str != null) ? str : "The server is temporarily over guest capacity. Check back later.");
                }
            }
        }
    }

    @ModuleEvent(event = PlayerJoinEvent.class, priority = ModuleEventPriority.HIGHEST)
    public void onPlayerJoin(BukkitEventWrapper wrapper)
    {
        PlayerJoinEvent event = (PlayerJoinEvent) wrapper.getEvent();
        GuestPlayer gp = VoxelGuest.getGuestPlayer(event.getPlayer());

        if (PermissionsManager.getHandler().hasPermission(gp.getPlayer().getName(), "voxelguest.greylist.bypass")) {
            
            if (gatekeeperIsEnabled) {
                if (helpers.contains(event.getPlayer().getName()) || PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.greylist.whitelist")) {
                    NotificationCentre.sharedCentre().addObserver(event.getPlayer().getName(), NEW_REVIEW_UUID);
                }
            }
            
            return;
        }

        if (!explorationMode) {
            if (!greylist.contains(gp.getPlayer().getName().toLowerCase())) {
                gp.getPlayer().kickPlayer((getConfiguration().getString("greylist-not-greylisted-kick-message") != null) ? getConfiguration().getString("greylist-not-greylisted-kick-message") : "You are not greylisted on this server.");
                event.setJoinMessage("");
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
        } else {
            if (getConfiguration().getBoolean("announce-visitor-logins")
                    && !greylist.contains(event.getPlayer().getName())
                    && !PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.greylist.bypass")) {

                event.setJoinMessage("");
            }
        }
    }

    @ModuleEvent(event = PlayerQuitEvent.class)
    public void onPlayerQuit(BukkitEventWrapper wrapper)
    {
        PlayerQuitEvent event = (PlayerQuitEvent) wrapper.getEvent();

        if (explorationMode) {
            if (getConfiguration().getBoolean("announce-visitor-logins")
                    && !greylist.contains(event.getPlayer().getName())
                    && !PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.greylist.bypass")) {

                event.setQuitMessage("");
            }
        } else if (!explorationMode && onlineGreys.contains(event.getPlayer().getName())) {
            onlineGreys.remove(event.getPlayer().getName());
        }
        
        if (gatekeeperIsEnabled) {
            if (openReviewTickets.containsKey(event.getPlayer().getName()))
                closeReview(event.getPlayer().getName());
        }
    }

    @ModuleEvent(event = PlayerKickEvent.class)
    public void onPlayerKick(BukkitEventWrapper wrapper)
    {
        PlayerKickEvent event = (PlayerKickEvent) wrapper.getEvent();

        if (explorationMode) {
            if (getConfiguration().getBoolean("announce-visitor-logins")
                    && !greylist.contains(event.getPlayer().getName())
                    && !PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.greylist.bypass")) {

                event.setLeaveMessage("");
            }
        } else if (!explorationMode && onlineGreys.contains(event.getPlayer().getName())) {
            onlineGreys.remove(event.getPlayer().getName());
        }
        
        if (gatekeeperIsEnabled) {
            if (openReviewTickets.containsKey(event.getPlayer().getName()))
                closeReview(event.getPlayer().getName());
        }
    }

    private void saveGreylist()
    {
        if (!greylist.isEmpty()) {
            Iterator<String> it = greylist.listIterator();
            String[] toSave = new String[greylist.size()];
            int i = 0;

            while (it.hasNext()) {
                String entry = it.next();

                if (entry == null) {
                    continue;
                }

                toSave[i] = entry.toLowerCase();
                ++i;
            }

            FlatFileManager.save(toSave, "greylist");
        }
    }

    private void backupEntries()
    {
        if (!greylist.isEmpty()) {
            Iterator<String> it = greylist.listIterator();
            String[] toSave = new String[greylist.size()];
            int i = 0;

            while (it.hasNext()) {
                String entry = it.next();

                if (entry == null) {
                    continue;
                }

                toSave[i] = entry.toLowerCase();
                ++i;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            FlatFileManager.save(toSave, dateFormat.format(new Date()), "/greylist-backups");
        }
    }

    private void announceGreylist(String user)
    {
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + user + ChatColor.DARK_GRAY + " was added to the greylist.");
    }

    private void announceGreylist(List<String> users)
    {
        Iterator<String> it = users.listIterator();

        while (it.hasNext()) {
            String user = it.next();
            announceGreylist(user);
        }
    }

    private void injectGreylist(String str)
    {
        if (str == null) {
            VoxelGuest.log("DERP");
        }

        if (!greylist.contains(str.toLowerCase())) {
            greylist.add(str.toLowerCase());
        }
    }

    private void injectGreylist(String[] strs)
    {
        if (strs == null) {
            return;
        }

        for (String str : strs) {
            if (!greylist.contains(str.toLowerCase())) {
                greylist.add(str.toLowerCase());
            }
        }
    }

    private void injectGreylist(List<String> list)
    {
        if (list.isEmpty() || list == null) {
            return;
        }

        Iterator<String> it = list.listIterator();

        while (it.hasNext()) {
            String next = it.next();

            if (!greylist.contains(next.toLowerCase())) {
                greylist.add(next.toLowerCase());
            }
        }
    }

    public boolean hasGreylistee(String name)
    {
        return greylist.contains(name);
    }

    private String interpretStreamInput(String input)
    {
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

        public StreamThread(GreylistModule module)
        {
            try {
                this.serverSocket = new ServerSocket(streamPort);
            } catch (IOException ex) {
                this.serverSocket = null;
                VoxelGuest.log(name, "Could not bind to port " + streamPort + ". Perhaps it is already in use?", 2);
            }
        }

        public void killProcesses()
        {
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
        public void run()
        {
            if (serverSocket == null) {
                return;
            }

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

        public StreamReader(Socket s)
        {
            socket = s;
        }

        public int getStatus()
        {
            // -1 : Not yet called
            // 100: In process
            // 200: Exited with no error
            // 201: Exited for no greylist to add
            // 202: Exited with socket being null
            // 222: Exited with error

            return status;
        }

        @Override
        public void run()
        {
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
            }
        }

        private synchronized List<String> readSocket(Socket socket)
        {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                List<String> list = new ArrayList<String>();
                String line = null;

                while ((line = in.readLine()) != null) {
                    String toAdd = interpretStreamInput(line);

                    if (toAdd != null) {
                        if (!list.contains(toAdd)) {
                            list.add(toAdd);
                        }
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
    
    // -- Begin Gatekeeper portion --
    
    private HistoryEntry getHistoryEntry(String greylistee)
    {
        HistoryEntry entry = null;
        
        if (driver.checkTable(REVIEW_HISTORY_TABLE)) {
            try {

                Statement statement;
                ResultSet result;

                try {
                    driver.getConnection();
                    statement = driver.getStatement();
                    result = statement.executeQuery("SELECT * FROM " + REVIEW_HISTORY_TABLE + " WHERE Name='" + greylistee + "'");
                    
                    while (result.next()) {
                        String _grey = result.getString("Name");
                        int _count = result.getInt("Count");
                        String _lastReview = result.getString("LastReview");
                        
                        entry = new HistoryEntry(_grey, _lastReview, _count);
                    }
                } catch (SQLException ex) {
                    VoxelGuest.log("SQLException caught in GreylistModule.getHistoryEntry(): " + ex.getMessage());
                } finally {
                    driver.release();
                }
            } catch (SQLException ex) {
                VoxelGuest.log("SQLException caught in GreylistModule.getHistoryEntry(): " + ex.getMessage());
            }
        } else {
            driver.createTable("CREATE TABLE " + REVIEW_HISTORY_TABLE + "(Name varchar(255), Count int, LastReview varchar(255))");
        }
        
        return entry;
    }
    
    private void updateHistoryEntry(String greylistee)
    {
        HistoryEntry entry = getHistoryEntry(greylistee);
        
        if (entry == null) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
            String lastReviewRequest = format.format(now);
            
            entry = new HistoryEntry(greylistee, lastReviewRequest, 1);
            
            try {

                Statement statement;

                try {
                    driver.getConnection();
                    statement = driver.getStatement();
                    statement.execute("INSERT INTO " + REVIEW_HISTORY_TABLE + " VALUES('" + entry.getGreylistee() + "', " + entry.getReviewCount() + ", '" + entry.getLastReviewRequest() + "')");

                } catch (SQLException ex) {
                    VoxelGuest.log("SQLException caught in GreylistModule.getHistoryEntry(): " + ex.getMessage());
                } finally {
                    driver.release();
                }
            } catch (SQLException ex) {
                VoxelGuest.log("SQLException caught in GreylistModule.getHistoryEntry(): " + ex.getMessage());
            }
        } else {
            entry.update();
            
            try {

                Statement statement;

                try {
                    driver.getConnection();
                    statement = driver.getStatement();
                    statement.execute("UPDATE " + REVIEW_HISTORY_TABLE + " SET Count=" + entry.getReviewCount() + ", LastReview='" + entry.getLastReviewRequest() + "' WHERE Name='" + greylistee + "'");

                } catch (SQLException ex) {
                    VoxelGuest.log("SQLException caught in GreylistModule.getHistoryEntry(): " + ex.getMessage());
                } finally {
                    driver.release();
                }
            } catch (SQLException ex) {
                VoxelGuest.log("SQLException caught in GreylistModule.getHistoryEntry(): " + ex.getMessage());
            }
        }
    }
    
    private void showHistory(String greylistee, Player player)
    {
        if (getHistoryEntry(greylistee) == null) {
            player.sendMessage("§7\"§a" + greylistee + "§7\" has never been reviewed.");
            return;
        }

        int count = getHistoryEntry(greylistee).getReviewCount();
        String lastReview = getHistoryEntry(greylistee).getLastReviewRequest();

        player.sendMessage("§7\"§a" + greylistee + "§7\" has been reviewed §a" + count + " §7times.");
        player.sendMessage("§7\"§a" + greylistee + "§7\" was last reviewed at §a" + lastReview + " §.");
    }
    
    private boolean openReview(String greylistee)
    {
        if (openReviewTickets.containsKey(greylistee))
            return false;
        
        String message = "\"" + greylistee + "\" has submitted a new review request";
        
        Notification note = new Notification(NEW_REVIEW_UUID, VoxelGuest.getInstance(), message, null, NEW_REVIEW_PROPERTIES);
        NotificationCentre.sharedCentre().call(note);
        
        openReviewTickets.put(greylistee, note.getSessionId());
        
        return true;
    }
    
    private void closeReview(String greylistee)
    {
        if (!openReviewTickets.containsKey(greylistee))
            return;
        
        NotificationCentre.sharedCentre().cancelNotification(openReviewTickets.get(greylistee));
        openReviewTickets.remove(greylistee);
        
        updateHistoryEntry(greylistee);
    }
    
    private void closeReviewNoUpdate(String greylistee)
    {
        if (!openReviewTickets.containsKey(greylistee))
            return;
        
        NotificationCentre.sharedCentre().cancelNotification(openReviewTickets.get(greylistee));
        openReviewTickets.remove(greylistee);
    }
    
    private void showBlacklistedGreylistees(CommandSender cs)
    {
        if (reviewBlacklist.isEmpty()) {
            cs.sendMessage("§cNo one is blacklisted.");
            return;
        }
        
        cs.sendMessage("§8====================");
        cs.sendMessage("§6Blacklisted Greylistees");
        cs.sendMessage("§6");
        
        for (String str : reviewBlacklist) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(str);
            
            cs.sendMessage((op.isOnline() ? "§a" : "§7") + str);
        }
        
        cs.sendMessage("§8====================");
    }
    
    private void showHelpers(CommandSender cs)
    {
        if (helpers.isEmpty()) {
            cs.sendMessage("§cNo one is a helper.");
            return;
        }
        
        cs.sendMessage("§8====================");
        cs.sendMessage("§6Blacklisted Greylistees");
        cs.sendMessage("§6");
        
        for (String str : helpers) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(str);
            
            cs.sendMessage((op.isOnline() ? "§a" : "§7") + str);
        }
        
        cs.sendMessage("§8====================");
    }
    
    final class HistoryEntry {
        
        private final String greylistee;
        private String lastReviewRequest;
        private int reviewCount;

        public HistoryEntry(String greylistee, String lastReviewRequest, int reviewCount)
        {
            this.greylistee = greylistee;
            this.lastReviewRequest = lastReviewRequest;
            this.reviewCount = reviewCount;
        }
        
        public String getGreylistee()
        {
            return greylistee;
        }
        
        public String getLastReviewRequest()
        {
            return lastReviewRequest;
        }
        
        public int getReviewCount()
        {
            return reviewCount;
        }
        
        public void update()
        {
            ++reviewCount;
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
            lastReviewRequest = format.format(now);
        }
    }
    
    // -- End Gatekeeper portion --

    public void setPassword(String name, String input) throws CouldNotStoreEncryptedPasswordException
    {
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

    private String convertToHex(byte[] data)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private String getHeader()
    {
        HashMap<String, List<String>> storage = new HashMap<String, List<String>>();
        String defaultGroupId = VoxelGuest.getGroupManager().getDefaultConfiguration().getString("group-id");
        
        String header = "";
        
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
            
            groupId = "§8[" + groupId + "§8]";
            
            if (!storage.containsKey(groupId)) {
                List<String> l = new ArrayList<String>();
                l.add(p.getName());
                storage.put(groupId, l);
            } else {
                List<String> l = storage.get(groupId);
                l.add(p.getName());
                storage.put(groupId, l);
            }
        }
        
        header = writeHeader(storage, Bukkit.getOnlinePlayers().length - getFakequitSize());
        return header;
    }

    private String writeHeader(HashMap<String, List<String>> storage, int onlineNumber)
    {
        String header = "";
        String defaultGroupId = VoxelGuest.getGroupManager().getDefaultConfiguration().getString("group-id");

        for (String group : VoxelGuest.getGroupManager().getRegisteredGroups()) {
            String groupId = VoxelGuest.getGroupManager().getGroupConfiguration(group).getString("group-id");

            if (groupId == null) {
                groupId = defaultGroupId;
            }

            String groupTest = "§8[" + groupId + "§8]";

            if (storage.containsKey(groupTest)) {
                header = header + "§8[" + groupId + ":" + storage.get(groupTest).size() + "§8] ";
            } else {
                header = header + "§8[" + groupId + ":0§8] ";
            }
        }

        return (header.trim() + (" §8(§fO:" + onlineNumber + "§8)"));
    }

    private boolean isInFakeQuit(Player p)
    {
        try {
            VanishModule module = (VanishModule) ModuleManager.getManager().getModule(VanishModule.class);
            return module.isInFakequit(p);
        } catch (ModuleException ex) {
            return false;
        }
    }

    private int getFakequitSize()
    {
        try {
            VanishModule module = (VanishModule) ModuleManager.getManager().getModule(VanishModule.class);
            return module.getFakequitSize();
        } catch (ModuleException ex) {
            return 0;
        }
    }
}
