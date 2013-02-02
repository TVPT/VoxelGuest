package com.thevoxelbox.voxelguest.modules.afk;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author keto23
 */
public class AFKList 
{
    private List<Player> afkPlayers = new ArrayList<>();

    public final void clear() {
	afkPlayers.clear();
    }

    public final void toggleAFK(Player player) {
	if (isAFK(player))
	{
	    afkPlayers.remove(player);
	}
	else
	{
	    afkPlayers.add(player);
	}
    }

    public final boolean isAFK(Player player) {
	return afkPlayers.contains(player);
    }

}
