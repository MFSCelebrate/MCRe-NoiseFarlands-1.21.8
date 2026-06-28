/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;

public record DebugBreezeCustomPayload(BreezeInfo breezeInfo) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugBreezeCustomPayload> CODEC = CustomPayload.codecOf(DebugBreezeCustomPayload::write, DebugBreezeCustomPayload::new);
    final static public CustomPayload.Id<DebugBreezeCustomPayload> ID = CustomPayload.id("debug/breeze");

    private DebugBreezeCustomPayload(PacketByteBuf buf) {
        this(new BreezeInfo(buf));
    }

    private void write(PacketByteBuf buf) {
        this.breezeInfo.write(buf);
    }

    public CustomPayload.Id<DebugBreezeCustomPayload> getId() {
        return ID;
    }

    public record BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
        public BreezeInfo(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readInt(), buf.readNullable(PacketByteBuf::readInt), buf.readNullable(BlockPos.PACKET_CODEC));
        }

        public void write(PacketByteBuf buf) {
            buf.writeUuid(this.uuid);
            buf.net_minecraft_network_PacketByteBuf_writeInt(this.id);
            buf.writeNullable(this.attackTarget, PacketByteBuf::writeInt);
            buf.writeNullable(this.jumpTarget, BlockPos.PACKET_CODEC);
        }

        public String getName() {
            return NameGenerator.name(this.uuid);
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }
}

