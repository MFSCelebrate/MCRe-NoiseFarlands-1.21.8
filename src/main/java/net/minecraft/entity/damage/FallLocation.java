/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.damage;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record FallLocation(String id) {
    final static public FallLocation GENERIC = new FallLocation("generic");
    final static public FallLocation LADDER = new FallLocation("ladder");
    final static public FallLocation VINES = new FallLocation("vines");
    final static public FallLocation WEEPING_VINES = new FallLocation("weeping_vines");
    final static public FallLocation TWISTING_VINES = new FallLocation("twisting_vines");
    final static public FallLocation SCAFFOLDING = new FallLocation("scaffolding");
    final static public FallLocation OTHER_CLIMBABLE = new FallLocation("other_climbable");
    final static public FallLocation WATER = new FallLocation("water");

    public static FallLocation fromBlockState(BlockState state) {
        if (state.isOf(Blocks.LADDER) || state.isIn(BlockTags.TRAPDOORS)) {
            return LADDER;
        }
        if (state.isOf(Blocks.VINE)) {
            return VINES;
        }
        if (state.isOf(Blocks.WEEPING_VINES) || state.isOf(Blocks.WEEPING_VINES_PLANT)) {
            return WEEPING_VINES;
        }
        if (state.isOf(Blocks.TWISTING_VINES) || state.isOf(Blocks.TWISTING_VINES_PLANT)) {
            return TWISTING_VINES;
        }
        if (state.isOf(Blocks.SCAFFOLDING)) {
            return SCAFFOLDING;
        }
        return OTHER_CLIMBABLE;
    }

    @Nullable
    public static FallLocation fromEntity(LivingEntity entity) {
        Optional<BlockPos> optional = entity.getClimbingPos();
        if (optional.isPresent()) {
            BlockState blockState = entity.net_minecraft_world_World_getWorld().getBlockState(optional.get());
            return FallLocation.fromBlockState(blockState);
        }
        if (entity.isTouchingWater()) {
            return WATER;
        }
        return null;
    }

    public String getDeathMessageKey() {
        return "death.fell.accident." + this.id;
    }
}

