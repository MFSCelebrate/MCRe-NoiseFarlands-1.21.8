/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.WitherSkullEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class WitherSkullEntityRenderer
extends EntityRenderer<WitherSkullEntity, WitherSkullEntityRenderState> {
    final static private Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither_invulnerable.png");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither.png");
    final private SkullEntityModel model;

    public WitherSkullEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SkullEntityModel(context.getPart(EntityModelLayers.WITHER_SKULL));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 35).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    protected int getBlockLight(WitherSkullEntity witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(WitherSkullEntityRenderState witherSkullEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(witherSkullEntityRenderState)));
        this.model.setHeadRotation(0.0f, witherSkullEntityRenderState.yaw, witherSkullEntityRenderState.pitch);
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(witherSkullEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    private Identifier getTexture(WitherSkullEntityRenderState state) {
        return state.charged ? INVULNERABLE_TEXTURE : TEXTURE;
    }

    @Override
    public WitherSkullEntityRenderState net_minecraft_client_render_entity_state_WitherSkullEntityRenderState_createRenderState() {
        return new WitherSkullEntityRenderState();
    }

    @Override
    public void updateRenderState(WitherSkullEntity witherSkullEntity, WitherSkullEntityRenderState witherSkullEntityRenderState, float f) {
        super.updateRenderState(witherSkullEntity, witherSkullEntityRenderState, f);
        witherSkullEntityRenderState.charged = witherSkullEntity.isCharged();
        witherSkullEntityRenderState.yaw = witherSkullEntity.getLerpedYaw(f);
        witherSkullEntityRenderState.pitch = witherSkullEntity.getLerpedPitch(f);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_WitherSkullEntityRenderState_createRenderState();
    }
}

