/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.block;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockPredicate
implements Predicate<BlockState> {
    final private Block block;

    public BlockPredicate(Block block) {
        this.block = block;
    }

    public static BlockPredicate make(Block block) {
        return new BlockPredicate(block);
    }

    @Override
    public boolean test(@Nullable BlockState blockState) {
        return blockState != null && blockState.isOf(this.block);
    }

    @Override
    public boolean test(@Nullable Object context) {
        return this.test((BlockState)context);
    }
}

