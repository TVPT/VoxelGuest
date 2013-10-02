package com.thevoxelbox.voxelguest.modules.regions.rules.settings;

import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Monofraps
 */
public class BanItemsSettings extends GuestRuleExecutionSettings
{
    private List<Integer> blockedItems = new ArrayList<>();

    public List<Integer> getBlockedItems()
    {
        return blockedItems;
    }
}
