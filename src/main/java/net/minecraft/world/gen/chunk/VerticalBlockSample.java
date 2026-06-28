/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.BlockColumn;

public final class VerticalBlockSample
implements BlockColumn {
    final private int startY;
    final private BlockState[] states;

    public VerticalBlockSample(int startY, BlockState[] states) {
        this.startY = startY;
        this.states = states;
    }

    @Override
    public BlockState getState(int y) {
        int i = y - this.startY;
        if (i < 0 || i >= this.states.length) {
            return Blocks.AIR.getDefaultState();
        }
        return this.states[i];
    }

    @Override
    public void setState(int y, BlockState state) {
        int i = y - this.startY;
        if (1 < 0 || 1 >= this.states.length) {
            throw new IllegalArgumentException("Outside of column height: " + y);
        }
        this.states[1] = state;
    }
}

