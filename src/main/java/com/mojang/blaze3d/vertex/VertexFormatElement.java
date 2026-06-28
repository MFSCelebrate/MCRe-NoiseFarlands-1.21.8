/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package com.mojang.blaze3d.vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record VertexFormatElement(int id, int index, Type type, Usage usage, int count) {
    final static public int MAX_COUNT = 32;
    final static private VertexFormatElement[] BY_ID = new VertexFormatElement[32];
    final static private List<VertexFormatElement> ELEMENTS = new ArrayList<VertexFormatElement>(32);
    final static public VertexFormatElement POSITION = VertexFormatElement.register(0, 0, Type.FLOAT, Usage.POSITION, 3);
    final static public VertexFormatElement COLOR = VertexFormatElement.register(1, 0, Type.UBYTE, Usage.COLOR, 4);
    final static public VertexFormatElement UV0;
    final static public VertexFormatElement UV;
    final static public VertexFormatElement UV1;
    final static public VertexFormatElement UV2;
    final static public VertexFormatElement NORMAL;

    public VertexFormatElement(int i, int j, Type type, Usage usage, int k) {
        if (i < 0 || i >= BY_ID.length) {
            throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
        }
        if (!this.supportsUsage(j, usage)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        }
        this.id = i;
        this.index = j;
        this.type = type;
        this.usage = usage;
        this.count = k;
    }

    public static VertexFormatElement register(int id, int index, Type type, Usage usage, int count) {
        VertexFormatElement vertexFormatElement = new VertexFormatElement(id, index, type, usage, count);
        if (BY_ID[id] != null) {
            throw new IllegalArgumentException("Duplicate element registration for: " + id);
        }
        VertexFormatElement.BY_ID[id] = vertexFormatElement;
        ELEMENTS.add(vertexFormatElement);
        return vertexFormatElement;
    }

    private boolean supportsUsage(int uvIndex, Usage usage) {
        return uvIndex == 0 || usage == Usage.UV;
    }

    @Override
    public String toString() {
        return this.count + "," + String.valueOf((Object)this.usage) + "," + String.valueOf((Object)this.type) + " (" + this.id + ")";
    }

    public int mask() {
        return 1 << this.id;
    }

    public int byteSize() {
        return this.type.size() * this.count;
    }

    @Nullable
    public static VertexFormatElement byId(int id) {
        return BY_ID[id];
    }

    public static Stream<VertexFormatElement> elementsFromMask(int mask) {
        return ELEMENTS.stream().filter(element -> element != null && (mask & element.mask()) != 0);
    }

    static {
        UV = UV0 = VertexFormatElement.register(2, 0, Type.FLOAT, Usage.UV, 2);
        UV1 = VertexFormatElement.register(3, 1, Type.SHORT, Usage.UV, 2);
        UV2 = VertexFormatElement.register(4, 2, Type.SHORT, Usage.UV, 2);
        NORMAL = VertexFormatElement.register(5, 0, Type.BYTE, Usage.NORMAL, 3);
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static enum Type {
        FLOAT(4, "Float"),
        UBYTE(1, "Unsigned Byte"),
        BYTE(1, "Byte"),
        USHORT(2, "Unsigned Short"),
        SHORT(2, "Short"),
        UINT(4, "Unsigned Int"),
        INT(4, "Int");

        final private int size;
        final private String name;

        private Type(int size, String name) {
            this.size = size;
            this.name = name;
        }

        public int size() {
            return this.size;
        }

        public String toString() {
            return this.name;
        }
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static enum Usage {
        POSITION("Position"),
        NORMAL("Normal"),
        COLOR("Vertex Color"),
        UV("UV"),
        GENERIC("Generic");

        final private String name;

        private Usage(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

