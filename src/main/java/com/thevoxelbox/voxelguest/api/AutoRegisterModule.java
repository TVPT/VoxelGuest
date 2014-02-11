package com.thevoxelbox.voxelguest.api;

/**
 * Classes annotated by this will be detected by the auto-registration method of the module manager.
 * @author monofraps
 */
public @interface AutoRegisterModule
{
    boolean autoEnable() default false;
}
