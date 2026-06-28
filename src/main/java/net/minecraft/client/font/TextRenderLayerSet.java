/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record TextRenderLayerSet(RenderLayer normal, RenderLayer seeThrough, RenderLayer polygonOffset, RenderPipeline guiPipeline) {
    public static TextRenderLayerSet ofIntensity(Identifier textureId) {
        return new TextRenderLayerSet(RenderLayer.getTextIntensity(textureId), RenderLayer.getTextIntensitySeeThrough(textureId), RenderLayer.getTextIntensityPolygonOffset(textureId), RenderPipelines.RENDERTYPE_TEXT_INTENSITY);
    }

    public static TextRenderLayerSet of(Identifier textureId) {
        return new TextRenderLayerSet(RenderLayer.getText(textureId), RenderLayer.getTextSeeThrough(textureId), RenderLayer.getTextPolygonOffset(textureId), RenderPipelines.RENDERTYPE_TEXT);
    }

    public RenderLayer getRenderLayer(TextRenderer.TextLayerType layerType) {
        return switch (layerType) {
            default -> throw new MatchException(null, null);
            case TextRenderer.TextLayerType.NORMAL -> this.normal;
            case TextRenderer.TextLayerType.SEE_THROUGH -> this.seeThrough;
            case TextRenderer.TextLayerType.POLYGON_OFFSET -> this.polygonOffset;
        };
    }
}

