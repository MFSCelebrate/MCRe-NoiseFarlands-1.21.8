/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemUsageContext {
    @Nullable
    final private PlayerEntity player;
    final private Hand hand;
    final private BlockHitResult hit;
    final private World world;
    final private ItemStack stack;

    public ItemUsageContext(PlayerEntity player, Hand hand, BlockHitResult hit) {
        this(player.net_minecraft_world_World_getWorld(), player, hand, player.getStackInHand(hand), hit);
    }

    public ItemUsageContext(World world, @Nullable PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hit) {
        this.player = player;
        this.hand = hand;
        this.hit = hit;
        this.stack = stack;
        this.world = world;
    }

    protected final BlockHitResult getHitResult() {
        return this.hit;
    }

    public BlockPos getBlockPos() {
        return this.hit.getBlockPos();
    }

    public Direction getSide() {
        return this.hit.getSide();
    }

    public Vec3d getHitPos() {
        return this.hit.getPos();
    }

    public boolean hitsInsideBlock() {
        return this.hit.isInsideBlock();
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @Nullable
    public PlayerEntity getPlayer() {
        return this.player;
    }

    public Hand getHand() {
        return this.hand;
    }

    public World getWorld() {
        return this.world;
    }

    public Direction getHorizontalPlayerFacing() {
        return this.player == null ? Direction.NORTH : this.player.getHorizontalFacing();
    }

    public boolean shouldCancelInteraction() {
        return this.player != null && this.player.shouldCancelInteraction();
    }

    public float getPlayerYaw() {
        return this.player == null ? 0.0f : this.player.getYaw();
    }
}

