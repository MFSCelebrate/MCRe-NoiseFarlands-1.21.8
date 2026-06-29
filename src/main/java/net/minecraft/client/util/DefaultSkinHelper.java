/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DefaultSkinHelper {
    final static private SkinTextures[] SKINS = new SkinTextures[]{DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/alex.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/ari.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/efe.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/kai.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/makena.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/noor.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/steve.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/sunny.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/slim/zuri.png", SkinTextures.Model.SLIM), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/alex.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/ari.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/efe.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/kai.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/makena.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/noor.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/steve.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/sunny.png", SkinTextures.Model.WIDE), DefaultSkinHelper.createSkinTextures("textures/entity/player/wide/zuri.png", SkinTextures.Model.WIDE)};

    public static Identifier getTexture() {
        return DefaultSkinHelper.getSteve().texture();
    }

    public static SkinTextures getSteve() {
        return SKINS[6];
    }

    public static SkinTextures getSkinTextures(UUID uuid) {
        return SKINS[Math.floorMod(uuid.hashCode(), SKINS.length)];
    }

    public static SkinTextures getSkinTextures(GameProfile profile) {
        return DefaultSkinHelper.getSkinTextures(profile.getId());
    }

    private static SkinTextures createSkinTextures(String texture, SkinTextures.Model model) {
        return new SkinTextures(Identifier.ofVanilla(texture), null, null, null, model, true);
    }
}

