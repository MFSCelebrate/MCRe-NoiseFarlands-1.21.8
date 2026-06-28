/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class BufferBuilderStorage {
    final private BlockBufferAllocatorStorage blockBufferBuilders = new BlockBufferAllocatorStorage();
    final private BlockBufferBuilderPool blockBufferBuildersPool;
    final private VertexConsumerProvider.Immediate entityVertexConsumers;
    final private VertexConsumerProvider.Immediate effectVertexConsumers;
    final private OutlineVertexConsumerProvider outlineVertexConsumers;

    public BufferBuilderStorage(int maxBlockBuildersPoolSize) {
        this.blockBufferBuildersPool = BlockBufferBuilderPool.allocate(maxBlockBuildersPoolSize);
        SequencedMap sequencedMap = (SequencedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), map -> {
            map.put((Object)TexturedRenderLayers.getEntitySolid(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.SOLID));
            map.put((Object)TexturedRenderLayers.getEntityCutout(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.CUTOUT));
            map.put((Object)TexturedRenderLayers.getBannerPatterns(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.CUTOUT_MIPPED));
            map.put((Object)TexturedRenderLayers.getItemEntityTranslucentCull(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.TRANSLUCENT));
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, TexturedRenderLayers.getShieldPatterns());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, TexturedRenderLayers.getBeds());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, TexturedRenderLayers.getShulkerBoxes());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, TexturedRenderLayers.getSign());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, TexturedRenderLayers.getHangingSign());
            map.put((Object)TexturedRenderLayers.getChest(), (Object)new BufferAllocator(786432));
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, RenderLayer.getArmorEntityGlint());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, RenderLayer.getGlint());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, RenderLayer.getGlintTranslucent());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, RenderLayer.getEntityGlint());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)map, RenderLayer.getWaterMask());
        });
        this.entityVertexConsumers = VertexConsumerProvider.immediate(sequencedMap, new BufferAllocator(786432));
        this.outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);
        SequencedMap sequencedMap2 = (SequencedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), objectMap -> ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(renderLayer -> BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>)objectMap, renderLayer)));
        this.effectVertexConsumers = VertexConsumerProvider.immediate(sequencedMap2, new BufferAllocator(0));
    }

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage, RenderLayer layer) {
        builderStorage.put((Object)layer, (Object)new BufferAllocator(layer.getExpectedBufferSize()));
    }

    public BlockBufferAllocatorStorage getBlockBufferBuilders() {
        return this.blockBufferBuilders;
    }

    public BlockBufferBuilderPool getBlockBufferBuildersPool() {
        return this.blockBufferBuildersPool;
    }

    public VertexConsumerProvider.Immediate getEntityVertexConsumers() {
        return this.entityVertexConsumers;
    }

    public VertexConsumerProvider.Immediate getEffectVertexConsumers() {
        return this.effectVertexConsumers;
    }

    public OutlineVertexConsumerProvider getOutlineVertexConsumers() {
        return this.outlineVertexConsumers;
    }
}

