package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;
import com.thevoxelbox.voxelguest.modules.regions.rules.BanBlocks;
import com.thevoxelbox.voxelguest.modules.regions.rules.BanItems;
import com.thevoxelbox.voxelguest.modules.regions.rules.DisableCreatureSpawn;
import com.thevoxelbox.voxelguest.modules.regions.rules.DisableFireSpread;
import com.thevoxelbox.voxelguest.modules.regions.rules.DisableFoodLevelChange;
import com.thevoxelbox.voxelguest.modules.regions.rules.DisableMonsterSpawn;
import com.thevoxelbox.voxelguest.modules.regions.rules.DisableTntBreakPainting;
import com.thevoxelbox.voxelguest.modules.regions.rules.RestrictRegionInteraction;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableBlockBurn;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableBlockDrop;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableBlockGrowth;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableBlockPhysics;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableBlockSpread;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableDragonEggMovement;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableIceFormation;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableIceMelting;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableLavaFlow;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableLeaveDecay;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableSnowFormation;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableSnowMelting;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableSoilDehydration;
import com.thevoxelbox.voxelguest.modules.regions.rules.blocks.DisableWaterFlow;
import com.thevoxelbox.voxelguest.modules.regions.rules.explosion.DisableCreeperExplosions;
import com.thevoxelbox.voxelguest.modules.regions.rules.explosion.DisableTntExplosion;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.DisableEnchantment;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableBlockExplosionDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableCactusDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableDrowningDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableEntityDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableEntityExplosionDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableFallDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableFireDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableFireTickDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableHungerDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableLavaDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableLightningDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableMagicDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableMonsterDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisablePoisonDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableProjectileDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisablePvpDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableSuffocationDamage;
import com.thevoxelbox.voxelguest.modules.regions.rules.player.damage.DisableVoidDamage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Monofraps
 */
public final class RuleIndex
{
    private static Map<String, RegionRule> ruleIndex = new HashMap<>();
    private static Map<String, List<String>> ruleGroups = new HashMap<>();

    static
    {
        registerRule(new DisableBlockBurn());
        registerRule(new DisableBlockDrop());
        registerRule(new DisableBlockGrowth());
        registerRule(new DisableBlockPhysics());
        registerRule(new DisableBlockSpread());
        registerRule(new DisableDragonEggMovement());
        registerRule(new DisableIceFormation());
        registerRule(new DisableIceMelting());
        registerRule(new DisableLavaFlow());
        registerRule(new DisableLeaveDecay());
        registerRule(new DisableSnowFormation());
        registerRule(new DisableSnowMelting());
        registerRule(new DisableSoilDehydration());
        registerRule(new DisableWaterFlow());

        registerRule(new DisableCreeperExplosions());
        registerRule(new DisableTntExplosion());

        registerRule(new DisableBlockExplosionDamage());
        registerRule(new DisableCactusDamage());
        registerRule(new DisableDrowningDamage());
        registerRule(new DisableEntityDamage());
        registerRule(new DisableEntityExplosionDamage());
        registerRule(new DisableFallDamage());
        registerRule(new DisableFireDamage());
        registerRule(new DisableFireTickDamage());
        registerRule(new DisableHungerDamage());
        registerRule(new DisableLavaDamage());
        registerRule(new DisableLightningDamage());
        registerRule(new DisableMagicDamage());
        registerRule(new DisableMonsterDamage());
        registerRule(new DisablePoisonDamage());
        registerRule(new DisableProjectileDamage());
        registerRule(new DisablePvpDamage());
        registerRule(new DisableSuffocationDamage());
        registerRule(new DisableVoidDamage());

        registerRule(new DisableEnchantment());

        registerRule(new BanBlocks());
        registerRule(new BanItems());
        registerRule(new DisableCreatureSpawn());
        registerRule(new DisableFireSpread());
        registerRule(new DisableFoodLevelChange());
        registerRule(new DisableMonsterSpawn());
        registerRule(new DisableTntBreakPainting());
        registerRule(new RestrictRegionInteraction());

        final List<String> playerDamageGroup = createRuleGroup("def_PlayerDamage");
        playerDamageGroup.add(DisableBlockExplosionDamage.class.getName());
        playerDamageGroup.add(DisableCactusDamage.class.getName());
        playerDamageGroup.add(DisableDrowningDamage.class.getName());
        playerDamageGroup.add(DisableEntityDamage.class.getName());
        playerDamageGroup.add(DisableEntityExplosionDamage.class.getName());
        playerDamageGroup.add(DisableFallDamage.class.getName());
        playerDamageGroup.add(DisableFireDamage.class.getName());
        playerDamageGroup.add(DisableFireTickDamage.class.getName());
        playerDamageGroup.add(DisableHungerDamage.class.getName());
        playerDamageGroup.add(DisableLavaDamage.class.getName());
        playerDamageGroup.add(DisableLightningDamage.class.getName());
        playerDamageGroup.add(DisableMagicDamage.class.getName());
        playerDamageGroup.add(DisableMonsterDamage.class.getName());
        playerDamageGroup.add(DisablePoisonDamage.class.getName());
        playerDamageGroup.add(DisablePvpDamage.class.getName());
        playerDamageGroup.add(DisableSuffocationDamage.class.getName());
        playerDamageGroup.add(DisableVoidDamage.class.getName());
    }

    private RuleIndex()
    {

    }

    public static void registerRule(@NotNull final RegionRule rule)
    {
        if (ruleIndex.containsKey(rule.getClass().getName()))
        {
            Bukkit.getLogger().severe("Failed to register rule. Rule already registered: " + rule.getClass().getName());
            return;
        }

        for (final RegionRule hRule : ruleIndex.values())
        {
            for (final String registeredHandle : hRule.getHandles())
            {
                for (final String newHandle : rule.getHandles())
                {
                    if (registeredHandle.equalsIgnoreCase(newHandle))
                    {
                        Bukkit.getLogger().severe("Failed to register rule. Rule Handle conflict: " + rule.getClass().getName());
                        return;
                    }
                }
            }
        }

        ruleIndex.put(rule.getClass().getName(), rule);
    }

    public static RegionRule getRule(@NotNull final String id)
    {
        return ruleIndex.get(id);
    }

    public static void createRuleGroup(@NotNull final String groupName, @NotNull final List<String> rules)
    {
        ruleGroups.put(groupName, rules);
    }

    public static List<String> createRuleGroup(@NotNull final String groupName)
    {
        final List<String> ruleGroup = new ArrayList<>();
        ruleGroups.put(groupName, ruleGroup);

        return ruleGroup;
    }

    public static List<String> getRuleGroup(@NotNull final String groupName)
    {
        return ruleGroups.get(groupName);
    }

    public static String translateHandle(@NotNull final String handle)
    {
        for (String ruleId : ruleIndex.keySet())
        {
            RegionRule registeredRule = ruleIndex.get(ruleId);
            for (String currentHandle : registeredRule.getHandles())
            {
                if (currentHandle.equalsIgnoreCase(handle))
                {
                    return ruleId;
                }
            }
        }

        return "";
    }
}
