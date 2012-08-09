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

import com.patrickanker.lib.config.PropertyConfiguration;
import com.patrickanker.lib.permissions.PermissionsManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.entity.Player;

public class GroupManager {

    protected static Map<String, PropertyConfiguration> groupMap = new TreeMap<String, PropertyConfiguration>();
    protected static HashMap<String, List<String>> playerMap = new HashMap<String, List<String>>();
    // Basic group defaults
    private final String defaultGroupName = "Group";
    private final String defaultGroupID = "§fG";
    private final PropertyConfiguration defaultConfig = new PropertyConfiguration(defaultGroupName, "/VoxelGuest/groups");

    public GroupManager()
    {
        File dir = new File("plugins/VoxelGuest/groups/");

        if (!dir.isDirectory()) {
            dir.mkdirs();
            return;
        }

        String[] files = dir.list();

        for (String file : files) {
            if (file.endsWith(".properties")) {
                String f = file.replace(".properties", "");
                PropertyConfiguration config = new PropertyConfiguration(f, "/VoxelGuest/groups");
                groupMap.put(f, config);
            }
        }

        defaultConfig.setString("group-id", defaultGroupID);
    }

    public PropertyConfiguration getGroupConfiguration(String name)
    {
        if (groupMap.containsKey(name)) {
            return groupMap.get(name);
        }

        PropertyConfiguration config = new PropertyConfiguration(name, "/VoxelGuest/groups");
        setGroupConfiguration(name, config);
        return config;
    }

    public void setGroupConfiguration(String name, PropertyConfiguration config)
    {
        groupMap.put(name, config);
    }

    public String findGroup(String key, Object value) throws GroupNotFoundException
    {
        for (Map.Entry<String, PropertyConfiguration> entry : groupMap.entrySet()) {
            PropertyConfiguration config = entry.getValue();

            if (config.getEntry(key) != null && value.equals(config.getEntry(key))) {
                return entry.getKey();
            }
        }

        throw new GroupNotFoundException("No group found for key-value pair");
    }

    public String findGroup(String str)
    {
        for (Map.Entry<String, PropertyConfiguration> entry : groupMap.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(str.toLowerCase())) {
                return entry.getKey();
            }
        }

        return null;
    }

    public void saveGroupConfigurations()
    {
        for (Map.Entry<String, PropertyConfiguration> entry : groupMap.entrySet()) {
            saveGroupConfiguration(entry.getKey());
        }
    }

    public void saveGroupConfiguration(String name)
    {
        PropertyConfiguration config = groupMap.get(name);
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

        for (Map.Entry<String, PropertyConfiguration> entry : groupMap.entrySet()) {
            if (!l.contains(entry.getKey())) {
                l.add(entry.getKey());
            }
        }

        return l;
    }

    public PropertyConfiguration getDefaultConfiguration()
    {
        return defaultConfig;
    }
}
