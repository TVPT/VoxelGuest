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

import com.patrickanker.lib.commands.*;
import com.patrickanker.lib.config.PropertyConfiguration;
import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.commands.MiscellaneousCommands;
import com.thevoxelbox.voxelguest.commands.ServerAdministrationCommands;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleManager;
import com.thevoxelbox.voxelguest.players.GroupManager;
import com.thevoxelbox.voxelguest.players.GuestPlayer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class VoxelGuest extends JavaPlugin {

    private static VoxelGuest instance;
    protected static CommandManager commandsManager = new CommandManager();
    protected static SystemListener listener = new SystemListener();
    protected static List<GuestPlayer> guestPlayers = new LinkedList<GuestPlayer>();
    protected static Map<Plugin, String> pluginIds = new HashMap<Plugin, String>();
    protected static GroupManager groupManager;
    protected static PermissionsManager perms;
    protected static ModuleManager moduleManager;
    protected static final PropertyConfiguration config = new PropertyConfiguration("VoxelGuest", "/VoxelGuest");
    public static int ONLINE_MEMBERS = 0;
    
    protected Class<? extends Module>[] availableModules = new Class[]{
        AFKModule.class,
        SpawnModule.class,
        CubicleModule.class,
        AsshatMitigationModule.class,
        CreatureProtectionModule.class,
        GreylistModule.class,
        OfflineModeModule.class,
        PlayerProtectionModule.class,
        RegionModule.class,
        VanishModule.class,
        SignLoggerModule.class,
        WorldProtectionModule.class
    };

    @Override
    public void onDisable()
    {
        ListIterator<GuestPlayer> it = guestPlayers.listIterator();

        while (it.hasNext()) {
            GuestPlayer gp = it.next();
            gp.saveData(getPluginId(this));
        }

        guestPlayers.clear();
        groupManager.saveGroupConfigurations();

        moduleManager.shutDownModules();

        getConfigData().save();
    }

    @Override
    public void onEnable()
    {
        instance = this;

        perms = new PermissionsManager(this.getServer(), "[VoxelGuest]", config);
        groupManager = new GroupManager();
        moduleManager = new ModuleManager(this, commandsManager);
        registerPluginIds();

        // Register system / miscellaneous commands
        commandsManager.registerCommands(MiscellaneousCommands.class, VoxelGuest.getInstance());
        commandsManager.registerCommands(ServerAdministrationCommands.class, VoxelGuest.getInstance());

        // Load system event listeners
        Bukkit.getPluginManager().registerEvents(listener, this);
        Bukkit.getPluginManager().registerEvents(perms, this);

        // Load permissions system
        perms.registerActiveHandler();

        // Load players
        for (Player player : Bukkit.getOnlinePlayers()) {
            GuestPlayer gp = new GuestPlayer(player);

            if (isPlayerRegistered(gp)) {
                continue;
            }

            groupManager.verifyPlayerGroupExistence(player);
            guestPlayers.add(gp); // KEEP THIS LAST
            ONLINE_MEMBERS++;
        }

        // Load modules
        ModuleManager.setActiveModuleManager(moduleManager);
        moduleManager.loadModules(availableModules);

        // Load module events into the system listener
        listener.registerModuleEvents();

        if (getConfigData().getString("reset") == null || getConfigData().getString("reset").equalsIgnoreCase("yes")) {
            loadFactorySettings();
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args)
    {   
        return commandsManager.executeCommandProcessErrors(command, cs, args, this);
    }

    public static PropertyConfiguration getConfigData()
    {
        return config;
    }

    public static GuestPlayer getGuestPlayer(Player player)
    {
        Iterator<GuestPlayer> it = guestPlayers.listIterator();

        while (it.hasNext()) {
            GuestPlayer gp = it.next();

            if (player.equals(gp.getPlayer())) {
                return gp;
            }
        }

        return new GuestPlayer(player);
    }

    public static GuestPlayer registerPlayer(Player player)
    {
        GuestPlayer gp = new GuestPlayer(player);

        if (!isPlayerRegistered(gp)) {
            guestPlayers.add(gp);
        }

        return gp;
    }

    public static void unregsiterPlayer(GuestPlayer gp)
    {
        if (isPlayerRegistered(gp)) {
            guestPlayers.remove(gp);
        }
    }

    public static GuestPlayer[] getRegisteredPlayers()
    {
        GuestPlayer[] gps = new GuestPlayer[guestPlayers.size()];
        return guestPlayers.toArray(gps);
    }

    public static boolean isPlayerRegistered(GuestPlayer gp)
    {
        return guestPlayers.contains(gp);
    }

    private void registerPluginIds()
    {
        if (pluginIds.isEmpty()) {
            for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                if (pluginIds.containsKey(plugin)) {
                    VoxelGuest.log("Attempted to register multiple IDs for plugin \"" + plugin.getDescription().getName() + "\"", 1);
                    continue;
                }

                String sample = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
                char[] key = new char[16];
                Random rand = new Random();

                for (int i = 0; i < 16; i++) {
                    key[i] = sample.charAt(rand.nextInt(sample.length()));
                }

                String id = new String(key);
                pluginIds.put(plugin, id);
            }
        }
    }

    public static String getPluginId(Plugin plugin)
    {
        if (pluginIds.containsKey(plugin)) {
            return pluginIds.get(plugin);
        }

        return null;
    }

    public static VoxelGuest getInstance()
    {
        return instance;
    }

    public static CommandManager getCommandsManager()
    {
        return commandsManager;
    }

    public static GroupManager getGroupManager()
    {
        return groupManager;
    }

    public void loadFactorySettings()
    {
        getConfigData().setString("join-message-format", "&8(&6$nonline&8) &3$n &7joined");
        getConfigData().setString("leave-message-format", "&8(&6$nonline&8) &3$n &7left");
        getConfigData().setString("kick-message-format", "&8(&6$nonline&8) &3$n &4was kicked out");

        getConfigData().setBoolean("permissions-multigroup", false);
        getConfigData().setBoolean("permissions-multiworld", false);
        getConfigData().setBoolean("permissions-default-op", false);

        getConfigData().setBoolean("enable-ram-clear-cycle", false);
        getConfigData().setInt("ram-clear-cycle-time", 60);

        for (Module module : ModuleManager.getManager().getModules()) {
            if (module.getConfiguration() != null) {
                module.getConfiguration().reset();
            }
        }

        getConfigData().setString("reset", "no");
        log("| ========================================== |");
        log("| * VOXELGUEST 4                             |");
        log("| *                                          |");
        log("| * The premiere server adminstration suite  |");
        log("| *                                          |");
        log("| * Built by: psanker & VoxelPlugineering    |");
        log("| * Licensed by the BSD License - 2012       |");
        log("| ========================================== |");
        log("Factory settings loaded");
    }

    public static void log(String str)
    {
        ConsoleLogger.getLogger("VoxelGuest").log(str);
    }

    public static void log(String str, int importance)
    {
        ConsoleLogger.getLogger("VoxelGuest").log(str, importance);
    }

    public static void log(String module, String str, int importance)
    {
        ConsoleLogger.getLogger("VoxelGuest").log(module, str, importance);
    }
}
