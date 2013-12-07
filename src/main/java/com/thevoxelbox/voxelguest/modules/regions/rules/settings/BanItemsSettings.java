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

    @Override
    public void handleChange(final String arguments)
    {
        for (final String arg : arguments.split(" "))
        {
            if (arg.startsWith("+"))
            {
                blockedItems.add(Integer.valueOf(arg.replace("+", "")));
            }
            else if (arg.startsWith("-"))
            {
                blockedItems.remove(Integer.valueOf(arg.replace("-", "")));
            }
        }
    }
}
