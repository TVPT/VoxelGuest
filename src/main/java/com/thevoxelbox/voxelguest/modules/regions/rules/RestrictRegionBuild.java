package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import com.thevoxelbox.voxelguest.utils.EventInformationProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Monofraps
 */
@Rule(groups = {""})
public class RestrictRegionBuild extends GuestRegionRule
{
    private static final String BUILD_PERMISSION_ROOT = "voxelguest.modules.region.modify.";

    @Override
    public final boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final String regionPermissionNode = BUILD_PERMISSION_ROOT + region.getName().toLowerCase();
        final Player player = EventInformationProvider.getPlayerFromEvent(event);

        if (!player.hasPermission(regionPermissionNode))
        {
            player.sendMessage("You are not allowed to build here.");
            return true;
        }
        return false;
    }

    @Override
    public boolean handles(final Event event)
    {
        return (event instanceof BlockBreakEvent) || (event instanceof BlockPlaceEvent);
    }

    @Override
    public final String[] getHandles()
    {
        return new String[]{"RestrictRegionBuild", "RRB"};
    }
}
