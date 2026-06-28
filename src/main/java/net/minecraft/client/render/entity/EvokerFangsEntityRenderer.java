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
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerFangsEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity, EvokerFangsEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker_fangs.png");
    final private EvokerFangsEntityModel model;

    public EvokerFangsEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new EvokerFangsEntityModel(context.getPart(EntityModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(EvokerFangsEntityRenderState evokerFangsEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float f = evokerFangsEntityRenderState.animationProgress;
        if (f == 0.0f) {
            return;
        }
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f - evokerFangsEntityRenderState.yaw));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        this.model.setAngles(evokerFangsEntityRenderState);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(evokerFangsEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public EvokerFangsEntityRenderState net_minecraft_client_render_entity_state_EvokerFangsEntityRenderState_createRenderState() {
        return new EvokerFangsEntityRenderState();
    }

    @Override
    public void updateRenderState(EvokerFangsEntity evokerFangsEntity, EvokerFangsEntityRenderState evokerFangsEntityRenderState, float f) {
        super.updateRenderState(evokerFangsEntity, evokerFangsEntityRenderState, f);
        evokerFangsEntityRenderState.yaw = evokerFangsEntity.getYaw();
        evokerFangsEntityRenderState.animationProgress = evokerFangsEntity.getAnimationProgress(f);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_EvokerFangsEntityRenderState_createRenderState();
    }
}

