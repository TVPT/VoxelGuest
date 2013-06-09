package com.thevoxelbox.voxelguest.commands.arguments;

import org.kohsuke.args4j.Option;

/**
 * @author Monofraps
 */
public final class ModulesCommandArguments
{
    @Option(name = "-list", aliases = {"-l"}, usage = "Lists all available modules.")
    private boolean listModules = false;
    @Option(name = "-enable", aliases = {"-e"}, metaVar = "<module-name>", usage = "Enables a given module.")
    private String moduleToEnable = "";
    @Option(name = "-disable", aliases = {"-d"}, metaVar = "<module-name>", usage = "Disables a given module.")
    private String moduleToDisable = "";

    /**
     * Returns true if -list or -l was specified in the command args.
     *
     * @return Returns true if -list or -l was specified in the command args.
     */
    public boolean isListModules()
    {
        return listModules;
    }

    /**
     * Returns true if -enable or -e was specified in the command args.
     *
     * @return Returns true if -enable or -e was specified in the command args.
     */
    public String getModuleToEnable()
    {
        return moduleToEnable;
    }

    /**
     * Returns true if -disable or -d was specified in the command args.
     *
     * @return Returns true if -disable or -d was specified in the command args.
     */
    public String getModuleToDisable()
    {
        return moduleToDisable;
    }
}
