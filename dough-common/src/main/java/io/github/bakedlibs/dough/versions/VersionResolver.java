package io.github.bakedlibs.dough.versions;

import io.github.bakedlibs.dough.common.DoughLogger;
import io.github.bakedlibs.dough.versions.v1_21_11.VersionAdapterV1_21_11;
import io.github.bakedlibs.dough.versions.v26_1_1.VersionAdapterV26_1_1;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class VersionResolver {

    private static final String LTS_VERSION = "1.21.11";
    private static final String EXPERIMENTAL_VERSION = "26.1.1";
    private static final String LTS_ADAPTER = "v1_21_11";
    private static final String EXPERIMENTAL_ADAPTER = "v26_1_1";
    private static final AtomicBoolean STARTUP_LOGGED = new AtomicBoolean(false);

    private VersionResolver() {
    }

    public static @Nonnull VersionAdapter resolve() {
        return resolveInfo().getAdapter();
    }

    public static @Nonnull VersionSupportInfo resolveInfo() {
        String detectedVersion = detectMinecraftVersion();
        VersionSupportInfo info = resolveInfo(detectedVersion);
        logStartupInfo(info);
        return info;
    }

    public static @Nonnull VersionSupportInfo resolveInfo(@Nonnull String detectedVersion) {
        if (LTS_VERSION.equals(detectedVersion)) {
            return new VersionSupportInfo(detectedVersion, SupportLevel.STABLE, LTS_ADAPTER, true, new VersionAdapterV1_21_11());
        }

        if (EXPERIMENTAL_VERSION.equals(detectedVersion)) {
            return new VersionSupportInfo(detectedVersion, SupportLevel.EXPERIMENTAL, EXPERIMENTAL_ADAPTER, true, new VersionAdapterV26_1_1());
        }

        return fallbackToStable(detectedVersion);
    }

    private static @Nonnull VersionSupportInfo fallbackToStable(@Nonnull String detectedVersion) {
        return new VersionSupportInfo(detectedVersion, SupportLevel.STABLE, LTS_ADAPTER, false, new VersionAdapterV1_21_11());
    }

    private static @Nonnull String detectMinecraftVersion() {
        try {
            String version = Bukkit.getMinecraftVersion();
            if (version != null && !version.isEmpty()) {
                return version;
            }
        } catch (Exception ignored) {
            // Fall through to SemanticVersion-based detection.
        }

        try {
            MinecraftVersion version = MinecraftVersion.get();
            return version.getMajorVersion() + "." + version.getMinorVersion() + "." + version.getPatchVersion();
        } catch (Exception ignored) {
            return "unknown";
        }
    }

    private static void logStartupInfo(@Nonnull VersionSupportInfo info) {
        if (!STARTUP_LOGGED.compareAndSet(false, true)) {
            return;
        }

        DoughLogger logger = new DoughLogger("versions");
        logger.log(Level.INFO, "[Dough] Minecraft Version: {0}", info.getDetectedVersion());
        logger.log(Level.INFO, "[Dough] Support Level: {0}", info.getSupportLevel());
        logger.log(Level.INFO, "[Dough] Adapter Loaded: {0}", info.getAdapterName());

        if (!info.isSupported()) {
            logger.log(Level.WARNING, "[Dough] Unsupported version detected, using fallback stable adapter {0}", LTS_ADAPTER);
        }
    }
}
