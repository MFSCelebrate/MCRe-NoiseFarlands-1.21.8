/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class WetSpongeBlock
extends Block {
    final static public MapCodec<WetSpongeBlock> CODEC = WetSpongeBlock.createCodec(WetSpongeBlock::new);

    public MapCodec<WetSpongeBlock> getCodec() {
        return CODEC;
    }

    public WetSpongeBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.getDimension().ultrawarm()) {
            world.setBlockState(pos, Blocks.SPONGE.getDefaultState(), Block.NOTIFY_ALL);
            world.syncWorldEvent(WorldEvents.WET_SPONGE_DRIES_OUT, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_WET_SPONGE_DRIES, SoundCategory.BLOCKS, 1.0f, (1.0f + world.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = Direction.random(random);
        if (direction == Direction.UP) {
            return;
        }
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (state.isOpaque() && blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
            return;
        }
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        if (direction == Direction.DOWN) {
            e -= 0.05;
            d += random.nextDouble();
            f += random.nextDouble();
        } else {
            e += random.nextDouble() * 0.8;
            if (direction.getAxis() == Direction.Axis.X) {
                f += random.nextDouble();
                d = direction == Direction.EAST ? (d += 1.1) : (d += 0.05);
            } else {
                d += random.nextDouble();
                f = direction == Direction.SOUTH ? (f += 1.1) : (f += 0.05);
            }
        }
        world.addParticleClient(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
    }
}

