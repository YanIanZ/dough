package io.github.bakedlibs.dough.skins;

import java.net.URL;
import java.lang.reflect.Method;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;

/**
 * A wrapper around {@link GameProfile} that uses composition instead of inheritance.
 * This is necessary because GameProfile is final in newer versions of Minecraft.
 */
public final class CustomGameProfile {

    /**
     * The player name for this profile.
     * "CS-CoreLib" for historical reasons and backwards compatibility.
     */
    private static final String PLAYER_NAME = "CS-CoreLib";

    /**
     * The skin's property key.
     */
    private static final String PROPERTY_KEY = "textures";

    private final UUID uuid;
    private final URL skinUrl;
    private final String texture;
    private final GameProfile gameProfile;

    CustomGameProfile(@Nonnull UUID uuid, @Nullable String texture, @Nonnull URL url) {
        this.uuid = uuid;
        this.skinUrl = url;
        this.texture = texture;
        this.gameProfile = new GameProfile(uuid, PLAYER_NAME);

        if (texture != null) {
            putTextureProperty(gameProfile, texture);
        }
    }

    private static void putTextureProperty(@Nonnull GameProfile profile, @Nonnull String texture) {
        try {
            Object properties = getPropertiesView(profile);
            Method putMethod = properties.getClass().getMethod("put", Object.class, Object.class);
            putMethod.invoke(properties, PROPERTY_KEY, new Property(PROPERTY_KEY, texture));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set texture property on GameProfile", e);
        }
    }

    @Nonnull
    private static Object getPropertiesView(@Nonnull GameProfile profile) throws ReflectiveOperationException {
        try {
            Method legacyAccessor = GameProfile.class.getMethod("getProperties");
            return legacyAccessor.invoke(profile);
        } catch (NoSuchMethodException ignored) {
            Method modernAccessor = GameProfile.class.getMethod("properties");
            return modernAccessor.invoke(profile);
        }
    }

    /**
     * Get the underlying GameProfile.
     * 
     * @return The GameProfile instance
     */
    @Nonnull
    public GameProfile getGameProfile() {
        return gameProfile;
    }

    /**
     * Get the UUID of this profile.
     * 
     * @return The UUID
     */
    @Nonnull
    public UUID getId() {
        return uuid;
    }

    void apply(@Nonnull SkullMeta meta) throws NoSuchFieldException, IllegalAccessException, UnknownServerVersionException {
        // Use PlayerProfile API for 1.20+ (modern approach, works on all servers including Folia)
        if (MinecraftVersion.get().isAtLeast(MinecraftVersion.parse("1.20"))) {
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(this.uuid, PLAYER_NAME);
            PlayerTextures playerTextures = playerProfile.getTextures();
            playerTextures.setSkin(this.skinUrl);
            playerProfile.setTextures(playerTextures);
            meta.setOwnerProfile(playerProfile);
        } else {
            // Legacy approach for older versions using reflection
            // Forces SkullMeta to properly deserialize and serialize the profile
            ReflectionUtils.setFieldValue(meta, "profile", this.gameProfile);

            meta.setOwningPlayer(meta.getOwningPlayer());

            // Now override the texture again
            ReflectionUtils.setFieldValue(meta, "profile", this.gameProfile);
        }
    }

    /**
     * Get the base64 encoded texture from the underlying GameProfile.
     *
     * @return the base64 encoded texture.
     */
    @Nullable
    public String getBase64Texture() {
        return this.texture;
    }
}
