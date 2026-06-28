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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LlamaSpitEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityRenderer
extends EntityRenderer<LlamaSpitEntity, LlamaSpitEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/llama/spit.png");
    final private LlamaSpitEntityModel model;

    public LlamaSpitEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new LlamaSpitEntityModel(context.getPart(EntityModelLayers.LLAMA_SPIT));
    }

    @Override
    public void render(LlamaSpitEntityRenderState llamaSpitEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0f, 0.15f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(llamaSpitEntityRenderState.yaw - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(llamaSpitEntityRenderState.pitch));
        this.model.setAngles(llamaSpitEntityRenderState);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(llamaSpitEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public LlamaSpitEntityRenderState net_minecraft_client_render_entity_state_LlamaSpitEntityRenderState_createRenderState() {
        return new LlamaSpitEntityRenderState();
    }

    @Override
    public void updateRenderState(LlamaSpitEntity llamaSpitEntity, LlamaSpitEntityRenderState llamaSpitEntityRenderState, float f) {
        super.updateRenderState(llamaSpitEntity, llamaSpitEntityRenderState, f);
        llamaSpitEntityRenderState.pitch = llamaSpitEntity.getLerpedPitch(f);
        llamaSpitEntityRenderState.yaw = llamaSpitEntity.getLerpedYaw(f);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_LlamaSpitEntityRenderState_createRenderState();
    }
}

