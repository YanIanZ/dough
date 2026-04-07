package io.github.bakedlibs.dough.versions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TestVersionResolver {

    @Test
    void testStableLtsMapping() {
        VersionSupportInfo info = VersionResolver.resolveInfo("1.21.11");

        assertEquals("1.21.11", info.getDetectedVersion());
        assertEquals(SupportLevel.STABLE, info.getSupportLevel());
        assertEquals("v1_21_11", info.getAdapterName());
        assertTrue(info.isSupported());
        assertTrue(info.getAdapter().isSupported());
    }

    @Test
    void testExperimentalMapping() {
        VersionSupportInfo info = VersionResolver.resolveInfo("26.1.1");

        assertEquals("26.1.1", info.getDetectedVersion());
        assertEquals(SupportLevel.EXPERIMENTAL, info.getSupportLevel());
        assertEquals("v26_1_1", info.getAdapterName());
        assertTrue(info.isSupported());
        assertTrue(info.getAdapter().isSupported());
    }

    @Test
    void testUnsupportedFallbackToStable() {
        VersionSupportInfo info = VersionResolver.resolveInfo("99.99.99");

        assertEquals("99.99.99", info.getDetectedVersion());
        assertEquals(SupportLevel.STABLE, info.getSupportLevel());
        assertEquals("v1_21_11", info.getAdapterName());
        assertFalse(info.isSupported());
        assertTrue(info.getAdapter().isSupported());
    }
}
