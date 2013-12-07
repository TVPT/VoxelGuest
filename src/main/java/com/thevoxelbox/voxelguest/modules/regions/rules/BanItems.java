package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.rules.settings.BanItemsSettings;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class BanItems extends GuestRegionRule
{
    @Override
    public boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        if (settings instanceof BanItemsSettings)
        {
            final ItemStack itemUsed = ((PlayerInteractEvent) event).getItem();
            if (itemUsed == null)
            {
                return false;
            }

            final int usedItemId = itemUsed.getType().getId();
            final List<Integer> bannedItems = ((BanItemsSettings) settings).getBlockedItems();
            return bannedItems.contains(usedItemId);
        }

        return false;
    }

    @Override
    public String[] getHandles()
    {
        return new String[]{"BanItems", "BannedItems", "BI"};
    }

    @Override
    public boolean handles(final Event event)
    {
        return event instanceof PlayerInteractEvent;
    }

    @Override
    public GuestRuleExecutionSettings instantiateExecutorSettingsClass()
    {
        return new BanItemsSettings();
    }
}
