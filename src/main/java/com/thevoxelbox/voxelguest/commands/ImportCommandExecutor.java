package com.thevoxelbox.voxelguest.commands;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.A4JUtils;
import com.thevoxelbox.voxelguest.commands.arguments.ImportCommandArguments;
import com.thevoxelbox.voxelguest.modules.asshat.ban.Banlist;
import com.thevoxelbox.voxelguest.modules.general.AfkMessage;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistDAO;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

/**
 * Handles /vgimport commands.
 *
 * @author Monofraps
 * @author TheCryoknight
 */
public final class ImportCommandExecutor implements TabExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        // we assume the player wants to see the usage if he does not enter any argument
        if (args.length == 0)
        {
            return false;
        }

        final ImportCommandArguments arguments = A4JUtils.parse(ImportCommandArguments.class, args, sender);
        if (arguments == null)
        {
            return false;
        }

        if (arguments.isImportBans())
        {
            importVG3Bans(sender);
            importVG4Bans(sender);
            sender.sendMessage(ChatColor.GRAY + "Banlist import done.");
        }

        if (arguments.isImportAfmMessages())
        {
            if (this.importAfkMessages(sender))
            {
                sender.sendMessage(ChatColor.GRAY + "Afk message import completed.");
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Afk message import failed.");
            }
        }

        if (arguments.isImportGreylist())
        {
            if (this.importGreylist(sender))
            {
                sender.sendMessage(ChatColor.GRAY + "greylist import completed.");
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "greylist import failed.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        List<String> matches = new ArrayList<>();
        if (args.length >= 1)
        {
            if (args[0].toLowerCase().startsWith("-b"))
            {
                matches.add("-bans");
            }
            else if (args[0].toLowerCase().startsWith("-g"))
            {
                matches.add("-greylist");
            }
            else if (args[0].toLowerCase().startsWith("-a"))
            {
                matches.add("-afkmessages");
            }
        }
        else
        {
            matches.add("-bans");
            matches.add("-greylist");
            matches.add("-afkmessages");

        }
        return matches;
    }

    /**
     * Imports all bans from VoxelGuest version 3.
     * If a player is already banned it will do nothing.
     * These are the bans stored at "plugins\VoxelGuest\banned.txt".
     *
     * @param sender User running the command
     */
    private void importVG3Bans(final CommandSender sender)
    {
        final File banfileVG3 = new File("plugins" + File.separator + "VoxelGuest" + File.separator + "banned.txt");
        final Scanner scanner;

        try
        {
            scanner = new Scanner(new FileInputStream(banfileVG3));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "VG3 Ban import: Could not find banned.txt file.");
            return;
        }

        try
        {
            while (scanner.hasNextLine())
            {
                final String[] data = scanner.nextLine().split(">");
                if (!Banlist.isPlayerBanned(data[0]))
                {
                    Banlist.ban(data[0], data[1]);
                }
                sender.sendMessage(ChatColor.DARK_AQUA + "Imported ban: " + ChatColor.GOLD + data[0]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An internal error occurred: " + e.getMessage());
        }
        finally
        {
            scanner.close();
        }
    }

    /**
     * Imports all bans from VoxelGuest version 4.
     * If a player is already banned it will do nothing.
     * These are the bans stored at "plugins/VoxelGuest/asshatmitigation/banned.properties".
     */
    private void importVG4Bans(final CommandSender sender)
    {
        final Properties properties = new Properties();
        final File banfileVG4 = new File("plugins" + File.separator + "VoxelGuest" + File.separator + "asshatmitigation" + File.separator + "banned.properties");

        try
        {
            properties.load(new FileInputStream(banfileVG4));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "VG4 Ban import: Could not find banned.properties file.");
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "VG4 Ban import: Could not load banned.properties file.");
            return;
        }

        for (final Entry<Object, Object> ban : properties.entrySet())
        {
            try
            {
                Preconditions.checkState(ban.getKey() instanceof String);
                Preconditions.checkState(ban.getValue() instanceof String);
            }
            catch (IllegalStateException e)
            {
                e.printStackTrace();
                continue;
            }

            final String bannedName = (String) ban.getKey();
            final String banReason = (String) ban.getValue();
            if (!Banlist.isPlayerBanned(bannedName))
            {
                Banlist.ban(bannedName, banReason);
            }
            sender.sendMessage(ChatColor.DARK_AQUA + "Imported ban: " + ChatColor.GOLD + bannedName);
        }
    }

    /**
     * Imports all random afk messages from VoxelGuest 3.
     *
     * @param sender Sender to inform of imports.
     *
     * @return Returns true if the import was successful, false otherwise.
     */
    private boolean importAfkMessages(final CommandSender sender)
    {
        final File msgFile = new File("plugins" + File.separator + "VoxelGuest" + File.separator + "afkmsg.txt");
        final Scanner scan;
        try
        {
            scan = new Scanner(new FileInputStream(msgFile));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not find afkmsg.txt file.");
            return false;
        }

        try
        {
            while (scan.hasNextLine())
            {
                final String line = scan.nextLine();
                Persistence.getInstance().save(new AfkMessage(line));
                sender.sendMessage(ChatColor.DARK_AQUA + "Imported message: " + ChatColor.GRAY + line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An internal error occurred: " + e.getMessage());
            return false;
        }
        finally
        {
            scan.close();
        }
        return true;
    }

    /**
     * Imports all greylists from prior versions of VoxelGuest.
     *
     * @param sender Sender to inform of imports.
     *
     * @return Returns true if the import was successful, false otherwise.
     */
    private boolean importGreylist(final CommandSender sender)
    {
        final File afkMsgFile = new File("plugins" + File.separator + "VoxelGuest" + File.separator + "greylist.txt");
        final Scanner scanner;

        try
        {
            scanner = new Scanner(new FileInputStream(afkMsgFile));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not find greylist.txt file.");
            return false;
        }

        try
        {
            while (scanner.hasNextLine())
            {
                final String name = scanner.nextLine();
                if (!GreylistDAO.isOnPersistentGreylist(name))
                {
                    GreylistDAO.greylist(name);
                    sender.sendMessage(ChatColor.DARK_AQUA + "Imported greylistee: " + ChatColor.GRAY + name);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An internal error occurred: " + e.getMessage());
            return false;
        }
        finally
        {
            scanner.close();
        }

        return true;
    }
}
