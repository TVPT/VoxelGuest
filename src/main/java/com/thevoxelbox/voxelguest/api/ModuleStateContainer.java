package com.thevoxelbox.voxelguest.api;

import com.thevoxelbox.voxelguest.api.modules.Module;

/**
 * Handles the state and state transitions of single modules.
 * @author monofraps
 */
public interface ModuleStateContainer
{
    boolean enable();

    boolean disable();

    void associateModule(final Module module);

    void persist();

    boolean loadStateFromPersistence();

    Module getModule();

    boolean isEnabled();
}
