package com.thevoxelbox.voxelguest.modules.general.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Executes entity purge commands.
 */
public final class EntityPurgeCommandExecutor implements TabExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.RED + "Please enter a world name");
            return false;
        }

        boolean isAllEntitys = false;
        for (String arg : args)
        {
            if (arg.equalsIgnoreCase("-all"))
            {
                isAllEntitys = true;
                break;
            }
        }

        if (args[0].equals("*"))
        {
            for (World world : Bukkit.getWorlds())
            {
                sender.sendMessage(ChatColor.GRAY + "Purging entities from: " + ChatColor.GREEN + world.getName());
                Thread purgeThread = new EntityPurgeThread(world, sender, isAllEntitys);
                purgeThread.start();
            }
            return true;
        }
        for (String worldName : args)
        {
            final World world = Bukkit.getWorld(worldName);

            if (world != null)
            {
                sender.sendMessage(ChatColor.GRAY + "Purging entities from: " + ChatColor.GREEN + world.getName());
                Thread purgeThread = new EntityPurgeThread(world, sender, isAllEntitys);
                purgeThread.start();
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Unknown world name " + worldName);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        if (sender.hasPermission("voxelguest.general.ep"))
        {
            final List<String> worldNames = this.getWorldNames();
            if (args.length == 0)
            {
                return worldNames;
            }
        }
        return Collections.emptyList();
    }

    /**
     * @return Returns a list of names of all worlds currently loaded.
     */
    public List<String> getWorldNames()
    {
        final List<String> worldNames = new ArrayList<>();
        for (World world : Bukkit.getWorlds())
        {
            worldNames.add(world.getName());
        }
        return worldNames;
    }

    /**
     * Represents a runnable purge thread.
     */
    private final class EntityPurgeThread extends Thread
    {
        private final World         world;
        private final CommandSender sender;
        private final boolean       allEntities;

        public EntityPurgeThread(final World world, final CommandSender sender, final boolean allEntities)
        {
            this.world = world;
            this.sender = sender;
            this.allEntities = allEntities;
        }

        @Override
        public void run()
        {
            final List<Entity> entities = world.getEntities();

            if (this.allEntities)
            {
                for (Entity entity : entities)
                {
                    if (!((entity instanceof Player) || (entity instanceof Painting) || (entity instanceof ItemFrame)))
                    {
                        entity.remove();
                    }
                }
            }
            else
            {
                for (Entity entity : entities)
                {
                    if (!((entity instanceof Player) || (entity instanceof Painting) || (entity instanceof ItemFrame) || (entity instanceof Minecart) || (entity instanceof Villager)))
                    {
                        entity.remove();
                    }
                }
            }
            sender.sendMessage("§aEntity purge complete");
        }
    }
}
