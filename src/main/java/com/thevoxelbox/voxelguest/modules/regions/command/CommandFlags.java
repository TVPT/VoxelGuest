package com.thevoxelbox.voxelguest.modules.regions.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Helps dealing with the multitudes of various flags used to define region properties.
 * Also, Helps with the command help.
 *
 * @author TheCryoknight
 */
public enum CommandFlags
{
    FIRE_SPREAD_ALLOWED("-fs", "Fire spread"),
    LEAF_DECAY_ALLOWED("-ld", "Leaf decay"),
    BLOCK_GROWTH_ALLOWED("-bg", "Block growth"),
    BLOCK_SPREAD_ALLOWED("-bs", "Block spread"),
    BLOCK_DROP_ALLOWED("-bd", "Block drop"),
    CREEPER_EXPLOSION_ALLOWED("-ce", "Creeper explosion"),
    TNT_BREAKING_PAINTINGS_ALLOWED("-tbp", "TNT breaking paintings"),
    LAVA_FLOW_ALLOWED("-lf", "Lava flow"),
    WATER_FLOW_ALLOWED("-wf", "Water flow"),
    DRAGON_EGG_MOVEMENT_ALLOWED("-dem", "Dragon egg movement"),
    SNOW_MELTING_ALLOWED("-sm", "Snow melting"),
    ICE_MELTING_ALLOWED("-im", "Ice melting"),
    SNOW_FORMATION_ALLOWED("-sf", "Formation of snow"),
    ICE_FORMATION_ALLOWED("-if", "Formtion of ice"),
    ENCHANTING_ALLOWED("-en", "Enchantment"),
    PHYSICS_ALLOWED("-phy", "block Physics"),
    BANNED_BLOCKS("-bb", "Banned blocks"),
    BANNED_ITEMS("-bi", "Banned items"),
    SOIL_DEHYDRATION_ALLOWED("-sd", "Soil dehydration"),

    MOB_SPAWN_ALLOWED("-ms", "Mob Spawn"),
    CREATURE_SPAWN_ALLOWED("-cs", "Creature Spawn"),

    //Player
    PVP_DAMMAGE_ALLOWED("-pvp", "Player vs. Player dammage"),
    LAVA_DAMMAGE_ALLOWED("-lad", "Lava damage"),
    CACTUS_DAMMAGE_ALLOWED("-cd", "Cactus dammage"),
    TNT_DAMMAGE_ALLOWED("-tntd", "TNT damage"),
    DROWNING_DAMMAGE_ALLOWED("-dd", "Drowning damage"),
    EXPLOSIVE_DAMMAGE_ALLOWED("-exd", "Explosives player damage"),
    FALL_DAMMAGE_ALLOWED("-fad", "Fall damage"),
    FIRE_DAMMAGE_ALLOWED("-fid", "Fire damage"),
    POISON_DAMMAGE_ALLOWED("-poid", "Poison damage"),
    MAGIC_DAMMAGE_ALLOWED("-mad", "Magic damage"),
    PROJECTILE_DAMMAGE_ALLOWED("-prod", "Projectile damage"),
    HUNGER_DAMMAGE_ALLOWED("-hund", "Hunger dammage"),
    VOID_DAMMAGE_ALLOWED("-void", "Void Dammage"),
    FIRETICK_DAMMAGE_ALLOWED("-ftd", "Fire tick dammage"),
    LIGHTNING_DAMMAGE_ALLOWED("-lid", "Lightning dammage"),
    SUFFOCATION_DAMMAGE_ALLOWED("-sud", "Suffocation damage"),
    FOOD_CHANGE_ALLOWED("-flc", "Food change");

    private final String cmdFlag;
    private final String name;

    private CommandFlags(final String cmdFlag, final String name)
    {
        this.cmdFlag = cmdFlag;
        this.name = name;
    }

    /**
     * Parses the various region flags from region commands to map.
     *
     * @param args arguments to parse
     *
     * @return A Map containing the flags chosen as the key and unparsed state of the flag as the value
     */
    public static Map<CommandFlags, String> parseFlags(final String[] args)
    {
        Map<CommandFlags, String> parsedFlags = new HashMap<>();
        for (String arg : args)
        {
            String[] parseArg = arg.split(":");
            if (parseArg.length == 2)
            {
                for (CommandFlags flag : CommandFlags.values())
                {
                    if (parseArg[0].equalsIgnoreCase(flag.getCommandFlag()))
                    {
                        parsedFlags.put(flag, parseArg[1]);
                        break;
                    }
                }
            }
        }
        return parsedFlags;
    }

    /**
     * @return The command line flag for this element
     */
    public String getCommandFlag()
    {
        return cmdFlag;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
