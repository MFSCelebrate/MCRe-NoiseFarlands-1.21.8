/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class BedBlockEntity
extends BlockEntity {
    final private DyeColor color;

    public BedBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((BedBlock)state.getBlock()).getColor());
    }

    public BedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(BlockEntityType.BED, pos, state);
        this.color = color;
    }

    public BlockEntityUpdateS2CPacket net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public Packet net_minecraft_network_packet_Packet_toUpdatePacket() {
        return this.net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket();
    }
}

