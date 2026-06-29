/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EmptyBakedGlyph
extends BakedGlyph {
    final static public EmptyBakedGlyph INSTANCE = new EmptyBakedGlyph();

    public EmptyBakedGlyph() {
        super(TextRenderLayerSet.of(Identifier.ofVanilla("")), null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void draw(BakedGlyph.DrawnGlyph glyph, Matrix4f matrix, VertexConsumer vertexConsumer, int light, boolean fixedZ) {
    }
}

