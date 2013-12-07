package com.thevoxelbox.voxelguest.modules.regions.settings;

import com.thevoxelbox.voxelguest.api.modules.regions.Region;
import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.modules.regions.RuleIndex;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Add provider interface
 *
 * @author Monofraps
 */
public final class RuleSettingsDAO
{
    private RuleSettingsDAO()
    {

    }

    /**
     * Saves region rule settings.
     *
     * @param region   The region the settings belong to.
     * @param settings The settings instance to save.
     * @param ruleId   The rule ID of the rule the settings are related to.
     */
    public static void save(final Region region, final RuleExecutionSettings settings, final String ruleId)
    {
        final HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("regionId", region.getId());
        selectRestrictions.put("ruleId", ruleId);

        final List<RuleSettingsData> registeredSettings = Persistence.getInstance().loadAll(RuleSettingsData.class, selectRestrictions);
        final RuleSettingsData newSettings;

        if (registeredSettings.size() > 0)
        {
            newSettings = registeredSettings.get(0);
        }
        else
        {
            newSettings = new RuleSettingsData(ruleId, region.getId(), "{}");
        }

        final JSONObject jsonObject = new JSONObject();
        for (final Field field : settings.getClass().getDeclaredFields())
        {
            field.setAccessible(true);

            try
            {
                jsonObject.put(field.getName(), field.get(settings));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                Bukkit.getLogger().warning("Unable to access " + field.getName() + " value in rule settings object of " + ruleId + " rule.");
            }
        }

        newSettings.setJsonString(jsonObject.toJSONString());
        Persistence.getInstance().save(newSettings);
    }

    /**
     * Reads rule settings form database.
     *
     * @param region The region the settings are related to.
     * @param ruleId The rule the settings are related to.
     *
     * @return Returns a rule settings instance.
     */
    public static RuleExecutionSettings getSettings(final Region region, final String ruleId)
    {
        final HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("regionId", region.getId());
        selectRestrictions.put("ruleId", ruleId);

        final List<RuleSettingsData> registeredSettings = Persistence.getInstance().loadAll(RuleSettingsData.class, selectRestrictions);

        final RuleExecutionSettings executorSettings = RuleIndex.getRule(ruleId).instantiateExecutorSettingsClass();

        if (registeredSettings.size() > 0)
        {
            final RuleSettingsData storedSettings = registeredSettings.get(0);
            final JSONParser parser = new JSONParser();
            final JSONObject jsonObject;

            try
            {
                jsonObject = (JSONObject) parser.parse(storedSettings.getJsonString());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return executorSettings;
            }

            for (Object key : jsonObject.keySet())
            {
                if (key instanceof String)
                {
                    final String fieldName = (String) key;
                    final Field field;

                    try
                    {
                        field = executorSettings.getClass().getDeclaredField(fieldName);
                    }
                    catch (NoSuchFieldException e)
                    {
                        e.printStackTrace();
                        continue;
                    }

                    field.setAccessible(true);
                    try
                    {
                        field.set(executorSettings, jsonObject.get(key));
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return executorSettings;
    }
}
