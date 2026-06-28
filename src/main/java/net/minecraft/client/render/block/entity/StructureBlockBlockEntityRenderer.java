/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBoxRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    public StructureBlockBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
            return;
        }
        StructureBoxRendering.RenderMode renderMode = ((StructureBoxRendering)entity).getRenderMode();
        if (renderMode == StructureBoxRendering.RenderMode.NONE) {
            return;
        }
        StructureBoxRendering.StructureBox structureBox = ((StructureBoxRendering)entity).getStructureBox();
        BlockPos blockPos = structureBox.localPos();
        Vec3i vec3i = structureBox.size();
        if (vec3i.getX() < 1 || vec3i.getY() < 1 || vec3i.getZ() < 1) {
            return;
        }
        float f = 1.0f;
        float g = 0.9f;
        float h = 0.5f;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        BlockPos blockPos2 = blockPos.net_minecraft_util_math_BlockPos_add(vec3i);
        VertexRendering.drawBox(matrices, vertexConsumer, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        if (renderMode == StructureBoxRendering.RenderMode.BOX_AND_INVISIBLE_BLOCKS && ((BlockEntity)entity).getWorld() != null) {
            this.renderInvisibleBlocks(entity, ((BlockEntity)entity).getWorld(), blockPos, vec3i, vertexConsumers, matrices);
        }
    }

    private void renderInvisibleBlocks(T entity, BlockView world, BlockPos pos, Vec3i size, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        BlockPos blockPos = ((BlockEntity)entity).getPos();
        BlockPos blockPos2 = blockPos.net_minecraft_util_math_BlockPos_add(pos);
        for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.net_minecraft_util_math_BlockPos_add(size).net_minecraft_util_math_BlockPos_add(-1, -1, -1))) {
            boolean bl5;
            BlockState blockState = world.getBlockState(blockPos3);
            boolean bl = blockState.isAir();
            boolean bl2 = blockState.isOf(Blocks.STRUCTURE_VOID);
            boolean bl3 = blockState.isOf(Blocks.BARRIER);
            boolean bl4 = blockState.isOf(Blocks.LIGHT);
            boolean bl6 = bl5 = bl2 || bl3 || bl4;
            if (!bl && !bl5) continue;
            float f = bl ? 0.05f : 0.0f;
            double d = (float)(blockPos3.getX() - blockPos.getX()) + 0.45f - f;
            double e = (float)(blockPos3.getY() - blockPos.getY()) + 0.45f - f;
            double g = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.45f - f;
            double h = (float)(blockPos3.getX() - blockPos.getX()) + 0.55f + f;
            double i = (float)(blockPos3.getY() - blockPos.getY()) + 0.55f + f;
            double j = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.55f + f;
            if (bl) {
                VertexRendering.drawBox(matrices, vertexConsumer, d, e, g, h, i, j, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            if (bl2) {
                VertexRendering.drawBox(matrices, vertexConsumer, d, e, g, h, i, j, 1.0f, 0.75f, 0.75f, 1.0f, 1.0f, 0.75f, 0.75f);
                continue;
            }
            if (bl3) {
                VertexRendering.drawBox(matrices, vertexConsumer, d, e, g, h, i, j, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
                continue;
            }
            if (!bl4) continue;
            VertexRendering.drawBox(matrices, vertexConsumer, d, e, g, h, i, j, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        }
    }

    private void renderStructureVoids(T entity, BlockPos pos, Vec3i size, VertexConsumer vertexConsumer, MatrixStack matrices) {
        World blockView = ((BlockEntity)entity).getWorld();
        if (blockView == null) {
            return;
        }
        BlockPos blockPos = ((BlockEntity)entity).getPos();
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(size.getX(), size.getY(), size.getZ());
        for (BlockPos blockPos2 : BlockPos.iterate(pos, pos.net_minecraft_util_math_BlockPos_add(size).net_minecraft_util_math_BlockPos_add(-1, -1, -1))) {
            if (!blockView.getBlockState(blockPos2).isOf(Blocks.STRUCTURE_VOID)) continue;
            ((VoxelSet)voxelSet).set(blockPos2.getX() - pos.getX(), blockPos2.getY() - pos.getY(), blockPos2.getZ() - pos.getZ());
        }
        voxelSet.forEachDirection((direction, x, y, z) -> {
            float f = 0.48f;
            float g = (float)(x + pos.getX() - blockPos.getX()) + 0.5f - 0.48f;
            float h = (float)(y + pos.getY() - blockPos.getY()) + 0.5f - 0.48f;
            float i = (float)(z + pos.getZ() - blockPos.getZ()) + 0.5f - 0.48f;
            float j = (float)(x + pos.getX() - blockPos.getX()) + 0.5f + 0.48f;
            float k = (float)(y + pos.getY() - blockPos.getY()) + 0.5f + 0.48f;
            float l = (float)(z + pos.getZ() - blockPos.getZ()) + 0.5f + 0.48f;
            VertexRendering.drawSide(matrices, vertexConsumer, direction, g, h, i, j, k, l, 0.75f, 0.75f, 1.0f, 0.2f);
        });
    }

    @Override
    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 96;
    }
}

