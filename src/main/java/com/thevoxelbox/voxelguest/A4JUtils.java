package com.thevoxelbox.voxelguest;

import org.bukkit.command.CommandSender;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.ByteArrayOutputStream;

/**
 * An args4j utility class.
 *
 * @author Monofraps
 */
public final class A4JUtils
{
    private A4JUtils()
    {

    }

    /**
     * Wrapper function to simplify args4j parser usage.
     *
     * @param argumentClass The class that holds the a4j annotated fields.
     * @param args          The arguments to parse.
     * @param sender        The command sender. This is used to send error messages.
     * @param <T>           The type of the object (with a4j annotated fields) that will hold the parsed arguments.
     *
     * @return An instance of <code>argumentClass</code> holding the parse result.
     */
    public static <T> T parse(final Class<T> argumentClass, final String[] args, final CommandSender sender)
    {
        final T argumentsHolder;

        try
        {
            argumentsHolder = argumentClass.newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            sender.sendMessage("An internal error occurred.");
            return null;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            sender.sendMessage("An internal error occurred.");
            return null;
        }

        final CmdLineParser parser = new CmdLineParser(argumentsHolder);

        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            e.printStackTrace();
            sender.sendMessage(e.getMessage());
            final ByteArrayOutputStream usageBuffer = new ByteArrayOutputStream();
            parser.printUsage(usageBuffer);
            sender.sendMessage(usageBuffer.toString());
            return null;
        }

        return argumentsHolder;
    }
}
