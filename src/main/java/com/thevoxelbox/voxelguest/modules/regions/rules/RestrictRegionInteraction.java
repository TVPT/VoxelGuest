package com.thevoxelbox.voxelguest.modules.regions.rules;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.GuestRuleExecutionSettings;
import com.thevoxelbox.voxelguest.utils.EventInformationProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * @author Monofraps
 */
public class RestrictRegionInteraction extends GuestRegionRule
{
    private static final String BUILD_PERMISSION_ROOT = "voxelguest.modules.region.modify.";

    @Override
    public final boolean check(final Event event, final Region region, final RuleExecutionSettings settings)
    {
        final String regionPermissionNode = BUILD_PERMISSION_ROOT + region.getName().toLowerCase();
        final Player player = EventInformationProvider.getPlayerFromEvent(event);

        if (!player.hasPermission(regionPermissionNode))
        {
            player.sendMessage("TODO: Change this text");  //TODO
            return true;
        }
        return false;
    }

    @Override
    public final String[] getHandles()
    {
        return new String[]{"RestrictRegionInteraction", "RRI"};
    }
}
