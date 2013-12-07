package com.thevoxelbox.voxelguest.modules.regions.command.handler;

import com.thevoxelbox.voxelguest.modules.regions.command.RuleModification;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Monofraps
 */
public class RuleModificationListHandler extends OptionHandler<RuleModification>
{
    private static final List<String> OPTIONS = Arrays.asList("true", "on", "yes", "1", "enable", "enabled", "allow", "allowed",
            "false", "off", "no", "0", "disable", "disabled", "disallow", "disallowed");

    public RuleModificationListHandler(final CmdLineParser parser, final OptionDef option, final Setter<? super RuleModification> setter)
    {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(final Parameters params) throws CmdLineException
    {
        final String ruleHandle = params.getParameter(0);
        final boolean newState;
        final int index = OPTIONS.indexOf(params.getParameter(1));

        newState = index == -1 || index < OPTIONS.size() / 2;

        setter.addValue(new RuleModification(ruleHandle, newState));

        return 2;
    }

    @Override
    public String getDefaultMetaVariable()
    {
        return "";
    }
}
