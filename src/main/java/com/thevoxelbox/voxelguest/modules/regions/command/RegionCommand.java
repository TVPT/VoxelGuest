package com.thevoxelbox.voxelguest.modules.regions.command;

import com.thevoxelbox.voxelguest.modules.regions.Region;
import com.thevoxelbox.voxelguest.modules.regions.RegionModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Butters
 */
public class RegionCommand implements CommandExecutor
{

	private RegionModule regionModule;

	public RegionCommand(RegionModule regionModule)
	{
		this.regionModule = regionModule;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmnd, final String string, final String[] args)
	{
		if (args.length == 0)
		{
			cs.sendMessage("/vgregion <option>");
			cs.sendMessage("Ex: /vgregion help");
			return false;
		}

		if (args[0].equalsIgnoreCase("create"))
		{
			createRegion(cs, args);
			return true;
		}

		if (args[0].equalsIgnoreCase("help"))
		{
			printHelp(cs);
			return true;
		}

		if (args[0].equalsIgnoreCase("edit"))
		{
			//to-do
			return true;
		}

		if (args[0].equalsIgnoreCase("remove"))
		{
			//to-do
			return true;
		}

		return false;
	}

	private void printHelp(CommandSender cs)
	{
		//to-do
	}

	private void createRegion(final CommandSender cs, final String[] args)
	{
		if (args.length <= 9)
		{
			cs.sendMessage(ChatColor.RED + "Invalid paramaters - Use /vgregion <create> [point1x] [point1z] [point2x] [point2z] [build perm.] [region name] [world name | Not needed for players]");
			return;
		}

		String regionName = null;
		String buildPerm = null;
		World regionWorld = null;
		Integer point1X = null;
		Integer point1Z = null;
		Integer point2X = null;
		Integer point2Z = null;

		for (int index = 0; index < args.length; index++)
		{
			if (args[index].startsWith("P1X:"))
			{
				try
				{
					point1X = Integer.parseInt(args[index].replace("P1X:", ""));
				} catch (NumberFormatException ex)
				{
					cs.sendMessage(ChatColor.RED + "");
				}
			}
			else if (args[index].startsWith("P1Z:"))
			{
				try
				{
					point1Z = Integer.parseInt(args[index].replace("P1Z:", ""));
				} catch (NumberFormatException ex)
				{
					cs.sendMessage(ChatColor.RED + "");
				}
			}
			else if (args[index].startsWith("P2X:"))
			{
				try
				{
					point2X = Integer.parseInt(args[index].replace("P2X:", ""));
				} catch (NumberFormatException ex)
				{
					cs.sendMessage(ChatColor.RED + "");
				}
			}
			else if (args[index].startsWith("P2Z:"))
			{
				try
				{
					point2Z = Integer.parseInt(args[index].replace("P2Z:", ""));
				} catch (NumberFormatException ex)
				{
					cs.sendMessage(ChatColor.RED + "");
				}
			}
			else if (args[index].startsWith("RN:"))
			{
				regionName = args[index].replace("RN:", "");
			}
			else if (args[index].startsWith("BP:"))
			{
				buildPerm = args[index].replace("BP:", "");
			}
			else if (args[index].startsWith("W:"))
			{
				regionWorld = Bukkit.getWorld(args[index].replace("W:", ""));
			}
		}

		if (regionWorld == null)
		{
			if (cs instanceof Player)
			{
				Player player = (Player) cs;
				regionWorld = player.getWorld();
			}
			else
			{
				cs.sendMessage(ChatColor.RED + "No region world provided");
				return;
			}
		}

		if (regionName == null)
		{
			cs.sendMessage(ChatColor.RED + "No region name provided");
			return;
		}

		if (buildPerm == null)
		{
			buildPerm = "";
		}

		//For open worlds that have no boundry
		if (regionWorld != null && (point1X == null && point1Z == null && point2X == null && point2Z == null))
		{
			Region region = new Region(regionWorld.getName(), null, null, regionName, buildPerm);
			boolean regionMade = regionModule.addRegion(region);
			if (regionMade)
			{
				cs.sendMessage(ChatColor.GREEN + "Region created!");
				printRegionInfo(cs, region);
			}
			else
			{
				cs.sendMessage(ChatColor.RED + "Region failed to create");
			}
			return;
		}

		//Since not open world make sure all points are valid
		if (point1X == null || point1Z == null || point2X == null || point2Z == null)
		{
			cs.sendMessage(ChatColor.RED + "Invalid region points");
			return;
		}

		Location pointOne = new Location(regionWorld, point1X.intValue(), 0, point1Z);
		Location pointTwo = new Location(regionWorld, point2X.intValue(), regionWorld.getMaxHeight(), point2Z.intValue());
		Region region = new Region(regionWorld.getName(), pointOne, pointTwo, regionName, buildPerm);

		boolean regionMade = regionModule.addRegion(region);
		if (regionMade)
		{
			cs.sendMessage(ChatColor.GREEN + "Region created!");
			printRegionInfo(cs, region);
		}
		else
		{
			cs.sendMessage(ChatColor.RED + "Region failed to be created");
		}
	}

	private void printRegionInfo(CommandSender cs, Region region)
	{
		cs.sendMessage(ChatColor.GRAY + "| Region info for: " + ChatColor.BLACK + region.getRegionName() + ChatColor.GRAY + " |");
		cs.sendMessage(ChatColor.GRAY + "World: " + ChatColor.BLACK + region.getPointOne().getWorld().getName());
		cs.sendMessage(ChatColor.GRAY + "Point one: " + ChatColor.BLACK + region.getPointOne().getX() + ", " + region.getPointOne().getZ());
		cs.sendMessage(ChatColor.GRAY + "Point two: " + ChatColor.BLACK + region.getPointTwo().getX() + ", " + region.getPointTwo().getZ());
	}

}
