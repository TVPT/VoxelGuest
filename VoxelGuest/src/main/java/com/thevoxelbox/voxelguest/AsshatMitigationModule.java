package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.commands.engine.Command;
import com.thevoxelbox.voxelguest.commands.engine.CommandPermission;
import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.Setting;
import com.thevoxelbox.voxelguest.util.Configuration;
import com.thevoxelbox.voxelguest.util.Formatter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

/**
 * 
 * @author Razorcane
 */
@MetaData(name = "Asshat Mitigator", description = "Major asshat handling.")
public class AsshatMitigationModule extends Module {

	protected Configuration bannedList = new Configuration("banned", "/asshatmitigation");
	public List<String> gagged = new ArrayList<String>();
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

	@Override
	public void enable() {
		setConfiguration(new AsshatMitigationConfiguration(this));
		bannedList.load();
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
	@CommandPermission(permission = "voxelguest.asshat.ban")
	public void ban(CommandSender cs, String[] args) {
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

		if (getConfiguration().getBoolean("save-banlist-on ban"))
			bannedList.save();
	}

	/*
	 * Asshat Mitigation - Unban Written by: Razorcane
	 * 
	 * Controls the unbanning of banned players. Name must be exact, and player
	 * must be banned, in order to be unbanned.
	 */
	@Command(aliases = { "unban", "vunban" }, bounds = { 1, -1 }, help = "To unban someone, simply type\n" + "§c/unban [player]")
	@CommandPermission(permission = "voxelguest.asshat.unban")
	public void unban(CommandSender cs, String[] args) {
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
	
	@Command(aliases={"soapbox"},
			bounds={0,0},
			help="Toggle the silence")
	@CommandPermission(permission="voxelguest.admin.silence")
	public void silence(CommandSender cs, String[] args) {
		silenceMode = !silenceMode;
		Player p = (Player) cs;
		getConfiguration().setBoolean("silence-mode",silenceMode);
		cs.sendMessage(ChatColor.GOLD + "Silent mode has been" + ((silenceMode) ? "enabled" : "disabled"));
	}

	/*
	 * Asshat Mitigation - Gag Written by: Razorcane
	 * 
	 * Gags a player, or prevents them from talking until they are ungagged,
	 * there is a server restart, or they type the designated phrase.
	 */
	@Command(aliases = { "gag", "vgag" }, bounds = { 1, -1 }, help = "To gag someone, simply type\n" + "§c/gag [player] (reason)", playerOnly = false)
	@CommandPermission(permission = "voxelguest.asshat.gag")
	public void gag(CommandSender cs, String[] args) {
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
	@Command(aliases = { "kick", "vkick" }, bounds = { 1, -1 }, help = "To kick someone, simply type\n" + "§c/kick [player] (reason)", playerOnly = false)
	@CommandPermission(permission = "voxelguest.asshat.kick")
	public void kick(CommandSender cs, String[] args) {
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

	@ModuleEvent(event = PlayerPreLoginEvent.class, ignoreCancelledEvents = false)
	public void onPlayerPreLogin(BukkitEventWrapper wrapper) {
		PlayerPreLoginEvent event = (PlayerPreLoginEvent) wrapper.getEvent();
		String player = event.getName();

		if (bannedList.hasEntry(player)) {
			event.setResult(PlayerPreLoginEvent.Result.KICK_FULL);
			event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "You are banned for: " + bannedList.getString(player));
		}
	}

	@ModuleEvent(event = PlayerChatEvent.class, ignoreCancelledEvents = false)
	public void onPlayerChat(BukkitEventWrapper wrapper) {
		PlayerChatEvent event = (PlayerChatEvent) wrapper.getEvent();
		Player p = event.getPlayer();
		if (silenceMode) {
			if (PermissionsManager.getHandler().hasPermission(event.getPlayer().getName(), "voxelguest.bypass.silence"))
				event.setCancelled(true);
		}

		if (gagged.contains(p.getName())) {
			if (event.getMessage().equals(getConfiguration().getString("unrestrict-chat-message"))) {
				gagged.remove(p.getName());

				for (String str : Formatter.selectFormatter(SimpleFormatter.class).format(getConfiguration().getString("ungag-message-format"), VoxelGuest.getGuestPlayer(p))) {
					p.sendMessage(str);
				}

				event.setCancelled(true);
			} else {
				for (String str : Formatter.selectFormatter(SimpleFormatter.class).format(getConfiguration().getString("gag-message-format"), VoxelGuest.getGuestPlayer(p))) {
					p.sendMessage(str);
				}

				event.setCancelled(true);
			}
		}
	}
}
