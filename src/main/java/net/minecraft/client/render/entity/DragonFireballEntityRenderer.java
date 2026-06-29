/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity, EntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_fireball.png");
    final static private RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    public DragonFireballEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(DragonFireballEntity dragonFireballEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(2.0f, 2.0f, 2.0f);
        matrices.multiply((Quaternionfc)this.dispatcher.getRotation());
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, entry, light, 0.0f, 0, 0, 1);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, entry, light, 1.0f, 0, 1, 1);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, entry, light, 1.0f, 1, 1, 0);
        DragonFireballEntityRenderer.produceVertex(vertexConsumer, entry, light, 0.0f, 1, 0, 0);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, int light, float x, int z, int textureU, int textureV) {
        vertexConsumer.vertex(matrix, x - 0.5f, (float)z - 0.25f, 0.0f).color(Colors.WHITE).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

