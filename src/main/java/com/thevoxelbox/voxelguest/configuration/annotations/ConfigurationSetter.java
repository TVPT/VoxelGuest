package com.thevoxelbox.voxelguest.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MikeMatrix
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigurationSetter
{
    /**
     * The name of the configuration entry. (This name will be displayed in the plain text file.) Needs a matching @ConfigurationSetter statement.
     *
     * @return Returns the name of the configuration entry.
     */
    String value();
}
