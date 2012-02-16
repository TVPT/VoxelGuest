package com.thevoxelbox.voxelguest.util;

import java.lang.reflect.Method;

public abstract class Formatter {
    
    public static Formatter selectFormatter(Class<? extends Formatter> cls) {
        try {
            Method method = cls.getMethod("install");
            
            Formatter formatter = (Formatter) method.invoke(null);
            return formatter;
        } catch (Throwable t) {
            return null;
        }
    }
    
    public String encodeColors(String input) {
        for (FormatColors color : FormatColors.values()) {
            input = input.replace(color.getColorCode(), color.getBukkitColorCode());
        }
        
        return input;
    }
    
    public abstract Formatter install();
    
    public abstract String[] format(String in);
}

enum FormatColors {
    WHITE("&f"),
    DARK_BLUE("&1"),
    DARK_GREEN("&2"),
    TEAL("&3"),
    DARK_RED("&4"),
    PURPLE("&5"),
    ORANGE("&6"),
    LIGHT_GREY("&7"),
    DARK_GREY("&8"),
    INDIGO("&9"),
    LIGHT_GREEN("&a"),
    CYAN("&b"),
    RED("&c"),
    PINK("&d"),
    YELLOW("&e"),
    BLACK("&0");
    
    private String color;
    
    private FormatColors(String c) {
        color = c;
    }
    
    public String getColorCode() {
        return color;
    }
    
    public String getBukkitColorCode() {
        return color.replace("&", "\u00A7");
    }
 }
