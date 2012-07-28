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
package com.thevoxelbox.voxelguest.players;

import com.thevoxelbox.voxelguest.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.util.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class GroupManager {

    protected static Map<String, Configuration> groupMap = new TreeMap<String, Configuration>();
    protected static HashMap<String, List<String>> playerMap = new HashMap<String, List<String>>();
    // Basic group defaults
    private final String defaultGroupName = "Group";
    private final String defaultGroupID = "§fG";
    private final Configuration defaultConfig = new Configuration(defaultGroupName, "/groups");
    private final Configuration config = new Configuration("config","/groups");  

    public GroupManager()
    {
        File dir = new File("plugins/VoxelGuest/groups/");
        File mainFile = new File(dir, "config.properties");
        
        if (!dir.isDirectory()) {
            dir.mkdirs();
            return;
        }
        //create main file
        if (!mainFile.exists()) {
        	try {
				mainFile.createNewFile();
			} catch (IOException e) {}
        }
        
        
        config.load();
        //now to auto add the groups and id's.... first letter of group in CAPITALS????
        //make a list of groups first.
        
     
        
        //set who-order
        if (config.getString("who-order") == null)
        config.setString("who-order", "A-C-S-L-M-G-V");
        
        groupMap.put("config", config);
        config.save();

        defaultConfig.setString("group-id", defaultGroupID);
    }

    public Configuration getGroupConfiguration(String name)
    {
        if (groupMap.containsKey(name)) {
            return groupMap.get(name);
        }

        Configuration config = new Configuration(name, "/groups");
        setGroupConfiguration(name, config);
        return config;
    }
    
    public String getGroupId(String groupName) {
    	String id = config.getString(groupName);
    	return id;
    }

    public void setGroupConfiguration(String name, Configuration config)
    {
        groupMap.put(name, config);
    }

    public String findGroup(String key, Object value) throws GroupNotFoundException
    {
        for (Map.Entry<String, Configuration> entry : groupMap.entrySet()) {
            Configuration config = entry.getValue();

            if (config.getEntry(key) != null && value.equals(config.getEntry(key))) {
                return entry.getKey();
            }
        }

        throw new GroupNotFoundException("No group found for key-value pair");
    }

    public String findGroup(String str)
    {
        for (Map.Entry<String, Configuration> entry : groupMap.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(str.toLowerCase())) {
                return entry.getKey();
            }
        }

        return null;
    }

    public void saveGroupConfigurations()
    {
        for (Map.Entry<String, Configuration> entry : groupMap.entrySet()) {
            saveGroupConfiguration(entry.getKey());
        }
    }

    public void saveGroupConfiguration(String name)
    {
        Configuration config = groupMap.get(name);
        config.save();
    }

    public void verifyPlayerGroupExistence(Player p)
    {
        String[] groups = PermissionsManager.getHandler().getGroups(p.getName());

        if (groups != null && groups.length > 0) {
            String group = groups[0];

            if (!groupMap.containsKey(group)) {
                groupMap.put(group, defaultConfig);
                getGroupConfiguration(group).assignTarget(group);
            }

        } else {
            groupMap.put(defaultGroupName, defaultConfig);
        }
    }

    public List<String> getPlayerListForGroup(String group)
    {
        return playerMap.get(group);
    }

    public List<String> getRegisteredGroups()
    {
        List<String> l = new ArrayList<String>();
        HashMap<String, Object> map = config.getAllEntries();
        map.remove("who-order");        
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!l.contains(entry.getKey())) {
                l.add(entry.getKey());
            }
        }

        return l;
    }

    public Configuration getDefaultConfiguration()
    {
        return defaultConfig;
    }
}
