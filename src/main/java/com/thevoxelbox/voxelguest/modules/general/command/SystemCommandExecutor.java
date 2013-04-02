package com.thevoxelbox.voxelguest.modules.general.command;

import com.google.common.base.Preconditions;
import com.sun.management.OperatingSystemMXBean;
import com.thevoxelbox.voxelguest.modules.general.runnables.TPSTicker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles system commands.
 *
 * @author Monofraps
 * @author TheCryoknight
 * @author Deamon
 */
public final class SystemCommandExecutor implements TabExecutor
{
    private static final String[] COMMAND_FLAGS = {"gc", "mem", "lag"};
    private static final int TPS_PER_SECOND_THRESHOLD = 20;
    private static final int BAR_SEGMENTS = 20;
    private static final int BYTES_PER_MB = 1048576;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        Preconditions.checkNotNull(sender);
        Preconditions.checkNotNull(command);
        Preconditions.checkNotNull(args);

        if (args.length == 0)
        {
            this.printSpecs(sender);
            return true;
        }

        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("gc"))
            {
                System.gc();
                return true;
            }

            if (args[0].equalsIgnoreCase("mem"))
            {
                printMemInfo(sender, true);
                return true;
            }

            if (args[0].equalsIgnoreCase("lag"))
            {
                sender.sendMessage("§7TPS§f: " + DisplayUtils.renderTPSBar(TPSTicker.calculateTPS(), TPS_PER_SECOND_THRESHOLD));
                return true;
            }
        }

        return false;
    }

    private void printMemInfo(final CommandSender sender, final boolean detailed)
    {
        if (detailed)
        {
            sender.sendMessage("§8==============================");
            sender.sendMessage("§bMemory Details");
            final double memUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / SystemCommandExecutor.BYTES_PER_MB;
            final double memMax = Runtime.getRuntime().maxMemory() / SystemCommandExecutor.BYTES_PER_MB;

            sender.sendMessage("§8==============================");
            sender.sendMessage("§7JVM Memory§f: " + DisplayUtils.renderBar(memUsed, memMax));
            for (MemoryPoolMXBean memData : ManagementFactory.getMemoryPoolMXBeans())
            {
                sender.sendMessage("§8==============================");
                sender.sendMessage("§7Name§f: §a" + memData.getName());
                sender.sendMessage("§7Type§f: §a" + memData.getType());
                sender.sendMessage("§7Usage§f: §a" + (memData.getUsage().getUsed() / SystemCommandExecutor.BYTES_PER_MB) + "MB");
                sender.sendMessage("§7Peak usage§f: §a" + (memData.getPeakUsage().getUsed() / SystemCommandExecutor.BYTES_PER_MB) + "MB");
                sender.sendMessage("§7Max usage§f: §a" + (memData.getUsage().getMax() / SystemCommandExecutor.BYTES_PER_MB) + "MB");

                if (memData.isUsageThresholdSupported())
                {
                    sender.sendMessage("§7Threshold§f: §a" + memData.getUsageThreshold());
                }
                if (memData.getUsage().getMax() != -1)
                {
                    final long typeMemUsed = memData.getUsage().getUsed();
                    final long typeMemMax = memData.getUsage().getMax();
                    sender.sendMessage(DisplayUtils.renderBar(typeMemUsed, typeMemMax, true, ""));
                }
            }
            sender.sendMessage("§8==============================");
        }
        else
        {
            sender.sendMessage("§8==============================");
            sender.sendMessage("§bMemory Specs");

            final double memUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / SystemCommandExecutor.BYTES_PER_MB;
            final double memMax = Runtime.getRuntime().maxMemory() / SystemCommandExecutor.BYTES_PER_MB;
            double permGenUsage = -1;
            for (final MemoryPoolMXBean item : ManagementFactory.getMemoryPoolMXBeans())
            {
                final String name = item.getName();
                final MemoryUsage usage = item.getUsage();
                if (name != null && name.contains("Perm Gen"))
                {
                    permGenUsage = Math.round(((double) usage.getUsed() / (double) usage.getMax()) * 100f);
                    break;
                }
            }

            sender.sendMessage("§7JVM Memory§f: " + DisplayUtils.renderBar(memUsed, memMax));
            sender.sendMessage("§7JVM Heap Memory (MB)§f: §a" + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / SystemCommandExecutor.BYTES_PER_MB);
            sender.sendMessage("§7JVM Free Memory (MB)§f: §a" + Runtime.getRuntime().freeMemory() / SystemCommandExecutor.BYTES_PER_MB);
            sender.sendMessage("§7JVM Maximum Memory (MB)§f: §a" + ((Runtime.getRuntime().maxMemory() == Long.MAX_VALUE) ? "No defined limit" : Runtime.getRuntime().maxMemory() / SystemCommandExecutor.BYTES_PER_MB));
            sender.sendMessage("§7JVM Used Memory (MB)§f: §a" + Runtime.getRuntime().totalMemory() / SystemCommandExecutor.BYTES_PER_MB);
            sender.sendMessage("§7JVM Perm Gen usage§f: §a" + DisplayUtils.colorPercentage(permGenUsage) + "%");
        }
    }

    private void printSpecs(final CommandSender sender)
    {
        sender.sendMessage("§8==============================");
        sender.sendMessage("§bServer Specs");

        OperatingSystemMXBean operatingSystemBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        sender.sendMessage("§7Operating System§f: §a" + operatingSystemBean.getName() + " version " + operatingSystemBean.getVersion());
        sender.sendMessage("§7Architecture§f: §a" + operatingSystemBean.getArch());

        sender.sendMessage("§8==============================");
        sender.sendMessage("§bCPU Specs");
        sender.sendMessage("§7CPU Usage§f: " + DisplayUtils.renderBar(operatingSystemBean.getSystemCpuLoad(), 1));
        sender.sendMessage("§7Available cores§f: §a" + Runtime.getRuntime().availableProcessors());
        sender.sendMessage("§7Current Threads§f: §a" + Thread.activeCount());

        printMemInfo(sender, false);

        sender.sendMessage("§8==============================");
        sender.sendMessage("§bBukkit Specs");
        sender.sendMessage("§7CraftBukkit Version§f: §a" + Bukkit.getVersion());

        final List<World> loadedWorlds = Bukkit.getWorlds();
        sender.sendMessage("§7Loaded Worlds§f:");
        for (World world : loadedWorlds)
        {
            sender.sendMessage("§a- " + world.getName() + " §7[§fChunks: §6" + world.getLoadedChunks().length + "§f, Entities: §6" + world.getEntities().size() + "§7]");
        }

        sender.sendMessage("§7TPS§f: " + DisplayUtils.renderTPSBar(TPSTicker.calculateTPS(), TPS_PER_SECOND_THRESHOLD));
    }

    /**
     * @author Monofraps
     */
    private static class DisplayUtils
    {
        public static String colorPercentage(final double percentage)
        {
            return colorPercentage(percentage, 50, 70, 90);
        }

        public static String colorPercentage(final double percentage, final double yellow, final double orange, final double red)
        {
            if (percentage > red)
            {
                return ChatColor.RED + String.valueOf(percentage);
            }

            if (percentage > orange)
            {
                return ChatColor.GOLD + String.valueOf(percentage);
            }

            if (percentage > yellow)
            {
                return ChatColor.YELLOW + String.valueOf(percentage);
            }

            return ChatColor.GREEN + String.valueOf(percentage);
        }

        public static String renderTPSBar(final double actualValue, final double maxValue)
        {
            return renderBar(actualValue > TPS_PER_SECOND_THRESHOLD ? TPS_PER_SECOND_THRESHOLD : actualValue, maxValue, false, "TPS");
        }

        public static String renderBar(final double actualValue, final double maxValue)
        {
            return renderBar(actualValue, maxValue, true, "");
        }

        public static String renderBar(final double actualValue, final double maxValue, final boolean showPercentage, final String numOutSuffix)
        {
            final double percentage = BAR_SEGMENTS * (actualValue / maxValue);
            final int percentageRounded = (int) Math.round(percentage);
            String bar = "§8[";

            for (int i = 0; i < BAR_SEGMENTS; i++)
            {
                bar += ((i + 1) <= percentageRounded) ? "§b#" : "§7_";
            }

            final double percent = (percentage / BAR_SEGMENTS);
            final NumberFormat format = NumberFormat.getPercentInstance();

            if (showPercentage)
            {
                return (bar + "§8] (§f" + format.format(percent) + "§8)");
            }
            else
            {
                return (bar + "§8] (§f" + Math.round(actualValue) + " " + numOutSuffix + "§8)");
            }
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        if (sender.hasPermission("voxelguest.gereral.sys"))
        {
            if (args.length == 0)
            {
                return Arrays.asList(SystemCommandExecutor.COMMAND_FLAGS);
            }
            else
            {
                final List<String> tmpList = new ArrayList<>();
                final String completingParam = args[args.length - 1];
                for (String flag : SystemCommandExecutor.COMMAND_FLAGS)
                {
                    if (flag.toLowerCase().startsWith(completingParam.toLowerCase()))
                    {
                        tmpList.add(flag);
                    }
                }
                return tmpList;
            }
        }
        return Collections.emptyList();
    }
}
