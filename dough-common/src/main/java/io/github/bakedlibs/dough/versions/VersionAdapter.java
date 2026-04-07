package io.github.bakedlibs.dough.versions;

public interface VersionAdapter {

    void registerItems();

    void handleNMS();

    boolean isSupported();
}
