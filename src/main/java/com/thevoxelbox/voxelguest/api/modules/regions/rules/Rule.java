package com.thevoxelbox.voxelguest.api.modules.regions.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author monofraps
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Rule
{
    /**
     * A list of groups to add this rule to.
     * @return .
     */
    String[] groups();
}
