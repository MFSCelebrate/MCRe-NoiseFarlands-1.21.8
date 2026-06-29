/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;

public class BlockEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<RegistryByteBuf, BlockEventS2CPacket> CODEC = Packet.createCodec(BlockEventS2CPacket::write, BlockEventS2CPacket::new);
    final private BlockPos pos;
    final private int type;
    final private int data;
    final private Block block;

    public BlockEventS2CPacket(BlockPos pos, Block block, int type, int data) {
        this.pos = pos;
        this.block = block;
        this.type = type;
        this.data = data;
    }

    private BlockEventS2CPacket(RegistryByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.type = buf.readUnsignedByte();
        this.data = buf.readUnsignedByte();
        this.block = (Block)PacketCodecs.registryValue(RegistryKeys.BLOCK).decode(buf);
    }

    private void write(RegistryByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.type);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.data);
        PacketCodecs.registryValue(RegistryKeys.BLOCK).encode(buf, this.block);
    }

    @Override
    public PacketType<BlockEventS2CPacket> getPacketType() {
        return PlayPackets.BLOCK_EVENT;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBlockEvent(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public Block getBlock() {
        return this.block;
    }
}

