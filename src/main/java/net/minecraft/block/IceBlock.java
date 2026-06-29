/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class IceBlock
extends TranslucentBlock {
    final static public MapCodec<IceBlock> CODEC = IceBlock.createCodec(IceBlock::new);

    public MapCodec<? extends IceBlock> getCodec() {
        return CODEC;
    }

    public IceBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public static BlockState getMeltedState() {
        return Blocks.WATER.getDefaultState();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_ICE_MELTING)) {
            if (world.getDimension().ultrawarm()) {
                world.removeBlock(pos, false);
                return;
            }
            BlockState blockState = world.getBlockState(pos.net_minecraft_util_math_BlockPos_down());
            if (blockState.blocksMovement() || blockState.isLiquid()) {
                world.setBlockState(pos, IceBlock.getMeltedState());
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity()) {
            this.melt(state, world, pos);
        }
    }

    protected void melt(BlockState state, World world, BlockPos pos) {
        if (world.getDimension().ultrawarm()) {
            world.removeBlock(pos, false);
            return;
        }
        world.setBlockState(pos, IceBlock.getMeltedState());
        world.updateNeighbor(pos, IceBlock.getMeltedState().getBlock(), null);
    }
}

