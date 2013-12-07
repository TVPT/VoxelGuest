package com.thevoxelbox.voxelguest.modules.regions.settings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Monofraps
 */
@DatabaseTable
public final class RuleSettingsData
{
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private String ruleId;
    @DatabaseField
    private Long regionId;
    @DatabaseField
    private String jsonString;

    /**
     * Default constructor.
     */
    public RuleSettingsData()
    {
    }

    /**
     * Constructor.
     *
     * @param ruleId     ID of the related rule.
     * @param regionId   ID of the owning region.
     * @param jsonString Values to store.
     */
    public RuleSettingsData(final String ruleId, final Long regionId, final String jsonString)
    {
        this.ruleId = ruleId;
        this.regionId = regionId;
        this.jsonString = jsonString;
    }

    /**
     * Returns the ID of the related rule.
     *
     * @return Returns the ID of the related rule.
     */
    public String getRuleId()
    {
        return ruleId;
    }

    /**
     * Sets the ID of the related rule.
     *
     * @param ruleId New ID of the related rule.
     */
    public void setRuleId(final String ruleId)
    {
        this.ruleId = ruleId;
    }

    /**
     * Returns the ID of the owning region.
     *
     * @return Returns the ID of the owning region.
     */
    public Long getRegionId()
    {
        return regionId;
    }

    /**
     * Sets the ID of the owning region.
     *
     * @param regionId The new ID of the owning region.
     */
    public void setRegionId(final Long regionId)
    {
        this.regionId = regionId;
    }

    /**
     * Returns the stored data as a JSON string.
     *
     * @return Returns the stored data as a JSON string.
     */
    public String getJsonString()
    {
        return jsonString;
    }

    /**
     * Sets the stored data.
     *
     * @param jsonString New value of stored data (as JSON string).
     */
    public void setJsonString(final String jsonString)
    {
        this.jsonString = jsonString;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("ruleId", ruleId)
                .append("regionId", regionId)
                .append("jsonString", jsonString)
                .toString();
    }
}
