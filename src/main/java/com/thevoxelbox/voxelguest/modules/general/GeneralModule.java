package com.thevoxelbox.voxelguest.modules.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.general.command.EntityPurgeCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.FakequitCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.VanishCommandExecutor;
import com.thevoxelbox.voxelguest.modules.general.command.WhoCommandExecutor;

public class GeneralModule extends GuestModule {
	
	private static final String VANISH_PERM = "voxelguest.general.vanish";
	private static final String FAKEQUIT_PERM = "voxelguest.general.fakequit";
	public static final String ENTITY_PURGE_PERM = "voxelguest.general.ep";
	private static final String JOIN_FORMAT = ChatColor.DARK_GRAY + "(" + ChatColor.GOLD + "$no" + ChatColor.DARK_GRAY + ") " 
			+ ChatColor.DARK_AQUA + "$n" + ChatColor.GRAY + " joined";
	private static final String LEAVE_FORMAT = ChatColor.DARK_GRAY + "(" + ChatColor.GOLD + "$no" + ChatColor.DARK_GRAY + ") " 
			+ ChatColor.DARK_AQUA + "$n" + ChatColor.GRAY + " left";
	private static final String FAKEQUIT_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "FQ" + ChatColor.DARK_GRAY + "]";
	
	
	private static final ChatColor ADMIN_COLOUR = ChatColor.GOLD;
	private static final ChatColor CURATOR_COLOUR = ChatColor.DARK_PURPLE;
	private static final ChatColor SNIPER_COLOUR = ChatColor.DARK_GREEN;
	private static final ChatColor LITESNIPER_COLOUR = ChatColor.GREEN;
	private static final ChatColor MEMBER_COLOUR = ChatColor.WHITE;
	private static final ChatColor GUEST_COLOUR = ChatColor.GRAY;
	private static final ChatColor VISITOR_COLOUR = ChatColor.DARK_GRAY;
	private static final ChatColor VIP_COLOUR = ChatColor.DARK_AQUA;
	
	
	
	protected List<String> vanished = new ArrayList<String>();
    protected List<String> ovanished = new ArrayList<String>();
    protected List<String> fakequit = new ArrayList<String>();
    protected List<String> ofakequit = new ArrayList<String>();
    /*
     * these will be used to persist vanished and fakequit players through reloads and restarts
    private String[] reloadVanishedList;
    private String[] reloadFakequitList;
    private String[] reloadOfflineFQList;
    */

    
    //CommandExecuters
    private EntityPurgeCommandExecutor entityPurgeCommandExecutor;
    private VanishCommandExecutor vanishCommandExecutor;
    private FakequitCommandExecutor fakequitCommandExecutor;
    private WhoCommandExecutor whoCommandExecutor;
    
    //Listener
    private ConnectionEventListener connectionEventListener;
    
    private Permission perms = null;
	
	public GeneralModule(Permission p) {
		setName("General Module");
		entityPurgeCommandExecutor = new EntityPurgeCommandExecutor(this);
		vanishCommandExecutor = new VanishCommandExecutor(this);
		connectionEventListener = new ConnectionEventListener(this);
		fakequitCommandExecutor = new FakequitCommandExecutor(this);
		whoCommandExecutor = new WhoCommandExecutor(this);
		this.perms = p;
	}

	@Override
	public final void onEnable()
	{
		//load persisted vanished players
		
		super.onEnable();
		
		
	}

	@Override
	public final void onDisable()
	{
		//save vanished players
		
		super.onDisable();
		
		
	}

	@Override
	public final HashSet<Listener> getListeners()
	{
		final HashSet<Listener> listeners = new HashSet<>();
		listeners.add(connectionEventListener);
		return listeners;
	}
	
    @Override
    public HashMap<String, CommandExecutor> getCommandMappings()
    {
        HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
        commandMappings.put("ep", entityPurgeCommandExecutor);
        commandMappings.put("vanish", vanishCommandExecutor);
        commandMappings.put("fakequit", fakequitCommandExecutor);
        commandMappings.put("who", whoCommandExecutor);
        

        return commandMappings;
    }

	
	
	public void who(CommandSender sender) {
		boolean admin = false;
		if(sender.hasPermission("FAKEQUIT_PERM")) {
			admin = true;
		}
		HashMap<String, List<String>> groups = new HashMap<String, List<String>>();
		for(Player p: Bukkit.getOnlinePlayers()) {
			boolean fq = fakequit.contains(p.getName());
			if(fq && !admin) {
				continue;
			}
			String group = perms.getPrimaryGroup(p);
			List<String> names = new ArrayList<String>();
			if(groups.containsKey(group)) {
				names = groups.get(group);
			}
			names.add(fq? FAKEQUIT_PREFIX + ((Player)sender).getDisplayName() : ((Player)sender).getDisplayName());
			groups.put(group, names);
			
		}
		
		sender.sendMessage(ChatColor.DARK_GRAY + "------------------------------");
		
		String header = "";
		for(String s: groups.keySet()) {
			header += ChatColor.DARK_GRAY + "[" + getColour(s) + s.substring(0, 1).toUpperCase() + ":" + groups.get(s).size() + ChatColor.DARK_GRAY + "] ";
		}
		String online = Bukkit.getOnlinePlayers().length - fakequit.size() + "";
		header += ChatColor.DARK_GRAY + "(" + ChatColor.WHITE + "O:" + online + ChatColor.DARK_GRAY + ")";
		sender.sendMessage(header);
		
		
		for(String s: groups.keySet()) {
			List<String> names = groups.get(s);
			String groupOut = ChatColor.DARK_GRAY + "[" + getColour(s) + s.substring(0, 1).toUpperCase() + ChatColor.DARK_GRAY + "] ";
			for(int i = 0; i < names.size(); i++) {
				groupOut += ChatColor.WHITE + names.get(i);
				if(i < names.size()-1) groupOut += ChatColor.GOLD + ", ";
			}
			sender.sendMessage(groupOut);
		}
		
		sender.sendMessage(ChatColor.DARK_GRAY + "------------------------------");
	}
	
	private ChatColor getColour(String s) {
		if(s.equalsIgnoreCase("admin")) {
			return ADMIN_COLOUR;
		}
		if(s.equalsIgnoreCase("curator")) {
			return CURATOR_COLOUR;
		}
		if(s.equalsIgnoreCase("sniper")) {
			return SNIPER_COLOUR;
		}
		if(s.equalsIgnoreCase("litesniper")) {
			return LITESNIPER_COLOUR;
		}
		if(s.equalsIgnoreCase("member")) {
			return MEMBER_COLOUR;
		}
		if(s.equalsIgnoreCase("guest")) {
			return GUEST_COLOUR;
		}
		if(s.equalsIgnoreCase("visitor")) {
			return VISITOR_COLOUR;
		}
		if(s.equalsIgnoreCase("vip")) {
			return VIP_COLOUR;
		}
		return ChatColor.WHITE;
	}

	/*
	 * Toggles fakequit for the specified player (if they have permission)
	 */
	public void fakequitPlayer(CommandSender sender) {
		if(!sender.hasPermission(FAKEQUIT_PERM)) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
			return;
		}
		if(ofakequit.contains(sender.getName())) ofakequit.remove(sender.getName());
		if(fakequit.contains(sender.getName())) {
			sender.sendMessage(ChatColor.AQUA + "You have un-fakequit!");
			fakequit.remove(sender.getName());

			String online = Bukkit.getOnlinePlayers().length - fakequit.size() + "";
			String s = JOIN_FORMAT.replaceAll("$n", sender.getName()).replaceAll("$no", online);
			Bukkit.broadcastMessage(s);
		} else {
			sender.sendMessage(ChatColor.AQUA + "You have fakequit!");
			
			fakequit.add(sender.getName());
			if(!vanished.contains(sender.getName())) {
				vanished.add(sender.getName());
				hidePlayerForAll((Player) sender);
			}
			
			String online = Bukkit.getOnlinePlayers().length - fakequit.size() + "";
			String s = LEAVE_FORMAT.replaceAll("$n", sender.getName()).replaceAll("$no", online);
			Bukkit.broadcastMessage(s);
		}
	}
	
	/*
	 * Toggles vanished state for the specified player (if they have permission)
	 */
	public void vanishPlayer(CommandSender sender) {
		if(!sender.hasPermission(VANISH_PERM)) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
			return;
		}
		if(ovanished.contains(sender.getName())) ovanished.remove(sender.getName());
		if(vanished.contains(sender.getName())) {
			sender.sendMessage(ChatColor.AQUA + "You have reappeared!");
			vanished.remove(sender.getName());
		} else {
			sender.sendMessage(ChatColor.AQUA + "You have vanished!");
			vanished.add(sender.getName());
			hidePlayerForAll((Player) sender);
		}
	}
	
	/*
	 * Hides the specified player for all online players
	 */
	public void hidePlayerForAll(Player hidden) {
		if(hidden == null) return;
		for(Player p: Bukkit.getOnlinePlayers()) {
			if(!p.hasPermission(VANISH_PERM)) {
				p.hidePlayer(hidden);
			}
		}
	}
	
	/*
	 * Hides all online vanished players for the specified player
	 */
	public void hideAllForPlayer(Player player) {
		if(player == null) return;
		if(player.hasPermission(VANISH_PERM)) return;
		for(String s: vanished) {
			Player hidden = Bukkit.getPlayer(s);
			if(hidden != null) player.hidePlayer(hidden);
		}
	}
	

}
