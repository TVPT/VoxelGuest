package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;

/**
 * @author Monofraps
 */
public class RuleExecutionResult
{
    private RuleExecutionSettings settings;
    private RegionRule rule;
    private Region region;
    private boolean cancellationSuggested;

    public RuleExecutionResult(final RuleExecutionSettings settings, final RegionRule rule, final Region region, final boolean cancellationSuggested)
    {
        this.settings = settings;
        this.rule = rule;
        this.region = region;
        this.cancellationSuggested = cancellationSuggested;
    }

    public RuleExecutionSettings getSettings()
    {
        return settings;
    }

    public RegionRule getRule()
    {
        return rule;
    }

    public Region getRegion()
    {
        return region;
    }

    public boolean isCancellationSuggested()
    {
        return cancellationSuggested;
    }
}
