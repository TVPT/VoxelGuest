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
import com.patrickanker.lib.permissions.PermissionsHandler;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.cubicle.Cubicle;
import com.thevoxelbox.voxelguest.cubicle.CubicleGenerator;
import com.thevoxelbox.voxelguest.cubicle.CubicleManager;
import com.thevoxelbox.voxelguest.modules.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
@MetaData(name = "Cubicle Coordinator", description = "Handles the Cubicle world")
public class CubicleModule extends Module {

    public static int CUBICLE_SIZE;
    public static int CUBICLE_REGEN_TIME;
    private CubicleManager manager = new CubicleManager();
    private UUID worldUID;
    //
    public static final String PERM_CREATE = "voxelguest.cubicle.command.create";
    public static final String PERM_CREATE_SYSTEM = "voxelguest.cubicle.command.create.system";
    public static final String PERM_TELE = "voxelguest.cubicle.command.warp";
    public static final String PERM_SET_TELE = "voxelguest.cubicle.command.setwarp";
    public static final String PERM_SET_TELE_MSG = "voxelguest.cubicle.command.setwarpmsg";
    public static final String PERM_LOCK = "voxelguest.cubicle.command.lock";
    public static final String PERM_SET_NAME = "voxelguest.cubicle.command.setname";
    public static final String PERM_SET_OWNER = "voxelguest.cubicle.command.setowner";
    public static final String PERM_DELETE = "voxelguest.cubicle.command.delete";
    public static final String PERM_REGENERATE = "voxelguest.cubicle.command.regenerate";

    public CubicleModule()
    {
        super(CubicleModule.class.getAnnotation(MetaData.class));
    }

    class CubicleConfiguration extends ModuleConfiguration {

        @Setting("disable-module") public boolean moduleDissabled = true;
        @Setting("world-name") public String worldName = "cubicle";
        @Setting("chunk-size") public int chunkSize = 4;
        @Setting("terrain-top") public int terrainTopLayer = 30;
        @Setting("top-wall-level") public int wallTopLayer = 60;
        @Setting("terrain-filler-id") public byte fillerID = 3;
        @Setting("terrain-top-id") public byte topID = 2;
        @Setting("wall-id-1") public byte colour1 = 7;
        @Setting("wall-id-2") public byte colour2 = 49;
        @Setting("save-world-on-reload") public boolean saveWorld = false;
        @Setting("create-bedrock") public boolean bedrock = false;
        @Setting("cubicle-regeneration-speed") public int regenSpeed = 5;

        public CubicleConfiguration(CubicleModule parent)
        {
            super(parent);
        }
    }

    @Override
    public void enable() throws ModuleException
    {
        setConfiguration(new CubicleConfiguration(this));

        if (getConfiguration().getBoolean("disable-module")) {
            return;
        }

        ModuleConfiguration c = getConfiguration();

        CUBICLE_SIZE = c.getInt("chunk-size") * 16;
        CUBICLE_REGEN_TIME = c.getInt("cubicle-regeneration-speed");

        World cWorld = Bukkit.getServer().createWorld(
                new WorldCreator(c.getString("world-name")).type(WorldType.FLAT).generator(
                new CubicleGenerator(
                c.getInt("chunk-size"),
                c.getInt("terrain-top"),
                c.getInt("top-wall-level"),
                (byte) c.getInt("terrain-filler-id"),
                (byte) c.getInt("terrain-top-id"),
                (byte) c.getInt("wall-id-1"),
                (byte) c.getInt("wall-id-2"),
                c.getBoolean("create-bedrock"))));
        worldUID = cWorld.getUID();
    }

    @Override
    public String getLoadMessage()
    {
        return "Cubicle Module enabled -- cubicle world is "
                + (Bukkit.getServer().getWorld(worldUID) == null ? "not loaded" : "loaded");
    }

    @Override
    public void disable() throws ModuleException
    {
        if (getConfiguration().getBoolean("save-world-on-reload")) {
            if (Bukkit.getServer().getWorld(getConfiguration().getString("world-name")) != null) {
                Bukkit.getServer().getWorld(getConfiguration().getString("world-name")).save();
            }
        }
        manager.saveCubicles();
    }

    @ModuleEvent(event = PlayerInteractEvent.class, ignoreCancelledEvents = true)
    public void playerInteract(BukkitEventWrapper wrap)
    {
        PlayerInteractEvent e = (PlayerInteractEvent) wrap.getEvent();
        if (e.getPlayer().getWorld().getUID().equals(worldUID)) {
            if (e.getClickedBlock() == null) {
                return;
            }
            Cubicle cb = manager.getCubicle(e.getClickedBlock().getLocation());
            if (cb != null) {
                if (cb.isLocked()) {
                    if (!PermissionsManager.getHandler().hasPermission(e.getPlayer().getName(), cb.getPermissionString())) {
                        e.setCancelled(true);
                        e.setUseInteractedBlock(Event.Result.DENY);
                        e.setUseItemInHand(Event.Result.DENY);
                    }
                }
            }
        }
    }

    @ModuleEvent(event = PlayerMoveEvent.class, ignoreCancelledEvents = true)
    public void playerMove(BukkitEventWrapper wrap)
    {
        PlayerMoveEvent e = (PlayerMoveEvent) wrap.getEvent();
        if (e.getPlayer().getWorld().getUID().equals(worldUID)) {
            if (!manager.hasCubicle(e.getTo())) {
                if (!manager.hasCubicle(e.getFrom())) {
                    e.getPlayer().teleport(new Location(Bukkit.getWorld(worldUID), 0, 256, 0));
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @ModuleEvent(event = PlayerVelocityEvent.class, ignoreCancelledEvents = true)
    public void playerVelocity(BukkitEventWrapper wrap)
    {
        PlayerVelocityEvent e = (PlayerVelocityEvent) wrap.getEvent();
        if (e.getPlayer().getWorld().getUID().equals(worldUID)) {
            if (!manager.hasCubicle(e.getPlayer().getLocation().toVector().add(e.getVelocity()).toLocation(e.getPlayer().getWorld()))) {
                e.setCancelled(true);
            }
        }
    }

    @Command(aliases = {"cwarp"},
        bounds = {2, 2},
        playerOnly = true,
        help = "/cwarp allows you to warp to a players' cubicle.")
    @CommandPermission("voxelguest.cubicle.command.warp")
    public void cubicleWarp(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        String fullName = getFullName(p, args[1]);
    }

    @Command(aliases = {"cubicle", "cube"},
        bounds = {0, -1},
        playerOnly = true,
        help = "/cubicle allows you to manage the cubicles.")
    @CommandPermission("voxelguest.cubicle.command")
    public void cubicleCommand(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        if (args == null || args.length == 0) {
            p.sendMessage(ChatColor.AQUA + "The available subcommands are as follows:");
            p.sendMessage(new String[]{
                        ChatColor.GREEN + "/cubicle create (system) -- allows you to create your personal cubicle (or system)",
                        ChatColor.GOLD + "All the following will accept these parameters: (if ommited will use the cubicle you own)",
                        ChatColor.GOLD + "\"-p name\", \"-a alias\", \"-xz # #\", \"-n #\", \"-l\"",
                        ChatColor.GREEN + "/cubicle info [param] - print out information about the cubicle",
                        ChatColor.GRAY + "/cubicle warp [param] - warps you to specified cubicle",
                        ChatColor.GREEN + "/cubicle setwarp [param] - allows you to set the cubicles warp location",
                        ChatColor.GRAY + "/cubicle setwarpmsg [param] MESSAGE - allows you to set the cubicles warp message",
                        ChatColor.GREEN + "/cubicle lock [param] - locks the cubicle",
                        ChatColor.GRAY + "/cubicle unlock [param] - unlocks the cubicle",
                        ChatColor.GREEN + "/cubicle setname [param] NAME - sets the alias name of the cubicle",
                        ChatColor.GRAY + "/cubicle setowner [param] NAME - sets the new owner of the cubicle",
                        ChatColor.GREEN + "/cubicle addowner [param] NAME - allows you to add a co-owner of the cubicle",
                        ChatColor.GRAY + "/cubicle removeowner [param] NAME - removes the co-owner of the cubicle",
                        ChatColor.GREEN + "/cubicle regenerate [param] - regenerates the provided cubicle",
                        ChatColor.GRAY + "/cubicle delete [param] - deleted the cubicle"});
        } else {
            PermissionsHandler ph = PermissionsManager.getHandler();
            if (args[0].equalsIgnoreCase("create")) {// /cube warp -p(layer) przerwap  | -xz 10 -3 | -a(lias) organix | -l(ocation) | -n(umber) 7
                if (ph.hasPermission(p.getName(), PERM_CREATE)) {
                    if (args.length >= 2 && args[1].equalsIgnoreCase("system")) {
                        if (ph.hasPermission(p.getName(), PERM_CREATE_SYSTEM)) {
                            manager.createCubicle(p, Bukkit.getWorld(worldUID), true);
                        } else {
                            noPerm(p);
                        }
                    } else {
                        if (manager.hasCubicle(p)) {
                            p.sendMessage(ChatColor.RED + "You already own a cubicle!");
                        } else {
                            manager.createCubicle(p, Bukkit.getWorld(worldUID), false);
                        }
                    }
                } else {
                    noPerm(p);
                }
            } else {
                Cubicle cb = getFromPlayerInput(p, args);
                args = removeParameters(args);
                if (cb != null) {
                    if (args[0].equalsIgnoreCase("warp")) {
                        if (ph.hasPermission(p.getName(), PERM_TELE)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            cb.teleport(p, Bukkit.getWorld(worldUID));
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("setwarp")) {
                        if (ph.hasPermission(p.getName(), PERM_SET_TELE)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            cb.setTpLoc(p.getLocation());
                            p.sendMessage(ChatColor.GOLD + "Warp location set to where you stand!");
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("setwarpmsg")) {
                        if (ph.hasPermission(p.getName(), PERM_SET_TELE_MSG)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            if (args.length > 1) {
                                String msg = "";
                                for (int i = 1; i < args.length; i++) {
                                    msg += args[i] + " ";
                                }
                                cb.setTpMessage(msg);
                                p.sendMessage(ChatColor.GOLD + "Warp message has been set to:");
                                p.sendMessage(ChatColor.BLUE + msg);
                            } else {
                                p.sendMessage(ChatColor.RED + "Please provide a message!");
                            }
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("lock")) {
                        if (ph.hasPermission(p.getName(), PERM_LOCK)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            cb.lock();
                            p.sendMessage(ChatColor.GOLD + "The Cubicle " + cb + " is now locked!");
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("unlock")) {
                        if (ph.hasPermission(p.getName(), PERM_LOCK)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            cb.unLock();
                            p.sendMessage(ChatColor.GOLD + "The Cubicle " + cb + " is now unlocked!");
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("setname")) {
                        if (ph.hasPermission(p.getName(), PERM_SET_NAME)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            if (args.length < 2) {
                                p.sendMessage(ChatColor.RED + "Please provide the new name");
                            } else {
                                manager.addAlias(cb, args[1], p);
                            }
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("setowner")) {
                        if (ph.hasPermission(p.getName(), PERM_SET_OWNER)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            if (args.length < 2) {
                                p.sendMessage(ChatColor.RED + "Please provide the name of the new owner.");
                            } else {
                                String fullName = getFullName(p, args[1]);
                                if (fullName != null) {
                                    manager.setOwner(cb, p, fullName);
                                }
                            }
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("addowner")) {
                        if (ph.hasPermission(p.getName(), PERM_SET_OWNER)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            if (args.length < 2) {
                                p.sendMessage(ChatColor.RED + "Please provide the name of the new owner.");
                            } else {
                                String fullName = getFullName(p, args[1]);
                                if (fullName != null) {
                                    if (cb.hasOwner(fullName)) {
                                        p.sendMessage(ChatColor.RED + "This user is already an owner of this Cubicle " + cb);
                                    } else {
                                        ph.givePermission(fullName, cb.getPermissionString());
                                        cb.addOwner(fullName);
                                        p.sendMessage(ChatColor.GOLD + "The owners of the Cubicle " + cb + ChatColor.GOLD + " have been updated.");
                                    }
                                }
                            }
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("removeowner")) {
                        if (ph.hasPermission(p.getName(), PERM_SET_OWNER)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            if (args.length < 2) {
                                p.sendMessage(ChatColor.RED + "Please provide the name of the owner.");
                            } else {
                                String fullName = getFullName(p, args[1]);
                                if (fullName != null) {
                                    if (!cb.hasOwner(fullName)) {
                                        p.sendMessage(ChatColor.RED + "This user is not an owner of this Cubicle " + cb);
                                    } else {
                                        ph.removePermission(fullName, cb.getPermissionString());
                                        cb.removeOwner(fullName);
                                        p.sendMessage(ChatColor.GOLD + "The owners of the Cubicle " + cb + ChatColor.GOLD + " have been updated.");
                                    }
                                }
                            }
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("delete")) {
                        if (ph.hasPermission(p.getName(), PERM_DELETE)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            manager.removeCubicle(p, cb);
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("regenerate")) {
                        if (ph.hasPermission(p.getName(), PERM_REGENERATE)
                                || ph.hasPermission(p.getName(), cb.getPermissionString())) {
                            manager.regenerateCubicle(cb, Bukkit.getWorld(worldUID), p);
                        } else {
                            noPerm(p);
                        }
                    } else if (args[0].equalsIgnoreCase("info")) {
                        cb.info(p);
                    }
                }
            }
        }
    }

    public void noPerm(Player user)
    {
        user.sendMessage(ChatColor.RED + "You do not have the permission to use this command.");
    }

    private String[] removeParameters(String[] args)
    {
        int i = args.length;
        for (String st : args) {
            if (st.equals("--")) {
                i--;
            }
        }
        String[] temp = new String[i];
        int j = 0;
        for (String st : args) {
            if (!st.equals("--")) {
                temp[j++] = st;
            }
        }
        return temp;
    }

    private Cubicle getFromPlayerInput(Player user, String[] args)
    {
        if (args.length == 1) {
            if (manager.hasCubicle(user)) {
                return manager.getCubicle(user);
            } else {
                user.sendMessage(ChatColor.GOLD + "You do not own a cubicle.");
                return null;
            }
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("-l")) {
                if (user.getWorld().getUID().equals(worldUID)) {
                    if (manager.hasCubicle(user.getLocation())) {
                        args[1] = "--";
                        return manager.getCubicle(user.getLocation());
                    } else {
                        user.sendMessage(ChatColor.GOLD + "There appears to be no cubicle where you stand.");
                        return null;
                    }
                } else {
                    user.sendMessage(ChatColor.GOLD + "You are not located on the cubicle world.");
                    return null;
                }
            } else if (manager.hasCubicle(user)) {
                return manager.getCubicle(user);
            } else {
                user.sendMessage(ChatColor.GOLD + "Please provide more parameters.");
                return null;
            }
        } else if (args.length >= 3) {
            try {
                if (args[1].equalsIgnoreCase("-l")) {
                    if (user.getWorld().getUID().equals(worldUID)) {
                        if (manager.hasCubicle(user.getLocation())) {
                            args[1] = "--";
                            return manager.getCubicle(user.getLocation());
                        } else {
                            user.sendMessage(ChatColor.GOLD + "There appears to be no cubicle where you stand.");
                            return null;
                        }
                    } else {
                        user.sendMessage(ChatColor.GOLD + "You are not located on the cubicle world.");
                        return null;
                    }
                } else if (args[1].equalsIgnoreCase("-p")) {
                    String fullName = getFullName(user, args[2]);
                    if (fullName != null) {
                        if (manager.hasCubicle(fullName)) {
                            Cubicle cb = manager.getCubicle(fullName);
                            args[1] = "--";
                            args[2] = "--";
                            return cb;
                        } else {
                            user.sendMessage(ChatColor.RED + "This player does not own a cubicle!");
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else if (args[1].equalsIgnoreCase("-n")) {
                    try {
                        Cubicle cb = manager.getSpiralNumber(Integer.parseInt(args[2]));
                        if (cb != null) {
                            args[1] = "--";
                            args[2] = "--";
                            return cb;
                        } else {
                            user.sendMessage(ChatColor.GOLD + "There is no cubicle with that number.");
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        user.sendMessage(ChatColor.RED + "Please use valid numbers!");
                        return null;
                    }
                } else if (args[1].equalsIgnoreCase("-a")) {
                    if (manager.hasAlias(args[2])) {
                        String temp = args[2];
                        args[1] = "--";
                        args[2] = "--";
                        return manager.getAlias(temp);
                    } else {
                        user.sendMessage(ChatColor.GOLD + "There is no cubicle with that alias.");
                        return null;
                    }
                } else if (args[1].equalsIgnoreCase("-xz")) {
                    try {
                        if (manager.hasCubicle(Integer.parseInt(args[2]), Integer.parseInt(args[3]))) {
                            Cubicle cb = manager.getCubicle(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                            args[1] = "--";
                            args[2] = "--";
                            args[3] = "--";
                            return cb;
                        } else {
                            user.sendMessage(ChatColor.GOLD + "There is no cubicle at those coordinates!");
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        user.sendMessage(ChatColor.RED + "Please use valid numbers!");
                        return null;
                    }
                } else if (manager.hasCubicle(user)) {
                    return manager.getCubicle(user);
                } else {
                    user.sendMessage(ChatColor.GOLD + "Please provide more parameters.");
                    return null;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                user.sendMessage(ChatColor.RED + "An error has occured please revise your input.");
                return null;
            }
        }
        return null;
    }

    private String getFullName(Player user, String partial)
    {
        List<Player> onlineMatch = Bukkit.matchPlayer(partial);
        if (onlineMatch.isEmpty()) {
            List<OfflinePlayer> offlineMatch = matchOfflinePlayer(partial);
            if (offlineMatch.isEmpty()) {
                user.sendMessage(ChatColor.GOLD + "No player matched your input.");
                return null;
            } else if (offlineMatch.size() > 1) {
                user.sendMessage(ChatColor.GOLD + "More than one name matched your input.");
                return null;
            } else {
                return offlineMatch.get(0).getName();
            }
        } else if (onlineMatch.size() > 1) {
            user.sendMessage(ChatColor.GOLD + "More than one name matched your input.");
            return null;
        } else {
            return onlineMatch.get(0).getName();
        }
    }

    public List<OfflinePlayer> matchOfflinePlayer(String partialName)
    {
        List matchedPlayers = new ArrayList();

        for (OfflinePlayer iterPlayer : Bukkit.getOfflinePlayers()) {
            String iterPlayerName = iterPlayer.getName();

            if (partialName.equalsIgnoreCase(iterPlayerName)) {
                matchedPlayers.clear();
                matchedPlayers.add(iterPlayer);
                break;
            }
            if (iterPlayerName.toLowerCase().indexOf(partialName.toLowerCase()) == -1) {
                continue;
            }
            matchedPlayers.add(iterPlayer);
        }

        return matchedPlayers;
    }
}
