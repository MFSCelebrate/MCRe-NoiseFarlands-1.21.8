/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class SmithingScreenHandler
extends ForgingScreenHandler {
    final static public int TEMPLATE_ID = 0;
    final static public int EQUIPMENT_ID = 1;
    final static public int MATERIAL_ID = 2;
    final static public int OUTPUT_ID = 3;
    final static public int TEMPLATE_X = 8;
    final static public int EQUIPMENT_X = 26;
    final static public int MATERIAL_X = 44;
    final static private int OUTPUT_X = 98;
    final static public int SLOT_Y = 48;
    final private World world;
    final private RecipePropertySet basePropertySet;
    final private RecipePropertySet templatePropertySet;
    final private RecipePropertySet additionPropertySet;
    final private Property invalidRecipe = Property.create();

    public SmithingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public SmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        this(syncId, playerInventory, context, playerInventory.player.net_minecraft_world_World_getWorld());
    }

    private SmithingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, World world) {
        super(ScreenHandlerType.SMITHING, syncId, playerInventory, context, SmithingScreenHandler.createForgingSlotsManager(world.net_minecraft_recipe_RecipeManager_getRecipeManager()));
        this.world = world;
        this.basePropertySet = world.net_minecraft_recipe_RecipeManager_getRecipeManager().getPropertySet(RecipePropertySet.SMITHING_BASE);
        this.templatePropertySet = world.net_minecraft_recipe_RecipeManager_getRecipeManager().getPropertySet(RecipePropertySet.SMITHING_TEMPLATE);
        this.additionPropertySet = world.net_minecraft_recipe_RecipeManager_getRecipeManager().getPropertySet(RecipePropertySet.SMITHING_ADDITION);
        this.addProperty(this.invalidRecipe).set(0);
    }

    private static ForgingSlotsManager createForgingSlotsManager(RecipeManager recipeManager) {
        RecipePropertySet recipePropertySet = recipeManager.getPropertySet(RecipePropertySet.SMITHING_BASE);
        RecipePropertySet recipePropertySet2 = recipeManager.getPropertySet(RecipePropertySet.SMITHING_TEMPLATE);
        RecipePropertySet recipePropertySet3 = recipeManager.getPropertySet(RecipePropertySet.SMITHING_ADDITION);
        return ForgingSlotsManager.builder().input(0, 8, 48, recipePropertySet2::canUse).input(1, 26, 48, recipePropertySet::canUse).input(2, 44, 48, recipePropertySet3::canUse).output(3, 98, 48).build();
    }

    @Override
    protected boolean canUse(BlockState state) {
        return state.isOf(Blocks.SMITHING_TABLE);
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraftByPlayer(player, stack.getCount());
        this.output.unlockLastRecipe(player, this.getInputStacks());
        this.decrementStack(0);
        this.decrementStack(1);
        this.decrementStack(2);
        this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, (BlockPos)pos, 0));
    }

    private List<ItemStack> getInputStacks() {
        return List.of(this.input.getStack(0), this.input.getStack(1), this.input.getStack(2));
    }

    private SmithingRecipeInput createRecipeInput() {
        return new SmithingRecipeInput(this.input.getStack(0), this.input.getStack(1), this.input.getStack(2));
    }

    private void decrementStack(int slot) {
        ItemStack itemStack = this.input.getStack(slot);
        if (!itemStack.isEmpty()) {
            itemStack.decrement(1);
            this.input.setStack(slot, itemStack);
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (this.world instanceof ServerWorld) {
            boolean bl = this.getSlot(0).hasStack() && this.getSlot(1).hasStack() && this.getSlot(2).hasStack() && !this.getSlot(this.getResultSlotIndex()).hasStack();
            this.invalidRecipe.set(bl ? 1 : 0);
        }
    }

    @Override
    public void updateResult() {
        Optional<RecipeEntry<Object>> optional;
        SmithingRecipeInput smithingRecipeInput = this.createRecipeInput();
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            optional = serverWorld.net_minecraft_recipe_ServerRecipeManager_getRecipeManager().getFirstMatch(RecipeType.SMITHING, smithingRecipeInput, serverWorld);
        } else {
            optional = Optional.empty();
        }
        optional.ifPresentOrElse(recipe -> {
            ItemStack itemStack = ((SmithingRecipe)recipe.value()).craft(smithingRecipeInput, this.world.getRegistryManager());
            this.output.setLastRecipe((RecipeEntry<?>)recipe);
            this.output.setStack(0, itemStack);
        }, () -> {
            this.output.setLastRecipe(null);
            this.output.setStack(0, ItemStack.EMPTY);
        });
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public boolean isValidIngredient(ItemStack stack) {
        if (this.templatePropertySet.canUse(stack) && !this.getSlot(0).hasStack()) {
            return true;
        }
        if (this.basePropertySet.canUse(stack) && !this.getSlot(1).hasStack()) {
            return true;
        }
        return this.additionPropertySet.canUse(stack) && !this.getSlot(2).hasStack();
    }

    public boolean hasInvalidRecipe() {
        return this.invalidRecipe.get() > 0;
    }
}

