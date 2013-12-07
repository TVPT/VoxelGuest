package com.thevoxelbox.voxelguest.modules.regions.command;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Monofraps
 */
public final class RuleModification
{
    private String rulId;
    private boolean enable;

    /**
     * Instantiates a rule modification.
     *
     * @param rulId  The rule id.
     * @param enable True if the rule should be enabled, false otherwise.
     */
    public RuleModification(final String rulId, final boolean enable)
    {
        this.rulId = rulId;
        this.enable = enable;
    }

    /**
     * Returns the rule ID.
     *
     * @return Returns the rule ID.
     */
    public String getRulId()
    {
        return rulId;
    }

    /**
     * Returns whether the rule should be enabled or not.
     *
     * @return Returns true if the rule is supposed to be enabled.
     */
    public boolean isEnable()
    {
        return enable;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("rulId", rulId)
                .append("enable", enable)
                .toString();
    }
}
