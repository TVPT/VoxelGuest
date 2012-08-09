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

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
public class CubicleGenerator extends ChunkGenerator {

    protected int chunkSize;
    protected int grassLevel;
    protected int wallHeigth;
    protected byte fillerID;
    protected byte topID;
    protected byte colour1;
    protected byte colour2;
    protected boolean makeBedrock;

    public CubicleGenerator(int size, int grass, int wall, byte filler, byte topLayer, byte c1, byte c2, boolean bedrock)
    {
        chunkSize = size;
        grassLevel = grass;
        wallHeigth = wall;
        if (wallHeigth < grassLevel) {
            wallHeigth = grassLevel + 1;
        }
        fillerID = filler;
        topID = topLayer;
        colour1 = c1;
        colour2 = c2;
        makeBedrock = bedrock;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
    {
        byte[][] blocks = new byte[world.getMaxHeight() / 16][];
        for (int gy = 0; gy < grassLevel; gy++) {
            for (int gx = 0; gx < 16; gx++) {
                for (int gz = 0; gz < 16; gz++) {
                    setBlock(blocks, gx, gy, gz, fillerID);
                }
            }
        }
        for (int gx = 0; gx < 16; gx++) {
            for (int gz = 0; gz < 16; gz++) {
                setBlock(blocks, gx, grassLevel, gz, topID);
            }
        }

        if (x % chunkSize == 0) {
            if (z % 2 == 0) {
                for (int gy = 0; gy <= wallHeigth; gy++) {
                    for (int gz = 0; gz < 16; gz++) {
                        setBlock(blocks, 0, gy, gz, colour1);
                    }
                }
            } else {
                for (int gy = 0; gy <= wallHeigth; gy++) {
                    for (int gz = 0; gz < 16; gz++) {
                        setBlock(blocks, 0, gy, gz, colour2);
                    }
                }
            }
        }

        if (z % chunkSize == 0) {
            if (x % 2 == 0) {
                for (int gy = 0; gy <= wallHeigth; gy++) {
                    for (int gx = 0; gx < 16; gx++) {
                        setBlock(blocks, gx, gy, 0, colour1);
                    }
                }
            } else {
                for (int gy = 0; gy <= wallHeigth; gy++) {
                    for (int gx = 0; gx < 16; gx++) {
                        setBlock(blocks, gx, gy, 0, colour2);
                    }
                }
            }
        }

        if (makeBedrock) {
            for (int gx = 0; gx < 16; gx++) {
                for (int gz = 0; gz < 16; gz++) {
                    setBlock(blocks, gx, 0, gz, (byte) 7);
                }
            }
        }

        return blocks;
    }

    private void setBlock(byte[][] result, int x, int y, int z, byte blkid)
    {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    @Override
    public boolean canSpawn(World world, int x, int z)
    {
        return false;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random)
    {
        return new Location(world, 0, 128, 0);
    }
}
