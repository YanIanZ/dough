package io.github.bakedlibs.dough.versions;

import javax.annotation.Nonnull;

public final class VersionSupportInfo {

    private final String detectedVersion;
    private final SupportLevel supportLevel;
    private final String adapterName;
    private final boolean supported;
    private final VersionAdapter adapter;

    public VersionSupportInfo(
        @Nonnull String detectedVersion,
        @Nonnull SupportLevel supportLevel,
        @Nonnull String adapterName,
        boolean supported,
        @Nonnull VersionAdapter adapter
    ) {
        this.detectedVersion = detectedVersion;
        this.supportLevel = supportLevel;
        this.adapterName = adapterName;
        this.supported = supported;
        this.adapter = adapter;
    }

    public @Nonnull String getDetectedVersion() {
        return detectedVersion;
    }

    public @Nonnull SupportLevel getSupportLevel() {
        return supportLevel;
    }

    public @Nonnull String getAdapterName() {
        return adapterName;
    }

    public boolean isSupported() {
        return supported;
    }

    public @Nonnull VersionAdapter getAdapter() {
        return adapter;
    }
}
