package com.thevoxelbox.voxelguest.api.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import org.bukkit.event.Event;

/**
 * @author monofraps
 */
public interface RegionRule
{
    /**
     * This constant can be used to let the rule inherit the priority from its region.
     */
    final int INHERIT_PRIORITY_FROM_REGION = Integer.MIN_VALUE;

    /**
     * This function will be called when a monitored event is fired in a registered region.
     * The way the "location" of an event is determined depends on the type of event. See {@link com.thevoxelbox.voxelguest.modules.regions.listener.BukkitEventListener} for more information
     *
     * @param event    The event that got fired.
     * @param region   The region this rule is associated with.
     * @param settings The settings (if there are any) associated with this rule.
     *
     * @return true if an even cancellation is suggested, otherwise false
     */
    boolean check(Event event, Region region, RuleExecutionSettings settings);

    /**
     * Called to find out if this rule handles the raised event.
     *
     * @param event An instance of the event to handle.
     *
     * @return true if the rule handles the event, false otherwise
     */
    boolean handles(Event event);

    /**
     * This function is called to get a list of names a user can use (e.g. in a command) to refer to this rule.
     *
     * @return an array of user friendly names for this rule
     */
    String[] getHandles();

    /**
     * Called when an instance of a {@link com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings} (or a subclass of it) is needed.
     * These instances are usually used to fill them with data from the settings database or to pass them to the
     * A4J parser engine and map command arguments to its fields.
     *
     * @return an instance of {@link com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings} or a subclass of it
     */
    RuleExecutionSettings instantiateExecutorSettingsClass();
}
