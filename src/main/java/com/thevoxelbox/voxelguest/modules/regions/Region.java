/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelguest.modules.regions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author Joe
 */
public class Region implements Serializable
{
    
    public final String regionName;
    public final String worldName;
    private Location pointOne;
    private Location pointTwo;
    
    //World
    private boolean allowMobSpawn = false;
    private boolean allowFireSpread = false;
    private boolean allowLeafDecay = false;
    private boolean allowBlockGrowth = false;
    private boolean allowBlockSpread = false;
    private boolean allowCreeperExplosions = false;
    private boolean allowBreakingPaintings = false;
    private boolean allowLavaFlow = false;
    private boolean allowWaterFlow = false;
    private boolean allowDragonEggMovement = false;
    private boolean allowSnowMelting = false;
    private boolean allowIceMelting = false;
    private boolean allowSnowFormation = false;
    private boolean allowIceFormation = false;
    private boolean allowEnchanting = false;
    private List<Block> bannedBlocks = new ArrayList<>();
    private List<ItemStack> bannedItems = new ArrayList<>();
    private String buildPermission;
    
    //Player
    private boolean allowPlayerDamage;
    private boolean allowHunger;

    public Region(String worldName, Location pointOne, Location pointTwo, String regionName, String buildPermission) {
        this.worldName = worldName;
        this.pointOne = pointOne;
        this.pointTwo = pointTwo;
        this.regionName = regionName;
        this.buildPermission = buildPermission;
    }
    
    public boolean isLocationInRegion(Location locationToCheck){
        if(!locationToCheck.getWorld().getName().equalsIgnoreCase(worldName)){
            return false;
        }
        
        //For open worlds that do not have specified points
        if(pointOne == null && pointTwo == null){
            return true;
        }
        
        if(locationToCheck.toVector().isInAABB(Vector.getMinimum(pointOne.toVector(), pointTwo.toVector()), Vector.getMaximum(pointOne.toVector(), pointTwo.toVector()))){
            return true;
        }
        else{
            return false;
        }
        
    }
    
    public Location getPointOne() {
        return pointOne;
    }

    public void setPointOne(Location pointOne) {
        this.pointOne = pointOne;
    }

    public Location getPointTwo() {
        return pointTwo;
    }

    public void setPointTwo(Location pointTwo) {
        this.pointTwo = pointTwo;
    }

    public boolean isMobSpawnAllowed() {
        return allowMobSpawn;
    }

    public void setAllowMobSpawn(boolean allowMobSpawn) {
        this.allowMobSpawn = allowMobSpawn;
    }

    public boolean isFireSpreadAllowed() {
        return allowFireSpread;
    }

    public void setAllowFireSpread(boolean allowFireSpread) {
        this.allowFireSpread = allowFireSpread;
    }

    public boolean isLeafDecayAllowed() {
        return allowLeafDecay;
    }

    public void setAllowLeafDecay(boolean allowLeafDecay) {
        this.allowLeafDecay = allowLeafDecay;
    }

    public boolean isBlowGrowthAllowed() {
        return allowBlockGrowth;
    }

    public void setAllowBlockGrowth(boolean allowBlockGrowth) {
        this.allowBlockGrowth = allowBlockGrowth;
    }

    public boolean isBlockSpreadAllowed() {
        return allowBlockSpread;
    }

    public void setAllowBlockSpread(boolean allowBlockSpread) {
        this.allowBlockSpread = allowBlockSpread;
    }

    public boolean isCreeperExplosionsAllowed() {
        return allowCreeperExplosions;
    }

    public void setAllowCreeperExplosions(boolean allowCreeperExplosions) {
        this.allowCreeperExplosions = allowCreeperExplosions;
    }

    public boolean isBreakingPaintingsAllowed() {
        return allowBreakingPaintings;
    }

    public void setAllowBreakingPaintings(boolean allowBreakingPaintings) {
        this.allowBreakingPaintings = allowBreakingPaintings;
    }

    public List<Block> getBannedBlocks() {
        return bannedBlocks;
    }

    public void setBannedBlocks(List<Block> bannedBlocks) {
        this.bannedBlocks = bannedBlocks;
    }

    public List<ItemStack> getBannedItems() {
        return bannedItems;
    }

    public void setBannedItems(List<ItemStack> bannedItems) {
        this.bannedItems = bannedItems;
    }

    public boolean isPlayerDamageAllowed() {
        return allowPlayerDamage;
    }

    public void setAllowPlayerDamage(boolean allowPlayerDamage) {
        this.allowPlayerDamage = allowPlayerDamage;
    }

    public boolean isPlayerHungerAllowed() {
        return allowHunger;
    }

    public void setAllowHunger(boolean allowHunger) {
        this.allowHunger = allowHunger;
    }

    public String getBuildPermission() {
        return buildPermission;
    }

    public boolean isLavaFlowAllowed() {
        return allowLavaFlow;
    }

    public void setAllowLavaFlow(boolean allowLavaFlow) {
        this.allowLavaFlow = allowLavaFlow;
    }

    public boolean isWaterFlowAllowed() {
        return allowWaterFlow;
    }

    public void setAllowWaterFlow(boolean allowWaterFlow) {
        this.allowWaterFlow = allowWaterFlow;
    }

    public boolean isDragonEggMovementAllowed() {
        return allowDragonEggMovement;
    }

    public void setAllowDragonEggMovement(boolean allowDragonEggMovement) {
        this.allowDragonEggMovement = allowDragonEggMovement;
    }

    public boolean isSnowMeltingAllowed() {
        return allowSnowMelting;
    }

    public void setAllowSnowMelting(boolean allowSnowMelting) {
        this.allowSnowMelting = allowSnowMelting;
    }

    public boolean isIceMeltingAllowed() {
        return allowIceMelting;
    }

    public void setAllowIceMelting(boolean allowIceMelting) {
        this.allowIceMelting = allowIceMelting;
    }

    public boolean isSnowFormationAllowed() {
        return allowSnowFormation;
    }

    public boolean isIceFormationAllowed() {
        return allowIceFormation;
    }

    public boolean isEnchantingAllowed() {
        return allowEnchanting;
    }

    public void setAllowEnchanting(boolean allowEnchanting) {
        this.allowEnchanting = allowEnchanting;
    }

    public void setAllowSnowFormation(boolean allowSnowFormation) {
        this.allowSnowFormation = allowSnowFormation;
    }

    public void setAllowIceFormation(boolean allowIceFormation) {
        this.allowIceFormation = allowIceFormation;
    }
    
}
