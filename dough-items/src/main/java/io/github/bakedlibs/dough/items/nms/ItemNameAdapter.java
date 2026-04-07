package io.github.bakedlibs.dough.items.nms;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.papermc.lib.PaperLib;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.DoughLogger;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.bakedlibs.dough.versions.VersionResolver;
import io.github.bakedlibs.dough.versions.VersionSupportInfo;

public interface ItemNameAdapter {

    @ParametersAreNonnullByDefault
    @Nonnull
    String getName(ItemStack item) throws IllegalAccessException, InvocationTargetException;

    public static @Nullable ItemNameAdapter get() {
        try {
            VersionSupportInfo supportInfo = VersionResolver.resolveInfo();

            if (MinecraftVersion.isMocked()) {
                // Special case for MockBukkit
                return new ItemNameAdapterMockBukkit();
            }

            MinecraftVersion version = MinecraftVersion.get();
            String adapterName = supportInfo.getAdapterName();

            if ("v26_1_1".equals(adapterName)) {
                if (PaperLib.isPaper()) {
                    return new ItemNameAdapterPaper();
                }

                return new ItemNameAdapter20v5();
            }

            if ("v1_21_11".equals(adapterName) && PaperLib.isPaper()) {
                return new ItemNameAdapterPaper();
            }

            if (version.isAtLeast(1, 20, 4) && PaperLib.isPaper()) {
                return new ItemNameAdapterPaper();
            }

            if (version.isAtLeast(1, 20, 5)) {
                return new ItemNameAdapter20v5();
            } else if (version.isAtLeast(1, 20)) {
                return new ItemNameAdapter20();
            } else if (version.isAtLeast(1, 19)) {
                return new ItemNameAdapter19();
            } else if (version.isAtLeast(1, 18, 2)) {
                return new ItemNameAdapter18v2();
            } else if (version.isAtLeast(1, 18)) {
                // 1.18+ mappings
                return new ItemNameAdapter18();
            } else if (version.isAtLeast(1, 17)) {
                // 1.17+ mappings
                return new ItemNameAdapter17();
            } else {
                // Old mappings
                return new ItemNameAdapterBefore17();
            }
        } catch (Exception x) {
            DoughLogger logger = new DoughLogger("items");
            logger.log(Level.SEVERE, "Failed to detect items nbt methods", x);
            return null;
        }

    }
}
