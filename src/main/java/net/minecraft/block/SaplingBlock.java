/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SaplingBlock
extends PlantBlock
implements Fertilizable {
    final static public MapCodec<SaplingBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SaplingGenerator.CODEC.fieldOf("tree").forGetter(block -> block.generator), SaplingBlock.createSettingsCodec()).apply((Applicative)instance, SaplingBlock::new));
    final static public IntProperty STAGE = Properties.STAGE;
    final static private VoxelShape SHAPE = Block.createColumnShape(12.0, 0.0, 12.0);
    final protected SaplingGenerator generator;

    public MapCodec<? extends SaplingBlock> getCodec() {
        return CODEC;
    }

    public SaplingBlock(SaplingGenerator generator, AbstractBlock.Settings settings) {
        super(settings);
        this.generator = generator;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(STAGE, 0));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(pos.net_minecraft_util_math_BlockPos_up()) >= 9 && random.nextInt(7) == 0) {
            this.generate(world, pos, state, random);
        }
    }

    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        if (state.get(STAGE) == 0) {
            world.setBlockState(pos, (BlockState)state.cycle(STAGE), Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        } else {
            this.generator.generate(world, world.net_minecraft_server_world_ServerChunkManager_getChunkManager().getChunkGenerator(), pos, state, random);
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)world.random.nextFloat() < 0.45;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.generate(world, pos, state, random);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
}

