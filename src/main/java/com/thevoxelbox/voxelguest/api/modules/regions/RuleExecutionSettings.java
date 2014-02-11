package com.thevoxelbox.voxelguest.api.modules.regions;

/**
 * @author monofraps
 */
public interface RuleExecutionSettings
{
    /**
     * Returns the executor priority.
     *
     * @return Returns an integer representing the executor priority.
     */
    int getPriority();

    /**
     * Sets the rules priority.
     *
     * @param newPriority the new priority
     */
    void setPriority(final int newPriority);

    /**
     * Sets rule parameters based on the input arguments.
     *
     * @param arguments a list of arguments provided by the player
     */
    void handleChange(final String arguments);
}
