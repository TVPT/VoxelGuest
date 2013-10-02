package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;
import org.bukkit.event.Event;

/**
 * An abstract class every region rule implementation has to extend from.
 *
 * @author Monofraps
 */
public abstract class GuestRegionRule implements RegionRule
{
    /**
     * This function will be called when a monitored event is fired in a registered region.
     * The way the "location" of an event determined depends on the type of event. See {@link com.thevoxelbox.voxelguest.modules.regions.listener.BukkitEventListener} for more information
     *
     * @param event    The event that got fired.
     * @param region   The region this rule is associated with.
     * @param settings The settings (if there are any) associated with this rule.
     *
     * @return true if an even cancellation is suggested, otherwise false
     */
    @Override
    public abstract boolean check(Event event, Region region, RuleExecutionSettings settings);

    /**
     * This function is called to get a list of names a user can use (e.g. in a command) to refer to this rule.
     *
     * @return an array of user friendly names for this rule
     */
    @Override
    public abstract String[] getHandles();

    /**
     * Called when an instance of an {@link com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings} (or a subclass of it) is needed.
     * These instances are usually used to fill them with data from the settings database or to pass them to the
     * A4J parser engine and map command arguments to its fields.
     *
     * @return an instance of {@link com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings} or a subclass of it
     */
    @Override
    public final GuestRuleExecutionSettings instantiateExecutorSettingsClass()
    {
        return new GuestRuleExecutionSettings()
        {
        };
    }
}
