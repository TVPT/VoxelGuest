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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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


/**
 * 
 * @author Razorcane
 * @author Monofraps (cleanup and fixes)
 */
@MetaData(name = "Asshat Mitigator", description = "Major asshat handling.")
public class AsshatMitigationModule extends Module {

	private final PropertyConfiguration bannedList = new PropertyConfiguration("banned", "/VoxelGuest/asshatmitigation");
	private final List<String> gagged = new ArrayList<String>();

	private final List<String> frozen = new ArrayList<String>();
	private boolean allFreeze = false;

	private boolean silenceMode = false;

	public AsshatMitigationModule() {
		super(AsshatMitigationModule.class.getAnnotation(MetaData.class));
	}

	class AsshatMitigationConfiguration extends ModuleConfiguration {

		@Setting("default-asshat-reason")
		public String defaultAsshatReason = "&cAsshat";
		@Setting("save-banlist-on-ban")
		public boolean saveBanlistOnBan = false;
		@Setting("unrestrict-chat-message")
		public String unrestrictChatMessage = "I agree. Allow me to chat.";
		@Setting("gag-message-format")
		public String gagMessageFormat = "&cYou have been gagged. You cannot chat until you say\n" + "&6the ungag key phrase.";
		@Setting("ungag-message-format")
		public String ungagMessageFormat = "&aYou have been ungagged.";

		public AsshatMitigationConfiguration(AsshatMitigationModule parent) {
			super(parent);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void enable() {
		setConfiguration(new AsshatMitigationConfiguration(this));
		
		bannedList.load();
		// update ban list
		final HashMap<String, Object> _updatedMap = new HashMap<String, Object>();
		for(final String _name : bannedList.getAllEntries().keySet()) {
			if(_updatedMap.containsKey(_name.toLowerCase())) {
				continue;
			}
			
			_updatedMap.put(_name.toLowerCase(), bannedList.getEntry(_name));
		}
		bannedList.clear();
		for(final String _name : _updatedMap.keySet()) {
			bannedList.setString(_name, (String)_updatedMap.get(_name));
		}
		bannedList.save();
		
		gagged.clear();
	}

	@Override
	public void disable() {
		bannedList.save();
	}

	@Override
	public String getLoadMessage() {
		return "Asshat Mitigator has been loaded.";
	}

	/*
	 * Asshat Mitigation - Ban Written by: Razorcane
	 * 
	 * Handles the banning of both online and offline players. However, exact
	 * player names must be given when banning offline players.
	 */
	@Command(aliases = { "ban", "vban", "vbano", "bano" }, bounds = { 1, -1 }, help = "To ban someone, simply type\n" + "§c/ban [player] (reason)")
	@CommandPermission("voxelguest.asshat.ban")
	public void ban(final CommandSender cs, final String[] args) {
		final String _playerName = args[0];
		final List<Player> _players = Bukkit.matchPlayer(_playerName);
		
		String _reason = "";
		boolean _silent = false;

		if (args.length > 1) {
			for (short i = 1; i < args.length; ++i) {
				String arg = args[i];

				if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si")) {
					_silent = true;
				} else {
					_reason += args[i] + " ";
				}
			}
		} 

		if(_reason.isEmpty()){
			_reason = getConfiguration().getString("default-asshat-reason");
		} else if (_players.size() > 1) {
			cs.sendMessage(ChatColor.RED + "Partial match:");
			String _nameList = "";
			for (final Player _p : _players) {
				_nameList += _p.getName() + " | ";
			}
			cs.sendMessage(_nameList);
			return;
		}

		banPlayer(_playerName, _reason);
		
		if (_silent) {
			Bukkit.getLogger().info(String.format("Player %s has been banned by %s for: %s", _playerName, cs.getName(), _reason));
		} else {
			Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + _playerName + ChatColor.DARK_GRAY + " has been banned by "
					+ ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
			Bukkit.broadcastMessage(ChatColor.BLUE + _reason);
		}
	}

	/*
	 * Asshat Mitigation - Unban Written by: Razorcane
	 * 
	 * Controls the unbanning of banned players. Name must be exact, and player
	 * must be banned, in order to be unbanned.
	 */
	@Command(aliases = { "unban", "vunban" }, bounds = { 1, -1 }, help = "To unban someone, simply type\n" + "§c/unban [player]")
	@CommandPermission("voxelguest.asshat.unban")
	public void unban(final CommandSender cs, final String[] args) {
		boolean _silent = false;

		for (final String _arg : args) {
			if (_arg.equalsIgnoreCase("-silent") || _arg.equalsIgnoreCase("-si")) {
				_silent = true;
			}
		}

		final String _playerName = args[0];
		if(isPlayerBanned(_playerName)) {
			unbanPlayer(_playerName);
			
			if (_silent) {
				Bukkit.getLogger().info(String.format("Player %s has been unbanned by %s",  _playerName, cs.getName()));
			} else {
				Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + _playerName + ChatColor.DARK_GRAY + " has been unbanned by "
						+ ChatColor.RED + cs.getName());
			}
		} else {
			cs.sendMessage(ChatColor.RED + "Player isn't banned.");
		}
	}

	/*
	 * Asshat Mitigation - Gag Written by: Razorcane
	 * 
	 * Gags a player, or prevents them from talking until they are ungagged,
	 * there is a server restart, or they type the designated phrase.
	 */
	@Command(aliases = { "gag", "vgag" }, bounds = { 1, -1 }, help = "To gag someone, simply type\n" + "§c/gag [player] (reason)", playerOnly = false)
	@CommandPermission("voxelguest.asshat.gag")
	public void gag(final CommandSender cs, final String[] args) {
		final String _playerName = args[0];
		final List<Player> _players = Bukkit.matchPlayer(_playerName);
		
		String _reason = "";
		boolean _silent = false;

		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				final String arg = args[i];

				if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si")) {
					_silent = true;
				} else {
					_reason += args[i] + " ";
				}
			}
		} 

		if(_reason.isEmpty()){
			_reason = getConfiguration().getString("default-asshat-reason");
		}

		if (_players.isEmpty()) {
			cs.sendMessage(ChatColor.RED + "No player with the name " + _playerName + " found.");
		} else if (_players.size() > 1) {
			cs.sendMessage(ChatColor.RED + "Partial match:");
			String _nameList = "";
			for (final Player _p : _players) {
				_nameList += _p.getName() + " | ";
			}
			cs.sendMessage(_nameList);
		} else {
			if (gagged.contains(_playerName)) {
				gagged.remove(_playerName);
				cs.sendMessage(ChatColor.RED + _playerName + ChatColor.WHITE + " has been ungagged.");
			} else {
				gagged.add(_playerName);
				if (_silent) {
					Bukkit.getLogger().info(String.format("Player %s has been gagged by %s for: %s", _playerName, cs.getName(), _reason));
				} else {
					Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + _playerName + ChatColor.DARK_GRAY + " has been gagged by "
							+ ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
					Bukkit.broadcastMessage(ChatColor.BLUE + _reason);
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
	@Command(aliases = { "kick", "vkick" }, bounds = { 1, -1 }, help = "To kick someone, simply type\n" + "§c/kick [player] (reason)", playerOnly = false)
	@CommandPermission("voxelguest.asshat.kick")
	public void kick(final CommandSender cs, final String[] args) {
		final String _playerName = args[0];
		final List<Player> _players = Bukkit.matchPlayer(_playerName);
		
		String _reason = "";
		boolean _silent = false;

		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				String arg = args[i];

				if (arg.equalsIgnoreCase("-silent") || arg.equalsIgnoreCase("-si")) {
					_silent = true;
				} else {
					_reason += args[i] + " ";
				}
			}
		}

		if (_reason.isEmpty()) {
			_reason = getConfiguration().getString("default-asshat-reason");
		}

		if (_players.isEmpty()) {
			cs.sendMessage(ChatColor.RED + "No player with the name " + _playerName + " found.");
		} else if (_players.size() > 1) {
			cs.sendMessage(ChatColor.RED + "Partial match:");
			String _nameList = "";
			for (final Player _p : _players) {
				_nameList += _p.getName() + " | ";
			}
			cs.sendMessage(_nameList);
		} else {
			_players.get(0).kickPlayer(_reason);
			if (_silent) {
				Bukkit.getLogger().info(String.format("Player %s has been kicked by %s for: %s", _playerName, cs.getName(), _reason));
			} else {
				Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Player " + ChatColor.RED + _players.get(0).getName() + ChatColor.DARK_GRAY
						+ " has been kicked by " + ChatColor.RED + cs.getName() + ChatColor.DARK_GRAY + " for:");
				Bukkit.broadcastMessage(ChatColor.BLUE + _reason);
			}
		}
	}

	@Command(aliases = { "freeze", "fr" }, bounds = { 1, 1 }, help = "Freezes the defined player in\n"
			+ "§c/freeze [player]§f or freeze all players (except those with \"voxelguest.asshat.freeze.bypass\") with\n" + "§c/freeze --all§f or §c/freeze -a")
	@CommandPermission("voxelguest.asshat.freeze.freeze")
	public void freeze(final CommandSender cs, final String[] args) {
		if (args[0].equalsIgnoreCase("--all") || args[0].equalsIgnoreCase("-a")) {
			allFreeze = !allFreeze;

			if (allFreeze) {
				for (final Player _p : Bukkit.getOnlinePlayers()) {
					if (!PermissionsManager.getHandler().hasPermission(_p.getName(), "voxelguest.asshat.freeze.bypass")) {
						frozen.add(_p.getName());
					}
				}

				cs.sendMessage("§aEveryone §7has been §bfrozen.");
			} else {
				frozen.clear();
				cs.sendMessage("§aEveryone §7has been §cthawed.");
			}
			
			return;
		}

		final String _playerName = args[0];
		final List<Player> _players = Bukkit.matchPlayer(_playerName);

		if (_players.isEmpty()) {
			cs.sendMessage(ChatColor.RED + "No player found with that name.");
		} else if (_players.size() > 1) {
			cs.sendMessage(ChatColor.RED + "Partial match:");
			String _nameList = "";
			for (final Player _p : _players) {
				_nameList += _p.getName() + " | ";
			}
			cs.sendMessage(_nameList);
		} else {
			if (frozen.contains(_playerName)) {
				frozen.remove(_playerName);
				cs.sendMessage("§a" + _playerName + " §7has been §cthawed.");
			} else {
				frozen.add(_playerName);
				cs.sendMessage("§a" + _playerName + " §7has been §bfrozen.");
			}
		}
	}

	@Command(aliases = { "soapbox", "silence" }, bounds = { 0, 0 }, help = "Toggle the silence")
	@CommandPermission("voxelguest.admin.silence")
	public void silence(final CommandSender cs, final String[] args) {
		silenceMode = !silenceMode;
		getConfiguration().setBoolean("silence-mode", silenceMode);
		cs.sendMessage(ChatColor.GOLD + "Silent mode has been " + ((silenceMode) ? "enabled" : "disabled"));
	}

	@ModuleEvent(event = PlayerPreLoginEvent.class, ignoreCancelledEvents = false)
	public void onPlayerPreLogin(final BukkitEventWrapper wrapper) {
		final PlayerPreLoginEvent _event = (PlayerPreLoginEvent) wrapper.getEvent();
		final String _playerName = _event.getName();

		if (isPlayerBanned(_playerName)) {
			_event.setResult(PlayerPreLoginEvent.Result.KICK_FULL);
			_event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "You are banned for: " + getBanReason(_playerName));
		}
	}

	@ModuleEvent(event = AsyncPlayerChatEvent.class, ignoreCancelledEvents = false)
	public void onPlayerChat(final BukkitEventWrapper wrapper) {
		final AsyncPlayerChatEvent _event = (AsyncPlayerChatEvent) wrapper.getEvent();
		final Player _player = _event.getPlayer();

		if (silenceMode) {
			if (!PermissionsManager.getHandler().hasPermission(_event.getPlayer().getName(), "voxelguest.bypass.silence")) {
				_event.setCancelled(true);
			}
		}

		if (gagged.contains(_player.getName())) {
			if (_event.getMessage().equals(getConfiguration().getString("unrestrict-chat-message"))) {
				gagged.remove(_player.getName());

				for (String str : Formatter.selectFormatter(SimpleFormatter.class).formatMessages(getConfiguration().getString("ungag-message-format"),
						VoxelGuest.getGuestPlayer(_player))) {
					_player.sendMessage(str);
				}

				_event.setCancelled(true);
			} else {
				for (String str : Formatter.selectFormatter(SimpleFormatter.class).formatMessages(getConfiguration().getString("gag-message-format"),
						VoxelGuest.getGuestPlayer(_player))) {
					_player.sendMessage(str);
				}

				_event.setCancelled(true);
			}
		}
	}

	@ModuleEvent(event = PlayerMoveEvent.class)
	public void onPlayerMove(final BukkitEventWrapper wrapper) {
		final PlayerMoveEvent _event = (PlayerMoveEvent) wrapper.getEvent();

		if (frozen.contains(_event.getPlayer().getName())) {
			final Location _orig = _event.getFrom();
			final Location _dest = _event.getTo();
			
			// only cancel if the player actually walks; do not cancel rotation
			if((_orig.getBlockX() != _dest.getBlockX()) || (_orig.getBlockY() != _dest.getBlockY()) || (_orig.getBlockZ() != _dest.getBlockZ())) {
				_event.setTo(_orig);
				_event.setCancelled(true);
			}
		}
	}

	@ModuleEvent(event = PlayerTeleportEvent.class)
	public void onPlayerTeleport(final BukkitEventWrapper wrapper) {
		final PlayerTeleportEvent event = (PlayerTeleportEvent) wrapper.getEvent();

		if (frozen.contains(event.getPlayer().getName())) {
			event.setTo(event.getFrom());
			event.setCancelled(true);
		}
	}

	private void banPlayer(final String playerName, final String reason) {
		Player player = Bukkit.getPlayerExact(playerName);
		
		if (player != null) {
			player.kickPlayer("You are banned for: " + reason);
		}
		
		bannedList.setString(playerName.toLowerCase(), reason);
		
		if (getConfiguration().getBoolean("save-banlist-on ban")) {
			bannedList.save();
		}
	}
	
	private void unbanPlayer(final String playerName) {
		if (isPlayerBanned(playerName.toLowerCase())) {
			bannedList.removeEntry(playerName.toLowerCase());
		}
		
		bannedList.save();
	}
	
	private boolean isPlayerBanned(final String playerName) {
		return bannedList.hasEntry(playerName.toLowerCase());
	}
	
	private String getBanReason(final String playerName) {
		return bannedList.getString(playerName.toLowerCase());
	}
}
