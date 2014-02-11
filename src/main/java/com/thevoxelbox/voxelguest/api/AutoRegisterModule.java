package com.thevoxelbox.voxelguest.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Classes annotated by this will be detected by the auto-registration method of the module manager.
 * @author monofraps
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegisterModule
{
    boolean autoEnable() default false;
}
