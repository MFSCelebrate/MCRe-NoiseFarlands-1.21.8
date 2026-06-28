/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Shorts
 *  com.google.common.primitives.SignedBytes
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerInteractionManager {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private MinecraftClient client;
    final private ClientPlayNetworkHandler networkHandler;
    private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
    private ItemStack selectedStack = ItemStack.EMPTY;
    private float currentBreakingProgress;
    private float blockBreakingSoundCooldown;
    private int blockBreakingCooldown;
    private boolean breakingBlock;
    private GameMode gameMode = GameMode.DEFAULT;
    @Nullable
    private GameMode previousGameMode;
    private int lastSelectedSlot;

    public ClientPlayerInteractionManager(MinecraftClient client, ClientPlayNetworkHandler networkHandler) {
        this.client = client;
        this.networkHandler = networkHandler;
    }

    public void copyAbilities(PlayerEntity player) {
        this.gameMode.setAbilities(player.getAbilities());
    }

    public void setGameModes(GameMode gameMode, @Nullable GameMode previousGameMode) {
        this.gameMode = gameMode;
        this.previousGameMode = previousGameMode;
        this.gameMode.setAbilities(this.client.player.getAbilities());
    }

    public void setGameMode(GameMode gameMode) {
        if (gameMode != this.gameMode) {
            this.previousGameMode = this.gameMode;
        }
        this.gameMode = gameMode;
        this.gameMode.setAbilities(this.client.player.getAbilities());
    }

    public boolean hasStatusBars() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean breakBlock(BlockPos pos) {
        if (this.client.player.isBlockBreakingRestricted(this.client.world, pos, this.gameMode)) {
            return false;
        }
        ClientWorld world = this.client.world;
        BlockState blockState = world.getBlockState(pos);
        if (!this.client.player.getMainHandStack().canMine(blockState, world, pos, this.client.player)) {
            return false;
        }
        Block block = blockState.getBlock();
        if (block instanceof OperatorBlock && !this.client.player.isCreativeLevelTwoOp()) {
            return false;
        }
        if (blockState.isAir()) {
            return false;
        }
        block.onBreak(world, pos, blockState, this.client.player);
        FluidState fluidState = world.getFluidState(pos);
        boolean bl = world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL_AND_REDRAW);
        if (bl) {
            block.onBroken(world, pos, blockState);
        }
        return bl;
    }

    public boolean attackBlock(BlockPos pos, Direction direction) {
        if (this.client.player.isBlockBreakingRestricted(this.client.world, pos, this.gameMode)) {
            return false;
        }
        if (!this.client.world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (this.client.player.getAbilities().creativeMode) {
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 1.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                this.breakBlock(pos);
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
            this.blockBreakingCooldown = 5;
        } else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
            if (this.breakingBlock) {
                this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction));
            }
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 0.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                boolean bl;
                boolean bl2 = bl = !blockState.isAir();
                if (bl && this.currentBreakingProgress == 0.0f) {
                    blockState.onBlockBreakStart(this.client.world, pos, this.client.player);
                }
                if (bl && blockState.calcBlockBreakingDelta(this.client.player, this.client.player.net_minecraft_world_World_getWorld(), pos) >= 1.0f) {
                    this.breakBlock(pos);
                } else {
                    this.breakingBlock = true;
                    this.currentBreakingPos = pos;
                    this.selectedStack = this.client.player.getMainHandStack();
                    this.currentBreakingProgress = 0.0f;
                    this.blockBreakingSoundCooldown = 0.0f;
                    this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, this.getBlockBreakingProgress());
                }
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
        }
        return true;
    }

    public void cancelBlockBreaking() {
        if (this.breakingBlock) {
            BlockState blockState = this.client.world.getBlockState(this.currentBreakingPos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, this.currentBreakingPos, blockState, -1.0f);
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));
            this.breakingBlock = false;
            this.currentBreakingProgress = 0.0f;
            this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, -1);
            this.client.player.resetLastAttackedTicks();
        }
    }

    public boolean updateBlockBreakingProgress(BlockPos pos, Direction direction) {
        this.syncSelectedSlot();
        if (this.blockBreakingCooldown > 0) {
            --this.blockBreakingCooldown;
            return true;
        }
        if (this.client.player.getAbilities().creativeMode && this.client.world.getWorldBorder().contains(pos)) {
            this.blockBreakingCooldown = 5;
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 1.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                this.breakBlock(pos);
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
            return true;
        }
        if (this.isCurrentlyBreaking(pos)) {
            BlockState blockState = this.client.world.getBlockState(pos);
            if (blockState.isAir()) {
                this.breakingBlock = false;
                return false;
            }
            this.currentBreakingProgress += blockState.calcBlockBreakingDelta(this.client.player, this.client.player.net_minecraft_world_World_getWorld(), pos);
            if (this.blockBreakingSoundCooldown % 4.0f == 0.0f) {
                BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                this.client.getSoundManager().play(new PositionedSoundInstance(blockSoundGroup.getHitSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 8.0f, blockSoundGroup.getPitch() * 0.5f, SoundInstance.createRandom(), pos));
            }
            this.blockBreakingSoundCooldown += 1.0f;
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0f, 1.0f));
            if (this.currentBreakingProgress >= 1.0f) {
                this.breakingBlock = false;
                this.sendSequencedPacket(this.client.world, sequence -> {
                    this.breakBlock(pos);
                    return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction, sequence);
                });
                this.currentBreakingProgress = 0.0f;
                this.blockBreakingSoundCooldown = 0.0f;
                this.blockBreakingCooldown = 5;
            }
        } else {
            return this.attackBlock(pos, direction);
        }
        this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, this.getBlockBreakingProgress());
        return true;
    }

    private void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {
        PendingUpdateManager pendingUpdateManager = world.getPendingUpdateManager().incrementSequence();
        try {
            int i = pendingUpdateManager.getSequence();
            Packet<ServerPlayPacketListener> packet = packetCreator.predict(1);
            this.networkHandler.sendPacket(packet);
            if (pendingUpdateManager != null) {
                pendingUpdateManager.close();
            }
        }
        catch (Throwable throwable) {
            if (pendingUpdateManager != null) {
                try {
                    pendingUpdateManager.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    public void tick() {
        this.syncSelectedSlot();
        if (this.networkHandler.getConnection().isOpen()) {
            this.networkHandler.getConnection().tick();
        } else {
            this.networkHandler.getConnection().handleDisconnection();
        }
    }

    private boolean isCurrentlyBreaking(BlockPos pos) {
        ItemStack itemStack = this.client.player.getMainHandStack();
        return pos.equals(this.currentBreakingPos) && ItemStack.areItemsAndComponentsEqual(itemStack, this.selectedStack);
    }

    private void syncSelectedSlot() {
        int i = this.client.player.getInventory().getSelectedSlot();
        if (i != this.lastSelectedSlot) {
            this.lastSelectedSlot = i;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
        }
    }

    public ActionResult interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        this.syncSelectedSlot();
        if (!this.client.world.getWorldBorder().contains(hitResult.getBlockPos())) {
            return ActionResult.FAIL;
        }
        MutableObject mutableObject = new MutableObject();
        this.sendSequencedPacket(this.client.world, sequence -> {
            mutableObject.setValue((Object)this.interactBlockInternal(player, hand, hitResult));
            return new PlayerInteractBlockC2SPacket(hand, hitResult, sequence);
        });
        return (ActionResult)mutableObject.getValue();
    }

    private ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ActionResult actionResult3;
        boolean bl2;
        BlockPos blockPos = hitResult.getBlockPos();
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.CONSUME;
        }
        boolean bl = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        boolean bl3 = bl2 = player.shouldCancelInteraction() && bl;
        if (!bl2) {
            ActionResult actionResult2;
            BlockState blockState = this.client.world.getBlockState(blockPos);
            if (!this.networkHandler.hasFeature(blockState.getBlock().getRequiredFeatures())) {
                return ActionResult.FAIL;
            }
            ActionResult actionResult = blockState.onUseWithItem(player.getStackInHand(hand), this.client.world, player, hand, hitResult);
            if (actionResult.isAccepted()) {
                return actionResult;
            }
            if (actionResult instanceof ActionResult.PassToDefaultBlockAction && hand == Hand.MAIN_HAND && (actionResult2 = blockState.onUse(this.client.world, player, hitResult)).isAccepted()) {
                return actionResult2;
            }
        }
        if (itemStack.isEmpty() || player.getItemCooldownManager().isCoolingDown(itemStack)) {
            return ActionResult.PASS;
        }
        ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, hitResult);
        if (player.isInCreativeMode()) {
            int i = itemStack.getCount();
            actionResult3 = itemStack.useOnBlock(itemUsageContext);
            itemStack.setCount(i);
        } else {
            actionResult3 = itemStack.useOnBlock(itemUsageContext);
        }
        return actionResult3;
    }

    public ActionResult interactItem(PlayerEntity player, Hand hand) {
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        this.syncSelectedSlot();
        MutableObject mutableObject = new MutableObject();
        this.sendSequencedPacket(this.client.world, sequence -> {
            ItemStack itemStack2;
            PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(hand, sequence, player.getYaw(), player.getPitch());
            ItemStack itemStack = player.getStackInHand(hand);
            if (player.getItemCooldownManager().isCoolingDown(itemStack)) {
                mutableObject.setValue((Object)ActionResult.PASS);
                return playerInteractItemC2SPacket;
            }
            ActionResult actionResult = itemStack.use(this.client.world, player, hand);
            if (actionResult instanceof ActionResult.Success) {
                ActionResult.Success success = (ActionResult.Success)actionResult;
                itemStack2 = Objects.requireNonNullElseGet(success.getNewHandStack(), () -> player.getStackInHand(hand));
            } else {
                itemStack2 = player.getStackInHand(hand);
            }
            if (itemStack2 != itemStack) {
                player.setStackInHand(hand, itemStack2);
            }
            mutableObject.setValue((Object)actionResult);
            return playerInteractItemC2SPacket;
        });
        return (ActionResult)mutableObject.getValue();
    }

    public ClientPlayerEntity createPlayer(ClientWorld world, StatHandler statHandler, ClientRecipeBook recipeBook) {
        return this.createPlayer(world, statHandler, recipeBook, PlayerInput.DEFAULT, false);
    }

    public ClientPlayerEntity createPlayer(ClientWorld world, StatHandler statHandler, ClientRecipeBook recipeBook, PlayerInput lastPlayerInput, boolean lastSprinting) {
        return new ClientPlayerEntity(this.client, world, this.networkHandler, statHandler, recipeBook, lastPlayerInput, lastSprinting);
    }

    public void attackEntity(PlayerEntity player, Entity target) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, player.isSneaking()));
        if (this.gameMode != GameMode.SPECTATOR) {
            player.attack(target);
            player.resetLastAttackedTicks();
        }
    }

    public ActionResult interactEntity(PlayerEntity player, Entity entity, Hand hand) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(entity, player.isSneaking(), hand));
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        return player.interact(entity, hand);
    }

    public ActionResult interactEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand) {
        this.syncSelectedSlot();
        Vec3d vec3d = hitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
        this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interactAt(entity, player.isSneaking(), hand, vec3d));
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        return entity.interactAt(player, vec3d, hand);
    }

    public void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;
        if (syncId != screenHandler.syncId) {
            LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", (Object)syncId, (Object)screenHandler.syncId);
            return;
        }
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList list = Lists.newArrayListWithCapacity((int)i);
        for (Slot slot : defaultedList) {
            list.add(slot.getStack().copy());
        }
        screenHandler.onSlotClick(slotId, button, actionType, player);
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = (ItemStack)list.get(j);
            if (ItemStack.areEqual(itemStack, itemStack2 = defaultedList.get(j).getStack())) continue;
            int2ObjectMap.put(j, (Object)ItemStackHash.fromItemStack(itemStack2, this.networkHandler.getComponentHasher()));
        }
        ItemStackHash itemStackHash = ItemStackHash.fromItemStack(screenHandler.getCursorStack(), this.networkHandler.getComponentHasher());
        this.networkHandler.sendPacket(new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), Shorts.checkedCast((long)slotId), SignedBytes.checkedCast((long)button), actionType, (Int2ObjectMap<ItemStackHash>)int2ObjectMap, itemStackHash));
    }

    public void clickRecipe(int syncId, NetworkRecipeId recipeId, boolean craftAll) {
        this.networkHandler.sendPacket(new CraftRequestC2SPacket(syncId, recipeId, craftAll));
    }

    public void clickButton(int syncId, int buttonId) {
        this.networkHandler.sendPacket(new ButtonClickC2SPacket(syncId, buttonId));
    }

    public void clickCreativeStack(ItemStack stack, int slotId) {
        if (this.client.player.isInCreativeMode() && this.networkHandler.hasFeature(stack.getItem().getRequiredFeatures())) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slotId, stack));
        }
    }

    public void dropCreativeStack(ItemStack stack) {
        boolean bl;
        boolean bl2 = bl = this.client.currentScreen instanceof HandledScreen && !(this.client.currentScreen instanceof CreativeInventoryScreen);
        if (this.client.player.isInCreativeMode() && !bl && !stack.isEmpty() && this.networkHandler.hasFeature(stack.getItem().getRequiredFeatures())) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));
            this.client.player.getItemDropCooldown().increment();
        }
    }

    public void stopUsingItem(PlayerEntity player) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
        player.stopUsingItem();
    }

    public boolean hasExperienceBar() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean hasLimitedAttackSpeed() {
        return !this.gameMode.isCreative();
    }

    public boolean hasRidingInventory() {
        return this.client.player.hasVehicle() && this.client.player.getVehicle() instanceof RideableInventory;
    }

    public boolean isFlyingLocked() {
        return this.gameMode == GameMode.SPECTATOR;
    }

    @Nullable
    public GameMode getPreviousGameMode() {
        return this.previousGameMode;
    }

    public GameMode getCurrentGameMode() {
        return this.gameMode;
    }

    public boolean isBreakingBlock() {
        return this.breakingBlock;
    }

    public int getBlockBreakingProgress() {
        return this.currentBreakingProgress > 0.0f ? (int)(this.currentBreakingProgress * 10.0f) : -1;
    }

    public void pickItemFromBlock(BlockPos pos, boolean includeData) {
        this.networkHandler.sendPacket(new PickItemFromBlockC2SPacket(pos, includeData));
    }

    public void pickItemFromEntity(Entity entity, boolean includeData) {
        this.networkHandler.sendPacket(new PickItemFromEntityC2SPacket(entity.getId(), includeData));
    }

    public void slotChangedState(int slot, int screenHandlerId, boolean newState) {
        this.networkHandler.sendPacket(new SlotChangedStateC2SPacket(slot, screenHandlerId, newState));
    }
}

