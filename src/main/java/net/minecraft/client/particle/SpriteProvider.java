/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public interface SpriteProvider {
    public Sprite getSprite(int var1, int var2);

    public Sprite getSprite(Random var1);
}

