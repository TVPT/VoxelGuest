package com.thevoxelbox.voxelguest.modules.greylist;

import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerGreylistEvent;
import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerGreylistedEvent;
import com.thevoxelbox.voxelguest.modules.greylist.event.PlayerUngreylistedEvent;
import com.thevoxelbox.voxelguest.modules.greylist.model.Greylistee;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;

/**
 * @author MikeMatrix
 * @author TheCryoknight
 * @author Monofraps
 */
public final class GreylistDAO
{
    private GreylistDAO()
    {

    }

    /**
     * Checks if someone is on the greylist.
     *
     * @param name The name of the guest to check.
     *
     * @return Returns true of the given name is on the greylist.
     */
    public static boolean isOnPersistentGreylist(final String name)
    {
        final List<Greylistee> greylistees;

        try
        {
            final HashMap<String, Object> selectRestrictions = new HashMap<>();
            selectRestrictions.put("name", name.toLowerCase());

            greylistees = Persistence.getInstance().loadAll(Greylistee.class, selectRestrictions);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        for (Greylistee greylistee : greylistees)
        {
            if (greylistee.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Greylists a name.
     *
     * @param name The name to greylist.
     */
    public static void greylist(final String name)
    {
        try
        {
            final PlayerGreylistEvent playerGreylistEvent = new PlayerGreylistEvent(name);
            Bukkit.getPluginManager().callEvent(playerGreylistEvent);
            if (playerGreylistEvent.isCancelled())
            {
                return;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if (GreylistDAO.isOnPersistentGreylist(name))
        {
            return;
        }
        Persistence.getInstance().save(new Greylistee(name.toLowerCase()));

        try
        {
            final PlayerGreylistedEvent playerGreylistedEvent = new PlayerGreylistedEvent(name);
            Bukkit.getPluginManager().callEvent(playerGreylistedEvent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Removes a name from the greylist.
     *
     * @param name The name to remove.
     */
    public static void ungreylist(final String name)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("name", name.toLowerCase());
        final List<Greylistee> greylistees = Persistence.getInstance().loadAll(Greylistee.class, selectRestrictions);

        for (Greylistee greylistee : greylistees)
        {
            if (greylistee.getName().equalsIgnoreCase(name))
            {
                Persistence.getInstance().delete(greylistee);
            }
        }

        try
        {
            final PlayerUngreylistedEvent playerUngreylistedEvent = new PlayerUngreylistedEvent(name);
            Bukkit.getPluginManager().callEvent(playerUngreylistedEvent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
