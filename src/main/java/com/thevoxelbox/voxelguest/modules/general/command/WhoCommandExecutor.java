package com.thevoxelbox.voxelguest.modules.general.command;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.VoxelGuest;
import com.thevoxelbox.voxelguest.modules.general.GeneralModule;
import com.thevoxelbox.voxelguest.modules.general.GeneralModuleConfiguration;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class WhoCommandExecutor implements CommandExecutor
{
    private GeneralModule module;
    private GeneralModuleConfiguration configuration;

    public WhoCommandExecutor(final GeneralModule generalModule)
    {
        this.module = generalModule;

        Preconditions.checkState(generalModule.getConfiguration() instanceof GeneralModuleConfiguration);
        this.configuration = (GeneralModuleConfiguration) generalModule.getConfiguration();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        final boolean canSeeFQ = sender.hasPermission(GeneralModule.FAKEQUIT_PERM);
        sender.sendMessage(ChatColor.DARK_GRAY + "------------------------------");
        sender.sendMessage(this.getHeader(canSeeFQ));

        for (Entry<String, List<Player>> entry : this.createGroupPlayerMap().entrySet())
        {
            final String groupName = entry.getKey();
            final StringBuilder groupStrBuilder = new StringBuilder();
            groupStrBuilder.append(ChatColor.DARK_GRAY + "[" + this.getColor(groupName) + this.getGroupChar(groupName) + ChatColor.DARK_GRAY + "] ");
            final ListIterator<Player> playerItr = entry.getValue().listIterator();
            while (playerItr.hasNext())
            {
                Player player = playerItr.next();
                if (this.module.getVanishFakequitHandler().isPlayerFakequit(player))
                {
                    if (!canSeeFQ)
                    {
                        continue;
                    }
                    groupStrBuilder.append(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "FQ" + ChatColor.DARK_GRAY + "] ");
                }

                if (player.hasMetadata("isHelper"))
                {
                    groupStrBuilder.append(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "H" + ChatColor.DARK_GRAY + "] ");
                }

                if (this.module.getAfkManager().isPlayerAfk(player))
                {
                    groupStrBuilder.append(ChatColor.GRAY + player.getDisplayName());
                }
                else
                {
                    groupStrBuilder.append(ChatColor.WHITE + player.getDisplayName());
                }
                if (playerItr.hasNext())
                {
                    groupStrBuilder.append(ChatColor.GOLD + ", ");
                }
            }
            sender.sendMessage(groupStrBuilder.toString());
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "------------------------------");

        return true;
    }

    /**
     * Creates a header that that contains all of the current groups
     * on the server and how many people in the group are online. If
     * a vault error occurs it sorts online players into OPs and Non-OPs.
     *
     * @param canSeeFQ Weather or not the player requesting the header can see people who are fake quit
     * @return Preformated group information header
     */
    private String getHeader(final boolean canSeeFQ)
    {
        final Map<String, Integer> groupCount = new HashMap<>();

        try
        {
            final Permission perms = VoxelGuest.getPerms();
            for (String groupName : perms.getGroups())
            {
                groupCount.put(groupName, 0);
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (this.module.getVanishFakequitHandler().isPlayerFakequit(player))
                    {
                        if (!canSeeFQ)
                        {
                            continue;
                        }
                    }
                    if (perms.getPrimaryGroup(player).equals(groupName))
                    {
                        groupCount.put(groupName, groupCount.get(groupName) + 1);
                    }
                }
            }
            final StringBuilder stringBuilder = new StringBuilder();
            for (String groupName : groupCount.keySet())
            {
                final int groupSize = groupCount.get(groupName);
                final char groupChar = this.getGroupChar(groupName);
                stringBuilder.append(ChatColor.DARK_GRAY + "[" + this.getColor(groupName) + groupChar + ":" + groupSize + ChatColor.DARK_GRAY + "] ");
            }
            final int onlinePlayers = canSeeFQ ? Bukkit.getOnlinePlayers().length : Bukkit.getOnlinePlayers().length - this.module.getVanishFakequitHandler().getFakequitSize();
            stringBuilder.append(ChatColor.DARK_GRAY + "(" + ChatColor.WHITE + "O:" + onlinePlayers + ChatColor.DARK_GRAY + ")");
            return stringBuilder.toString();
        }
        catch (final Exception e)
        {
            groupCount.clear();

            groupCount.put("OPs", 0);
            groupCount.put("Players", 0);

            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (this.module.getVanishFakequitHandler().isPlayerFakequit(player))
                {
                    if (!canSeeFQ)
                    {
                        continue;
                    }
                }
                if (player.isOp())
                {
                    groupCount.put("OPs", groupCount.get("OPs") + 1);
                }
                else
                {
                    groupCount.put("Players", groupCount.get("Players") + 1);
                }
            }
            final StringBuilder stringBuilder = new StringBuilder();
            for (String groupName : groupCount.keySet())
            {
                final int groupSize = groupCount.get(groupName);
                final char groupChar = this.getGroupChar(groupName);
                stringBuilder.append(ChatColor.DARK_GRAY + "[" + this.getColor(groupName) + groupChar + ":" + groupSize + ChatColor.DARK_GRAY + "] ");
            }
            final int onlinePlayers = canSeeFQ ? Bukkit.getOnlinePlayers().length : Bukkit.getOnlinePlayers().length - this.module.getVanishFakequitHandler().getFakequitSize();
            stringBuilder.append(ChatColor.DARK_GRAY + "(" + ChatColor.WHITE + "O:" + onlinePlayers + ChatColor.DARK_GRAY + ")");
            return stringBuilder.toString();
        }
    }

    /**
     * Gets the char to refer to this group as.
     *
     * @param groupName name of the group to create the char for
     * @return char to refer to this group with
     */
    private char getGroupChar(final String groupName)
    {
        return groupName.toUpperCase().charAt(0);
    }

    /**
     * Creates a map containing all the groups who have someone currently
     * online to represent them as a key, and in the value it store the
     * list of online players that are part of the corresponding group.
     * <br />
     * If a vault error occurs it sorts players into OPs and Non-OPs.
     * Non-OPs will be listed under players and OPs will be listed under
     * Players.
     *
     * @return Map with represented groups as the key and players in the group as the values
     */
    public Map<String, List<Player>> createGroupPlayerMap()
    {
        final Map<String, List<Player>> groupPlayerMap = new HashMap<>();
        try
        {
            final Permission perms = VoxelGuest.getPerms();
    
            for (Player player : Bukkit.getOnlinePlayers())
            {
                final String groupName = perms.getPrimaryGroup(player);
                if (!groupPlayerMap.containsKey(groupName))
                {
                    final List<Player> newGroupList = new ArrayList<>();
                    newGroupList.add(player);
                    groupPlayerMap.put(groupName, newGroupList);
                }
                else
                {
                    groupPlayerMap.get(groupName).add(player);
                }
            }
        }
        catch (Exception e)
        {
            groupPlayerMap.clear();
            groupPlayerMap.put("OPs", new ArrayList<Player>());
            groupPlayerMap.put("Players", new ArrayList<Player>());

            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (player.isOp())
                {
                    groupPlayerMap.get("OPs").add(player);
                }
                else
                {
                    groupPlayerMap.get("Players").add(player);
                }
            }
        }
        return groupPlayerMap;
    }

    /**
     * Gets the color that should appear in /who for the specified group name.
     *
     * @param groupName Name of group to find color foe
     * @return String containing the chat color that should be used
     */
    private String getColor(final String groupName)
    {
        //TODO: More flexible group system
        if (groupName.equalsIgnoreCase("admin"))
        {
            return configuration.getAdminColor();
        }

        if (groupName.equalsIgnoreCase("curator"))
        {
            return configuration.getCuratorColor();
        }

        if (groupName.equalsIgnoreCase("sniper"))
        {
            return configuration.getSniperColor();
        }

        if (groupName.equalsIgnoreCase("litesniper"))
        {
            return configuration.getLiteSniperColor();
        }

        if (groupName.equalsIgnoreCase("member"))
        {
            return configuration.getMemberColor();
        }

        if (groupName.equalsIgnoreCase("guest"))
        {
            return configuration.getGuestColor();
        }

        if (groupName.equalsIgnoreCase("visitor"))
        {
            return configuration.getVisitorColor();
        }

        if (groupName.equalsIgnoreCase("vip"))
        {
            return configuration.getVipColor();
        }

        if (groupName.equalsIgnoreCase("builder"))
        {
            return configuration.getBuilderColor();
        }

        return ChatColor.WHITE.toString();
    }
}
