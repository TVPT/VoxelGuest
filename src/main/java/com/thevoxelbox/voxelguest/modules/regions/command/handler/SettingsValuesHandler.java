package com.thevoxelbox.voxelguest.modules.regions.command.handler;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Parses vgregion-ruleparams rule settings parameters. All parameters that should be sent to the rule settings class
 * have to be enclosed by [ ] .
 *
 * @author Monofraps
 */
public class SettingsValuesHandler extends OptionHandler<String>
{

    public SettingsValuesHandler(final CmdLineParser parser, final OptionDef option, final Setter<? super String> setter)
    {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(final Parameters params) throws CmdLineException
    {
        final StringBuilder builder = new StringBuilder();

        boolean valArgsStarted = false;
        int argumentsConsumed = 0;

        for (int i = 0; i < params.size(); i++)
        {
            final String currentParameter = params.getParameter(i);

            if (currentParameter.startsWith("["))
            {
                valArgsStarted = true;
                builder.append(currentParameter.replace("[", "")).append(" ");
                argumentsConsumed++;
            }
            else if (currentParameter.endsWith("]"))
            {
                valArgsStarted = false;
                builder.append(currentParameter.replace("]", "")).append(" ");
                argumentsConsumed++;
            }
            else if (valArgsStarted)
            {
                builder.append(currentParameter).append(" ");
                argumentsConsumed++;
            }
        }

        setter.addValue(builder.toString());

        return argumentsConsumed;
    }

    @Override
    public String getDefaultMetaVariable()
    {
        return "";
    }
}
