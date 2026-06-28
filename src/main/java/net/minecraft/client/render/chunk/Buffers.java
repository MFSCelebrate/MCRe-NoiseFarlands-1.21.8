/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class Buffers
implements AutoCloseable {
    private GpuBuffer vertexBuffer;
    @Nullable
    private GpuBuffer indexBuffer;
    private int indexCount;
    private VertexFormat.IndexType indexType;

    public Buffers(GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer, int indexCount, VertexFormat.IndexType indexType) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.indexCount = indexCount;
        this.indexType = indexType;
    }

    public GpuBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    @Nullable
    public GpuBuffer getIndexBuffer() {
        return this.indexBuffer;
    }

    public void setIndexBuffer(@Nullable GpuBuffer indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public int getIndexCount() {
        return this.indexCount;
    }

    public VertexFormat.IndexType getIndexType() {
        return this.indexType;
    }

    public void setIndexType(VertexFormat.IndexType indexType) {
        this.indexType = indexType;
    }

    public void setIndexCount(int indexCount) {
        this.indexCount = indexCount;
    }

    public void setVertexBuffer(GpuBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    @Override
    public void close() {
        this.vertexBuffer.close();
        if (this.indexBuffer != null) {
            this.indexBuffer.close();
        }
    }
}

