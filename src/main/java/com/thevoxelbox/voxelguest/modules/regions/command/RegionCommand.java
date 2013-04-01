package com.thevoxelbox.voxelguest.modules.regions.command;

import com.thevoxelbox.voxelguest.modules.regions.Region;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Butters
 * @author TheCryoknight
 */
public final class RegionCommand implements TabExecutor
{

    private static String[] subcommands = {"create", "help", "edit", "remove", "regions"};
    private RegionModule regionModule;

    public RegionCommand(final RegionModule regionModule)
    {
        this.regionModule = regionModule;
    }

    /**
     * Applies all chosen flags to the region specified.
     *
     * @param flags
     * @param region
     */
    public static void processFlags(final Map<CommandFlags, String> flags, final Region region)
    {
        if (flags.containsKey(CommandFlags.BANNED_BLOCKS))
        {
            String state = flags.get(CommandFlags.BANNED_BLOCKS);
            flags.remove(CommandFlags.BANNED_BLOCKS);
            RegionCommand.processBlockList(state);
            region.setBannedBlocks(Arrays.asList(RegionCommand.processBlockList(state)));
        }
        if (flags.containsKey(CommandFlags.BANNED_ITEMS))
        {
            String state = flags.get(CommandFlags.BANNED_ITEMS);
            flags.remove(CommandFlags.BANNED_ITEMS);
            RegionCommand.processBlockList(state);
            region.setBannedBlocks(Arrays.asList(RegionCommand.processBlockList(state)));
        }
        for (Entry<CommandFlags, String> flag : flags.entrySet())
        {
            switch (flag.getKey())
            {
                case BLOCK_DROP_ALLOWED:
                    region.setBlockDropAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case BLOCK_GROWTH_ALLOWED:
                    region.setBlockGrowthAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case BLOCK_SPREAD_ALLOWED:
                    region.setBlockSpreadAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case CACTUS_DAMMAGE_ALLOWED:
                    region.setCactusDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case CREEPER_EXPLOSION_ALLOWED:
                    region.setCreeperExplosionAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case DRAGON_EGG_MOVEMENT_ALLOWED:
                    region.setDragonEggMovementAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case DROWNING_DAMMAGE_ALLOWED:
                    region.setDrowningDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case ENCHANTING_ALLOWED:
                    region.setEnchantingAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case EXPLOSIVE_DAMMAGE_ALLOWED:
                    region.setExplosiveDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case FALL_DAMMAGE_ALLOWED:
                    region.setFallDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case FIRETICK_DAMMAGE_ALLOWED:
                    region.setFireTickDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case FIRE_DAMMAGE_ALLOWED:
                    region.setFireDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case FIRE_SPREAD_ALLOWED:
                    region.setFireSpreadAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case FOOD_CHANGE_ALLOWED:
                    region.setFoodChangeAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case HUNGER_DAMMAGE_ALLOWED:
                    region.setHungerDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case ICE_FORMATION_ALLOWED:
                    region.setIceFormationAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case ICE_MELTING_ALLOWED:
                    region.setIceMeltingAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case LAVA_DAMMAGE_ALLOWED:
                    region.setLavaDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case LAVA_FLOW_ALLOWED:
                    region.setLavaFlowAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case LEAF_DECAY_ALLOWED:
                    region.setLeafDecayAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case PHYSICS_ALLOWED:
                    region.setPhysicsAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case LIGHTNING_DAMMAGE_ALLOWED:
                    region.setLightningDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case MAGIC_DAMMAGE_ALLOWED:
                    region.setMagicDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case POISON_DAMMAGE_ALLOWED:
                    region.setPoisonDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case PROJECTILE_DAMMAGE_ALLOWED:
                    region.setProjectileDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case PVP_DAMMAGE_ALLOWED:
                    region.setPvpDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case SNOW_FORMATION_ALLOWED:
                    region.setDrowningDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case SNOW_MELTING_ALLOWED:
                    region.setSnowMeltingAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case SUFFOCATION_DAMMAGE_ALLOWED:
                    region.setSuffocationDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case TNT_BREAKING_PAINTINGS_ALLOWED:
                    region.setTntBreakingPaintingsAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case TNT_DAMMAGE_ALLOWED:
                    region.setTntDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case VOID_DAMMAGE_ALLOWED:
                    region.setVoidDamageAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                case WATER_FLOW_ALLOWED:
                    region.setWaterFlowAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;

                case SOIL_DEHYDRATION_ALLOWED:
                    region.setSoilDehydrationAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;

                case CREATURE_SPAWN_ALLOWED:
                    region.setCreatureSpawnAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;

                case MOB_SPAWN_ALLOWED:
                    region.setMobSpawnAllowed(RegionCommand.parseCommandBool(flag.getValue()));
                    break;
                default:
                    break;
            }
        }
    }

    private static Integer[] processBlockList(final String list)
    {
        final String[] cleanList = list.replace("[", "").replace("]", "").split(",");
        final Integer[] bannedIds = new Integer[cleanList.length];
        try
        {
            for (int i = 0; i < cleanList.length; i++)
            {
                bannedIds[i] = Integer.parseInt(cleanList[i]);
            }
        }
        catch (final NumberFormatException e)
        {
            e.printStackTrace();
        }
        return bannedIds;
    }

    /**
     * Translates 't' and 'y' into true. Everything else will result in false.
     *
     * @param str The input string.
     *
     * @return Returns a beelean indicating if the string started with 't' or 'y'.
     */
    private static boolean parseCommandBool(final String str)
    {
        // `true`, `yes`, `on` and `1` will result in true
        return str.toLowerCase().startsWith("t") || str.toLowerCase().startsWith("y") || str.equals("1") || str.toLowerCase().startsWith("o");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmnd, final String string, final String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.GRAY + "/vgregion <option>");
            sender.sendMessage(ChatColor.GRAY + "Ex: /vgregion help");
            return false;
        }

        if (args[0].equalsIgnoreCase("create"))
        {
            createRegion(sender, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("help"))
        {
            printHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("edit"))
        {
            if (args.length >= 2)
            {
                final Region editedRegion = this.regionModule.getRegionManager().getRegion(args[1]);
                if (editedRegion != null)
                {
                    this.regionModule.getRegionManager().removeRegion(editedRegion);
                    RegionCommand.processFlags(CommandFlags.parseFlags(args), editedRegion);
                    this.regionModule.getRegionManager().addRegion(editedRegion);
                    sender.sendMessage(ChatColor.GRAY + "Successfully edited region!");
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No such region found");
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase("remove"))
        {
            if (args.length == 2)
            {
                final Region oldRegion = this.regionModule.getRegionManager().getRegion(args[1]);
                if (oldRegion != null)
                {
                    this.regionModule.getRegionManager().removeRegion(oldRegion);
                    sender.sendMessage(ChatColor.GRAY + "Successfully removed region " + ChatColor.GREEN + oldRegion.getRegionName());
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No such region found");
                    return true;
                }
            }
        }
        if (args[0].equalsIgnoreCase("regions"))
        {
            sender.sendMessage(ChatColor.GREEN + "Active regions");
            sender.sendMessage(ChatColor.GRAY + "-----------------------");
            for (Region region : this.regionModule.getRegionManager().getActiveRegions())
            {
                sender.sendMessage(region.toColoredString());
                sender.sendMessage(ChatColor.GRAY + "-----------------------");
            }
            return true;
        }

        return false;
    }

    private void printHelp(final CommandSender sender)
    {
        sender.sendMessage(ChatColor.GRAY + "------------" + ChatColor.GREEN + " Command Syntax " + ChatColor.GRAY + "------------");
        sender.sendMessage(ChatColor.GREEN + "To create a new region syntax is: /vgregion create [name] [x1] [z1] [x2] [z2] <-Flags>");
        sender.sendMessage(ChatColor.GREEN + "To create a new global region syntax is: /vgregion create [name] global <-Flags>");
        sender.sendMessage(ChatColor.GREEN + "To remove a region syntax is: /vgregion remove [name]");
        sender.sendMessage(ChatColor.GREEN + "To edit a region flags syntax is: /vgregion edit [name] <-Flags>");
        sender.sendMessage(ChatColor.GREEN + "Proper syntax for boolean flags are: -[flag]:[True|False]");
        sender.sendMessage(ChatColor.GREEN + "Proper syntax for list flags are: -[flag]:[id1,id2,id3...]");
        sender.sendMessage(ChatColor.GRAY + "------------" + ChatColor.GREEN + " Region Flags " + ChatColor.GRAY + "------------");
        for (CommandFlags flag : CommandFlags.values())
        {
            sender.sendMessage(ChatColor.GREEN + flag.toString() + ChatColor.GRAY + ": " + ChatColor.GREEN + flag.getCommandFlag());
        }
    }

    private void createRegion(final CommandSender sender, final String[] args)
    {
        final Player player;
        if ((sender instanceof Player))
        {
            player = (Player) sender;
        }
        else
        {
            sender.sendMessage("Command must be sent from a player");
            return;
        }

        if (args.length >= 3)
        {
            final String regionName = args[1];
            boolean isGlobal = false;
            Location pointOne = null;
            Location pointTwo = null;
            final World regionWorld = player.getWorld();
            if (args[2].equalsIgnoreCase("global"))
            {
                isGlobal = true;
            }

            if (!isGlobal)
            {
                if (args.length >= 6)
                {
                    int x1 = 0, z1 = 0, x2 = 0, z2 = 0;
                    try
                    {
                        x1 = Integer.parseInt(args[2]);
                        z1 = Integer.parseInt(args[3]);
                        x2 = Integer.parseInt(args[4]);
                        z2 = Integer.parseInt(args[5]);
                    }
                    catch (final NumberFormatException e)
                    {
                        sender.sendMessage("Error in  parsing arguments: invalid syntax");
                        sender.sendMessage(e.getMessage());
                    }
                    pointOne = new Location(regionWorld, x1, 0, z1);
                    pointTwo = new Location(regionWorld, x2, regionWorld.getMaxHeight(), z2);
                }
                else
                {
                    sender.sendMessage("Improper number of arguments for a nonglobal region");
                    return;
                }
            }
            final Map<CommandFlags, String> flags = CommandFlags.parseFlags(args); //Flag and state in a map
            final Region newRegion = new Region(regionWorld.getName(), pointOne, pointTwo, regionName);
            RegionCommand.processFlags(flags, newRegion);
            this.regionModule.getRegionManager().addRegion(newRegion);
            sender.sendMessage(newRegion.toColoredString());
        }
        else
        {
            sender.sendMessage("No arguments defined, invalid syntax");
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        if (args.length == 0)
        {
            return Arrays.asList(RegionCommand.subcommands);
        }
        List<String> matches = new ArrayList<>();
        if (args.length == 1)
        {
            for (String subcomm : RegionCommand.subcommands)
            {
                if (subcomm.startsWith(args[0].toLowerCase()))
                {
                    matches.add(subcomm);
                }
            }
        }
        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit"))
            {
                for (String regionName : this.regionModule.getRegionManager().getRegionNames())
                {
                    if (regionName.startsWith(args[1]))
                    {
                        matches.add(regionName);
                    }
                }
            }
        }
        Collections.sort(matches);
        return matches;
    }
}
