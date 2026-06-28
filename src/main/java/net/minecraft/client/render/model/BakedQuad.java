/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public record BakedQuad(int[] vertexData, int tintIndex, Direction face, Sprite sprite, boolean shade, int lightEmission) {
    public boolean hasTint() {
        return this.tintIndex != -1;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedQuad.class, "vertices;tintIndex;direction;sprite;shade;lightEmission", "vertexData", "tintIndex", "face", "sprite", "shade", "lightEmission"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedQuad.class, "vertices;tintIndex;direction;sprite;shade;lightEmission", "vertexData", "tintIndex", "face", "sprite", "shade", "lightEmission"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedQuad.class, "vertices;tintIndex;direction;sprite;shade;lightEmission", "vertexData", "tintIndex", "face", "sprite", "shade", "lightEmission"}, this, object);
    }
}

