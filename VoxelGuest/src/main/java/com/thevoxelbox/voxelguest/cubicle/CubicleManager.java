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

import com.google.gson.Gson;
import com.patrickanker.lib.permissions.PermissionsHandler;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.CubicleModule;
import com.thevoxelbox.voxelguest.VoxelGuest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
public class CubicleManager {

    private HashMap<String, Cubicle> cubicles = new HashMap<String, Cubicle>();
    private HashMap<String, Cubicle> aliasNames = new HashMap<String, Cubicle>();
    private HashMap<String, Cubicle> playerCubes = new HashMap<String, Cubicle>();

    public CubicleManager()
    {
        File f = new File("plugins/VoxelGuest/cubicles/data.json");

        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CubicleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Scanner snr = new Scanner(f);

            Gson gson = new Gson();
            while (snr.hasNext()) {
                try {
                    Cubicle cb = gson.fromJson(snr.nextLine(), Cubicle.class);
                    if (cb != null) {
                        cubicles.put(cb.getKey(), cb);
                        if (cb.hasName()) {
                            aliasNames.put(cb.getName(), cb);
                        }
                        if (!cb.getOwner().equals("system.cube")) {
                            playerCubes.put(cb.getOwner(), cb);
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger(CubicleManager.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CubicleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean hasCubicle(Location loc)
    {
        return cubicles.containsKey(getKey(loc));
    }

    public Cubicle getCubicle(Location loc)
    {
        return cubicles.get(getKey(loc));
    }

    public boolean hasCubicle(Player user)
    {
        return playerCubes.containsKey(user.getName());
    }

    public Cubicle getCubicle(Player user)
    {
        return playerCubes.get(user.getName());
    }

    public boolean hasCubicle(String userName)
    {
        return playerCubes.containsKey(userName);
    }

    public Cubicle getCubicle(String userName)
    {
        return playerCubes.get(userName);
    }

    public boolean hasAlias(String alias)
    {
        return aliasNames.containsKey(alias);
    }

    public Cubicle getAlias(String alias)
    {
        return aliasNames.get(alias);
    }

    public boolean hasCubicle(int x, int z)
    {
        return cubicles.containsKey(getKey(x, z));
    }

    public Cubicle getCubicle(int x, int z)
    {
        return cubicles.get(getKey(x, z));
    }

    public void createCubicle(Player user, World cubeWorld, boolean system)
    {
        if (hasCubicle(user) && !system) {
            user.sendMessage(ChatColor.RED + "You cannot own more than one cubicle!");
            return;
        }

        Cubicle cb = getFirstSpiral();
        cubicles.put(cb.getKey(), cb);
        if (!system) {
            cb.setOwner(user.getName());
            playerCubes.put(cb.getOwner(), cb);
            PermissionsManager.getHandler().givePermission(user.getName(), cb.getPermissionString());
            cb.teleport(user, cubeWorld);
            user.sendMessage(ChatColor.GOLD + "Your cubicle is ready to use!");
        } else {
            cb.teleport(user, cubeWorld);
            user.sendMessage(ChatColor.GOLD + "System cubicle created!");
        }
        saveCubicles();
    }

    public void removeCubicle(Player user, Cubicle cb)
    {
        cb.flushOwners();
        if (cb.isOwnedBy(user)) {
            PermissionsManager.getHandler().removePermission(user.getName(), cb.getPermissionString());
        }

        if (cb.hasName()) {
            aliasNames.remove(cb.getName());
        }

        playerCubes.remove(cb.getOwner());
        cubicles.remove(cb.getKey());
        saveCubicles();
        user.sendMessage(ChatColor.GOLD + "Cubicle " + cb + ChatColor.GOLD + " has been removed!");
    }

    public Cubicle getFirstSpiral()
    {
        int x = 0;
        int z = 0;

        int travel = 1;
        int steps = 0;
        int num = 0;
        boolean secondTurn = false;
        Direction dir = Direction.LEFT;

        while (cubicles.containsKey(x + "_" + z)) {
            if (steps < travel) {
                steps++;
                num++;
                switch (dir) {
                    case LEFT:
                        x--;
                        break;
                    case DOWN:
                        z--;
                        break;
                    case RIGHT:
                        x++;
                        break;
                    case UP:
                        z++;
                        break;
                }
            }

            if (steps == travel) {
                steps = 0;
                if (secondTurn) {
                    secondTurn = false;
                    travel++;
                    dir = dir.next();
                } else {
                    secondTurn = true;
                    dir = dir.next();
                }
            }
        }
        return createNew(x, z, num);
    }

    public Cubicle getSpiralNumber(int number)
    {
        if (number < 0) {
            return null;
        }
        int x = 0;
        int z = 0;

        int travel = 1;
        int steps = 0;
        int num = 0;
        boolean secondTurn = false;
        Direction dir = Direction.LEFT;

        while (num < number) {
            if (steps < travel) {
                steps++;
                num++;
                switch (dir) {
                    case LEFT:
                        x--;
                        break;
                    case DOWN:
                        z--;
                        break;
                    case RIGHT:
                        x++;
                        break;
                    case UP:
                        z++;
                        break;
                }
            }

            if (steps == travel) {
                steps = 0;
                if (secondTurn) {
                    secondTurn = false;
                    travel++;
                    dir = dir.next();
                } else {
                    secondTurn = true;
                    dir = dir.next();
                }
            }
        }
        return cubicles.get(getKey(x, z));
    }

    public void addAlias(Cubicle cb, String alias, Player cmdSender)
    {
        if (aliasNames.containsKey(alias)) {
            cmdSender.sendMessage(ChatColor.RED + "A Cubicle with that alias already exists!");
        } else {
            if (cb.hasName()) {
                aliasNames.remove(cb.getName());
            }
            cb.setName(alias);
            aliasNames.put(alias, cb);
            cmdSender.sendMessage(new String[]{ChatColor.GREEN + "Alias name \"" + ChatColor.GRAY + alias + ChatColor.GREEN + "\" has been registered for",
                        ChatColor.GREEN + "Cubicle " + ChatColor.GOLD + cb.getKey() + ChatColor.GREEN + " owned by: " + ChatColor.BLUE + cb.getOwner()});
        }
    }

    public void setOwner(Cubicle cb, Player user, String owner)
    {
        PermissionsHandler ph = PermissionsManager.getHandler();
        cb.flushOwners();
        playerCubes.remove(cb.getOwner());
        ph.removePermission(cb.getOwner(), cb.getPermissionString());
        cb.setOwner(owner);
        ph.givePermission(owner, cb.getPermissionString());
        playerCubes.put(owner, cb);
        user.sendMessage(ChatColor.GOLD + "The owner of the Cubicle " + cb + ChatColor.GOLD + " has been updated.");
    }

    public void regenerateCubicle(Cubicle cb, World cubeWorld, Player user)
    {
        RegenerationRunner rr = new RegenerationRunner(cb, user, cubeWorld);
        rr.setID(Bukkit.getScheduler().scheduleSyncRepeatingTask(VoxelGuest.getInstance(), rr, CubicleModule.CUBICLE_REGEN_TIME, CubicleModule.CUBICLE_REGEN_TIME));
    }

    public void saveCubicles()
    {
        File f = new File("plugins/VoxelGuest/cubicles/data.json");

        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CubicleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            PrintWriter pw = new PrintWriter(f);

            Gson gson = new Gson();
            for (Cubicle cb : cubicles.values()) {
                pw.println(gson.toJson(cb, Cubicle.class));
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CubicleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Cubicle createNew(int x, int z, int id)
    {
        Cubicle cube = new Cubicle();
        cube.setID(id);
        cube.setX(x);
        cube.setZ(z);
        cubicles.put(getKey(x, z), cube);
        return cube;
    }

    private String getKey(int x, int z)
    {
        return x + "_" + z;
    }

    private String getKey(Location loc)
    {
        return NumberConversions.floor(loc.getX() / CubicleModule.CUBICLE_SIZE)
                + "_"
                + NumberConversions.floor(loc.getZ() / CubicleModule.CUBICLE_SIZE);
    }

    private enum Direction {

        LEFT(0, 1),
        DOWN(1, 2),
        RIGHT(2, 3),
        UP(3, 0);
        private byte dir;
        private byte nextDir;

        private Direction(int d, int n)
        {
            dir = (byte) d;
            nextDir = (byte) n;
        }

        public Direction next()
        {
            return values()[nextDir];
        }
    }
}
