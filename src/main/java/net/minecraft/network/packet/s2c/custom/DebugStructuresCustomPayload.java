/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;

public record DebugStructuresCustomPayload(RegistryKey<World> dimension, BlockBox mainBB, List<Piece> pieces) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugStructuresCustomPayload> CODEC = CustomPayload.codecOf(DebugStructuresCustomPayload::write, DebugStructuresCustomPayload::new);
    final static public CustomPayload.Id<DebugStructuresCustomPayload> ID = CustomPayload.id("debug/structures");

    private DebugStructuresCustomPayload(PacketByteBuf buf) {
        this(buf.readRegistryKey(RegistryKeys.WORLD), DebugStructuresCustomPayload.readBox(buf), buf.readList(Piece::new));
    }

    private void write(PacketByteBuf buf) {
        buf.writeRegistryKey(this.dimension);
        DebugStructuresCustomPayload.writeBox(buf, this.mainBB);
        buf.writeCollection(this.pieces, (buf2, piece) -> piece.write(buf));
    }

    public CustomPayload.Id<DebugStructuresCustomPayload> getId() {
        return ID;
    }

    static BlockBox readBox(PacketByteBuf buf) {
        return new BlockBox(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    static void writeBox(PacketByteBuf buf, BlockBox box) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(box.getMinX());
        buf.net_minecraft_network_PacketByteBuf_writeInt(box.getMinY());
        buf.net_minecraft_network_PacketByteBuf_writeInt(box.getMinZ());
        buf.net_minecraft_network_PacketByteBuf_writeInt(box.getMaxX());
        buf.net_minecraft_network_PacketByteBuf_writeInt(box.getMaxY());
        buf.net_minecraft_network_PacketByteBuf_writeInt(box.getMaxZ());
    }

    public record Piece(BlockBox boundingBox, boolean isStart) {
        public Piece(PacketByteBuf buf) {
            this(DebugStructuresCustomPayload.readBox(buf), buf.readBoolean());
        }

        public void write(PacketByteBuf buf) {
            DebugStructuresCustomPayload.writeBox(buf, this.boundingBox);
            buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.isStart);
        }
    }
}

