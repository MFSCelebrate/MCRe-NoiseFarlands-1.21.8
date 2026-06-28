/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.test;

import net.minecraft.test.GameTestException;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PositionedException
extends GameTestException {
    final private BlockPos pos;
    final private BlockPos relativePos;

    public PositionedException(Text message, BlockPos pos, BlockPos relativePos, int tick) {
        super(message, tick);
        this.pos = pos;
        this.relativePos = relativePos;
    }

    @Override
    public Text getText() {
        return Text.translatable("test.error.position", this.message, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.relativePos.getX(), this.relativePos.getY(), this.relativePos.getZ(), this.tick);
    }

    @Nullable
    public String getDebugMessage() {
        return super.getMessage();
    }

    @Nullable
    public BlockPos getRelativePos() {
        return this.relativePos;
    }

    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }
}

