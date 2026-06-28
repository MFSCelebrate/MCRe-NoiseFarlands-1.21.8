/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public final class MapPostProcessingComponent
extends Enum<MapPostProcessingComponent> {
    final static public MapPostProcessingComponent LOCK = new MapPostProcessingComponent(0);
    final static public MapPostProcessingComponent SCALE = new MapPostProcessingComponent(1);
    final static public IntFunction<MapPostProcessingComponent> ID_TO_VALUE;
    final static public PacketCodec<ByteBuf, MapPostProcessingComponent> PACKET_CODEC;
    final private int id;
    final static private MapPostProcessingComponent[] field_49358;

    public static MapPostProcessingComponent[] values() {
        return (MapPostProcessingComponent[])field_49358.clone();
    }

    public static MapPostProcessingComponent valueOf(String string) {
        return Enum.valueOf(MapPostProcessingComponent.class, string);
    }

    private MapPostProcessingComponent(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    private static MapPostProcessingComponent[] method_57506() {
        return new MapPostProcessingComponent[]{LOCK, SCALE};
    }

    static {
        field_49358 = MapPostProcessingComponent.method_57506();
        ID_TO_VALUE = ValueLists.createIndexToValueFunction(MapPostProcessingComponent::getId, MapPostProcessingComponent.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, MapPostProcessingComponent::getId);
    }
}

