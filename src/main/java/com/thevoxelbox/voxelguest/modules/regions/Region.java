package com.thevoxelbox.voxelguest.modules.regions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import org.bukkit.Bukkit;

/**
 * @author Butters
 * @author Monofraps
 */
@Entity
@Table(name = "regions")
public class Region implements Serializable
{
    
	@Column
	private final String regionName;
        @Column
        private String worldName;
	@Id
	@Column
	private long id;
	@Column
	private int pointOneX;
        @Column
        private int pointOneY;
        @Column
        private int pointOneZ;
        @Column
        private int pointTwoX;
        @Column
        private int pointTwoY;
        @Column
        private int pointTwoZ;
        
	//World
	@Column
	private boolean allowMobSpawn = false;
	@Column
	private boolean allowFireSpread = false;
	@Column
	private boolean allowLeafDecay = false;
	@Column
	private boolean allowBlockGrowth = false;
	@Column
	private boolean allowBlockSpread = false;
	@Column
	private boolean allowCreeperExplosions = false;
	@Column
	private boolean allowTntBreakingPaintings = false;
	@Column
	private boolean allowLavaFlow = false;
	@Column
	private boolean allowWaterFlow = false;
	@Column
	private boolean allowDragonEggMovement = false;
	@Column
	private boolean allowSnowMelting = false;
	@Column
	private boolean allowIceMelting = false;
	@Column
	private boolean allowSnowFormation = false;
	@Column
	private boolean allowIceFormation = false;
	@Column
	private boolean allowEnchanting = false;
	@Column
        @ElementCollection
	private List<Integer> bannedBlocks = new ArrayList<>();
	@Column
        @ElementCollection
	private List<Integer> bannedItems = new ArrayList<>();
	@Column
	private String buildPermission;
	//Player
	@Column
	private boolean allowPvPDamage = false;
	@Column
	private boolean allowLavaDamage = false;
	@Column
	private boolean allowCactusDamage = false;
	@Column
	private boolean allowTnTDamage = false;
	@Column
	private boolean allowDrowningDamage = false;
	@Column
	private boolean allowExplosiveDamage = false;
	@Column
	private boolean allowFallDamage = false;
	@Column
	private boolean allowFireDamage = false;
	@Column
	private boolean allowPoisonDamage = false;
	@Column
	private boolean allowMagicDamage = false;
	@Column
	private boolean allowProjectileDamage = false;
	@Column
	private boolean allowHungerDamage = false;
	@Column
	private boolean allowVoidDamage = false;
	@Column
	private boolean allowFireTickDamage = false;
	@Column
	private boolean allowLightningDamage = false;
	@Column
	private boolean allowSuffocationDamage = false;
	@Column
	private boolean allowFoodChange = false;

	public Region(final String worldName, final Location pointOne, final Location pointTwo, final String regionName, final String buildPermission)
	{
                this.worldName = pointOne.getWorld().getName();
                this.pointOneX = pointOne.getBlockX();
                this.pointOneY = pointOne.getBlockY();
                this.pointOneZ = pointOne.getBlockZ();
                this.pointTwoX = pointTwo.getBlockX();
                this.pointTwoY = pointTwo.getBlockY();
                this.pointTwoZ = pointTwo.getBlockZ();
		this.regionName = regionName;
		this.buildPermission = buildPermission;
	}

	public final boolean isLocationInRegion(final Location locationToCheck)
	{
		if (!locationToCheck.getWorld().equals(getPointOne().getWorld()))
		{
			return false;
		}

		//For open worlds that do not have specified points
		if (getPointOne() == null && getPointTwo() == null)
		{
			return true;
		}

		return locationToCheck.toVector().isInAABB(Vector.getMinimum(getPointOne().toVector(), getPointTwo().toVector()), Vector.getMaximum(getPointOne().toVector(), getPointTwo().toVector()));

	}

	public Location getPointOne()
	{
		return new Location(Bukkit.getWorld(worldName), pointOneX, pointOneY, pointOneZ);
	}

	public void setPointOne(final Location pointOne)
	{
		this.worldName = pointOne.getWorld().getName();
                this.pointOneX = pointOne.getBlockX();
                this.pointOneY = pointOne.getBlockY();
                this.pointOneZ = pointOne.getBlockZ();
	}

	public Location getPointTwo()
	{
		return new Location(Bukkit.getWorld(worldName), pointTwoX, pointTwoY, pointTwoZ);
	}

	public void setPointTwo(final Location pointTwo)
	{
		this.worldName = pointTwo.getWorld().getName();
                this.pointTwoX = pointTwo.getBlockX();
                this.pointTwoY = pointTwo.getBlockY();
                this.pointTwoZ = pointTwo.getBlockZ();
	}

	public boolean isMobSpawnAllowed()
	{
		return allowMobSpawn;
	}

	public void setAllowMobSpawn(final boolean allowMobSpawn)
	{
		this.allowMobSpawn = allowMobSpawn;
	}

	public boolean isFireSpreadAllowed()
	{
		return allowFireSpread;
	}

	public void setAllowFireSpread(final boolean allowFireSpread)
	{
		this.allowFireSpread = allowFireSpread;
	}

	public boolean isLeafDecayAllowed()
	{
		return allowLeafDecay;
	}

	public void setAllowLeafDecay(final boolean allowLeafDecay)
	{
		this.allowLeafDecay = allowLeafDecay;
	}

	public boolean isBlowGrowthAllowed()
	{
		return allowBlockGrowth;
	}

	public void setAllowBlockGrowth(final boolean allowBlockGrowth)
	{
		this.allowBlockGrowth = allowBlockGrowth;
	}

	public boolean isBlockSpreadAllowed()
	{
		return allowBlockSpread;
	}

	public void setAllowBlockSpread(final boolean allowBlockSpread)
	{
		this.allowBlockSpread = allowBlockSpread;
	}

	public boolean isCreeperExplosionsAllowed()
	{
		return allowCreeperExplosions;
	}

	public void setAllowCreeperExplosions(final boolean allowCreeperExplosions)
	{
		this.allowCreeperExplosions = allowCreeperExplosions;
	}

	public boolean isTntBreakingPaintingsAllowed()
	{
		return allowTntBreakingPaintings;
	}

	public void setAllowBreakingPaintings(boolean allowBreakingPaintings)
	{
		this.allowTntBreakingPaintings = allowBreakingPaintings;
	}

	public List<Integer> getBannedBlocks()
	{
		return bannedBlocks;
	}

	public void setBannedBlocks(List<Integer> bannedBlocks)
	{
		this.bannedBlocks = bannedBlocks;
	}

	public List<Integer> getBannedItems()
	{
		return bannedItems;
	}

	public void setBannedItems(final List<Integer> bannedItems)
	{
		this.bannedItems = bannedItems;
	}

	public boolean isPlayerDamageAllowed()
	{
		return allowPvPDamage;
	}

	public void setAllowPlayerDamage(final boolean allowPlayerDamage)
	{
		this.allowPvPDamage = allowPlayerDamage;
	}

	public String getBuildPermission()
	{
		return buildPermission;
	}

	public boolean isLavaFlowAllowed()
	{
		return allowLavaFlow;
	}

	public void setAllowLavaFlow(final boolean allowLavaFlow)
	{
		this.allowLavaFlow = allowLavaFlow;
	}

	public boolean isWaterFlowAllowed()
	{
		return allowWaterFlow;
	}

	public void setAllowWaterFlow(final boolean allowWaterFlow)
	{
		this.allowWaterFlow = allowWaterFlow;
	}

	public boolean isDragonEggMovementAllowed()
	{
		return allowDragonEggMovement;
	}

	public void setAllowDragonEggMovement(final boolean allowDragonEggMovement)
	{
		this.allowDragonEggMovement = allowDragonEggMovement;
	}

	public boolean isSnowMeltingAllowed()
	{
		return allowSnowMelting;
	}

	public void setAllowSnowMelting(final boolean allowSnowMelting)
	{
		this.allowSnowMelting = allowSnowMelting;
	}

	public boolean isIceMeltingAllowed()
	{
		return allowIceMelting;
	}

	public void setAllowIceMelting(final boolean allowIceMelting)
	{
		this.allowIceMelting = allowIceMelting;
	}

	public boolean isSnowFormationAllowed()
	{
		return allowSnowFormation;
	}

	public boolean isIceFormationAllowed()
	{
		return allowIceFormation;
	}

	public boolean isEnchantingAllowed()
	{
		return allowEnchanting;
	}

	public void setAllowEnchanting(final boolean allowEnchanting)
	{
		this.allowEnchanting = allowEnchanting;
	}

	public void setAllowSnowFormation(final boolean allowSnowFormation)
	{
		this.allowSnowFormation = allowSnowFormation;
	}

	public void setAllowIceFormation(final boolean allowIceFormation)
	{
		this.allowIceFormation = allowIceFormation;
	}

	public boolean isAllowPvPDamage()
	{
		return allowPvPDamage;
	}

	public void setAllowPvPDamage(boolean allowPvPDamage)
	{
		this.allowPvPDamage = allowPvPDamage;
	}

	public boolean isAllowLavaDamage()
	{
		return allowLavaDamage;
	}

	public void setAllowLavaDamage(boolean allowLavaDamage)
	{
		this.allowLavaDamage = allowLavaDamage;
	}

	public boolean isAllowCactusDamage()
	{
		return allowCactusDamage;
	}

	public void setAllowCactusDamage(boolean allowCactusDamage)
	{
		this.allowCactusDamage = allowCactusDamage;
	}

	public boolean isAllowTnTDamage()
	{
		return allowTnTDamage;
	}

	public void setAllowTnTDamage(boolean allowTnTDamage)
	{
		this.allowTnTDamage = allowTnTDamage;
	}

	public boolean isAllowDrowningDamage()
	{
		return allowDrowningDamage;
	}

	public void setAllowDrowningDamage(boolean allowDrowningDamage)
	{
		this.allowDrowningDamage = allowDrowningDamage;
	}

	public boolean isAllowExplosiveDamage()
	{
		return allowExplosiveDamage;
	}

	public void setAllowExplosiveDamage(boolean allowExplosiveDamage)
	{
		this.allowExplosiveDamage = allowExplosiveDamage;
	}

	public boolean isAllowFallDamage()
	{
		return allowFallDamage;
	}

	public void setAllowFallDamage(boolean allowFallDamage)
	{
		this.allowFallDamage = allowFallDamage;
	}

	public boolean isAllowFireDamage()
	{
		return allowFireDamage;
	}

	public void setAllowFireDamage(boolean allowFireDamage)
	{
		this.allowFireDamage = allowFireDamage;
	}

	public boolean isAllowPoisonDamage()
	{
		return allowPoisonDamage;
	}

	public void setAllowPoisonDamage(boolean allowPoisonDamage)
	{
		this.allowPoisonDamage = allowPoisonDamage;
	}

	public boolean isAllowMagicDamage()
	{
		return allowMagicDamage;
	}

	public void setAllowMagicDamage(boolean allowMagicDamage)
	{
		this.allowMagicDamage = allowMagicDamage;
	}

	public boolean isAllowProjectileDamage()
	{
		return allowProjectileDamage;
	}

	public void setAllowProjectileDamage(boolean allowProjectileDamage)
	{
		this.allowProjectileDamage = allowProjectileDamage;
	}

	public boolean isAllowHungerDamage()
	{
		return allowHungerDamage;
	}

	public void setAllowHungerDamage(boolean allowHungerDamage)
	{
		this.allowHungerDamage = allowHungerDamage;
	}

	public boolean isAllowVoidDamage()
	{
		return allowVoidDamage;
	}

	public void setAllowVoidDamage(boolean allowVoidDamage)
	{
		this.allowVoidDamage = allowVoidDamage;
	}

	public boolean isAllowFoodChange()
	{
		return allowFoodChange;
	}

	public void setAllowFoodChange(boolean allowFoodChange)
	{
		this.allowFoodChange = allowFoodChange;
	}

	public boolean isAllowTntBreakingPaintings()
	{
		return allowTntBreakingPaintings;
	}

	public void setAllowTntBreakingPaintings(boolean allowTntBreakingPaintings)
	{
		this.allowTntBreakingPaintings = allowTntBreakingPaintings;
	}

	public boolean isAllowFireTickDamage()
	{
		return allowFireTickDamage;
	}

	public void setAllowFireTickDamage(boolean allowFireTickDamage)
	{
		this.allowFireTickDamage = allowFireTickDamage;
	}

	public boolean isAllowLightningDamage()
	{
		return allowLightningDamage;
	}

	public void setAllowLightningDamage(boolean allowLightningDamage)
	{
		this.allowLightningDamage = allowLightningDamage;
	}

	public boolean isAllowSuffocationDamage()
	{
		return allowSuffocationDamage;
	}

	public void setAllowSuffocationDamage(boolean allowSuffocationDamage)
	{
		this.allowSuffocationDamage = allowSuffocationDamage;
	}

	public String getRegionName()
	{
		return regionName;
	}


}
