package com.thevoxelbox.voxelguest.modules.regions;

import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Monofraps
 */
@DatabaseTable
public class RuleExecutorSettingsModel
{
    private Long id;
    private String executorId;
    private Long regionId;
    private String jsonString;

    public RuleExecutorSettingsModel()
    {
    }

    public RuleExecutorSettingsModel(final String executorId, final Long regionId, final String jsonString)
    {
        this.executorId = executorId;
        this.regionId = regionId;
        this.jsonString = jsonString;
    }

    public Long getId()
    {
        return id;
    }

    public String getExecutorId()
    {
        return executorId;
    }

    public void setExecutorId(String executorId)
    {
        this.executorId = executorId;
    }

    public Long getRegionId()
    {
        return regionId;
    }

    public void setRegionId(Long regionId)
    {
        this.regionId = regionId;
    }

    public String getJsonString()
    {
        return jsonString;
    }

    public void setJsonString(String jsonString)
    {
        this.jsonString = jsonString;
    }
}
