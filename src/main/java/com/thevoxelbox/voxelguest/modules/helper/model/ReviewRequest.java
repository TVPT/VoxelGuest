package com.thevoxelbox.voxelguest.modules.helper.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author TheCryoknight
 */
public final class ReviewRequest
{
    private final Location location;
    private final Player guest;

    /**
     * Creates a new review request instance.
     *
     * @param guest    The guest who requests the review.
     * @param location The location the guest is standing.
     */
    public ReviewRequest(final Player guest, final Location location)
    {
        this.location = location;
        this.guest = guest;
    }

    /**
     * @return The location of whitelist review
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * @return The guest that submitted the whitelist review
     */
    public Player getGuest()
    {
        return guest;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof ReviewRequest))
        {
            return false;
        }

        final ReviewRequest otherReq = (ReviewRequest) other;
        return this.getGuest().equals(otherReq.getGuest()) && this.getLocation().equals(otherReq.getLocation());
    }
}
