package com.thevoxelbox.voxelguest.modules.regions.rules.settings;

import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Monofraps
 */
public class BanBlocksSettings extends GuestRuleExecutionSettings
{
    private List<Integer> blockedBlocks = new ArrayList<>();

    public List<Integer> getBlockedBlocks()
    {
        return blockedBlocks;
    }

    @Override
    public void handleChange(final String arguments)
    {
        for (final String arg : arguments.split(" "))
        {
            if (arg.startsWith("+"))
            {
                blockedBlocks.add(Integer.valueOf(arg.replace("+", "")));
            }
            else if (arg.startsWith("-"))
            {
                blockedBlocks.remove(Integer.valueOf(arg.replace("-", "")));
            }
        }
    }
}
