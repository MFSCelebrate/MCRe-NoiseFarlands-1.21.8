/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SectionBuilder {
    final private BlockRenderManager blockRenderManager;
    final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public SectionBuilder(BlockRenderManager blockRenderManager, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.blockRenderManager = blockRenderManager;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }

    public RenderData build(ChunkSectionPos sectionPos, ChunkRendererRegion renderRegion, VertexSorter vertexSorter, BlockBufferAllocatorStorage allocatorStorage) {
        RenderData renderData = new RenderData();
        BlockPos blockPos = sectionPos.getMinPos();
        BlockPos blockPos2 = blockPos.net_minecraft_util_math_BlockPos_add(15, 15, 15);
        ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
        MatrixStack matrixStack = new MatrixStack();
        BlockModelRenderer.enableBrightnessCache();
        EnumMap<BlockRenderLayer, BufferBuilder> map = new EnumMap<BlockRenderLayer, BufferBuilder>(BlockRenderLayer.class);
        Random random = Random.create();
        ObjectArrayList list = new ObjectArrayList();
        for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
            BufferBuilder bufferBuilder;
            BlockRenderLayer blockRenderLayer;
            FluidState fluidState;
            BlockEntity blockEntity;
            BlockState blockState = renderRegion.getBlockState(blockPos3);
            if (blockState.isOpaqueFullCube()) {
                chunkOcclusionDataBuilder.markClosed(blockPos3);
            }
            if (blockState.hasBlockEntity() && (blockEntity = renderRegion.getBlockEntity(blockPos3)) != null) {
                this.addBlockEntity(renderData, blockEntity);
            }
            if (!(fluidState = blockState.getFluidState()).isEmpty()) {
                blockRenderLayer = RenderLayers.getFluidLayer(fluidState);
                bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, blockRenderLayer);
                this.blockRenderManager.renderFluid(blockPos3, renderRegion, bufferBuilder, blockState, fluidState);
            }
            if (blockState.getRenderType() != BlockRenderType.MODEL) continue;
            blockRenderLayer = RenderLayers.getBlockLayer(blockState);
            bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, blockRenderLayer);
            random.setSeed(blockState.getRenderingSeed(blockPos3));
            this.blockRenderManager.getModel(blockState).addParts(random, (List<BlockModelPart>)list);
            matrixStack.push();
            matrixStack.translate(ChunkSectionPos.getLocalCoord(blockPos3.getX()), ChunkSectionPos.getLocalCoord(blockPos3.getY()), ChunkSectionPos.getLocalCoord(blockPos3.getZ()));
            this.blockRenderManager.renderBlock(blockState, blockPos3, renderRegion, matrixStack, bufferBuilder, true, (List<BlockModelPart>)list);
            matrixStack.pop();
            list.clear();
        }
        for (Map.Entry entry : map.entrySet()) {
            BlockRenderLayer blockRenderLayer2 = (BlockRenderLayer)((Object)entry.getKey());
            BuiltBuffer builtBuffer = ((BufferBuilder)entry.getValue()).endNullable();
            if (builtBuffer == null) continue;
            if (blockRenderLayer2 == BlockRenderLayer.TRANSLUCENT) {
                renderData.translucencySortingData = builtBuffer.sortQuads(allocatorStorage.get(blockRenderLayer2), vertexSorter);
            }
            renderData.buffers.put(blockRenderLayer2, builtBuffer);
        }
        BlockModelRenderer.disableBrightnessCache();
        renderData.chunkOcclusionData = chunkOcclusionDataBuilder.build();
        return renderData;
    }

    private BufferBuilder beginBufferBuilding(Map<BlockRenderLayer, BufferBuilder> builders, BlockBufferAllocatorStorage allocatorStorage, BlockRenderLayer layer) {
        BufferBuilder bufferBuilder = builders.get((Object)layer);
        if (bufferBuilder == null) {
            BufferAllocator bufferAllocator = allocatorStorage.get(layer);
            bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            builders.put(layer, bufferBuilder);
        }
        return bufferBuilder;
    }

    private <E extends BlockEntity> void addBlockEntity(RenderData data, E blockEntity) {
        BlockEntityRenderer<E> blockEntityRenderer = this.blockEntityRenderDispatcher.get(blockEntity);
        if (blockEntityRenderer != null && !blockEntityRenderer.rendersOutsideBoundingBox()) {
            data.blockEntities.add(blockEntity);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RenderData {
        final public List<BlockEntity> blockEntities = new ArrayList<BlockEntity>();
        final public Map<BlockRenderLayer, BuiltBuffer> buffers = new EnumMap<BlockRenderLayer, BuiltBuffer>(BlockRenderLayer.class);
        public ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
        @Nullable
        public BuiltBuffer.SortState translucencySortingData;

        public void close() {
            this.buffers.values().forEach(BuiltBuffer::close);
        }
    }
}

