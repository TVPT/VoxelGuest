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
}
