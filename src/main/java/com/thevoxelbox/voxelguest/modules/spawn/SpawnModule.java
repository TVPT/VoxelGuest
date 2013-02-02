package com.thevoxelbox.voxelguest.modules.spawn;

import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationGetter;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationSetter;
import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.spawn.command.SetSpawnCommand;
import com.thevoxelbox.voxelguest.modules.spawn.command.SpawnCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author keto23
 */
public class SpawnModule extends GuestModule
{
	private SpawnCommand spawnCommand;
	private SetSpawnCommand setSpawnCommand;

	private boolean randomMessagesEnabled = true;
	private String randomMessages = "&aYour butt hurts,&3Woosh!,Weeee!,&dBuy Now! Ganz Ganz!,&bHuzzah!,&2*Blip*,&e*Pop*,&7Eat your veggies,&aShake-Shake-Shake";
	private Location spawnLocation = null;

	public SpawnModule()
	{
		this.setName("Spawn");
	}

	@Override
	public final void onEnable()
	{
		spawnCommand = new SpawnCommand(this);
		setSpawnCommand = new SetSpawnCommand(this);

		super.onEnable();
	}

	@Override
	public Object getConfiguration()
	{
		return this;
	}

	@Override
	public String getConfigFileName()
	{
		return "spawn";
	}

	@Override
	public Map<String, CommandExecutor> getCommandMappings()
	{
		final HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
		commandMappings.put("spawn", spawnCommand);
		commandMappings.put("setspawn", setSpawnCommand);

		return commandMappings;
	}

	@ConfigurationGetter("location")
	public final Object getSpawnLocation()
	{
		return spawnLocation;
	}

	@ConfigurationSetter("location")
	public final void setSpawnLocation(final Location location)
	{
		this.spawnLocation = location;
	}

	@ConfigurationGetter("random-messages-enabled")
	public final boolean areRandomMessagesEnabled()
	{
		return randomMessagesEnabled;
	}

	@ConfigurationSetter("random-messages-enabled")
	public final void setRandomMessagesEnabled(final boolean enabled)
	{
		this.randomMessagesEnabled = enabled;
	}

	@ConfigurationGetter("random-messages")
	public final String getRandomMessages()
	{
		return randomMessages;
	}

	@ConfigurationSetter("random-messages")
	public final void setRandomMessages(final String messages)
	{
		this.randomMessages = messages;
	}
}
