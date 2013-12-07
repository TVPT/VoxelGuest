package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class DisableFoodLevelChange extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final FoodLevelChangeEvent foodLevelChangeEvent = (FoodLevelChangeEvent) event;
        foodLevelChangeEvent.setFoodLevel(20);
        return true;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableFoodLevelChange", "DFLC", "FoodLevel"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof FoodLevelChangeEvent;
    }
}
