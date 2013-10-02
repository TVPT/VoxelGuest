package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;
import org.bukkit.event.Event;
import org.bukkit.event.hanging.HangingBreakEvent;

/**
 * @author Monofraps
 */
public class DisableTntBreakPainting extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return event instanceof HangingBreakEvent && ((HangingBreakEvent) event).getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION);

    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableTntBreakPainting", "DTNTBP"};
    }
}
