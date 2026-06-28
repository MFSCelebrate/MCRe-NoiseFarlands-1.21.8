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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityRenderer
extends EntityRenderer<EndCrystalEntity, EndCrystalEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
    final static private RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    final private EndCrystalEntityModel model;

    public EndCrystalEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.model = new EndCrystalEntityModel(context.getPart(EntityModelLayers.END_CRYSTAL));
    }

    @Override
    public void render(EndCrystalEntityRenderState endCrystalEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        this.model.setAngles(endCrystalEntityRenderState);
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(END_CRYSTAL), 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        Vec3d vec3d = endCrystalEntityRenderState.beamOffset;
        if (vec3d != null) {
            float f = EndCrystalEntityRenderer.getYOffset(endCrystalEntityRenderState.age);
            float g = (float)vec3d.x;
            float h = (float)vec3d.y;
            float j = (float)vec3d.z;
            matrixStack.translate(vec3d);
            EnderDragonEntityRenderer.renderCrystalBeam(-g, -h + f, -j, endCrystalEntityRenderState.age, matrixStack, vertexConsumerProvider, 1);
        }
        super.render(endCrystalEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    public static float getYOffset(float f) {
        float g = MathHelper.sin(f * 0.2f) / 2.0f + 0.5f;
        g = (g * g + g) * 0.4f;
        return g - 1.4f;
    }

    @Override
    public EndCrystalEntityRenderState net_minecraft_client_render_entity_state_EndCrystalEntityRenderState_createRenderState() {
        return new EndCrystalEntityRenderState();
    }

    @Override
    public void updateRenderState(EndCrystalEntity endCrystalEntity, EndCrystalEntityRenderState endCrystalEntityRenderState, float f) {
        super.updateRenderState(endCrystalEntity, endCrystalEntityRenderState, f);
        endCrystalEntityRenderState.age = (float)endCrystalEntity.endCrystalAge + f;
        endCrystalEntityRenderState.baseVisible = endCrystalEntity.shouldShowBottom();
        BlockPos blockPos = endCrystalEntity.getBeamTarget();
        endCrystalEntityRenderState.beamOffset = blockPos != null ? Vec3d.ofCenter(blockPos).subtract(endCrystalEntity.getLerpedPos(f)) : null;
    }

    @Override
    public boolean shouldRender(EndCrystalEntity endCrystalEntity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender(endCrystalEntity, frustum, d, e, f) || endCrystalEntity.getBeamTarget() != null;
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_EndCrystalEntityRenderState_createRenderState();
    }
}

