package com.thevoxelbox.voxelguest.modules.general;

import com.thevoxelbox.voxelguest.persistence.Persistence;

import java.util.HashMap;
import java.util.List;

/**
 * @author Monofraps
 */
public final class PlayerGroupDAO
{
    private PlayerGroupDAO()
    {
    }

    /**
     * {@link #byGroupName(String)} will query a {@link PlayerGroup} by its name.
     *
     * @param groupName The name to query for.
     *
     * @return The {@link PlayerGroup} with the name <code>groupName</code> or null. Where null indicates that no group
     *         with the name <code>groupName</code> was found.
     */
    public static PlayerGroup byGroupName(final String groupName)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("groupName", groupName);

        final List<PlayerGroup> playerGroups = Persistence.getInstance().loadAll(PlayerGroup.class, selectRestrictions);

        for (PlayerGroup playerGroup : playerGroups)
        {
            if (playerGroup.getGroupName().equalsIgnoreCase(groupName))
            {
                return playerGroup;
            }
        }

        return null;
    }

    /**
     * {@link #save(PlayerGroup)} will save or update a {@link PlayerGroup} entry.
     * @param playerGroup The {@link PlayerGroup} to save or update.
     */
    public static void save(final PlayerGroup playerGroup)
    {
        Persistence.getInstance().save(playerGroup);
    }
}
