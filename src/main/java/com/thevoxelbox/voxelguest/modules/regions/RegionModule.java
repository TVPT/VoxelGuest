/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.modules.GuestModule;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

/**
 *
 * @author Joe
 */
public class RegionModule extends GuestModule
{

    private List<Region> regions = new ArrayList<>();
    
    public RegionModule() {
        setName("Region Module");
    }

    @Override
    public void onEnable(){
        this.eventListeners.add(new BlockEventListener(this));
        this.eventListeners.add(new PlayerEventListener(this));
        super.onEnable();
    }

    @Override
    public void onDisable(){
        this.eventListeners.clear();
        super.onDisable();
    }
    
    public Region getRegionAtLocation(Location regionLocation){
        for(Region region : regions){
            if(region.isLocationInRegion(regionLocation)){
                return region;
            }
        }
        return null;
    } 
    
}
