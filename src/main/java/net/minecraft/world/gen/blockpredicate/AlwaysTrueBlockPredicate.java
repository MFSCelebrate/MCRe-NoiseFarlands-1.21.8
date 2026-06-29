/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;

class AlwaysTrueBlockPredicate
implements BlockPredicate {
    static public AlwaysTrueBlockPredicate instance = new AlwaysTrueBlockPredicate();
    final static public MapCodec<AlwaysTrueBlockPredicate> CODEC = MapCodec.unit(() -> instance);

    private AlwaysTrueBlockPredicate() {
    }

    @Override
    public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        return true;
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.TRUE;
    }

    @Override
    public boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

