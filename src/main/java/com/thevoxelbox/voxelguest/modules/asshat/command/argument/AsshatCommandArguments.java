package com.thevoxelbox.voxelguest.modules.asshat.command.argument;

import com.google.common.base.Joiner;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Monofraps
 */
public final class AsshatCommandArguments
{
    @Option(name = "-force", aliases = {"-f"}, usage = "Force the ban system to use the exact player name entered.")
    private boolean forceName = false;
    @Option(name = "-silent", aliases = {"-s", "-si"}, usage = "Do not show broadcasts.")
    private boolean silent = false;
    @Argument(metaVar = "<player-name>", index = 0, required = true, usage = "The Player's name.")
    private String playerName = "";
    @Argument(metaVar = "<ban-reason>", index = 1, usage = "A reason.")
    private List<String> reason = new ArrayList<>();

    /**
     * @return Returns true if the -force flag was set.
     */
    public boolean isForceName()
    {
        return forceName;
    }

    /**
     * @return Returns true if the -silent flag was set.
     */
    public boolean isSilent()
    {
        return silent;
    }

    /**
     * @return Returns the player name. (first string without '-' at the beginning)
     */
    public String getPlayerName()
    {
        return playerName.toLowerCase();
    }

    /**
     * @return Returns the asshat reason. (all other strings without '-' at the beginning)
     */
    public String getReason()
    {
        return Joiner.on(" ").join(reason);
    }
}
