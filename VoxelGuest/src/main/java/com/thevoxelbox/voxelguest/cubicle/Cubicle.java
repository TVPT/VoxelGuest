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

package com.thevoxelbox.voxelguest.cubicle;

import com.patrickanker.lib.permissions.PermissionsHandler;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.CubicleModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
public class Cubicle {// {"id":0,"locked"="true","owner"="system.cube"}

    private int id;
    private boolean locked = true;
    private String owner = "system.cube";
    private String name = null;
    private int x;
    private int z;
    private Loc tpLoc = null;
    private String tpMessage = ChatColor.GOLD + "Woosh!";
    private String[] owners = null;

    public Cubicle()
    {
    }

    public Cubicle(Player player, int cubeX, int cubeZ)
    {
        this.owner = player.getName();
        this.x = cubeX;
        this.z = cubeZ;
    }

    public void setID(int i)
    {
        id = i;
    }

    public int getX()
    {
        return x;
    }

    public int getZ()
    {
        return z;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void lock()
    {
        locked = true;
    }

    public void unLock()
    {
        locked = false;
    }

    public boolean hasName()
    {
        return name != null && !name.equals("");
    }

    public void setName(String alias)
    {
        name = alias;
    }

    public String getName()
    {
        return name;
    }

    public void setOwner(String oname)
    {
        owner = oname;
    }

    public String getOwner()
    {
        return owner;
    }

    public void addOwner(String oname)
    {
        if (owners == null || owners.length == 0) {
            owners = new String[]{oname};
        } else {
            if (!hasOwner(oname)) {
                String[] temp = new String[owners.length + 1];
                System.arraycopy(owners, 0, temp, 0, owners.length);
                temp[owners.length] = oname;
                owners = temp;
            }
        }
    }

    public void removeOwner(String oname)
    {
        if (hasOwner(oname)) {
            String[] temp = new String[owners.length - 1];
            for (int i = 0; i < owners.length; i++) {
                if (owners[i].equals(oname)) {
                    for (int j = i + 1; j < owners.length; j++) {
                        temp[j - 1] = owners[j];
                    }
                    break;
                } else {
                    temp[i] = owners[i];
                }
            }
            owners = temp;
        }
    }

    public void flushOwners()
    {
        if (owners == null || owners.length == 0) {
            return;
        }
        
        PermissionsHandler ph = PermissionsManager.getHandler();
        for (String str : owners) {
            ph.removePermission(str, getPermissionString());
        }
        owners = null;
    }

    public boolean hasOwner(String oname)
    {
        if (owners == null) {
            return false;
        } else {
            for (String str : owners) {
                if (str.equals(oname)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isOwnedBy(Player user)
    {
        return user.getName().equals(owner);
    }

    public String getKey()
    {
        return x + "_" + z;
    }

    public String getPermissionString()
    {
        return "voxelguest.cubicle.interact." + id;
    }

    public void teleport(Player p, World cubeWorld)
    {
        if (tpLoc == null) {
            tpLoc = new Loc();
            tpLoc.x = ((x < 0 ? (x + 1) : x) * CubicleModule.CUBICLE_SIZE) + ((Math.signum(x) == 0 ? 1 : Math.signum(x)) * (CubicleModule.CUBICLE_SIZE / 2));
            tpLoc.y = 256;
            tpLoc.z = ((z < 0 ? (z + 1) : z) * CubicleModule.CUBICLE_SIZE) + ((Math.signum(z) == 0 ? 1 : Math.signum(z)) * (CubicleModule.CUBICLE_SIZE / 2));
        }
        tpLoc.teleport(p, cubeWorld);
        p.sendMessage(tpMessage);
    }

    public void setTpLoc(Location loc)
    {
        tpLoc.x = loc.getX();
        tpLoc.y = loc.getY();
        tpLoc.z = loc.getZ();
    }

    public Loc getTpLoc()
    {
        return tpLoc;
    }

    public void setTpMessage(String message)
    {
        if (message != null && !message.equals("")) {
            tpMessage = ChatColor.BLUE + message;
        }
    }

    public void info(Player user)
    {
        user.sendMessage(ChatColor.GOLD + "Cubicle #" + ChatColor.GREEN + id + ChatColor.GOLD + " x:" + ChatColor.RED + x + ChatColor.GOLD + " z:" + ChatColor.RED + z);
        user.sendMessage(ChatColor.GOLD + "Owned by " + ChatColor.AQUA + owner + ChatColor.GOLD + " and is " + ((locked) ? (ChatColor.RED + "locked") : (ChatColor.GREEN + "unlocked")));
        if (hasName()) {
            user.sendMessage(ChatColor.GOLD + "Alias name set to \"" + ChatColor.AQUA + name + ChatColor.GOLD + "\"");
        } else {
            user.sendMessage(ChatColor.GOLD + "No alias name is set");
        }
        user.sendMessage(ChatColor.GOLD + "Warp message is " + ChatColor.AQUA + "\"" + tpMessage + ChatColor.AQUA + "\"");
        if (owners == null || owners.length == 0) {
            user.sendMessage(ChatColor.GOLD + "No co-owners are assigned");
        } else {
            String omsg = ChatColor.AQUA + owners[0];
            for (int i = 1; i < owners.length; i++) {
                omsg += ChatColor.RED + ", " + ChatColor.AQUA + owners[i];
            }
            user.sendMessage(ChatColor.GOLD + "Co-owners are: " + omsg);
        }
    }

    @Override
    public String toString()
    {
        return ChatColor.AQUA + "(" + ChatColor.DARK_GREEN + "#" + ChatColor.RED + id + ChatColor.DARK_GREEN + " owner:" + ChatColor.RED + owner + ChatColor.AQUA + ")";
    }

    public static class Loc {

        public double x;
        public double y;
        public double z;

        public void teleport(Player p, World w)
        {
            p.teleport(new Location(w, x, y, z));
        }
    }
}
