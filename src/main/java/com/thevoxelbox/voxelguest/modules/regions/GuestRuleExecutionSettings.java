package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;

/**
 * @author Monofraps
 */
public class GuestRuleExecutionSettings implements RuleExecutionSettings
{
    private int priority = RegionRule.INHERIT_PRIORITY_FROM_REGION;

    @Override
    public final int getPriority()
    {
        return priority;
    }

    @Override
    public void setPriority(final int newPriority)
    {
        priority = newPriority;
    }

    @Override
    public void handleChange(final String arguments)
    {

    }
}
