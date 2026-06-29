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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class TntEntityRenderer
extends EntityRenderer<TntEntity, TntEntityRenderState> {
    final private BlockRenderManager blockRenderManager;

    public TntEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0f, 0.5f, 0.0f);
        float f = tntEntityRenderState.fuse;
        if (tntEntityRenderState.fuse < 10.0f) {
            float g = 1.0f - tntEntityRenderState.fuse / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrixStack.translate(-0.5f, -0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        if (tntEntityRenderState.blockState != null) {
            TntMinecartEntityRenderer.renderFlashingBlock(this.blockRenderManager, tntEntityRenderState.blockState, matrixStack, vertexConsumerProvider, 1, (int)f / 5 % 2 == 0);
        }
        matrixStack.pop();
        super.render(tntEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public TntEntityRenderState net_minecraft_client_render_entity_state_TntEntityRenderState_createRenderState() {
        return new TntEntityRenderState();
    }

    @Override
    public void updateRenderState(TntEntity tntEntity, TntEntityRenderState tntEntityRenderState, float f) {
        super.updateRenderState(tntEntity, tntEntityRenderState, f);
        tntEntityRenderState.fuse = (float)tntEntity.getFuse() - f + 1.0f;
        tntEntityRenderState.blockState = tntEntity.getBlockState();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_TntEntityRenderState_createRenderState();
    }
}

