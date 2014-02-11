package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;

/**
 * An abstract class every region rule implementation has to extend from.
 *
 * @author Monofraps
 */
public abstract class GuestRegionRule implements RegionRule
{
    /**
     * Called when an instance of an {@link com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings} (or a subclass of it) is needed.
     * These instances are usually used to fill them with data from the settings database or to pass them to the
     * A4J parser engine and map command arguments to its fields.
     *
     * @return an instance of {@link com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings} or a subclass of it
     */
    @Override
    public GuestRuleExecutionSettings instantiateExecutorSettingsClass()
    {
        return new GuestRuleExecutionSettings();
    }
}
