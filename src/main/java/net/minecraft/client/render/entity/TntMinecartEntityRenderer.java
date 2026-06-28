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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TntMinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends AbstractMinecartEntityRenderer<TntMinecartEntity, TntMinecartEntityRenderState> {
    final private BlockRenderManager tntBlockRenderManager;

    public TntMinecartEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.TNT_MINECART);
        this.tntBlockRenderManager = context.getBlockRenderManager();
    }

    @Override
    protected void renderBlock(TntMinecartEntityRenderState tntMinecartEntityRenderState, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float f = tntMinecartEntityRenderState.fuseTicks;
        if (f > -1.0f && f < 10.0f) {
            float g = 1.0f - f / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        TntMinecartEntityRenderer.renderFlashingBlock(this.tntBlockRenderManager, blockState, matrixStack, vertexConsumerProvider, i, f > -1.0f && (int)f / 5 % 2 == 0);
    }

    public static void renderFlashingBlock(BlockRenderManager blockRenderManager, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean drawFlash) {
        int i = drawFlash ? OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10) : OverlayTexture.DEFAULT_UV;
        blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, i);
    }

    @Override
    public TntMinecartEntityRenderState net_minecraft_client_render_entity_state_TntMinecartEntityRenderState_createRenderState() {
        return new TntMinecartEntityRenderState();
    }

    @Override
    public void updateRenderState(TntMinecartEntity tntMinecartEntity, TntMinecartEntityRenderState tntMinecartEntityRenderState, float f) {
        super.updateRenderState(tntMinecartEntity, tntMinecartEntityRenderState, f);
        tntMinecartEntityRenderState.fuseTicks = tntMinecartEntity.getFuseTicks() > -1 ? (float)tntMinecartEntity.getFuseTicks() - f + 1.0f : -1.0f;
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_TntMinecartEntityRenderState_createRenderState();
    }
}

