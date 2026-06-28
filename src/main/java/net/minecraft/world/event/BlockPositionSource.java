/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.event;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class BlockPositionSource
implements PositionSource {
    final static public MapCodec<BlockPositionSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(blockPositionSource -> blockPositionSource.pos)).apply((Applicative)instance, BlockPositionSource::new));
    final static public PacketCodec<ByteBuf, BlockPositionSource> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, source -> source.pos, BlockPositionSource::new);
    final private BlockPos pos;

    public BlockPositionSource(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Optional<Vec3d> getPos(World world) {
        return Optional.of(Vec3d.ofCenter(this.pos));
    }

    public PositionSourceType<BlockPositionSource> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class Type
    implements PositionSourceType<BlockPositionSource> {
        @Override
        public MapCodec<BlockPositionSource> getCodec() {
            return CODEC;
        }

        @Override
        public PacketCodec<ByteBuf, BlockPositionSource> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}

