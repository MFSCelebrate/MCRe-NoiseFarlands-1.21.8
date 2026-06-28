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
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WallWitherSkullBlock
extends WallSkullBlock {
    final static public MapCodec<WallWitherSkullBlock> CODEC = WallWitherSkullBlock.createCodec(WallWitherSkullBlock::new);

    public MapCodec<WallWitherSkullBlock> getCodec() {
        return CODEC;
    }

    public WallWitherSkullBlock(AbstractBlock.Settings settings) {
        super(SkullBlock.Type.WITHER_SKELETON, settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        WitherSkullBlock.onPlaced(world, pos);
    }
}

