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

import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.Formatter;
import com.thevoxelbox.voxelguest.modules.ModuleSystemListener;
import com.thevoxelbox.voxelguest.players.GuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SystemListener extends ModuleSystemListener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        GuestPlayer gp = VoxelGuest.registerPlayer(event.getPlayer());
        VoxelGuest.getGroupManager().verifyPlayerGroupExistence(event.getPlayer());

        ++VoxelGuest.ONLINE_MEMBERS;

        if (VoxelGuest.ONLINE_MEMBERS != Bukkit.getOnlinePlayers().length) {
            VoxelGuest.ONLINE_MEMBERS = Bukkit.getOnlinePlayers().length;
        }

        try {
            String format = VoxelGuest.getConfigData().getString("join-message-format");
            event.setJoinMessage(formatJoinQuitKickMessage(format, gp));
        } catch (NullPointerException ex) {
            event.setJoinMessage(ChatColor.YELLOW + gp.getPlayer().getName() + " joined");
            ex.printStackTrace();
        }

        processModuleEvents(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        GuestPlayer gp = VoxelGuest.getGuestPlayer(event.getPlayer());
        --VoxelGuest.ONLINE_MEMBERS;

        try {
            String format = VoxelGuest.getConfigData().getString("leave-message-format");
            event.setQuitMessage(formatJoinQuitKickMessage(format, gp));
        } catch (NullPointerException ex) {
            event.setQuitMessage(ChatColor.YELLOW + gp.getPlayer().getName() + " left");
        }

        processModuleEvents(event);

        if (!event.getPlayer().isOnline()) {
            VoxelGuest.unregsiterPlayer(gp);
            gp.saveData(VoxelGuest.getPluginId(VoxelGuest.getInstance()));
        }

        if (VoxelGuest.ONLINE_MEMBERS != Bukkit.getOnlinePlayers().length) {
            VoxelGuest.ONLINE_MEMBERS = Bukkit.getOnlinePlayers().length;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKick(PlayerKickEvent event)
    {
        GuestPlayer gp = VoxelGuest.getGuestPlayer(event.getPlayer());
        --VoxelGuest.ONLINE_MEMBERS;

        try {
            String format = VoxelGuest.getConfigData().getString("kick-message-format");
            event.setLeaveMessage(formatJoinQuitKickMessage(format, gp));
        } catch (NullPointerException ex) {
            event.setLeaveMessage(ChatColor.YELLOW + gp.getPlayer().getName() + " was kicked out");
        }

        processModuleEvents(event);

        if (!event.getPlayer().isOnline()) {
            VoxelGuest.unregsiterPlayer(gp);
            gp.saveData(VoxelGuest.getPluginId(VoxelGuest.getInstance()));
        }

        if (VoxelGuest.ONLINE_MEMBERS != Bukkit.getOnlinePlayers().length) {
            VoxelGuest.ONLINE_MEMBERS = Bukkit.getOnlinePlayers().length;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onPlayerPreLogin(PlayerPreLoginEvent event)
    {
        if (PermissionsManager.getHandler().hasPermission(event.getName(), "system.maxcapacity.bypass")) {
            event.allow();
        }

        processModuleEvents(event);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        processModuleEvents(event);
    }

    @EventHandler
    public void signChangeEvent(SignChangeEvent event)
    {
        processModuleEvents(event);
    }

    private String formatJoinQuitKickMessage(String format, GuestPlayer gp)
    {
        return Formatter.selectFormatter(SimpleFormatter.class).formatMessage(format, gp);
    }
}
