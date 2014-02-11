package com.thevoxelbox.voxelguest.utils;

import java.util.Map;

/**
 * @author monofraps
 */
public final class MessageFormatter
{
    private MessageFormatter()
    {

    }

    public static String format(final String formatString, final Map<String, String> parameters)
    {
        String output = formatString;
        for (final Map.Entry<String, String> entry : parameters.entrySet())
        {
            output = output.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return output;
    }
}
