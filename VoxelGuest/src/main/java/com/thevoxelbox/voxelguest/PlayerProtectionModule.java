package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleConfiguration;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.Setting;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * The Player Protection Module was created to help maintain various player
 * aspects, such as death prevention. The Player Protection Module offers
 * extreme amounts of customization based on config variables.
 * 
 * Handles:
 *      Food Level Changes
 *      DamageCause Death Preventions
 *      Player XP Changes
 */

@MetaData(name="Player Protection", description="Various player protection methods.")
public class PlayerProtectionModule extends Module {
    
    public PlayerProtectionModule() {
        super(PlayerProtectionModule.class.getAnnotation(MetaData.class));
    }
    
    class PlayerProtectionConfiguration extends ModuleConfiguration {
        @Setting("disable-tnt-damage") public boolean tnt = false;
        @Setting("disable-cactus-damage") public boolean cactus = false;
        @Setting("disable-drowning-damage") public boolean drowning = false;
        @Setting("disable-pvp-damage") public boolean pvp = false;
        @Setting("disable-explosion-damage") public boolean explosion = false;
        @Setting("disable-fall-damage") public boolean fall = false;
        @Setting("disable-fire-damage") public boolean fire = false;
        @Setting("disable-firetick-damage") public boolean firetick = false;
        @Setting("disable-lava-damage") public boolean lava = false;
        @Setting("disable-lightning-damage") public boolean lightning = false;
        @Setting("disable-poison-damage") public boolean potion = false;
        @Setting("disable-magic-damage") public boolean magic = false;
        @Setting("disable-projectile-damage") public boolean projectile = false;
        @Setting("disable-starvation-damage") public boolean starvation = false;
        @Setting("disable-suffocation-damage") public boolean suffocation = false;
        @Setting("disable-void-damage") public boolean voiddamage = false;
        @Setting("disable-food-changes") public boolean foodchange = false;
        
        public PlayerProtectionConfiguration(PlayerProtectionModule parent) {
            super(parent);
        }
    }
    
    @Override
    public void enable() {
        setConfiguration(new PlayerProtectionConfiguration(this));
    }

    @Override
    public String getLoadMessage() {
        return "Player protection module loaded";
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void explorationToggle(boolean explorationSetting) {
        
    }
    
    /*
     * Player Protection - EntityDamageByBlock Event
     * Written by: Razorcane
     * 
     * Handles player damage dealt by a block.
     */
    @ModuleEvent(event=EntityDamageByBlockEvent.class)
    public void onEntityDamageByBlock(BukkitEventWrapper wrapper){
        EntityDamageByBlockEvent event = (EntityDamageByBlockEvent) wrapper.getEvent();
        Entity e = event.getEntity();
        DamageCause dc = event.getCause();
        
        if(e instanceof Player){
            switch(dc){
                case CONTACT:
                    if(getConfiguration().getBoolean("disable-cactus-damage"))
                        event.setCancelled(true);
                case LAVA:
                    if (getConfiguration().getBoolean("disable-lava-damage"))
                        event.setCancelled(true);
            }
        }
    }
    
    /*
     * PlayerProtection EntityDamageByEntity Event
     * Written by: Razorcane
     * 
     * Handles damage by entity-related damage causes.
     */
    @ModuleEvent(event=EntityDamageByEntityEvent.class)
    public void EntityDamageByEntity(BukkitEventWrapper wrapper){
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) wrapper.getEvent();
        Entity e = event.getEntity();
        DamageCause dc = event.getCause();
        
        if (e instanceof Player) {
            switch (dc){
                case ENTITY_ATTACK:
                    if (getConfiguration().getBoolean("disable-pvp-damage"))
                        event.setCancelled(true);
                case ENTITY_EXPLOSION:
                    if (getConfiguration().getBoolean("disable-explosion-damage"))
                        event.setCancelled(true);
                case PROJECTILE:
                    if (getConfiguration().getBoolean("disable-projectile-damage"))
                        event.setCancelled(true);
                case BLOCK_EXPLOSION:
                    if(getConfiguration().getBoolean("disable-tnt-damage"))
                        event.setCancelled(true);
            }
        } else if (e instanceof Painting) {
            switch (dc) {
                case BLOCK_EXPLOSION:
                        if(getConfiguration().getBoolean("disable-tnt-damage"))
                            event.setCancelled(true);
            }
        }
    }
    
    /*
     * Player Protection - EntityDamage Event
     * Written by: Razorcane
     * 
     * Handles certain non-block/entity damage causes.
     */
    @ModuleEvent(event=EntityDamageEvent.class)
    public void onEntityDamage(BukkitEventWrapper wrapper) {
        EntityDamageEvent event = (EntityDamageEvent) wrapper.getEvent();
        Entity e = event.getEntity();
        DamageCause dc = event.getCause();
        
        if (e instanceof Player) {
            Player p = (Player) e;
            
            switch (dc) {
                case DROWNING:
                    if (getConfiguration().getBoolean("disable-drowning-damage"))
                        event.setCancelled(true);
                case FALL:
                    if (getConfiguration().getBoolean("disable-fall-damage"))
                        event.setCancelled(true);
                case FIRE:
                    if(getConfiguration().getBoolean("disable-fire-damage"))
                        event.setCancelled(true);
                case FIRE_TICK:
                    if (getConfiguration().getBoolean("disable-firetick-damage")) {
                        p.setFireTicks(0);
                        event.setCancelled(true);
                    }
                case LIGHTNING:
                    if (getConfiguration().getBoolean("disable-lightning-damage"))
                        event.setCancelled(true);
                case MAGIC:
                    if (getConfiguration().getBoolean("disable-magic-damage"))
                        event.setCancelled(true);
                case POISON:
                    if (getConfiguration().getBoolean("disable-poison-damage"))
                        event.setCancelled(true);
                case STARVATION:
                    if (getConfiguration().getBoolean("disable-suffocation-damage"))
                        event.setCancelled(true);
                case VOID:
                    if (getConfiguration().getBoolean("disable-void-damage"))
                        event.setCancelled(true);
            }
        }
    }
    
     /*
     * Player Protection - FoodLevelChange Event
     * Written by: Razorcane
     * 
     * Handles food level changes, specifically those for players.
     */
    @ModuleEvent(event=FoodLevelChangeEvent.class)
    public void onFoodLevelChange(BukkitEventWrapper wrapper) {
        FoodLevelChangeEvent event = (FoodLevelChangeEvent) wrapper.getEvent();
        
        if(event.getFoodLevel() < 20 && getConfiguration().getBoolean("disable-food-changes")) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }
}
