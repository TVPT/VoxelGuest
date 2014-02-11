package com.thevoxelbox.voxelguest.modules.regions.command.handler;

import org.bukkit.util.Vector;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * @author monofraps
 */
public class RegionBoundHandler extends OptionHandler<Vector>
{
    public RegionBoundHandler(final CmdLineParser parser, final OptionDef option, final Setter<? super Vector> setter)
    {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(final Parameters params) throws CmdLineException
    {
        if (params.size() < 4)
        {
            throw new RuntimeException("Failed to parse region bounds: You have to pass 4 numeric values");
        }

        final Vector[] vectors = new Vector[2];

        for (int j = 0; j < 2; j++)
        {
            final int x = Integer.valueOf(params.getParameter(j * 2));
            final int z = Integer.valueOf(params.getParameter(j * 2 + 1));
            vectors[j] = new Vector(x, 0, z);
        }

        setter.addValue(vectors[0]);
        setter.addValue(vectors[1]);
        return 4;
    }

    @Override
    public String getDefaultMetaVariable()
    {
        return "bounds";
    }
}
