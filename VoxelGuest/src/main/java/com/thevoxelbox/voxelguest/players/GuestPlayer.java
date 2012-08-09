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

import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.JavaPropertiesFileManager;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.thevoxelbox.voxelguest.VoxelGuest;

public class GuestPlayer {

    protected Player p;
    protected Map<String, HashMap<String, Object>> storage = new HashMap<String, HashMap<String, Object>>();
    protected String[] groups;

    public GuestPlayer(Player player)
    {
        this.p = player;

        Map<String, Object> data = JavaPropertiesFileManager.load(p.getName(), "/VoxelGuest/players");
        storage.put(VoxelGuest.getPluginId(VoxelGuest.getInstance()), ((HashMap<String, Object>) data));

        groups = PermissionsManager.getHandler().getGroups(p.getName());
    }

    public Player getPlayer()
    {
        return this.p;
    }

    // -- Storage Stuff -- //
    public void store(String id, String k, Object v)
    {
        if (!storage.containsKey(id)) {
            VoxelGuest.log("Tried to store to unregistered plugin storage for player \"" + p.getName() + "\"", 1);
            return;
        }

        if (storage.get(k) != null) {
            HashMap<String, Object> _map = storage.get(k);
            _map.put(k, v);
            storage.put(id, _map);
        } else {
            HashMap<String, Object> _map = new HashMap<String, Object>();
            _map.put(k, v);
            storage.put(id, _map);
        }
    }

    public Object get(String id, String k)
    {
        if (!storage.containsKey(id)) {
            VoxelGuest.log("Tried to retrieve from unregistered plugin storage for player \"" + p.getName() + "\"", 1);
            return null;
        }

        if (storage.get(k) != null) {
            HashMap<String, Object> _map = storage.get(k);
            return _map.get(k);
        } else {
            return null;
        }
    }

    public void saveData(String pluginID)
    {
        HashMap<String, Object> map = storage.get(pluginID);

        if (map != null) {
            JavaPropertiesFileManager.save(p.getName(), map, "/VoxelGuest/players");
        }
    }

    public String[] getGroups()
    {
        return groups;
    }
}
