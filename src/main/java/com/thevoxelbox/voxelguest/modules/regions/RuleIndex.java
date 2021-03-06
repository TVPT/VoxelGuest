package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.rules.RegionRule;
import com.thevoxelbox.voxelguest.api.modules.regions.rules.Rule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Monofraps
 */
public final class RuleIndex
{
    private static Map<String, RegionRule>   ruleIndex  = new HashMap<>();
    private static Map<String, List<String>> ruleGroups = new HashMap<>();

    private RuleIndex()
    {

    }

    /**
     * Scans a package for classes annotated with @Rule and tries to register these as region rules.
     *
     * @param javaPackage The name of the package to scan.
     */
    public static void discoverRulesInPackage(@NotNull final String javaPackage)
    {
        final Reflections reflections = new Reflections(javaPackage);
        Set<Class<?>> rules = reflections.getTypesAnnotatedWith(Rule.class);

        for (final Class<?> rule : rules)
        {
            if (!RegionRule.class.isAssignableFrom(rule))
            {
                Bukkit.getLogger().severe(String.format("Failed to register rule %s. Class has to implement RegionRule.", rule.getName()));
            }

            try
            {
                final RegionRule ruleInstance = (RegionRule) rule.newInstance();
                if (!registerRule(ruleInstance))
                {
                    continue;
                }

                final Rule annotation = rule.getAnnotation(Rule.class);

                for (final String group : annotation.groups())
                {
                    if (!ruleGroups.containsKey(group))
                    {
                        createRuleGroup(group);
                    }

                    ruleGroups.get(group).add(rule.getName());
                }
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                Bukkit.getLogger().severe(String.format("Failed to instantiate RegionRule %s - did you forget to add a public default constructor?", rule.getName()));
                e.printStackTrace();
            }
        }
    }

    /**
     * Resets the RuleIndex.
     */
    public static void flipTables()
    {
        ruleGroups.clear();
        ruleIndex.clear();
    }

    /**
     * Registers a rule.
     *
     * @param rule An instance of the rule to register.
     *
     * @return Returns true if the rule has been registered successfully.
     */
    public static boolean registerRule(@NotNull final RegionRule rule)
    {
        if (ruleIndex.containsKey(rule.getClass().getName()))
        {
            Bukkit.getLogger().severe("Failed to register rule. Rule already registered: " + rule.getClass().getName());
            return false;
        }

        // Check for rule handle conflicts
        for (final RegionRule registeredRule : ruleIndex.values())
        {
            for (final String registeredHandle : registeredRule.getHandles())
            {
                for (final String newHandle : rule.getHandles())
                {
                    if (registeredHandle.equalsIgnoreCase(newHandle))
                    {
                        Bukkit.getLogger().severe("Failed to register rule " + registeredRule.getClass().getName() + ". Rule Handle conflict: " + rule.getClass().getName());
                        return false;
                    }
                }
            }
        }

        ruleIndex.put(rule.getClass().getName(), rule);
        return true;
    }

    /**
     * Translates a rule ID into the corresponding rule instance.
     *
     * @param id The ID to resolve.
     *
     * @return Returns an instance of RegionRule.
     */
    public static RegionRule getRule(@NotNull final String id)
    {
        return ruleIndex.get(translateHandle(id));
    }

    /**
     * Creates a group of rules.
     * Rules groups are meant to be a simple way to enable/disable a bunch of rules at once.
     *
     * @param groupName The name of the group.
     * @param rules     All rules that are in the group.
     */
    public static void createRuleGroup(@NotNull final String groupName, @NotNull final List<String> rules)
    {
        ruleGroups.put(groupName, rules);
    }

    /**
     * Creates an empty group of rules and registers it. The returned List can be used to add rules to the group.
     * Rules groups are meant to be a simple way to enable/disable a bunch of rules at once.
     *
     * @param groupName The name of the group.
     *
     * @return Returns an empty list.
     */
    public static List<String> createRuleGroup(@NotNull final String groupName)
    {
        final List<String> ruleGroup = new ArrayList<>();
        ruleGroups.put(groupName, ruleGroup);

        return ruleGroup;
    }

    /**
     * Returns the list of rules associated with a specific group name
     *
     * @param groupName The name of the group.
     *
     * @return Returns a list of rule IDs.
     */
    public static List<String> getRuleGroup(@NotNull final String groupName)
    {
        return ruleGroups.get(groupName);
    }

    /**
     * Translates a rule handle into the unique ID.
     *
     * @param handle The handle to translate.
     *
     * @return Returns the rule ID.
     */
    public static String translateHandle(@NotNull final String handle)
    {
        if (ruleIndex.containsKey(handle))
        {
            return handle;
        }

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
