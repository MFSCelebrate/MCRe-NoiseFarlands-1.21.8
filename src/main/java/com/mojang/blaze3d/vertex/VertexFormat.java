/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11541;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class VertexFormat {
    final static public int UNKNOWN_ELEMENT = -1;
    final static private boolean USE_STAGING_BUFFER_WORKAROUND = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS && Util.isOnAarch64();
    @Nullable
    static private GpuBuffer UPLOAD_STAGING_BUFFER;
    final private List<VertexFormatElement> elements;
    final private List<String> names;
    final private int vertexSize;
    final private int elementsMask;
    final private int[] offsetsByElement = new int[32];
    @Nullable
    private GpuBuffer immediateDrawVertexBuffer;
    @Nullable
    private GpuBuffer immediateDrawIndexBuffer;

    VertexFormat(List<VertexFormatElement> elements, List<String> names, IntList offsets, int vertexSize) {
        this.elements = elements;
        this.names = names;
        this.vertexSize = vertexSize;
        this.elementsMask = elements.stream().mapToInt(VertexFormatElement::mask).reduce(0, (a, b) -> a | b);
        for (int i = 0; i < this.offsetsByElement.length; ++i) {
            VertexFormatElement vertexFormatElement = VertexFormatElement.byId(i);
            int j = vertexFormatElement != null ? elements.indexOf(vertexFormatElement) : -1;
            this.offsetsByElement[i] = j != -1 ? offsets.getInt(j) : -1;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        return "VertexFormat" + String.valueOf(this.names);
    }

    public int getVertexSize() {
        return this.vertexSize;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public List<String> getElementAttributeNames() {
        return this.names;
    }

    public int[] getOffsetsByElement() {
        return this.offsetsByElement;
    }

    public int getOffset(VertexFormatElement element) {
        return this.offsetsByElement[element.id()];
    }

    public boolean contains(VertexFormatElement element) {
        return (this.elementsMask & element.mask()) != 0;
    }

    public int getElementsMask() {
        return this.elementsMask;
    }

    public String getElementName(VertexFormatElement element) {
        int i = this.elements.indexOf(element);
        if (i == -1) {
            throw new IllegalArgumentException(String.valueOf(element) + " is not contained in format");
        }
        return this.names.get(i);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VertexFormat)) return false;
        VertexFormat vertexFormat = (VertexFormat)o;
        if (this.elementsMask != vertexFormat.elementsMask) return false;
        if (this.vertexSize != vertexFormat.vertexSize) return false;
        if (!this.names.equals(vertexFormat.names)) return false;
        if (!Arrays.equals(this.offsetsByElement, vertexFormat.offsetsByElement)) return false;
        return true;
    }

    public int hashCode() {
        return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
    }

    private static GpuBuffer uploadToBuffer(@Nullable GpuBuffer gpuBuffer, ByteBuffer byteBuffer, int i, Supplier<String> supplier) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (gpuBuffer == null) {
            gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
        } else {
            CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
            if (gpuBuffer.size() < byteBuffer.remaining()) {
                gpuBuffer.close();
                gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
            } else {
                commandEncoder.writeToBuffer(gpuBuffer.slice(), byteBuffer);
            }
        }
        return gpuBuffer;
    }

    private GpuBuffer uploadToBufferWithWorkaround(@Nullable GpuBuffer gpuBuffer, ByteBuffer byteBuffer, int i, Supplier<String> supplier) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (USE_STAGING_BUFFER_WORKAROUND) {
            if (gpuBuffer == null) {
                gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
            } else {
                CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
                if (gpuBuffer.size() < byteBuffer.remaining()) {
                    gpuBuffer.close();
                    gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
                } else {
                    UPLOAD_STAGING_BUFFER = VertexFormat.uploadToBuffer(UPLOAD_STAGING_BUFFER, byteBuffer, i, supplier);
                    commandEncoder.copyToBuffer(UPLOAD_STAGING_BUFFER.slice(0, byteBuffer.remaining()), gpuBuffer.slice(0, byteBuffer.remaining()));
                }
            }
            return gpuBuffer;
        }
        if (class_11541.method_72243(gpuDevice).method_72242()) {
            if (gpuBuffer != null) {
                gpuBuffer.close();
            }
            return gpuDevice.createBuffer(supplier, i, byteBuffer);
        }
        return VertexFormat.uploadToBuffer(gpuBuffer, byteBuffer, i, supplier);
    }

    public GpuBuffer uploadImmediateVertexBuffer(ByteBuffer byteBuffer) {
        this.immediateDrawVertexBuffer = this.uploadToBufferWithWorkaround(this.immediateDrawVertexBuffer, byteBuffer, 40, () -> "Immediate vertex buffer for " + String.valueOf(this));
        return this.immediateDrawVertexBuffer;
    }

    public GpuBuffer uploadImmediateIndexBuffer(ByteBuffer byteBuffer) {
        this.immediateDrawIndexBuffer = this.uploadToBufferWithWorkaround(this.immediateDrawIndexBuffer, byteBuffer, 72, () -> "Immediate index buffer for " + String.valueOf(this));
        return this.immediateDrawIndexBuffer;
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static class Builder {
        final private ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
        final private IntList offsets = new IntArrayList();
        private int offset;

        Builder() {
        }

        public Builder add(String name, VertexFormatElement element) {
            this.elements.put((Object)name, (Object)element);
            this.offsets.add(this.offset);
            this.offset += element.byteSize();
            return this;
        }

        public Builder padding(int padding) {
            this.offset += padding;
            return this;
        }

        public VertexFormat build() {
            ImmutableMap immutableMap = this.elements.buildOrThrow();
            ImmutableList immutableList = immutableMap.values().asList();
            ImmutableList immutableList2 = immutableMap.keySet().asList();
            return new VertexFormat((List<VertexFormatElement>)immutableList, (List<String>)immutableList2, this.offsets, this.offset);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawMode
    extends Enum<DrawMode> {
        final static public DrawMode LINES = new DrawMode(2, 2, false);
        final static public DrawMode LINE_STRIP = new DrawMode(2, 1, true);
        final static public DrawMode DEBUG_LINES = new DrawMode(2, 2, false);
        final static public DrawMode DEBUG_LINE_STRIP = new DrawMode(2, 1, true);
        final static public DrawMode TRIANGLES = new DrawMode(3, 3, false);
        final static public DrawMode TRIANGLE_STRIP = new DrawMode(3, 1, true);
        final static public DrawMode TRIANGLE_FAN = new DrawMode(3, 1, true);
        final static public DrawMode QUADS = new DrawMode(4, 4, false);
        final public int firstVertexCount;
        final public int additionalVertexCount;
        final public boolean shareVertices;
        final static private DrawMode[] field_27386;

        public static DrawMode[] values() {
            return (DrawMode[])field_27386.clone();
        }

        public static DrawMode valueOf(String string) {
            return Enum.valueOf(DrawMode.class, string);
        }

        private DrawMode(int firstVertexCount, int additionalVertexCount, boolean shareVertices) {
            this.firstVertexCount = firstVertexCount;
            this.additionalVertexCount = additionalVertexCount;
            this.shareVertices = shareVertices;
        }

        public int getIndexCount(int vertexCount) {
            return switch (this.ordinal()) {
                case 1, 2, 3, 4, 5, 6 -> vertexCount;
                case 0, 7 -> vertexCount / 4 * 6;
                default -> 0;
            };
        }

        private static DrawMode[] method_36817() {
            return new DrawMode[]{LINES, LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
        }

        static {
            field_27386 = DrawMode.method_36817();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class IndexType
    extends Enum<IndexType> {
        final static public IndexType SHORT = new IndexType(2);
        final static public IndexType INT = new IndexType(4);
        final public int size;
        final static private IndexType[] field_27376;

        public static IndexType[] values() {
            return (IndexType[])field_27376.clone();
        }

        public static IndexType valueOf(String string) {
            return Enum.valueOf(IndexType.class, string);
        }

        private IndexType(int size) {
            this.size = size;
        }

        public static IndexType smallestFor(int i) {
            if ((i & 0xFFFF0000) != 0) {
                return INT;
            }
            return SHORT;
        }

        private static IndexType[] method_36816() {
            return new IndexType[]{SHORT, INT};
        }

        static {
            field_27376 = IndexType.method_36816();
        }
    }
}

