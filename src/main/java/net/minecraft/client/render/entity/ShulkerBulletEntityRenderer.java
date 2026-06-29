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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityRenderer
extends EntityRenderer<ShulkerBulletEntity, ShulkerBulletEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/shulker/spark.png");
    final static private RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
    final private ShulkerBulletEntityModel model;

    public ShulkerBulletEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ShulkerBulletEntityModel(context.getPart(EntityModelLayers.SHULKER_BULLET));
    }

    @Override
    protected int getBlockLight(ShulkerBulletEntity shulkerBulletEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(ShulkerBulletEntityRenderState shulkerBulletEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float f = shulkerBulletEntityRenderState.age;
        matrixStack.translate(0.0f, 0.15f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(f * 0.1f) * 180.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.cos(f * 0.1f) * 180.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(f * 0.15f) * 360.0f));
        matrixStack.scale(-0.5f, -0.5f, 0.5f);
        this.model.setAngles(shulkerBulletEntityRenderState);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(LAYER);
        this.model.render(matrixStack, vertexConsumer2, 1, OverlayTexture.DEFAULT_UV, 0x26FFFFFF);
        matrixStack.pop();
        super.render(shulkerBulletEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public ShulkerBulletEntityRenderState net_minecraft_client_render_entity_state_ShulkerBulletEntityRenderState_createRenderState() {
        return new ShulkerBulletEntityRenderState();
    }

    @Override
    public void updateRenderState(ShulkerBulletEntity shulkerBulletEntity, ShulkerBulletEntityRenderState shulkerBulletEntityRenderState, float f) {
        super.updateRenderState(shulkerBulletEntity, shulkerBulletEntityRenderState, f);
        shulkerBulletEntityRenderState.yaw = shulkerBulletEntity.getLerpedYaw(f);
        shulkerBulletEntityRenderState.pitch = shulkerBulletEntity.getLerpedPitch(f);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ShulkerBulletEntityRenderState_createRenderState();
    }
}

