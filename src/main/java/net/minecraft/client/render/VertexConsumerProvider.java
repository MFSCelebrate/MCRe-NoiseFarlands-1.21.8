/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.BufferAllocator;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumerProvider {
    public static Immediate immediate(BufferAllocator buffer) {
        return VertexConsumerProvider.immediate((SequencedMap<RenderLayer, BufferAllocator>)Object2ObjectSortedMaps.emptyMap(), buffer);
    }

    public static Immediate immediate(SequencedMap<RenderLayer, BufferAllocator> layerBuffers, BufferAllocator fallbackBuffer) {
        return new Immediate(fallbackBuffer, layerBuffers);
    }

    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class Immediate
    implements VertexConsumerProvider {
        final protected BufferAllocator allocator;
        final protected SequencedMap<RenderLayer, BufferAllocator> layerBuffers;
        final protected Map<RenderLayer, BufferBuilder> pending = new HashMap<RenderLayer, BufferBuilder>();
        @Nullable
        protected RenderLayer currentLayer;

        protected Immediate(BufferAllocator allocator, SequencedMap<RenderLayer, BufferAllocator> sequencedMap) {
            this.allocator = allocator;
            this.layerBuffers = sequencedMap;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer renderLayer) {
            BufferBuilder bufferBuilder = this.pending.get(renderLayer);
            if (bufferBuilder != null && !renderLayer.areVerticesNotShared()) {
                this.draw(renderLayer, bufferBuilder);
                bufferBuilder = null;
            }
            if (bufferBuilder != null) {
                return bufferBuilder;
            }
            BufferAllocator bufferAllocator = (BufferAllocator)this.layerBuffers.get(renderLayer);
            if (bufferAllocator != null) {
                bufferBuilder = new BufferBuilder(bufferAllocator, renderLayer.getDrawMode(), renderLayer.getVertexFormat());
            } else {
                if (this.currentLayer != null) {
                    this.draw(this.currentLayer);
                }
                bufferBuilder = new BufferBuilder(this.allocator, renderLayer.getDrawMode(), renderLayer.getVertexFormat());
                this.currentLayer = renderLayer;
            }
            this.pending.put(renderLayer, bufferBuilder);
            return bufferBuilder;
        }

        public void drawCurrentLayer() {
            if (this.currentLayer != null) {
                this.draw(this.currentLayer);
                this.currentLayer = null;
            }
        }

        public void draw() {
            this.drawCurrentLayer();
            for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
                this.draw(renderLayer);
            }
        }

        public void draw(RenderLayer layer) {
            BufferBuilder bufferBuilder = this.pending.remove(layer);
            if (bufferBuilder != null) {
                this.draw(layer, bufferBuilder);
            }
        }

        private void draw(RenderLayer layer, BufferBuilder builder) {
            BuiltBuffer builtBuffer = builder.endNullable();
            if (builtBuffer != null) {
                if (layer.isTranslucent()) {
                    BufferAllocator bufferAllocator = this.layerBuffers.getOrDefault(layer, this.allocator);
                    builtBuffer.sortQuads(bufferAllocator, RenderSystem.getProjectionType().getVertexSorter());
                }
                layer.draw(builtBuffer);
            }
            if (layer.equals(this.currentLayer)) {
                this.currentLayer = null;
            }
        }
    }
}

