/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of The VoxelPlugineering Team nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.thevoxelbox.voxelguest.modules;

import com.patrickanker.lib.config.PropertyConfiguration;
import java.lang.reflect.Field;
import java.util.HashMap;

public class ModuleConfiguration {

    protected final PropertyConfiguration config;
    private final Module parentModule;

    public ModuleConfiguration(Module parent)
    {
        parentModule = parent;
        config = new PropertyConfiguration(parentModule.getName(), "/VoxelGuest/modules");
    }

    public HashMap<String, Object> getAllEntries()
    {
        return config.getAllEntries();
    }

    public Object getEntry(String key)
    {
        return config.getEntry(key);
    }

    public String getString(String key)
    {
        return config.getString(key);
    }

    public boolean getBoolean(String key)
    {
        return config.getBoolean(key);
    }

    public int getInt(String key)
    {
        return config.getInt(key);
    }

    public double getDouble(String key)
    {
        return config.getDouble(key);
    }

    public void setEntry(String key, Object value)
    {
        config.setEntry(key, value);
    }

    public void setString(String key, String value)
    {
        config.setString(key, value);
    }

    public void setBoolean(String key, boolean value)
    {
        config.setBoolean(key, value);
    }

    public void setInt(String key, int value)
    {
        config.setInt(key, value);
    }

    public void setDouble(String key, double value)
    {
        config.setDouble(key, value);
    }

    public void load()
    {
        registerFieldSettings(this.getClass());
        config.load();
    }

    public void reset()
    {
        registerFieldSettings(getClass());
    }

    public void save()
    {
        config.save();
    }

    private void registerFieldSettings(Class<? extends ModuleConfiguration> cls)
    {
        for (Field field : cls.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                registerFieldSetting(field);
            } catch (IllegalArgumentException ex) {
                continue;
            } catch (IllegalAccessException ex) {
                continue;
            }
        }
    }

    private void registerFieldSetting(Field field) throws IllegalArgumentException,
            IllegalAccessException
    {

        if (!field.isAnnotationPresent(Setting.class)) {
            return;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        Setting setting = field.getAnnotation(Setting.class);
        String key = setting.value();
        Object value = field.get(this);

        setEntry(key, value);
    }
}
