package com.thevoxelbox.voxelguest.utils;

/**
 * Represents a simple immutable pair class.
 *
 * @param <F> Type of first pair element.
 * @param <S> Type of second pair element.
 *
 * @author Monofraps
 */
public final class Pair<F, S>
{
    private final F first;
    private final S second;

    /**
     * Instantiates a pair.
     *
     * @param first  First element of the pair.
     * @param second Second element of the pair.
     */
    public Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of the pair.
     *
     * @return Returns the first element of the pair.
     */
    public F getFirst()
    {
        return first;
    }

    /**
     * Returns the second element of the pair.
     *
     * @return Returns the second element of the pair.
     */
    public S getSecond()
    {
        return second;
    }
}
