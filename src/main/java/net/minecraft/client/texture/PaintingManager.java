/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.atlas.Atlases;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PaintingManager
extends SpriteAtlasHolder {
    final static private Identifier PAINTING_BACK_ID = Identifier.ofVanilla("back");

    public PaintingManager(TextureManager manager) {
        super(manager, Identifier.ofVanilla("textures/atlas/paintings.png"), Atlases.PAINTINGS);
    }

    public Sprite getPaintingSprite(PaintingVariant variant) {
        return this.getSprite(variant.assetId());
    }

    public Sprite getBackSprite() {
        return this.getSprite(PAINTING_BACK_ID);
    }
}

