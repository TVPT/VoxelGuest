package com.thevoxelbox.voxelguest.modules.regions.rules.player;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.GuestRegionRule;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * @author Monofraps
 */
public class DisableEnchantment extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        return event instanceof EnchantItemEvent;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"DisableEnchantment", "DE"};
    }
}
