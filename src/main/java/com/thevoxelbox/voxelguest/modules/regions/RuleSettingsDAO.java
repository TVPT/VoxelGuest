package com.thevoxelbox.voxelguest.modules.regions;

import com.thevoxelbox.voxelguest.api.modules.regions.RuleExecutionSettings;
import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * @author Monofraps
 */
public class RuleSettingsDAO
{
    public static void save(final Region region, final GuestRuleExecutionSettings settings, final String executorId)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("regionId", region.getId());
        selectRestrictions.put("executorId", region.getId());

        final List<RuleExecutorSettingsModel> registeredSettings = Persistence.getInstance().loadAll(RuleExecutorSettingsModel.class, selectRestrictions);
        final RuleExecutorSettingsModel newSettings;

        if (registeredSettings.size() > 0)
        {
            newSettings = registeredSettings.get(0);
        }
        else
        {
            newSettings = new RuleExecutorSettingsModel(executorId, region.getId(), "{}");
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
                Bukkit.getLogger().warning("Unable to access " + field.getName() + " value in executor settings object of " + executorId + " executor.");
            }
        }

        newSettings.setJsonString(jsonObject.toJSONString());
        Persistence.getInstance().save(newSettings);
    }

    public static RuleExecutionSettings getSettings(final Region region, final String executorId)
    {
        HashMap<String, Object> selectRestrictions = new HashMap<>();
        selectRestrictions.put("regionId", region.getId());
        selectRestrictions.put("executorId", region.getId());

        final List<RuleExecutorSettingsModel> registeredSettings = Persistence.getInstance().loadAll(RuleExecutorSettingsModel.class, selectRestrictions);
        final RuleExecutionSettings executorSettings = RuleIndex.getRule(executorId).instantiateExecutorSettingsClass();

        if (registeredSettings.size() > 0)
        {
            final RuleExecutorSettingsModel storedSettings = registeredSettings.get(0);
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
