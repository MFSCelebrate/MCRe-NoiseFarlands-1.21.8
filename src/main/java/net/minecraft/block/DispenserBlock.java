/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DispenserBlock
extends BlockWithEntity {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public MapCodec<DispenserBlock> CODEC = DispenserBlock.createCodec(DispenserBlock::new);
    final static public EnumProperty<Direction> FACING = FacingBlock.FACING;
    final static public BooleanProperty TRIGGERED = Properties.TRIGGERED;
    final static private ItemDispenserBehavior DEFAULT_BEHAVIOR = new ItemDispenserBehavior();
    final static public Map<Item, DispenserBehavior> BEHAVIORS = new IdentityHashMap<Item, DispenserBehavior>();
    final static private int SCHEDULED_TICK_DELAY = 4;

    public MapCodec<? extends DispenserBlock> getCodec() {
        return CODEC;
    }

    public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
        BEHAVIORS.put(provider.asItem(), behavior);
    }

    public static void registerProjectileBehavior(ItemConvertible projectile) {
        BEHAVIORS.put(projectile.asItem(), new ProjectileDispenserBehavior(projectile.asItem()));
    }

    public DispenserBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TRIGGERED, false));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (!world.isClient && (blockEntity = world.getBlockEntity(pos)) instanceof DispenserBlockEntity) {
            DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity)blockEntity;
            player.openHandledScreen(dispenserBlockEntity);
            player.incrementStat(dispenserBlockEntity instanceof DropperBlockEntity ? Stats.INSPECT_DROPPER : Stats.INSPECT_DISPENSER);
        }
        return ActionResult.SUCCESS;
    }

    protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
        DispenserBlockEntity dispenserBlockEntity = world.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);
        if (dispenserBlockEntity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", (Object)pos);
            return;
        }
        BlockPointer blockPointer = new BlockPointer(world, pos, state, dispenserBlockEntity);
        int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
        if (i < 0) {
            world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
            world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(dispenserBlockEntity.getCachedState()));
            return;
        }
        ItemStack itemStack = dispenserBlockEntity.getStack(i);
        DispenserBehavior dispenserBehavior = this.getBehaviorForItem(world, itemStack);
        if (dispenserBehavior != DispenserBehavior.NOOP) {
            dispenserBlockEntity.setStack(i, dispenserBehavior.net_minecraft_item_ItemStack_dispense(blockPointer, itemStack));
        }
    }

    protected DispenserBehavior getBehaviorForItem(World world, ItemStack stack) {
        if (!stack.isItemEnabled(world.getEnabledFeatures())) {
            return DEFAULT_BEHAVIOR;
        }
        DispenserBehavior dispenserBehavior = BEHAVIORS.get(stack.getItem());
        if (dispenserBehavior != null) {
            return dispenserBehavior;
        }
        return DispenserBlock.getBehaviorForItem(stack);
    }

    private static DispenserBehavior getBehaviorForItem(ItemStack stack) {
        if (stack.contains(DataComponentTypes.EQUIPPABLE)) {
            return EquippableDispenserBehavior.INSTANCE;
        }
        return DEFAULT_BEHAVIOR;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.net_minecraft_util_math_BlockPos_up());
        boolean bl2 = state.get(TRIGGERED);
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), Block.NOTIFY_LISTENERS);
        } else if (!bl && bl2) {
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, false), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.dispense(world, state, pos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DispenserBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }

    public static Position getOutputLocation(BlockPointer pointer) {
        return DispenserBlock.getOutputLocation(pointer, 0.7, Vec3d.ZERO);
    }

    public static Position getOutputLocation(BlockPointer pointer, double facingOffset, Vec3d constantOffset) {
        Direction direction = pointer.state().get(FACING);
        return pointer.centerPos().add(facingOffset * (double)direction.getOffsetX() + constantOffset.getX(), facingOffset * (double)direction.getOffsetY() + constantOffset.getY(), facingOffset * (double)direction.getOffsetZ() + constantOffset.getZ());
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }
}

