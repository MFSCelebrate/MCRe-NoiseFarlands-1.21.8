/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer<FallingBlockEntity, FallingBlockEntityRenderState> {
    final private BlockRenderManager blockRenderManager;

    public FallingBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public boolean shouldRender(FallingBlockEntity fallingBlockEntity, Frustum frustum, double d, double e, double f) {
        if (!super.shouldRender(fallingBlockEntity, frustum, d, e, f)) {
            return false;
        }
        return fallingBlockEntity.getBlockState() != fallingBlockEntity.net_minecraft_world_World_getWorld().getBlockState(fallingBlockEntity.getBlockPos());
    }

    @Override
    public void render(FallingBlockEntityRenderState fallingBlockEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BlockState blockState = fallingBlockEntityRenderState.blockState;
        if (blockState.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(-0.5, 0.0, -0.5);
        List<BlockModelPart> list = this.blockRenderManager.getModel(blockState).getParts(Random.create(blockState.getRenderingSeed(fallingBlockEntityRenderState.fallingBlockPos)));
        this.blockRenderManager.getModelRenderer().render(fallingBlockEntityRenderState, list, blockState, fallingBlockEntityRenderState.currentPos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(fallingBlockEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public FallingBlockEntityRenderState net_minecraft_client_render_entity_state_FallingBlockEntityRenderState_createRenderState() {
        return new FallingBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(FallingBlockEntity fallingBlockEntity, FallingBlockEntityRenderState fallingBlockEntityRenderState, float f) {
        super.updateRenderState(fallingBlockEntity, fallingBlockEntityRenderState, f);
        BlockPos blockPos = BlockPos.ofFloored(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
        fallingBlockEntityRenderState.fallingBlockPos = fallingBlockEntity.getFallingBlockPos();
        fallingBlockEntityRenderState.currentPos = blockPos;
        fallingBlockEntityRenderState.blockState = fallingBlockEntity.getBlockState();
        fallingBlockEntityRenderState.biome = fallingBlockEntity.net_minecraft_world_World_getWorld().getBiome(blockPos);
        fallingBlockEntityRenderState.world = fallingBlockEntity.net_minecraft_world_World_getWorld();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_FallingBlockEntityRenderState_createRenderState();
    }
}

