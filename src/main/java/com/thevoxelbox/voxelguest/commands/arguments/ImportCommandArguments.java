package com.thevoxelbox.voxelguest.commands.arguments;

import org.kohsuke.args4j.Option;

/**
 * @author Monofraps
 */
public final class ImportCommandArguments
{
    @Option(name = "-bans", aliases = {"-b"}, usage = "Import bans")
    private boolean importBans = false;
    @Option(name = "-greylist", aliases = {"-gl"}, usage = "Import greylist")
    private boolean importGreylist = false;
    @Option(name = "-afkmessages", aliases = {"-am"}, usage = "Import AFM messages")
    private boolean importAfmMessages = false;

    /**
     * Returns true if -bans or -b was specified in the command args.
     *
     * @return Returns true if -bans or -b was specified in the command args.
     */
    public boolean isImportBans()
    {
        return importBans;
    }

    /**
     * Returns true if -greylist or -gl was specified in the command args.
     *
     * @return Returns true if -greylist or -gl was specified in the command args.
     */
    public boolean isImportGreylist()
    {
        return importGreylist;
    }

    /**
     * Returns true if -afkmessages or -am was specified in the command args.
     *
     * @return Returns true if -afkmessages or -am was specified in the command args.
     */
    public boolean isImportAfmMessages()
    {
        return importAfmMessages;
    }
}
