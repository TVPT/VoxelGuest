package com.thevoxelbox.voxelguest;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Monofraps
 */
@DatabaseTable(tableName = "moduleMetaData")
public class ModuleMeta
{
    @DatabaseField(id = true)
    private String  moduleName;
    @DatabaseField
    private boolean enabled;

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(final String moduleName)
    {
        this.moduleName = moduleName;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }
}
