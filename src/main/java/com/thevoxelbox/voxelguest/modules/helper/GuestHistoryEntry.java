package com.thevoxelbox.voxelguest.modules.helper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author TheCryoknight
 */
@DatabaseTable(tableName = "reviewHistory")
public final class GuestHistoryEntry implements Comparable<GuestHistoryEntry>
{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String guestName = "";
    @DatabaseField
    private String reviewerName = "";
    @DatabaseField
    private long reviewTime = 0;
    @DatabaseField
    private String comment = "";

    public GuestHistoryEntry() {}

    public GuestHistoryEntry(final String guestName, final String reviewerName)
    {
        this.guestName = guestName;
        this.reviewerName = reviewerName;
        this.reviewTime = System.currentTimeMillis();
    }

    /**
     * @return the guest's name
     */
    public String getGuestName()
    {
        return guestName;
    }

    /**
     * @return the reviewer's name
     */
    public String getReviewerName()
    {
        return reviewerName;
    }

    /**
     * @return the time the review took place
     */
    public long getReviewTime()
    {
        return reviewTime;
    }

    /**
     * Sets the Comment for this review.
     *
     * @param comment
     */
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    @Override
    public int compareTo(final GuestHistoryEntry otherEntry)
    {
        return Long.compare(this.reviewTime, otherEntry.reviewTime);
    }

    /**
     * Gets the comment relating to this review.
     *
     * @return Comment from helper
     */
    public String getComment()
    {
        return this.comment;
    }
}
