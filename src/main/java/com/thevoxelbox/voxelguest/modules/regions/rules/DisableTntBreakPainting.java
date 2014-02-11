package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import org.bukkit.event.Event;
import org.bukkit.event.hanging.HangingBreakEvent;

/**
 * @author Monofraps
 */
@Rule
public class DisableTntBreakPainting extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return ((HangingBreakEvent) event).getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION);

    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof HangingBreakEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableTntBreakPainting", "DTNTBP"};
    }
}
