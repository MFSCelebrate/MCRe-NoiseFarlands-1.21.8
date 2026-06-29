/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.sprite.FabricErrorCollectingSpriteGetter
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

@Environment(value=EnvType.CLIENT)
public interface ErrorCollectingSpriteGetter
extends FabricErrorCollectingSpriteGetter {
    public Sprite get(SpriteIdentifier var1, SimpleModel var2);

    public Sprite getMissing(String var1, SimpleModel var2);

    default public Sprite get(ModelTextures texture, String name, SimpleModel model) {
        SpriteIdentifier spriteIdentifier = texture.get(name);
        return spriteIdentifier != null ? this.get(spriteIdentifier, model) : this.getMissing(name, model);
    }
}

