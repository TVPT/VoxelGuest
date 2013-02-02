package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.regions.command.RegionCommand;
import com.thevoxelbox.voxelguest.modules.regions.listener.BlockEventListener;
import com.thevoxelbox.voxelguest.modules.regions.listener.PlayerEventListener;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Butters
 */
public class RegionModule extends GuestModule
{
	private List<Region> regions = new ArrayList<>();
	private BlockEventListener blockEventListener;
	private PlayerEventListener playerEventListener;
	private RegionCommand regionCommand;

	public RegionModule()
	{
		setName("Region Module");

		Persistence.getInstance().registerPersistentClass(Region.class);

		blockEventListener = new BlockEventListener(this);
		playerEventListener = new PlayerEventListener(this);
		regionCommand = new RegionCommand(this);
	}

	@Override
	public final void onEnable()
	{



		regions.clear();
		List<Object> protoList = Persistence.getInstance().loadAll(Region.class);
		for (Object protoRegion : protoList)
		{
			regions.add((Region) protoRegion);
		}

		super.onEnable();
	}

	@Override
	public final void onDisable()
	{
		regions.clear();
		List<Object> protoList = new ArrayList<>();
		for (Region region : regions)
		{
			protoList.add(region);
		}

		super.onDisable();
	}

	@Override
	public String getConfigFileName()
	{
		return "region";
	}

	@Override
	public Object getConfiguration()
	{
		return null;
	}

	@Override
	public final HashSet<Listener> getListeners()
	{
		final HashSet<Listener> listeners = new HashSet<>();
		listeners.add(blockEventListener);
		listeners.add(playerEventListener);

		return listeners;
	}

	@Override
	public HashMap<String, CommandExecutor> getCommandMappings()
	{
		HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
		commandMappings.put("vgregion", regionCommand);
		return commandMappings;
	}

	public boolean addRegion(Region region)
	{
		if (region != null)
		{
			regions.add(region);
			Bukkit.getLogger().info("Created region: " + region.getRegionName());
			return true;
		}
		return false;
	}

	public final Region getRegionAtLocation(final Location regionLocation)
	{
		for (Region region : regions)
		{
			if (region.isLocationInRegion(regionLocation))
			{
				return region;
			}
		}
		return null;
	}

}
