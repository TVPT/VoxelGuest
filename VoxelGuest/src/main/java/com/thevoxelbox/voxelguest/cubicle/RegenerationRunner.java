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

import com.thevoxelbox.voxelguest.CubicleModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
public class RegenerationRunner implements Runnable {

    private int lowx;
    private int lowz;
    private int highx;
    private int highz;
    private int regenCount = 0;
    private int regenX;
    private int regenZ;
    private int maxChunk;
    private World cubeWorld;
    private Player user;
    private Cubicle cube;
    private int id;
    private boolean layerDone = false;

    public RegenerationRunner(Cubicle cb, Player cmdSender, World cWorld)
    {
        cubeWorld = cWorld;
        user = cmdSender;
        cube = cb;
        int otherx = ((cb.getX() < 0 ? (cb.getX() + 1) : cb.getX()) * CubicleModule.CUBICLE_SIZE) + (int) ((Math.signum(cb.getX()) == 0 ? 1 : Math.signum(cb.getX())) * (CubicleModule.CUBICLE_SIZE - (Math.signum(cb.getX()) >= 0 ? 16 : 0)));
        int otherz = ((cb.getZ() < 0 ? (cb.getZ() + 1) : cb.getZ()) * CubicleModule.CUBICLE_SIZE) + (int) ((Math.signum(cb.getZ()) == 0 ? 1 : Math.signum(cb.getZ())) * (CubicleModule.CUBICLE_SIZE - (Math.signum(cb.getZ()) >= 0 ? 16 : 0)));
        Chunk ch1 = cubeWorld.getChunkAt(cubeWorld.getBlockAt(otherx, 64, otherz));
        Chunk ch2 = cubeWorld.getChunkAt(cubeWorld.getBlockAt(
                ((cb.getX() < 0 ? (cb.getX() + 1) : cb.getX()) * CubicleModule.CUBICLE_SIZE) + (Math.signum(cb.getX()) < 0 ? -16 : 0),
                64,
                ((cb.getZ() < 0 ? (cb.getZ() + 1) : cb.getZ()) * CubicleModule.CUBICLE_SIZE) + (Math.signum(cb.getZ()) < 0 ? -16 : 0)));
        lowx = ch1.getX() < ch2.getX() ? ch1.getX() : ch2.getX();
        lowz = ch1.getZ() < ch2.getZ() ? ch1.getZ() : ch2.getZ();
        highx = ch1.getX() > ch2.getX() ? ch1.getX() : ch2.getX();
        highz = ch1.getZ() > ch2.getZ() ? ch1.getZ() : ch2.getZ();
        regenX = lowx;
        regenZ = lowz;
        maxChunk = (CubicleModule.CUBICLE_SIZE / 16) * (CubicleModule.CUBICLE_SIZE / 16);
        user.sendMessage(ChatColor.GOLD + "Regeneration of Cubicle " + cube + ChatColor.GOLD + " has begun!");
        cubeWorld.loadChunk(regenX, regenZ, true);
        cubeWorld.regenerateChunk(regenX, regenZ);
        cubeWorld.refreshChunk(regenX, regenZ);
    }

    public void setID(int myId)
    {
        id = myId;
    }

    @Override
    public void run()
    {
        if (regenX < highx || regenZ < highz) {
            if (layerDone) {
                regenX = lowx;
                regenZ++;
                layerDone = false;
            } else {
                regenX++;
            }

            if (regenX == highx) {
                layerDone = true;
            }

            regenCount++;
            cubeWorld.loadChunk(regenX, regenZ, true);
            cubeWorld.regenerateChunk(regenX, regenZ);
            cubeWorld.refreshChunk(regenX, regenZ);

            if (regenCount % (maxChunk / 4) == 0) {
                switch ((int) regenCount / (maxChunk / 4)) {
                    case 1:
                        percent(25);
                        break;

                    case 2:
                        percent(50);
                        break;

                    case 3:
                        percent(75);
                        break;

                    case 4:
                        percent(100);
                        break;
                }
            }
        } else {
            user.sendMessage(ChatColor.GOLD + "Regeneration of Cubicle " + cube + ChatColor.GOLD + " has finished!");
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    private void percent(int percent)
    {
        user.sendMessage(ChatColor.GREEN + "Regeneration in progress ... " + ChatColor.GOLD + percent + ChatColor.GREEN + "% complete.");
    }
}
