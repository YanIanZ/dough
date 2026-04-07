package io.github.bakedlibs.dough.skins.nms;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

// Not currently in use.
// This does not correctly update heads on updates currently.
public class PlayerHeadAdapterPaper implements PlayerHeadAdapter {

    @Override
    @ParametersAreNonnullByDefault
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) throws InvocationTargetException, IllegalAccessException {
        BlockState state = block.getState();
        if (!(state instanceof Skull)) return;

        Skull skull = (Skull) state;

        Property property = getTextureProperty(profile);

        PlayerProfile paperPlayerProfile = Bukkit.createProfile(profile.getId(), profile.getName());

        Method getName = ReflectionUtils.getMethod(Property.class, "getName");
        Method getValue = ReflectionUtils.getMethod(Property.class, "getValue");
        Method getSignature = ReflectionUtils.getMethod(Property.class, "getSignature");

        // Old authlib check
        if (getName != null && getValue != null && getSignature != null) {
            paperPlayerProfile.setProperty(new ProfileProperty((String) getName.invoke(property), (String) getValue.invoke(property), (String) getSignature.invoke(property)));
        } else {
            paperPlayerProfile.setProperty(new ProfileProperty(property.name(), property.value(), property.signature()));
        }

        skull.setPlayerProfile(paperPlayerProfile);

        if (sendBlockUpdate) {
            skull.update(true, false);
        }
    }

    @ParametersAreNonnullByDefault
    private Property getTextureProperty(GameProfile profile) throws InvocationTargetException, IllegalAccessException {
        try {
            Method legacyAccessor = ReflectionUtils.getMethod(GameProfile.class, "getProperties");
            Method modernAccessor = ReflectionUtils.getMethod(GameProfile.class, "properties");
            Method accessor = legacyAccessor != null ? legacyAccessor : modernAccessor;

            if (accessor == null) {
                throw new IllegalStateException("No GameProfile properties accessor available");
            }

            Object properties = accessor.invoke(profile);
            Method get = ReflectionUtils.getMethod(properties.getClass(), "get", Object.class);

            if (get == null) {
                throw new IllegalStateException("No PropertyMap#get method available");
            }

            Object textures = get.invoke(properties, "textures");

            if (textures instanceof Collection<?> collection && !collection.isEmpty()) {
                Object first = collection.iterator().next();

                if (first instanceof Property textureProperty) {
                    return textureProperty;
                }
            }

            throw new IllegalStateException("GameProfile does not contain a textures property");
        } catch (NoSuchMethodError e) {
            throw new IllegalStateException("Unable to read GameProfile properties", e);
        }
    }
}
