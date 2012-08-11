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
import com.thevoxelbox.voxelguest.modules.ModuleException;
import com.thevoxelbox.voxelguest.modules.Setting;
import com.thevoxelbox.voxelguest.regions.Region;
import com.thevoxelbox.voxelguest.regions.Vector3D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@MetaData(name = "Regions", description = "Manage region-based build protections on your server!")
public class RegionModule extends Module {

    public List<Region> loadedRegions = new ArrayList<Region>();

    public RegionModule()
    {
        super(RegionModule.class.getAnnotation(MetaData.class));
    }

    class RegionConfiguration extends ModuleConfiguration {

        @Setting("enable-general-build-outside-defined-regions")
        public boolean enableGeneralBuildOutsideDefinedRegions = true;

        public RegionConfiguration(RegionModule parent)
        {
            super(parent);
        }
    }

    @Override
    public void enable() throws ModuleException
    {
        setConfiguration(new RegionConfiguration(this));
        File dir = new File("plugins/VoxelGuest/regions/");

        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            if (dir.list().length != 0) {
                for (File f : dir.listFiles()) {
                    if (!f.getName().endsWith(".properties")) {
                        continue;
                    }

                    Region region = new Region(f.getName().replace(".properties", ""));

                    if (!loadedRegions.contains(region)) {
                        loadedRegions.add(region);
                    }
                }
            }
        }
    }

    @Override
    public void disable()
    {
        if (loadedRegions != null || !loadedRegions.isEmpty()) {
            Iterator<Region> it = loadedRegions.listIterator();

            while (it.hasNext()) {
                Region region = it.next();
                region.save();
            }

            loadedRegions.clear();
        }
    }

    @Override
    public String getLoadMessage()
    {
        return "Region module loaded - " + loadedRegions.size() + " regions registered";
    }

    @Command(aliases = {"regions", "rgs"},
    bounds = {0, -1},
    help = "/regions prints out the number of regions\n"
    + "The regions subcommands are as follows:\n"
    + "§b/regions [create, -c] [name] [x1] [y1] [z1] [x2] [y2] [z2]§f: Creates a\n"
    + "new region \"[name]\" from (x1, y1, z1) to (x2, y2, z2)\n"
    + "§b/regions [allow, -a] [(+/-)e(+/-)m(+/-)a]\n"
    + "[-p, -g] [name] [region]§f:\n"
    + "Allows player or group (-p or -g) \"[name]\" to\n"
    + "modify (+m), enter (+e), or administer (+a) region \"[region]\"\n"
    + "§b/regions [delete, -d] [region]§f: Deletes a region\n"
    + "§b/regions -g [region] [true/false]§f: Enables/disables the general build override.")
    @CommandPermission(permission = "voxelguest.regions.admin")
    public void regions(CommandSender cs, String[] args)
    {
        if (args.length == 0) {
            cs.sendMessage("§aThere are " + loadedRegions.size() + " registered regions.");
            return;
        } else {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("-c")) {
                if (!(cs instanceof Player)) {
                    cs.sendMessage("§cPlayer-only command");
                    return;
                }

                Player p = (Player) cs;

                if (args.length != 8) {
                    p.sendMessage("§cIncorrect formatting of command: /regions [create, -c] [name] [x1] [y1] [z1] [x2] [y2] [z2]");
                    return;
                }

                String regName = args[1];
                World world = p.getWorld();

                try {
                    int x1 = Integer.parseInt(args[2]);
                    int y1 = Integer.parseInt(args[3]);
                    int z1 = Integer.parseInt(args[4]);
                    int x2 = Integer.parseInt(args[5]);
                    int y2 = Integer.parseInt(args[6]);
                    int z2 = Integer.parseInt(args[7]);

                    Vector3D vec1 = new Vector3D(x1, y1, z1);
                    Vector3D vec2 = new Vector3D(x2, y2, z2);
                    Region region = new Region(regName, world, vec1, vec2);

                    if (!loadedRegions.contains(region)) {
                        loadedRegions.add(region);
                    }

                    PermissionsManager.getHandler().givePermission(p.getName(), "system.region." + regName.toLowerCase() + ".admin");
                    p.sendMessage("§aRegion \"" + regName + "\" registered.");
                    return;
                } catch (NumberFormatException ex) {
                    p.sendMessage("§cNon-number found: " + ex.getMessage());
                    p.sendMessage("§cIncorrect formatting of command: /regions [create, -c] [name] [x1] [y1] [z1] [x2] [y2] [z2]");
                    return;
                }
            } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("-d")) {
                if (args.length != 2) {
                    cs.sendMessage("§cIncorrect formatting of command: /regions [delete, -d] [name]");
                    return;
                }

                List<Region> l = matchRegion(args[1]);

                if (l.isEmpty()) {
                    cs.sendMessage("§cNo region found by that name.");
                    return;
                } else if (l.size() > 1) {
                    cs.sendMessage("§cMultiple regions found by that name.");
                    return;
                } else {
                    if ((cs instanceof Player) && !PermissionsManager.getHandler().hasPermission(cs.getName(), "system.regions." + l.get(0).getName().toLowerCase() + ".admin")) {
                        cs.sendMessage("§cYou are not authorized to administer region \"" + l.get(0).getName() + "\"");
                        return;
                    }

                    Region toDelete = l.get(0);
                    loadedRegions.remove(toDelete);
                    toDelete.delete();
                    cs.sendMessage("§aRegion \"" + toDelete.getName() + "\" deleted.");
                    return;
                }
            } else if (args[0].equalsIgnoreCase("-g")) {
                if (args.length != 3) {
                    cs.sendMessage("§cIncorrect format: /regions -g [region] [true/false]");
                    return;
                }

                if (!(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
                    cs.sendMessage("§cIncorrect format: /regions -g [region] [true/false]");
                    return;
                }

                List<Region> l = matchRegion(args[1]);

                if (l.isEmpty()) {
                    cs.sendMessage("§cNo region found by that name.");
                    return;
                } else if (l.size() > 1) {
                    cs.sendMessage("§cMultiple regions found by that name.");
                    return;
                } else {
                    if ((cs instanceof Player) && !PermissionsManager.getHandler().hasPermission(cs.getName(), "system.regions." + l.get(0).getName().toLowerCase() + ".admin")) {
                        cs.sendMessage("§cYou are not authorized to administer region \"" + l.get(0).getName() + "\"");
                        return;
                    }

                    l.get(0).setGeneralBuildOverrideDisable(!Boolean.parseBoolean(args[2]));
                    cs.sendMessage("§aGeneral build override " + ((!l.get(0).isGeneralBuildOverrideDisabled()) ? "enabled" : "diabled") + " for region \"" + l.get(0).getName() + "\"");
                    return;
                }
            } else if (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("-a")) {
                if (args.length != 5) {
                    cs.sendMessage("§cIncorrect format: /regions [allow, -a] [(+/-)e(+/-)m(+/-)a] [-p, -g] [name] [region]");
                    return;
                }

                List<Region> l = matchRegion(args[4]);

                if (l.isEmpty()) {
                    cs.sendMessage("§cNo region found by that name.");
                    return;
                } else if (l.size() > 1) {
                    cs.sendMessage("§cMultiple regions found by that name.");
                    return;
                } else {
                    if ((cs instanceof Player) && !PermissionsManager.getHandler().hasPermission(cs.getName(), "system.regions." + l.get(0).getName().toLowerCase() + ".admin")) {
                        cs.sendMessage("§cYou are not authorized to administer region \"" + l.get(0).getName() + "\"");
                        return;
                    }

                    Region region = l.get(0);

                    char[] flags = args[1].toCharArray();
                    boolean set = false;
                    boolean entry = false;
                    boolean modify = false;
                    boolean admin = false;

                    String entryPerm = "system.region." + region.getName().toLowerCase() + ".entry";
                    String modifyPerm = "system.region." + region.getName().toLowerCase() + ".modify";
                    String adminPerm = "system.region." + region.getName().toLowerCase() + ".admin";

                    for (int i = 0; i < flags.length; i++) {
                        if (flags[i] == '+') {
                            set = true;
                            continue;
                        }

                        if (flags[i] == '-') {
                            set = false;
                            continue;
                        }

                        if (flags[i] == 'e') {
                            entry = (set == true);
                        } else if (flags[i] == 'm') {
                            modify = (set == true);
                        } else if (flags[i] == 'a') {
                            admin = (set == true);
                        }
                    }

                    if (args[2].equalsIgnoreCase("-p")) {
                        List<Player> ps = Bukkit.matchPlayer(args[3]);
                        String player = "";

                        if (ps.isEmpty()) {
                            player = args[3];
                        } else if (ps.size() > 1) {
                            cs.sendMessage("§cMultiple players found with that name.");
                            return;
                        } else {
                            player = ps.get(0).getName();
                        }

                        if (entry) {
                            PermissionsManager.getHandler().givePermission(region.getWorld().getName(), player, entryPerm);
                        } else {
                            PermissionsManager.getHandler().removePermission(region.getWorld().getName(), player, entryPerm);
                        }

                        if (modify) {
                            PermissionsManager.getHandler().givePermission(region.getWorld().getName(), player, modifyPerm);
                        } else {
                            PermissionsManager.getHandler().removePermission(region.getWorld().getName(), player, modifyPerm);
                        }

                        if (admin) {
                            PermissionsManager.getHandler().givePermission(region.getWorld().getName(), player, adminPerm);
                        } else {
                            PermissionsManager.getHandler().removePermission(region.getWorld().getName(), player, adminPerm);
                        }

                        cs.sendMessage("§aSet \"" + player + "\" player's flags in \"" + region.getName() + "\" to " + args[1]);
                    } else if (args[2].equalsIgnoreCase("-g")) {
                        String group = args[3];

                        if (entry) {
                            PermissionsManager.getHandler().giveGroupPermission(region.getWorld().getName(), group, entryPerm);
                        } else {
                            PermissionsManager.getHandler().removeGroupPermission(region.getWorld().getName(), group, entryPerm);
                        }

                        if (modify) {
                            PermissionsManager.getHandler().giveGroupPermission(region.getWorld().getName(), group, modifyPerm);
                        } else {
                            PermissionsManager.getHandler().removeGroupPermission(region.getWorld().getName(), group, modifyPerm);
                        }

                        if (admin) {
                            PermissionsManager.getHandler().giveGroupPermission(region.getWorld().getName(), group, adminPerm);
                        } else {
                            PermissionsManager.getHandler().removeGroupPermission(region.getWorld().getName(), group, adminPerm);
                        }

                        cs.sendMessage("§aSet \"" + group + "\" group's flags in \"" + region.getName() + "\" to " + args[1]);
                    } else {
                        cs.sendMessage("§cIncorrect flag: " + args[1]);
                    }

                    return;
                }
            }
        }
    }

    @Command(aliases = {"listregions", "lr"},
    bounds = {0, 2},
    help = "List registered regions with /listregions\n"
    + "Show a region's subregions with /lr children [region]")
    @CommandPermission(permission = "voxelguest.regions.list.list")
    @Subcommands(arguments = {"children"}, permission = {"voxelguest.regions.list.children"})
    public void listRegions(CommandSender cs, String[] args)
    {
        if (args.length == 2 && args[0].equalsIgnoreCase("children")) {
            List<Region> l = matchRegion(args[1]);

            if (l.isEmpty()) {
                cs.sendMessage("§cNo region found with that name.");
                return;
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple regions found with that name.");
                return;
            } else {
                Region parent = l.get(0);

                Region[] subregions = new Region[getSubregions(parent).size()];
                subregions = getSubregions(parent).toArray(subregions);

                if (subregions.length == 0) {
                    cs.sendMessage("§cThis region has no subregions.");
                    return;
                }

                cs.sendMessage("§8==============================");
                cs.sendMessage("§fRegion Children for §6" + parent.getName());
                cs.sendMessage("§8==============================");

                for (int i = 0; i < subregions.length; i++) {
                    cs.sendMessage("§f" + (i + 1) + "§7.§8) §f" + subregions[i].getName());
                }

                return;
            }
        }

        Region[] regions = new Region[loadedRegions.size()];
        regions = loadedRegions.toArray(regions);

        if (regions.length == 0) {
            cs.sendMessage("§cNo regions are registered.");
            return;
        }

        cs.sendMessage("§8==============================");
        cs.sendMessage("§fRegistered Regions");
        cs.sendMessage("§8==============================");

        for (int i = 0; i < regions.length; i++) {
            cs.sendMessage("§f" + (i + 1) + "§7.§8) §f" + regions[i].getName() + ((!getSubregions(regions[i]).isEmpty()) ? (" §8[§fChildren: §6" + getSubregions(regions[i]).size() + "§8]") : ""));
        }

        return;
    }

    @Command(aliases = {"regioninfo", "ri"},
    bounds = {1, 1},
    help = "Displays info for a region using\n"
    + "§c/regioninfo [region]")
    public void regionInfo(CommandSender cs, String[] args)
    {
        List<Region> l = matchRegion(args[0]);

        if (l.isEmpty()) {
            cs.sendMessage("§cNo region found by that name.");
        } else if (l.size() > 1) {
            cs.sendMessage("§cMultiple regions found by that name.");
        } else {
            Region region = l.get(0);

            cs.sendMessage("§8==============================");
            cs.sendMessage("§fRegion: §6" + region.getName());
            cs.sendMessage("§8==============================");

            Vector3D min = region.getMinimumPoint();
            Vector3D max = region.getMaximumPoint();
            cs.sendMessage("§a-World: §f" + region.getWorld().getName());
            cs.sendMessage("§a-From: §8(§f" + min.getX() + "§7,§f " + min.getY() + "§7,§f " + min.getZ() + "§8)");
            cs.sendMessage("§a-To: §8(§f" + max.getX() + "§7,§f " + max.getY() + "§7,§f " + max.getZ() + "§8)");

        }
    }

    public List<Region> matchRegion(String name)
    {
        Region[] regions = new Region[loadedRegions.size()];
        regions = loadedRegions.toArray(regions);
        List<Region> ret = new ArrayList<Region>();

        for (Region region : regions) {
            if (name.toLowerCase().startsWith((region.getName().toLowerCase()))) {
                if (!ret.contains(region)) {
                    ret.add(region);
                }
            }
        }

        return ret;
    }

    public List<Region> getSubregions(Region region)
    {
        Region[] regions = new Region[loadedRegions.size()];
        regions = loadedRegions.toArray(regions);
        List<Region> subregions = new ArrayList<Region>();


        for (Region reg : regions) {
            if (reg.equals(reg)) {
                continue;
            }

            if (region.inBounds(reg)) {
                if (!subregions.contains(reg)) {
                    subregions.add(reg);
                }
            }
        }

        return subregions;
    }

    @ModuleEvent(event = PlayerMoveEvent.class)
    public void onPlayerMove(BukkitEventWrapper wrapper)
    {
        PlayerMoveEvent event = (PlayerMoveEvent) wrapper.getEvent();

        if (!canEnter(event.getPlayer(), event.getTo())) {
            event.getPlayer().sendMessage("§cYou are not authorized to enter this region.");
            event.getPlayer().teleport(event.getFrom());
            event.setCancelled(true);
        }
    }

    @ModuleEvent(event = PlayerTeleportEvent.class)
    public void onPlayerTeleport(BukkitEventWrapper wrapper)
    {
        PlayerTeleportEvent event = (PlayerTeleportEvent) wrapper.getEvent();

        if (!canEnter(event.getPlayer(), event.getTo())) {
            event.getPlayer().sendMessage("§cYou are not authorized to enter this region.");
            event.getPlayer().teleport(event.getFrom());
            event.setCancelled(true);
        }
    }

    @ModuleEvent(event = BlockBreakEvent.class)
    public void onBlockBreak(BukkitEventWrapper wrapper)
    {
        BlockBreakEvent event = (BlockBreakEvent) wrapper.getEvent();

        if (!canModify(event.getPlayer(), event.getBlock().getLocation())) {
            //event.getPlayer().sendMessage("§cYou cannot modify this area.");
            //event.setCancelled(true);
            return;
        }
    }

    @ModuleEvent(event = BlockDamageEvent.class)
    public void onBlockDamage(BukkitEventWrapper wrapper)
    {
        BlockDamageEvent event = (BlockDamageEvent) wrapper.getEvent();

        if (!canModify(event.getPlayer(), event.getBlock().getLocation())) {
            event.getPlayer().sendMessage("§cYou cannot modify this area.");
            event.setCancelled(true);
            return;
        }
    }

    @ModuleEvent(event = BlockPlaceEvent.class)
    public void onBlockPlace(BukkitEventWrapper wrapper)
    {
        BlockPlaceEvent event = (BlockPlaceEvent) wrapper.getEvent();

        if (!canModify(event.getPlayer(), event.getBlockPlaced().getLocation())) {
            event.getPlayer().sendMessage("§cYou cannot modify this area.");
            event.setCancelled(true);
            return;
        }
    }

    public boolean canEnter(Player p, Location loc)
    {
        Region[] regions = new Region[loadedRegions.size()];
        regions = loadedRegions.toArray(regions);

        return checkRegionStack(regions, p.getName(), loc, "entry");
    }

    public boolean canModify(Player p, Location loc)
    {
        Region[] regions = new Region[loadedRegions.size()];
        regions = loadedRegions.toArray(regions);

        return checkRegionStack(regions, p.getName(), loc, "modify");
    }

    private boolean checkRegionStack(Region[] regions, String playerName, Location loc, String permissionSuffix)
    {
        for (Region region : regions) {
            if (region.inBounds(loc)) {
                if (!getSubregions(region).isEmpty()) {
                    Region[] subregions = new Region[getSubregions(region).size()];
                    subregions = getSubregions(region).toArray(subregions);
                    return checkRegionStack(subregions, playerName, loc, permissionSuffix);
                }

                if (PermissionsManager.getHandler().hasPermission(region.getWorld().getName(), playerName, "system.region." + region.getName().toLowerCase() + ".admin")
                        || PermissionsManager.getHandler().hasPermission(region.getWorld().getName(), playerName, "system.region." + region.getName().toLowerCase() + "." + permissionSuffix)) {
                    return true;
                }

                if (permissionSuffix.equalsIgnoreCase("modify")
                        && !region.isGeneralBuildOverrideDisabled()
                        && PermissionsManager.getHandler().hasPermission(playerName, "system.build.general")) {
                    return true;
                }

                return false;
            }
        }

        if (permissionSuffix.equalsIgnoreCase("modify")
                && getConfiguration().getBoolean("enable-general-build-outside-defined-regions")
                && PermissionsManager.getHandler().hasPermission(playerName, "system.build.general")) {
            return true;
        } else if (permissionSuffix.equalsIgnoreCase("modify")
                && getConfiguration().getBoolean("enable-general-build-outside-defined-regions")
                && !PermissionsManager.getHandler().hasPermission(playerName, "system.build.general")) {
            return false;
        }

        return true;
    }
}
