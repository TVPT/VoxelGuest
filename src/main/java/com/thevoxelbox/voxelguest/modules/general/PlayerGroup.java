package com.thevoxelbox.voxelguest.modules.general;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class will store user group name - group color mappings only for now. It can be used to add more metadata to groups
 * later on.
 *
 * @author Monofraps
 */
@DatabaseTable(tableName = "groups")
public class PlayerGroup
{
    @DatabaseField(id = true)
    private String groupName;
    @DatabaseField
    private String color;

    public PlayerGroup()
    {
    }

    public PlayerGroup(final String groupName, final String color)
    {
        this.groupName = groupName;
        this.color = color;
    }

    public String getColor()
    {
        return color;
    }

    public String getGroupName()
    {
        return groupName;
    }
}
