package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * @author Monofraps
 */
public class DisableFoodLevelChange extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (event instanceof FoodLevelChangeEvent)
        {
            final FoodLevelChangeEvent foodLevelChangeEvent = (FoodLevelChangeEvent) event;
            foodLevelChangeEvent.setFoodLevel(20);
            return true;
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableFoodLevelChange", "DFLC", "FoodLevel"};
    }
}
