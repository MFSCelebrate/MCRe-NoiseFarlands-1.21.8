/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.chunk.Buffers;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface AbstractChunkRenderData
extends AutoCloseable {
    default public boolean hasPosition(NormalizedRelativePos pos) {
        return false;
    }

    default public boolean hasData() {
        return false;
    }

    default public boolean hasTranslucentLayers() {
        return false;
    }

    default public boolean containsLayer(BlockRenderLayer layer) {
        return true;
    }

    default public List<BlockEntity> getBlockEntities() {
        return Collections.emptyList();
    }

    public boolean isVisibleThrough(Direction var1, Direction var2);

    @Nullable
    default public Buffers getBuffersForLayer(BlockRenderLayer layer) {
        return null;
    }

    @Override
    default public void close() {
    }
}

