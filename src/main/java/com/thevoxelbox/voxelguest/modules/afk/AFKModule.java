package com.thevoxelbox.voxelguest.modules.afk;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

/**
 *
 * @author keto23
 */
public class AFKModule extends GuestModule 
{
    public AFKList afkList;
    private AFKCommand afkCommand;
    private AFKListener afkListener;
    
    public AFKModule() 
    {
	setName("AFK");
    }
    
    @Override
    public final void onEnable()
    {
	afkList = new AFKList();
	afkCommand = new AFKCommand(this);
	afkListener = new AFKListener(this);
	
	super.onEnable();
    }
    
    @Override
    public final void onDisable()
    {
	afkList.clear();
	
	super.onDisable();
    }
    
    @Override
    public Map<String, CommandExecutor> getCommandMappings()
    {
	final HashMap<String, CommandExecutor> commandMappings = new HashMap<>();
	commandMappings.put("afk", afkCommand);
	
	return commandMappings;
    }
    
    @Override
    public final Set<Listener> getListeners()
    {
	final HashSet<Listener> listeners = new HashSet<>();
	listeners.add(afkListener);

	return listeners;
    }
}
