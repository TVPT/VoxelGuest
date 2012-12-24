package com.thevoxelbox.voxelguest;

import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.thevoxelbox.voxelguest.modules.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The World Protection Module was created to help maintain various server
 * aspects, such as grief prevention. The World Protection Module offers extreme
 * amounts of customization based on config variables.
 *
 * Handles: Block Drops Leaf Decay Ice Melting/Forming Snow Melting/Forming All
 * Fire/Explosion Events Enchanting(allow or disallow) Vehicle Damage Vehicle
 * Creation Weather Controls Portal Creation
 */
@MetaData(name = "World Protection", description = "Various world protection methods.")
public class WorldProtectionModule extends Module {

    public Server s = Bukkit.getServer();
    public HashSet<Integer> bannedblocks = new HashSet<Integer>();
    public HashSet<Integer> banneditems = new HashSet<Integer>();
    private List<EntityPurgeThread> purgeThreads = new ArrayList<EntityPurgeThread>();
    private List<String> protectedWorlds = new ArrayList<String>();

    public WorldProtectionModule()
    {
        super(WorldProtectionModule.class.getAnnotation(MetaData.class));
    }

    class WorldProtectionConfiguration extends ModuleConfiguration {

        @Setting("enable-multi-worlds") public boolean multiworld = false;
        @Setting("protected-worlds") public String protectedWorlds = "";
        @Setting("disable-block-drops") public boolean blockdrops = false;
        @Setting("disable-leaf-decay") public boolean leafdecay = false;
        @Setting("disable-ice-melting") public boolean icemelt = false;
        @Setting("disable-snow-melting")public boolean snowmelt = false;
        @Setting("disable-ice-formation") public boolean iceform = false;
        @Setting("disable-snow-formation") public boolean snowform = false;
        @Setting("disable-block-burning") public boolean blockburn = false;
        @Setting("disable-block-ignite") public boolean blockignite = true;
        @Setting("disable-block-growth") public boolean blockgrow = true;
        @Setting("disable-fire-spread") public boolean firespred = false;
        @Setting("disable-enchanting") public boolean enchanting = false;
        @Setting("disable-creeper-explosion") public boolean creeperexplode = false;
        @Setting("unplacable-blocks") public String unplacable = "8,9,10,11,46";
        @Setting("unusable-items") public String unusableitems = "325,326,327";

        public WorldProtectionConfiguration(WorldProtectionModule parent)
        {
            super(parent);
        }
    }

    @Override
    public void enable()
    {
        setConfiguration(new WorldProtectionConfiguration(this));
        bannedblocks.clear();
        banneditems.clear();

        try {
            String[] st1 = getConfiguration().getString("unplacable-blocks").split(",");

            if (st1 != null) {
                for (String str : st1) {
                    bannedblocks.add(Integer.parseInt(str));
                }
            }
        } catch (Exception ex) {
            VoxelGuest.log("Ignoring block blacklist");
        }

        try {
            String[] st2 = getConfiguration().getString("unusable-items").split(",");

            if (st2 != null) {
                for (String str : st2) {
                    banneditems.add(Integer.parseInt(str));
                }
            }
        } catch (Exception ex) {
            VoxelGuest.log("Ignoring item blacklist");
        }

        if (getConfiguration().getBoolean("enable-multi-worlds")) {
            String[] worlds = getConfiguration().getString("protected-worlds").split(",");
            for (String world : worlds) {
                protectedWorlds.add(world);
            }
        }
    }

    @Override
    public String getLoadMessage()
    {
        return "World Protection has been loaded.";
    }

    @Override
    public void disable()
    {
        if (!purgeThreads.isEmpty()) {
            for (EntityPurgeThread thread : purgeThreads) {
                thread.interrupt();
            }
        }
    }

    @Command(aliases = {"entitypurge", "ep"},
        bounds = {1, 1},
        help = "Purge all non-players and non-paintings from worlds using\n"
        + "§c/entitypurge [world]")
    @CommandPermission("voxelguest.protection.entitypurge")
    public void entityPurge(CommandSender cs, String[] args)
    {
        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            cs.sendMessage("§cNo world found by that name. Did you spell the name correctly?");
            return;
        }

        EntityPurgeThread thread = new EntityPurgeThread(world, cs);
        thread.start();
    }

    private boolean isProtectedWorld(World world)
    {
        if (protectedWorlds.isEmpty()) {
            return true;
        }

        for (String _w : protectedWorlds) {
            if (world.getName().equals(_w)) {
                return true;
            }
        }

        return false;
    }

    /*
     * World Protection - BlockBreak Event Written by: Razorcane
     *
     * Handles Block Drops.
     */
    @ModuleEvent(event = BlockBreakEvent.class)
    public void onBlockBreak(BukkitEventWrapper wrapper)
    {
        BlockBreakEvent event = (BlockBreakEvent) wrapper.getEvent();
        Player p = event.getPlayer();
        Block b = event.getBlock();

        if (!isProtectedWorld(b.getWorld())) {
            return;
        }

        if (getConfiguration().getBoolean("diable-block-drops")) {
            b.setType(Material.AIR);
            event.setCancelled(true);
        }
        
        if (!bannedblocks.isEmpty() && bannedblocks.contains(b.getTypeId()) && !PermissionsManager.getHandler().hasPermission(p.getName(), "voxelguest.protection.bannedblocks")) {
            event.getPlayer().sendMessage(ChatColor.RED + "YOU CANNOT BREAK THIS.");
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - BlockPlace Event Written by: Razorcane
     *
     * Handles prevention of certain blocks from being placed.
     */
    @ModuleEvent(event = BlockPlaceEvent.class)
    public void onBlockPlace(BukkitEventWrapper wrapper)
    {
        BlockPlaceEvent event = (BlockPlaceEvent) wrapper.getEvent();
        Player player = event.getPlayer();
        Player[] p = Bukkit.getOnlinePlayers();
        int onlinecount = Bukkit.getOnlinePlayers().length;
        Block b = event.getBlock();

        if (!isProtectedWorld(b.getWorld())) {
            return;
        }

        if (!bannedblocks.isEmpty() && bannedblocks.contains(b.getTypeId()) && !PermissionsManager.getHandler().hasPermission(player.getName(), "voxelguest.protection.bannedblocks")) {
            event.getPlayer().sendMessage(ChatColor.RED + "YOU CANNOT PLACE THIS.");
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - PlayerInteract Event Written by: Razorcane
     *
     * Handles the prevention of using restricted items.
     */
    @ModuleEvent(event = PlayerInteractEvent.class)
    public void onPlayerInteract(BukkitEventWrapper wrapper)
    {
        PlayerInteractEvent event = (PlayerInteractEvent) wrapper.getEvent();
        Player player = event.getPlayer();
        Player[] p = Bukkit.getOnlinePlayers();
        int onlinecount = Bukkit.getOnlinePlayers().length;
        ItemStack is = event.getItem();

        if (!isProtectedWorld(player.getWorld())) {
            return;
        }

        if (is != null && !banneditems.isEmpty() && banneditems.contains(is.getTypeId()) && !PermissionsManager.getHandler().hasPermission(player.getName(), "voxelguest.protection.banneditems")) {
            event.getPlayer().sendMessage(ChatColor.RED + "YOU CANNOT USE THIS.");
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - LeavesDecay Event Written by: Razorcane
     *
     * Handles leaf decay, obviously.
     */
    @ModuleEvent(event = LeavesDecayEvent.class, ignoreCancelledEvents = true)
    public void onLeavesDecay(BukkitEventWrapper wrapper)
    {
        LeavesDecayEvent event = (LeavesDecayEvent) wrapper.getEvent();

        if (!isProtectedWorld(event.getBlock().getWorld())) {
            return;
        }

        if (getConfiguration().getBoolean("disable-leaf-decay")) {
            event.setCancelled(true);
        }
    }

    /*
     * Block Growth - BlockGrow Event Written by: Billyyjoee
     *
     * Handles Block Growth such as;
     * Wheat,
     * Pumpkins,
     * Sugar Cane,
     * Watermelons &
     * Cactus
     */
     @ModuleEvent(event = BlockGrowEvent.class, ignore CancelledEvents = true)
     public void onBlockGrow(BukkitEventWrapper wrapper)
    {
        BlockGrowEvent event = (BlockGrowEvent) wrapper.getEvent();

        if (!isProtectedWorld(event.getBlock().getWorld())) {
            return;
        }

        if (getConfiguration().getBoolean("disable-block-growth")) {
            event.setCancelled(true);
        }
    }
     
    /*
     * World Protection - BlockFade Event Written by: Razorcane
     *
     * Handles Snow/Ice Melting
     */
    @ModuleEvent(event = BlockFadeEvent.class, ignoreCancelledEvents = true)
    public void onBlockFade(BukkitEventWrapper wrapper)
    {
        BlockFadeEvent event = (BlockFadeEvent) wrapper.getEvent();
        Block b = event.getNewState().getBlock();

        if (!isProtectedWorld(b.getWorld())) {
            return;
        }

        if (b.getType().equals(Material.ICE) && getConfiguration().getBoolean("disable-ice-melting")) {
            event.setCancelled(true);
            return;
        }

        if (b.getType().equals(Material.SNOW) && getConfiguration().getBoolean("disable-snow-melting")) {
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - BlockForm Event Written by: Razorcane
     *
     * Handles Ice/Snow Forming
     */
    @ModuleEvent(event = BlockFormEvent.class, ignoreCancelledEvents = true)
    public void onBlockForm(BukkitEventWrapper wrapper)
    {
        BlockFormEvent event = (BlockFormEvent) wrapper.getEvent();
        Block b = event.getBlock();

        if (!isProtectedWorld(b.getWorld())) {
            return;
        }

        if (b.getType().equals(Material.ICE) && getConfiguration().getBoolean("disable-ice-formation")) {
            event.setCancelled(true);
            return;
        }

        if (b.getType().equals(Material.SNOW) && getConfiguration().getBoolean("disable-snow-formation")) {
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - BlockBurn Event Written by: Razorcane
     *
     * Handles Fire burning blocks
     */
    @ModuleEvent(event = BlockBurnEvent.class)
    public void onBlockBurn(BukkitEventWrapper wrapper)
    {
        BlockBurnEvent event = (BlockBurnEvent) wrapper.getEvent();

        if (!isProtectedWorld(event.getBlock().getWorld())) {
            return;
        }

        if (getConfiguration().getBoolean("disable-block-burning")) {
            event.setCancelled(true);
        }
    }

    /*
     * World Protection BlockIgnite Event Written by: Razorcane
     *
     * Handles the ignition(fire) of blocks, whether it be from lava, lightning,
     * or player means.
     */
    @ModuleEvent(event = BlockIgniteEvent.class)
    public void onBlockIgnite(BukkitEventWrapper wrapper)
    {
        BlockIgniteEvent event = (BlockIgniteEvent) wrapper.getEvent();
        IgniteCause cause = event.getCause();

        if (!isProtectedWorld(event.getBlock().getWorld())) {
            return;
        }

        boolean fireSpread = (cause == IgniteCause.SPREAD || cause == IgniteCause.LAVA || cause == IgniteCause.LIGHTNING);

        if (fireSpread && getConfiguration().getBoolean("disable-block-ignite")) {
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - BlockSpread Event Written by: Razorcane
     *
     * Handles Fire Spread.
     */
    @ModuleEvent(event = BlockSpreadEvent.class)
    public void onBlockSpread(BukkitEventWrapper wrapper)
    {
        BlockSpreadEvent event = (BlockSpreadEvent) wrapper.getEvent();
        boolean fireSpread = (event.getNewState().getType() == Material.FIRE);

        if (!isProtectedWorld(event.getBlock().getWorld())) {
            return;
        }

        if (fireSpread && getConfiguration().getBoolean("disable-fire-spread")) {
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - EnchantItem Event Written by: Razorcane
     *
     * Handles Item Enchanting, obviously.
     */
    @ModuleEvent(event = EnchantItemEvent.class)
    public void onEnchantItem(BukkitEventWrapper wrapper)
    {
        EnchantItemEvent event = (EnchantItemEvent) wrapper.getEvent();

        if (!isProtectedWorld(event.getEnchanter().getWorld())) {
            return;
        }

        if (getConfiguration().getBoolean("disable-enchanting")) {
            event.setCancelled(true);
        }
    }

    /*
     * World Protection - EntityExplode Event Written by: Razorcane
     *
     * Handles mob explosions, such as creepers.
     */
    @ModuleEvent(event = EntityExplodeEvent.class)
    public void onEntityExplode(BukkitEventWrapper wrapper)
    {
        EntityExplodeEvent event = (EntityExplodeEvent) wrapper.getEvent();

        if (!isProtectedWorld(event.getEntity().getWorld())) {
            return;
        }

        if (getConfiguration().getBoolean("disable-creeper-explosion")) {
            event.setCancelled(true);
        }
    }

    class EntityPurgeThread extends Thread {

        private final World world;
        private final CommandSender sender;

        public EntityPurgeThread(World w, CommandSender cs)
        {
            world = w;
            sender = cs;
            registerPurgeThread();
        }

        @Override
        public void run()
        {
            Entity[] entities = new Entity[world.getEntities().size()];
            entities = world.getEntities().toArray(entities);

            for (Entity e : entities) {
                if (!((e instanceof Player) || (e instanceof Painting) || (e instanceof ItemFrame))) {
                    e.remove();
                }
            }

            sender.sendMessage("§aEntity purge complete");

            if (purgeThreads.contains(this)) {
                purgeThreads.remove(this);
            }
        }

        private void registerPurgeThread()
        {
            purgeThreads.add(this);
        }
    }
}
