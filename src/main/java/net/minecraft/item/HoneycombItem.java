/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 */
package net.minecraft.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SignChangingItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class HoneycombItem
extends Item
implements SignChangingItem {
    final static public Supplier<BiMap<Block, Block>> UNWAXED_TO_WAXED_BLOCKS = Suppliers.memoize(() -> ImmutableBiMap.builder().put((Object)Blocks.COPPER_BLOCK, (Object)Blocks.WAXED_COPPER_BLOCK).put((Object)Blocks.EXPOSED_COPPER, (Object)Blocks.WAXED_EXPOSED_COPPER).put((Object)Blocks.WEATHERED_COPPER, (Object)Blocks.WAXED_WEATHERED_COPPER).put((Object)Blocks.OXIDIZED_COPPER, (Object)Blocks.WAXED_OXIDIZED_COPPER).put((Object)Blocks.CUT_COPPER, (Object)Blocks.WAXED_CUT_COPPER).put((Object)Blocks.EXPOSED_CUT_COPPER, (Object)Blocks.WAXED_EXPOSED_CUT_COPPER).put((Object)Blocks.WEATHERED_CUT_COPPER, (Object)Blocks.WAXED_WEATHERED_CUT_COPPER).put((Object)Blocks.OXIDIZED_CUT_COPPER, (Object)Blocks.WAXED_OXIDIZED_CUT_COPPER).put((Object)Blocks.CUT_COPPER_SLAB, (Object)Blocks.WAXED_CUT_COPPER_SLAB).put((Object)Blocks.EXPOSED_CUT_COPPER_SLAB, (Object)Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB).put((Object)Blocks.WEATHERED_CUT_COPPER_SLAB, (Object)Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB).put((Object)Blocks.OXIDIZED_CUT_COPPER_SLAB, (Object)Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB).put((Object)Blocks.CUT_COPPER_STAIRS, (Object)Blocks.WAXED_CUT_COPPER_STAIRS).put((Object)Blocks.EXPOSED_CUT_COPPER_STAIRS, (Object)Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS).put((Object)Blocks.WEATHERED_CUT_COPPER_STAIRS, (Object)Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS).put((Object)Blocks.OXIDIZED_CUT_COPPER_STAIRS, (Object)Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS).put((Object)Blocks.CHISELED_COPPER, (Object)Blocks.WAXED_CHISELED_COPPER).put((Object)Blocks.EXPOSED_CHISELED_COPPER, (Object)Blocks.WAXED_EXPOSED_CHISELED_COPPER).put((Object)Blocks.WEATHERED_CHISELED_COPPER, (Object)Blocks.WAXED_WEATHERED_CHISELED_COPPER).put((Object)Blocks.OXIDIZED_CHISELED_COPPER, (Object)Blocks.WAXED_OXIDIZED_CHISELED_COPPER).put((Object)Blocks.COPPER_DOOR, (Object)Blocks.WAXED_COPPER_DOOR).put((Object)Blocks.EXPOSED_COPPER_DOOR, (Object)Blocks.WAXED_EXPOSED_COPPER_DOOR).put((Object)Blocks.WEATHERED_COPPER_DOOR, (Object)Blocks.WAXED_WEATHERED_COPPER_DOOR).put((Object)Blocks.OXIDIZED_COPPER_DOOR, (Object)Blocks.WAXED_OXIDIZED_COPPER_DOOR).put((Object)Blocks.COPPER_TRAPDOOR, (Object)Blocks.WAXED_COPPER_TRAPDOOR).put((Object)Blocks.EXPOSED_COPPER_TRAPDOOR, (Object)Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR).put((Object)Blocks.WEATHERED_COPPER_TRAPDOOR, (Object)Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR).put((Object)Blocks.OXIDIZED_COPPER_TRAPDOOR, (Object)Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR).put((Object)Blocks.COPPER_GRATE, (Object)Blocks.WAXED_COPPER_GRATE).put((Object)Blocks.EXPOSED_COPPER_GRATE, (Object)Blocks.WAXED_EXPOSED_COPPER_GRATE).put((Object)Blocks.WEATHERED_COPPER_GRATE, (Object)Blocks.WAXED_WEATHERED_COPPER_GRATE).put((Object)Blocks.OXIDIZED_COPPER_GRATE, (Object)Blocks.WAXED_OXIDIZED_COPPER_GRATE).put((Object)Blocks.COPPER_BULB, (Object)Blocks.WAXED_COPPER_BULB).put((Object)Blocks.EXPOSED_COPPER_BULB, (Object)Blocks.WAXED_EXPOSED_COPPER_BULB).put((Object)Blocks.WEATHERED_COPPER_BULB, (Object)Blocks.WAXED_WEATHERED_COPPER_BULB).put((Object)Blocks.OXIDIZED_COPPER_BULB, (Object)Blocks.WAXED_OXIDIZED_COPPER_BULB).build());
    final static public Supplier<BiMap<Block, Block>> WAXED_TO_UNWAXED_BLOCKS = Suppliers.memoize(() -> UNWAXED_TO_WAXED_BLOCKS.get().inverse());

    public HoneycombItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        return HoneycombItem.getWaxedState(blockState).map(state -> {
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            if (playerEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
                Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, blockPos, itemStack);
            }
            itemStack.decrement(1);
            world.setBlockState(blockPos, (BlockState)state, Block.NOTIFY_ALL_AND_REDRAW);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, state));
            world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_WAXED, blockPos, 0);
            return ActionResult.SUCCESS;
        }).orElse(ActionResult.PASS);
    }

    public static Optional<BlockState> getWaxedState(BlockState state) {
        return Optional.ofNullable((Block)UNWAXED_TO_WAXED_BLOCKS.get().get((Object)state.getBlock())).map(block -> block.getStateWithProperties(state));
    }

    @Override
    public boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player) {
        if (signBlockEntity.setWaxed(true)) {
            world.syncWorldEvent(null, WorldEvents.BLOCK_WAXED, signBlockEntity.getPos(), 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean canUseOnSignText(SignText signText, PlayerEntity player) {
        return true;
    }
}

