/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.fabric.api.particle.v1.FabricBlockStateParticleEffect
 */
package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.particle.v1.FabricBlockStateParticleEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public class BlockStateParticleEffect
implements ParticleEffect,
FabricBlockStateParticleEffect {
    final static private Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(BlockState.CODEC, Registries.BLOCK.getCodec(), Block::getDefaultState);
    final private ParticleType<BlockStateParticleEffect> type;
    final private BlockState blockState;

    public static MapCodec<BlockStateParticleEffect> createCodec(ParticleType<BlockStateParticleEffect> type) {
        return BLOCK_STATE_CODEC.xmap(state -> new BlockStateParticleEffect(type, (BlockState)state), effect -> effect.blockState).fieldOf("block_state");
    }

    public static PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect> createPacketCodec(ParticleType<BlockStateParticleEffect> type) {
        return PacketCodecs.entryOf(Block.STATE_IDS).xmap(state -> new BlockStateParticleEffect(type, (BlockState)state), effect -> effect.blockState);
    }

    public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> type, BlockState blockState) {
        this.type = type;
        this.blockState = blockState;
    }

    public ParticleType<BlockStateParticleEffect> getType() {
        return this.type;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }
}

