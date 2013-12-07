package com.thevoxelbox.voxelguest.modules.regions.command.subcommand;

import org.bukkit.command.CommandSender;
import org.kohsuke.args4j.Argument;

/**
 * @author monofraps
 */
public final class Help implements Command
{
    @Argument(index = 0, metaVar = "<help-string>")
    private String helpString = "general";

    @Override
    public boolean execute(final CommandSender sender)
    {
        switch (helpString)
        {
            case "general":
                sender.sendMessage("The /vgregion command is your interface to VoxelGuest's region module functionality.");
                sender.sendMessage("Available subcommands:");
                sender.sendMessage("  create - Creates a region");
                sender.sendMessage("  modify - Modifies a region");
                sender.sendMessage("  resize - resizes a region");
                sender.sendMessage("  help - prints help about various commands");
                sender.sendMessage("If you need more detailed information about a command, type /vgregion help <commandName>");
                break;

            case "create":
                sender.sendMessage("/vgregion create creates a region based on input parameters");
                sender.sendMessage("/vgregion create <regionName> <x1> <z1> <x2> <z2>");
                sender.sendMessage("e.g. /vgregion create MyCoolRegion 0 0 500 500 - will create a region called MyCoolRegion from (0|0) to (500|500)");
                sender.sendMessage("Created region ar always from y=0 to world height");
                break;

            default:
                sender.sendMessage("There is no help about this available, yet.");
        }

        return true;
    }
}
