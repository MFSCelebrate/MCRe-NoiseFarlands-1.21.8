/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;

public class ScreenHandlerType<T extends ScreenHandler>
implements ToggleableFeature {
    final static public ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X1 = ScreenHandlerType.register("generic_9x1", GenericContainerScreenHandler::createGeneric9x1);
    final static public ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X2 = ScreenHandlerType.register("generic_9x2", GenericContainerScreenHandler::createGeneric9x2);
    final static public ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X3 = ScreenHandlerType.register("generic_9x3", GenericContainerScreenHandler::createGeneric9x3);
    final static public ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X4 = ScreenHandlerType.register("generic_9x4", GenericContainerScreenHandler::createGeneric9x4);
    final static public ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X5 = ScreenHandlerType.register("generic_9x5", GenericContainerScreenHandler::createGeneric9x5);
    final static public ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X6 = ScreenHandlerType.register("generic_9x6", GenericContainerScreenHandler::createGeneric9x6);
    final static public ScreenHandlerType<Generic3x3ContainerScreenHandler> GENERIC_3X3 = ScreenHandlerType.register("generic_3x3", Generic3x3ContainerScreenHandler::new);
    final static public ScreenHandlerType<CrafterScreenHandler> CRAFTER_3X3 = ScreenHandlerType.register("crafter_3x3", CrafterScreenHandler::new);
    final static public ScreenHandlerType<AnvilScreenHandler> ANVIL = ScreenHandlerType.register("anvil", AnvilScreenHandler::new);
    final static public ScreenHandlerType<BeaconScreenHandler> BEACON = ScreenHandlerType.register("beacon", BeaconScreenHandler::new);
    final static public ScreenHandlerType<BlastFurnaceScreenHandler> BLAST_FURNACE = ScreenHandlerType.register("blast_furnace", BlastFurnaceScreenHandler::new);
    final static public ScreenHandlerType<BrewingStandScreenHandler> BREWING_STAND = ScreenHandlerType.register("brewing_stand", BrewingStandScreenHandler::new);
    final static public ScreenHandlerType<CraftingScreenHandler> CRAFTING = ScreenHandlerType.register("crafting", CraftingScreenHandler::new);
    final static public ScreenHandlerType<EnchantmentScreenHandler> ENCHANTMENT = ScreenHandlerType.register("enchantment", EnchantmentScreenHandler::new);
    final static public ScreenHandlerType<FurnaceScreenHandler> FURNACE = ScreenHandlerType.register("furnace", FurnaceScreenHandler::new);
    final static public ScreenHandlerType<GrindstoneScreenHandler> GRINDSTONE = ScreenHandlerType.register("grindstone", GrindstoneScreenHandler::new);
    final static public ScreenHandlerType<HopperScreenHandler> HOPPER = ScreenHandlerType.register("hopper", HopperScreenHandler::new);
    final static public ScreenHandlerType<LecternScreenHandler> LECTERN = ScreenHandlerType.register("lectern", (syncId, playerInventory) -> new LecternScreenHandler(syncId));
    final static public ScreenHandlerType<LoomScreenHandler> LOOM = ScreenHandlerType.register("loom", LoomScreenHandler::new);
    final static public ScreenHandlerType<MerchantScreenHandler> MERCHANT = ScreenHandlerType.register("merchant", MerchantScreenHandler::new);
    final static public ScreenHandlerType<ShulkerBoxScreenHandler> SHULKER_BOX = ScreenHandlerType.register("shulker_box", ShulkerBoxScreenHandler::new);
    final static public ScreenHandlerType<SmithingScreenHandler> SMITHING = ScreenHandlerType.register("smithing", SmithingScreenHandler::new);
    final static public ScreenHandlerType<SmokerScreenHandler> SMOKER = ScreenHandlerType.register("smoker", SmokerScreenHandler::new);
    final static public ScreenHandlerType<CartographyTableScreenHandler> CARTOGRAPHY_TABLE = ScreenHandlerType.register("cartography_table", CartographyTableScreenHandler::new);
    final static public ScreenHandlerType<StonecutterScreenHandler> STONECUTTER = ScreenHandlerType.register("stonecutter", StonecutterScreenHandler::new);
    final private FeatureSet requiredFeatures;
    final private Factory<T> factory;

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<T>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, Factory<T> factory, FeatureFlag ... requiredFeatures) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<T>(factory, FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures)));
    }

    public ScreenHandlerType(Factory<T> factory, FeatureSet requiredFeatures) {
        this.factory = factory;
        this.requiredFeatures = requiredFeatures;
    }

    public T create(int syncId, PlayerInventory playerInventory) {
        return this.factory.create(syncId, playerInventory);
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.requiredFeatures;
    }

    public static interface Factory<T extends ScreenHandler> {
        public T create(int var1, PlayerInventory var2);
    }
}

