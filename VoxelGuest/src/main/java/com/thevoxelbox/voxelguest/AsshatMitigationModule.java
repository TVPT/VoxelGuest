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
import com.patrickanker.lib.config.PropertyConfiguration;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.Formatter;
import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.Setting;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Razorcane
 */
@MetaData(name = "Asshat Mitigator", description = "Major asshat handling.")
public class AsshatMitigationModule extends Module {

    protected PropertyConfiguration bannedList = new PropertyConfiguration("banned", "/VoxelGuest/asshatmitigation");
    public List<String> gagged = new ArrayList<String>();
    
    private final List<String> frozen = new ArrayList<String>();
    private boolean allFreeze = false;
    
    private boolean silenceMode = false;

    public AsshatMitigationModule()
    {
        super(AsshatMitigationModule.class.getAnnotation(MetaData.class));
    }

    class AsshatMitigationConfiguration extends ModuleConfiguration {

        @Setting("default-asshat-reason") public String defaultAsshatReason = "&cAsshat";
        @Setting("save-banlist-on-ban") public boolean saveBanlistOnBan = false;
        @Setting("unrestrict-chat-message") public String unrestrictChatMessage = "I agree. Allow me to chat.";
        @Setting("gag-message-format") public String gagMessageFormat = "&cYou have been gagged. You cannot chat until you say\n" + "&6the ungag key phrase.";
        @Setting("ungag-message-format") public String ungagMessageFormat = "&aYou have been ungagged.";

        public AsshatMitigationConfiguration(AsshatMitigationModule parent)
        {
            super(parent);
        }
    }

    @Override
    public void enable()
    {
        setConfiguration(new AsshatMitigationConfiguration(this));
        bannedList.load();
        gagged.clear();
    }

    @Override
    public void disable()
    {
        bannedList.save();
    }

    @Override
    public String getLoadMessage()
    {
        return "Asshat Mitigator has been loaded.";
    }

    /*
     * Asshat Mitigation - Ban Written by: Razorcane
     *
     * Handles the banning of both online and offline players. However, exact
     * player names must be given when banning offline players.
     */
    @Command(aliases = {"ban", "vban", "vbano", "bano"}, bounds = {1, -1}, help = "To ban someone, simply type\n" + "§c/ban [player] (reason)")
    @CommandPermission("voxelguest.asshat.ban")
    public void ban(CommandSender cs, String[] args)
    {
        List<Player> l = Bukkit.matchPlayer(args[0]);
        String reason = "";
        boolean silent = false;

        if (args.length > 1) {
            for (short i = 1; i < args.length; ++i) {
                String arg = args[i];

                if (arg.equals("-silent") || arg.equals("-si")) {
                    silent = true;
                } else {
                    reason += args[i] + " ";
                }
            }
        }

        if (l.size() > 1) {
            cs.sendMessage(ChatColor.RED + "Partial match.");
        } else if (l.isEmpty()) {
            String player = args[0];

            if (args.length > 1) {
                bannedList.setString(player, reason);

                if (silent) {
                    Bukkit.getLogger().info("Player " + player + " has been banned by " + cs.getName() + " for:");
                    Bukkit.getLogger().info(reason);
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + player + ChatColor.DARK_GRAY + " has been banned by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                    Bukkit.broadcastMessage(ChatColor.BLUE + reason);
                }
            } else {
                bannedList.setString(player, getConfiguration().getString("default-asshat-reason"));
                if (silent) {
                    Bukkit.getLogger().info("Player " + player + " has been banned by " + cs.getName() + " for:");
                    Bukkit.getLogger().info(getConfiguration().getString("default-asshat-reason"));
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + player + ChatColor.DARK_GRAY + " has been banned by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                    Bukkit.broadcastMessage(getConfiguration().getString("default-asshat-reason"));
                }
            }
        } else {
            Player toBan = l.get(0);

            if (args.length > 1) {
                toBan.kickPlayer("You have been banned for: " + reason);
                bannedList.setString(toBan.getName(), reason);

                if (silent) {
                    Bukkit.getLogger().info("Player " + toBan.getName() + " has been banned by " + cs.getName() + " for:");
                    Bukkit.getLogger().info(reason);
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + toBan.getName() + ChatColor.DARK_GRAY + " has been banned by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                    Bukkit.broadcastMessage(ChatColor.BLUE + reason);
                }
            } else {
                toBan.kickPlayer("You have been banned for: " + getConfiguration().getString("default-asshat-reason"));
                bannedList.setString(toBan.getName(), getConfiguration().getString("default-asshat-reason"));

                if (silent) {
                    Bukkit.getLogger().info("Player " + toBan.getName() + " has been banned by " + cs.getName() + " for:");
                    Bukkit.getLogger().info(getConfiguration().getString("default-asshat-reason"));
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + toBan.getName() + ChatColor.DARK_GRAY + " has been banned by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                    Bukkit.broadcastMessage(getConfiguration().getString("default-asshat-reason"));
                }
            }
        }

        if (getConfiguration().getBoolean("save-banlist-on ban")) {
            bannedList.save();
        }
    }

    /*
     * Asshat Mitigation - Unban Written by: Razorcane
     *
     * Controls the unbanning of banned players. Name must be exact, and player
     * must be banned, in order to be unbanned.
     */
    @Command(aliases = {"unban", "vunban"}, bounds = {1, -1}, help = "To unban someone, simply type\n" + "§c/unban [player]")
    @CommandPermission("voxelguest.asshat.unban")
    public void unban(CommandSender cs, String[] args)
    {
        boolean silent = false;

        if (args.length > 1) {
            for (short i = 1; i < args.length; ++i) {
                String arg = args[i];

                if (arg.equals("-silent") || arg.equals("-si")) {
                    silent = true;
                }
            }
        }

        if (args.length < 1) {
            cs.sendMessage(ChatColor.RED + "Invalid arguments.");
        } else {
            String player = args[0];
            if (bannedList.hasEntry(player)) {
                bannedList.removeEntry(player);

                if (silent) {
                    Bukkit.getLogger().info("Player " + player + " has been unbanned by " + cs.getName());
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + player + ChatColor.DARK_GRAY + " has been unbanned by " + ChatColor.RED + cs.getName());
                }

                bannedList.save();
                bannedList.load();
            } else {
                cs.sendMessage(ChatColor.RED + "Player isn't banned.");
            }
        }
    }

    /*
     * Asshat Mitigation - Gag Written by: Razorcane
     *
     * Gags a player, or prevents them from talking until they are ungagged,
     * there is a server restart, or they type the designated phrase.
     */
    @Command(aliases = {"gag", "vgag"}, bounds = {1, -1}, help = "To gag someone, simply type\n" + "§c/gag [player] (reason)", playerOnly = false)
    @CommandPermission("voxelguest.asshat.gag")
    public void gag(CommandSender cs, String[] args)
    {
        List<Player> l = Bukkit.matchPlayer(args[0]);
        String reason = "";
        boolean silent = false;

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                String arg = args[i];

                if (arg.equals("-silent") || arg.equals("-si")) {
                    silent = true;
                } else {
                    reason += args[i] + " ";
                }
            }
        }

        if (l.size() > 1) {
            cs.sendMessage(ChatColor.RED + "Partial match.");
        } else if (l.isEmpty()) {
            cs.sendMessage(ChatColor.RED + "No player to match.");
        } else {
            Player p = l.get(0);

            if (gagged.contains(p.getName())) {
                gagged.remove(p.getName());
                cs.sendMessage(ChatColor.RED + p.getName() + ChatColor.WHITE + " has been ungagged.");
            } else {
                gagged.add(p.getName());
                if (args.length > 1) {
                    if (silent) {
                        Bukkit.getLogger().info("Player " + p.getName() + " has been gagged by " + cs.getName() + " for:");
                        Bukkit.getLogger().info(reason);
                    } else {
                        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + p.getName() + ChatColor.DARK_GRAY + " has been gagged by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                        Bukkit.broadcastMessage(ChatColor.BLUE + reason);
                    }
                } else {
                    if (silent) {
                        Bukkit.getLogger().info("Player " + p.getName() + " has been gagged by " + cs.getName() + " for:");
                        Bukkit.getLogger().info(getConfiguration().getString("default-asshat-reason"));
                    } else {
                        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + p.getName() + ChatColor.DARK_GRAY + " has been gagged by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                        Bukkit.broadcastMessage(getConfiguration().getString("default-asshat-reason"));
                    }
                }
            }
        }
    }

    /*
     * Asshat Mitigation - Kick Written by: Razorcane
     *
     * Kicks a player from the server. Entering no reason defaults to the
     * default asshat reason, which is "Asshat".
     */
    @Command(aliases = {"kick", "vkick"}, bounds = {1, -1}, help = "To kick someone, simply type\n" + "§c/kick [player] (reason)", playerOnly = false)
    @CommandPermission("voxelguest.asshat.kick")
    public void kick(CommandSender cs, String[] args)
    {
        List<Player> l = Bukkit.matchPlayer(args[0]);
        String reason = "";
        boolean silent = false;

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                String arg = args[i];

                if (arg.equals("-silent") || arg.equals("-si")) {
                    silent = true;
                } else {
                    reason += args[i] + " ";
                }
            }
        }

        if (l.size() > 1) {
            cs.sendMessage(ChatColor.RED + "Partial match.");
        } else if (l.isEmpty()) {
            cs.sendMessage(ChatColor.RED + "No player to match.");
        } else {
            l.get(0).kickPlayer(reason);
            if (args.length > 1) {
                if (silent) {
                    Bukkit.getLogger().info("Player " + l.get(0).getName() + " has been kicked by " + cs.getName() + " for:");
                    Bukkit.getLogger().info(reason);
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + l.get(0).getName() + ChatColor.DARK_GRAY + " has been kicked by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                    Bukkit.broadcastMessage(ChatColor.BLUE + reason);
                }
            } else {
                if (silent) {
                    Bukkit.getLogger().info("Player " + l.get(0).getName() + " has been kicked by " + cs.getName() + " for:");
                    Bukkit.getLogger().info(getConfiguration().getString("default-asshat-reason"));
                } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + l.get(0).getName() + ChatColor.DARK_GRAY + " has been kicked by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
                    Bukkit.broadcastMessage(getConfiguration().getString("default-asshat-reason"));
                }
            }
        }
    }
    
    @Command(aliases={"freeze", "fr"}, bounds={1,1}, help="Freezes the defined player in\n" + "§c/freeze [player]§f or freeze all players (except those with \"voxelguest.asshat.freeze.bypass\") with\n" + "§c/freeze --all§f or §c/freeze -a")
    @CommandPermission("voxelguest.asshat.freeze.freeze")
    public void freeze(CommandSender cs, String[] args)
    {
        if (args[0].equalsIgnoreCase("--all") || args[0].equalsIgnoreCase("-a")) {
            allFreeze = !allFreeze;
            
            if (allFreeze) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.asshat.freeze.bypass"))
                        frozen.add(p.getName());
                }
                
                cs.sendMessage("§aEveryone §7has been §bfrozen.");
            } else {
                frozen.clear();
                cs.sendMessage("§aEveryone §7has been §cthawed.");
            }
        }
        
        List<Player> l = Bukkit.matchPlayer(args[0]);
        
        if (l.isEmpty()) {
            cs.sendMessage("§cNo player found with that name.");
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple players found with that name.");
        } else {
            String nameOfPlayer = l.get(0).getName();
            
            if (!frozen.contains(nameOfPlayer)) {
                frozen.add(nameOfPlayer);
                cs.sendMessage("§a" + nameOfPlayer + " §7has been §bfrozen.");
            } else {
                frozen.remove(nameOfPlayer);
                cs.sendMessage("§a" + nameOfPlayer + " §7has been §cthawed.");
            }
        }
    }

    @ModuleEvent(event = PlayerPreLoginEvent.class, ignoreCancelledEvents = false)
    public void onPlayerPreLogin(BukkitEventWrapper wrapper)
    {
        PlayerPreLoginEvent event = (PlayerPreLoginEvent) wrapper.getEvent();
        String player = event.getName();

        if (bannedList.hasEntry(player)) {
            event.setResult(PlayerPreLoginEvent.Result.KICK_FULL);
            event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "You are banned for: " + bannedList.getString(player));
        }
    }

    @ModuleEvent(event = PlayerChatEvent.class, ignoreCancelledEvents = false)
    public void onPlayerChat(BukkitEventWrapper wrapper)
    {
        PlayerChatEvent event = (PlayerChatEvent) wrapper.getEvent();
        Player p = event.getPlayer();
        
        if (silenceMode) {
            if (!PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.bypass.silence")) {
                event.setCancelled(true);
            }
        }

        if (gagged.contains(p.getName())) {
            if (event.getMessage().equals(getConfiguration().getString("unrestrict-chat-message"))) {
                gagged.remove(p.getName());

                for (String str : Formatter.selectFormatter(SimpleFormatter.class).formatMessages(getConfiguration().getString("ungag-message-format"), VoxelGuest.getGuestPlayer(p))) {
                    p.sendMessage(str);
                }

                event.setCancelled(true);
            } else {
                for (String str : Formatter.selectFormatter(SimpleFormatter.class).formatMessages(getConfiguration().getString("gag-message-format"), VoxelGuest.getGuestPlayer(p))) {
                    p.sendMessage(str);
                }

                event.setCancelled(true);
            }
        }
    }
    
    @ModuleEvent(event=PlayerMoveEvent.class)
    public void onPlayerMove(BukkitEventWrapper wrapper)
    {
        PlayerMoveEvent event = (PlayerMoveEvent) wrapper.getEvent();
        
        if (frozen.contains(event.getPlayer().getName())) {
            event.setTo(event.getFrom());
            event.setCancelled(true);
        }
    }
    
    @ModuleEvent(event=PlayerTeleportEvent.class)
    public void onPlayerTeleport(BukkitEventWrapper wrapper)
    {
        PlayerTeleportEvent event = (PlayerTeleportEvent) wrapper.getEvent();
        
        if (frozen.contains(event.getPlayer().getName())) {
            event.setTo(event.getFrom());
            event.setCancelled(true);
        }
    }
    
    @Command(aliases = {"soapbox", "silence"},
        bounds = {0, 0},
        help = "Toggle the silence")
    @CommandPermission("voxelguest.admin.silence")
    public void silence(CommandSender cs, String[] args)
    {
        silenceMode = !silenceMode;
        Player p = (Player) cs;
        getConfiguration().setBoolean("silence-mode", silenceMode);
        cs.sendMessage(ChatColor.GOLD + "Silent mode has been " + ((silenceMode) ? "enabled" : "disabled"));
    }
}
