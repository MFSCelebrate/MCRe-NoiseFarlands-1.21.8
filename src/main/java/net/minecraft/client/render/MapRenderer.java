/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.MapDecorationsAtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class MapRenderer {
    final static private float field_53102 = -0.01f;
    final static private float field_53103 = -0.001f;
    final static public int DEFAULT_IMAGE_WIDTH = 128;
    final static public int DEFAULT_IMAGE_HEIGHT = 128;
    final private MapTextureManager textureManager;
    final private MapDecorationsAtlasManager decorationsAtlasManager;

    public MapRenderer(MapDecorationsAtlasManager decorationsAtlasManager, MapTextureManager textureManager) {
        this.decorationsAtlasManager = decorationsAtlasManager;
        this.textureManager = textureManager;
    }

    public void draw(MapRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean bl, int light) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(state.texture));
        vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(Colors.WHITE).texture(0.0f, 1.0f).light(light);
        vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(Colors.WHITE).texture(1.0f, 1.0f).light(light);
        vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(Colors.WHITE).texture(1.0f, 0.0f).light(light);
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(Colors.WHITE).texture(0.0f, 0.0f).light(light);
        int i = 0;
        for (MapRenderState.Decoration decoration : state.decorations) {
            if (bl && !decoration.alwaysRendered) continue;
            matrices.push();
            matrices.translate((float)decoration.x / 2.0f + 64.0f, (float)decoration.z / 2.0f + 64.0f, -0.02f);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)(decoration.rotation * 360) / 16.0f));
            matrices.scale(4.0f, 4.0f, 3.0f);
            matrices.translate(-0.125f, 0.125f, 0.0f);
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            Sprite sprite = decoration.sprite;
            if (sprite != null) {
                VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getText(sprite.getAtlasId()));
                vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, (float)i * -0.001f).color(Colors.WHITE).texture(sprite.getMinU(), sprite.getMinV()).light(light);
                vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, (float)i * -0.001f).color(Colors.WHITE).texture(sprite.getMaxU(), sprite.getMinV()).light(light);
                vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, (float)i * -0.001f).color(Colors.WHITE).texture(sprite.getMaxU(), sprite.getMaxV()).light(light);
                vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, (float)i * -0.001f).color(Colors.WHITE).texture(sprite.getMinU(), sprite.getMaxV()).light(light);
                matrices.pop();
            }
            if (decoration.name != null) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                float f = textRenderer.getWidth(decoration.name);
                float f2 = 25.0f / f;
                Objects.requireNonNull(textRenderer);
                float g = MathHelper.clamp(f2, 0.0f, 0.6666667f);
                matrices.push();
                matrices.translate((float)decoration.x / 2.0f + 64.0f - f * g / 2.0f, (float)decoration.z / 2.0f + 64.0f + 4.0f, -0.025f);
                matrices.scale(g, g, -1.0f);
                matrices.translate(0.0f, 0.0f, 0.1f);
                textRenderer.draw(decoration.name, 0.0f, 0.0f, -1, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, Integer.MIN_VALUE, light);
                matrices.pop();
            }
            ++i;
        }
    }

    public void update(MapIdComponent mapId, MapState mapState, MapRenderState renderState) {
        renderState.texture = this.textureManager.getTextureId(mapId, mapState);
        renderState.decorations.clear();
        for (MapDecoration mapDecoration : mapState.getDecorations()) {
            renderState.decorations.add(this.createDecoration(mapDecoration));
        }
    }

    private MapRenderState.Decoration createDecoration(MapDecoration decoration) {
        MapRenderState.Decoration decoration2 = new MapRenderState.Decoration();
        decoration2.sprite = this.decorationsAtlasManager.getSprite(decoration);
        decoration2.x = decoration.x();
        decoration2.z = decoration.z();
        decoration2.rotation = decoration.rotation();
        decoration2.name = decoration.name().orElse(null);
        decoration2.alwaysRendered = decoration.isAlwaysRendered();
        return decoration2;
    }
}

