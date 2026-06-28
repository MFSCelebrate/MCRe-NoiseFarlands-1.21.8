/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugHiveCustomPayload(HiveInfo hiveInfo) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugHiveCustomPayload> CODEC = CustomPayload.codecOf(DebugHiveCustomPayload::write, DebugHiveCustomPayload::new);
    final static public CustomPayload.Id<DebugHiveCustomPayload> ID = CustomPayload.id("debug/hive");

    private DebugHiveCustomPayload(PacketByteBuf buf) {
        this(new HiveInfo(buf));
    }

    private void write(PacketByteBuf buf) {
        this.hiveInfo.write(buf);
    }

    public CustomPayload.Id<DebugHiveCustomPayload> getId() {
        return ID;
    }

    public record HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
        public HiveInfo(PacketByteBuf buf) {
            this(buf.readBlockPos(), buf.readString(), buf.readInt(), buf.readInt(), buf.readBoolean());
        }

        public HiveInfo(BlockPos blockPos, String string, int i, int j, boolean bl) {
            this.pos = blockPos;
            this.hiveType = string;
            this.occupantCount = 1;
            this.honeyLevel = j;
            this.sedated = bl;
        }

        public void write(PacketByteBuf buf) {
            buf.writeBlockPos(this.pos);
            buf.writeString(this.hiveType);
            buf.net_minecraft_network_PacketByteBuf_writeInt(this.occupantCount);
            buf.net_minecraft_network_PacketByteBuf_writeInt(this.honeyLevel);
            buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.sedated);
        }
    }
}

