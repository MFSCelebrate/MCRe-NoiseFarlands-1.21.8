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
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class SweetBerryBushBlock
extends PlantBlock
implements Fertilizable {
    final static public MapCodec<SweetBerryBushBlock> CODEC = SweetBerryBushBlock.createCodec(SweetBerryBushBlock::new);
    final static private float MIN_MOVEMENT_FOR_DAMAGE = 0.003f;
    final static public int MAX_AGE = 3;
    final static public IntProperty AGE = Properties.AGE_3;
    final static private VoxelShape SMALL_SHAPE = Block.createColumnShape(10.0, 0.0, 8.0);
    final static private VoxelShape LARGE_SHAPE = Block.createColumnShape(14.0, 0.0, 16.0);

    public MapCodec<SweetBerryBushBlock> getCodec() {
        return CODEC;
    }

    public SweetBerryBushBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Items.SWEET_BERRIES);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AGE)) {
            case 0 -> SMALL_SHAPE;
            default -> LARGE_SHAPE;
            case 3 -> VoxelShapes.fullCube();
        };
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(AGE);
        if (i < 3 && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.net_minecraft_util_math_BlockPos_up(), 0) >= 9) {
            BlockState blockState = (BlockState)state.with(AGE, i + 1);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
        }
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        Vec3d vec3d;
        ServerWorld serverWorld;
        block7: {
            block6: {
                if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.FOX || entity.getType() == EntityType.BEE) {
                    return;
                }
                entity.slowMovement(state, new Vec3d(0.8f, 0.75, 0.8f));
                if (!(world instanceof ServerWorld)) break block6;
                serverWorld = (ServerWorld)world;
                if (state.get(AGE) != 0) break block7;
            }
            return;
        }
        Vec3d vec3d2 = vec3d = entity.isControlledByPlayer() ? entity.getMovement() : entity.getLastRenderPos().subtract(entity.getPos());
        if (vec3d.horizontalLengthSquared() > 0.0) {
            double d = Math.abs(vec3d.getX());
            double e = Math.abs(vec3d.getZ());
            if (d >= (double)0.003f || e >= (double)0.003f) {
                entity.damage(serverWorld, world.getDamageSources().sweetBerryBush(), 1.0f);
            }
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean bl;
        int i = state.get(AGE);
        boolean bl2 = bl = i == 3;
        if (!bl && stack.isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        boolean bl;
        int i = state.get(AGE);
        boolean bl2 = bl = i == 3;
        if (i > 1) {
            int j = 1 + world.random.nextInt(2);
            SweetBerryBushBlock.dropStack(world, pos, new ItemStack(Items.SWEET_BERRIES, j + (bl ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
            BlockState blockState = (BlockState)state.with(AGE, 1);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(AGE) < 3;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.get(AGE) + 1);
        world.setBlockState(pos, (BlockState)state.with(AGE, i), Block.NOTIFY_LISTENERS);
    }
}

