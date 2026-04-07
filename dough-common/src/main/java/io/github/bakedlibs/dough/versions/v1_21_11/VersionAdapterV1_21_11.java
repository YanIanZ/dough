package io.github.bakedlibs.dough.versions.v1_21_11;

import io.github.bakedlibs.dough.versions.VersionAdapter;

public class VersionAdapterV1_21_11 implements VersionAdapter {

    @Override
    public void registerItems() {
        // No version-specific item registration is required in Dough itself.
    }

    @Override
    public void handleNMS() {
        // NMS operations are delegated by module-specific adapters.
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
