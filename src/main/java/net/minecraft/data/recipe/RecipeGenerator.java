/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.data.recipe.SmithingTrimRecipeJsonBuilder;
import net.minecraft.data.recipe.StonecuttingRecipeJsonBuilder;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeGenerator {
    final protected RegistryWrapper.WrapperLookup registries;
    final private RegistryEntryLookup<Item> itemLookup;
    final protected RecipeExporter exporter;
    final static private Map<BlockFamily.Variant, BlockFamilyRecipeFactory> VARIANT_FACTORIES = ImmutableMap.builder().put((Object)BlockFamily.Variant.BUTTON, (generator, output, input) -> generator.createButtonRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.CHISELED, (generator, output, input) -> generator.createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.CUT, (generator, output, input) -> generator.createCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.DOOR, (generator, output, input) -> generator.createDoorRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.CUSTOM_FENCE, (generator, output, input) -> generator.createFenceRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.FENCE, (generator, output, input) -> generator.createFenceRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.CUSTOM_FENCE_GATE, (generator, output, input) -> generator.createFenceGateRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.FENCE_GATE, (generator, output, input) -> generator.createFenceGateRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.SIGN, (generator, output, input) -> generator.createSignRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.SLAB, (generator, output, input) -> generator.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.STAIRS, (generator, output, input) -> generator.createStairsRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.PRESSURE_PLATE, (generator, output, input) -> generator.createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.POLISHED, (generator, output, input) -> generator.createCondensingRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.TRAPDOOR, (generator, output, input) -> generator.createTrapdoorRecipe(output, Ingredient.ofItem(input))).put((Object)BlockFamily.Variant.WALL, (generator, output, input) -> generator.getWallRecipe(RecipeCategory.DECORATIONS, output, Ingredient.ofItem(input))).build();

    protected RecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        this.registries = registries;
        this.itemLookup = registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.ITEM);
        this.exporter = exporter;
    }

    public abstract void generate();

    public void generateFamilies(FeatureSet enabledFeatures) {
        BlockFamilies.getFamilies().filter(BlockFamily::shouldGenerateRecipes).forEach(family -> this.generateFamily((BlockFamily)family, enabledFeatures));
    }

    public void offerSingleOutputShapelessRecipe(ItemConvertible output, ItemConvertible input, @Nullable String group) {
        this.offerShapelessRecipe(output, input, group, 1);
    }

    public void offerShapelessRecipe(ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount) {
        this.createShapeless(RecipeCategory.MISC, output, outputCount).input(input).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group(group).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter, RecipeGenerator.convertBetween(output, input));
    }

    public void offerSmelting(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
        this.offerMultipleOptions(RecipeSerializer.SMELTING, SmeltingRecipe::new, inputs, category, output, experience, cookingTime, group, "_from_smelting");
    }

    public void offerBlasting(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
        this.offerMultipleOptions(RecipeSerializer.BLASTING, BlastingRecipe::new, inputs, category, output, experience, cookingTime, group, "_from_blasting");
    }

    public final <T extends AbstractCookingRecipe> void offerMultipleOptions(RecipeSerializer<T> serializer, AbstractCookingRecipe.RecipeFactory<T> recipeFactory, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group, String suffix) {
        for (ItemConvertible itemConvertible : inputs) {
            CookingRecipeJsonBuilder.create(Ingredient.ofItem(itemConvertible), category, output, experience, cookingTime, serializer, recipeFactory).net_minecraft_data_recipe_CookingRecipeJsonBuilder_group(group).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(itemConvertible), (AdvancementCriterion)this.conditionsFromItem(itemConvertible)).offerTo(this.exporter, RecipeGenerator.getItemPath(output) + suffix + "_" + RecipeGenerator.getItemPath(itemConvertible));
        }
    }

    public void offerNetheriteUpgradeRecipe(Item input, RecipeCategory category, Item result) {
        SmithingTransformRecipeJsonBuilder.create(Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.ofItem(input), this.ingredientFromTag(ItemTags.NETHERITE_TOOL_MATERIALS), category, result).criterion("has_netherite_ingot", this.conditionsFromTag(ItemTags.NETHERITE_TOOL_MATERIALS)).offerTo(this.exporter, RecipeGenerator.getItemPath(result) + "_smithing");
    }

    public void offerSmithingTrimRecipe(Item input, RegistryKey<ArmorTrimPattern> pattern, RegistryKey<Recipe<?>> recipeKey) {
        RegistryEntry.Reference<ArmorTrimPattern> reference = this.registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.TRIM_PATTERN).getOrThrow(pattern);
        SmithingTrimRecipeJsonBuilder.create(Ingredient.ofItem(input), this.ingredientFromTag(ItemTags.TRIMMABLE_ARMOR), this.ingredientFromTag(ItemTags.TRIM_MATERIALS), reference, RecipeCategory.MISC).criterion("has_smithing_trim_template", this.conditionsFromItem(input)).offerTo(this.exporter, recipeKey);
    }

    public void offer2x2CompactingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.createShaped(category, output, 1).input(Character.valueOf('#'), input).pattern("##").pattern("##").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerCompactingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, String criterionName) {
        this.createShapeless(category, output).input(input, 9).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(criterionName, (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerCompactingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.offerCompactingRecipe(category, output, input, RecipeGenerator.hasItem(input));
    }

    public void offerPlanksRecipe2(ItemConvertible output, TagKey<Item> logTag, int count) {
        this.createShapeless(RecipeCategory.BUILDING_BLOCKS, output, count).input(logTag).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group("planks").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_log", (AdvancementCriterion)this.conditionsFromTag(logTag)).offerTo(this.exporter);
    }

    public void offerPlanksRecipe(ItemConvertible output, TagKey<Item> logTag, int count) {
        this.createShapeless(RecipeCategory.BUILDING_BLOCKS, output, count).input(logTag).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group("planks").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_logs", (AdvancementCriterion)this.conditionsFromTag(logTag)).offerTo(this.exporter);
    }

    public void offerBarkBlockRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 3).input(Character.valueOf('#'), input).pattern("##").pattern("##").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("bark").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_log", (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerBoatRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.TRANSPORTATION, output).input(Character.valueOf('#'), input).pattern("# #").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("boat").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("in_water", (AdvancementCriterion)RecipeGenerator.requireEnteringFluid(Blocks.WATER)).offerTo(this.exporter);
    }

    public void offerChestBoatRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShapeless(RecipeCategory.TRANSPORTATION, output).input(Blocks.CHEST).input(input).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group("chest_boat").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_boat", (AdvancementCriterion)this.conditionsFromTag(ItemTags.BOATS)).offerTo(this.exporter);
    }

    public final CraftingRecipeJsonBuilder createButtonRecipe(ItemConvertible output, Ingredient input) {
        return this.createShapeless(RecipeCategory.REDSTONE, output).input(input);
    }

    public CraftingRecipeJsonBuilder createDoorRecipe(ItemConvertible output, Ingredient input) {
        return this.createShaped(RecipeCategory.REDSTONE, output, 3).input(Character.valueOf('#'), input).pattern("##").pattern("##").pattern("##");
    }

    public final CraftingRecipeJsonBuilder createFenceRecipe(ItemConvertible output, Ingredient input) {
        int i = output == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item item = output == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return this.createShaped(RecipeCategory.DECORATIONS, output, 1).input(Character.valueOf('W'), input).input(Character.valueOf('#'), item).pattern("W#W").pattern("W#W");
    }

    public final CraftingRecipeJsonBuilder createFenceGateRecipe(ItemConvertible output, Ingredient input) {
        return this.createShaped(RecipeCategory.REDSTONE, output).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('W'), input).pattern("#W#").pattern("#W#");
    }

    public void offerPressurePlateRecipe(ItemConvertible output, ItemConvertible input) {
        this.createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItem(input)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public final CraftingRecipeJsonBuilder createPressurePlateRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return this.createShaped(category, output).input(Character.valueOf('#'), input).pattern("##");
    }

    public void offerSlabRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.createSlabRecipe(category, output, Ingredient.ofItem(input)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public CraftingRecipeJsonBuilder createSlabRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return this.createShaped(category, output, 6).input(Character.valueOf('#'), input).pattern("###");
    }

    public CraftingRecipeJsonBuilder createStairsRecipe(ItemConvertible output, Ingredient input) {
        return this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 4).input(Character.valueOf('#'), input).pattern("#  ").pattern("## ").pattern("###");
    }

    public CraftingRecipeJsonBuilder createTrapdoorRecipe(ItemConvertible output, Ingredient input) {
        return this.createShaped(RecipeCategory.REDSTONE, output, 2).input(Character.valueOf('#'), input).pattern("###").pattern("###");
    }

    public final CraftingRecipeJsonBuilder createSignRecipe(ItemConvertible output, Ingredient input) {
        return this.createShaped(RecipeCategory.DECORATIONS, output, 3).net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("sign").input(Character.valueOf('#'), input).input(Character.valueOf('X'), Items.STICK).pattern("###").pattern("###").pattern(" X ");
    }

    public void offerHangingSignRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.DECORATIONS, output, 6).net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("hanging_sign").input(Character.valueOf('#'), input).input(Character.valueOf('X'), Items.CHAIN).pattern("X X").pattern("###").pattern("###").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_stripped_logs", (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerDyeableRecipes(List<Item> dyes, List<Item> dyeables, String group, RecipeCategory category) {
        this.offerDyeablesRecipes(dyes, dyeables, null, group, category);
    }

    public void offerDyeablesRecipes(List<Item> dyes, List<Item> dyeables, @Nullable Item undyed, String group, RecipeCategory category) {
        for (int i = 0; i < dyes.size(); ++i) {
            Item item2 = dyes.get(i);
            Item item22 = dyeables.get(i);
            Stream<Item> stream = dyeables.stream().filter(item -> !item.equals(item22));
            if (undyed != null) {
                stream = Stream.concat(stream, Stream.of(undyed));
            }
            this.createShapeless(category, item22).input(item2).input(Ingredient.ofItems(stream)).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group(group).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_needed_dye", (AdvancementCriterion)this.conditionsFromItem(item2)).offerTo(this.exporter, "dye_" + RecipeGenerator.getItemPath(item22));
        }
    }

    public void offerCarpetRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.DECORATIONS, output, 3).input(Character.valueOf('#'), input).pattern("##").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("carpet").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerBedRecipe(ItemConvertible output, ItemConvertible inputWool) {
        this.createShaped(RecipeCategory.DECORATIONS, output).input(Character.valueOf('#'), inputWool).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("###").pattern("XXX").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("bed").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(inputWool), (AdvancementCriterion)this.conditionsFromItem(inputWool)).offerTo(this.exporter);
    }

    public void offerBannerRecipe(ItemConvertible output, ItemConvertible inputWool) {
        this.createShaped(RecipeCategory.DECORATIONS, output).input(Character.valueOf('#'), inputWool).input(Character.valueOf('|'), Items.STICK).pattern("###").pattern("###").pattern(" | ").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("banner").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(inputWool), (AdvancementCriterion)this.conditionsFromItem(inputWool)).offerTo(this.exporter);
    }

    public void offerStainedGlassDyeingRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 8).input(Character.valueOf('#'), Blocks.GLASS).input(Character.valueOf('X'), input).pattern("###").pattern("#X#").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("stained_glass").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_glass", (AdvancementCriterion)this.conditionsFromItem(Blocks.GLASS)).offerTo(this.exporter);
    }

    public void offerDriedGhast(ItemConvertible output) {
        this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 1).input(Character.valueOf('#'), Items.GHAST_TEAR).input(Character.valueOf('X'), Items.SOUL_SAND).pattern("###").pattern("#X#").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("dry_ghast").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(Items.GHAST_TEAR), (AdvancementCriterion)this.conditionsFromItem(Items.GHAST_TEAR)).offerTo(this.exporter);
    }

    public void offerHarness(ItemConvertible output, ItemConvertible wool) {
        this.createShaped(RecipeCategory.COMBAT, output).input(Character.valueOf('#'), wool).input(Character.valueOf('G'), Items.GLASS).input(Character.valueOf('L'), Items.LEATHER).pattern("LLL").pattern("G#G").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("harness").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_dried_ghast", (AdvancementCriterion)this.conditionsFromItem(Blocks.DRIED_GHAST)).offerTo(this.exporter);
    }

    public void offerStainedGlassPaneRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.DECORATIONS, output, 16).input(Character.valueOf('#'), input).pattern("###").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("stained_glass_pane").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_glass", (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerStainedGlassPaneDyeingRecipe(ItemConvertible output, ItemConvertible inputDye) {
        ((ShapedRecipeJsonBuilder)this.createShaped(RecipeCategory.DECORATIONS, output, 8).input(Character.valueOf('#'), Blocks.GLASS_PANE).input(Character.valueOf('$'), inputDye).pattern("###").pattern("#$#").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("stained_glass_pane").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_glass_pane", (AdvancementCriterion)this.conditionsFromItem(Blocks.GLASS_PANE))).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(inputDye), (AdvancementCriterion)this.conditionsFromItem(inputDye)).offerTo(this.exporter, RecipeGenerator.convertBetween(output, Blocks.GLASS_PANE));
    }

    public void offerTerracottaDyeingRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 8).input(Character.valueOf('#'), Blocks.TERRACOTTA).input(Character.valueOf('X'), input).pattern("###").pattern("#X#").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group("stained_terracotta").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_terracotta", (AdvancementCriterion)this.conditionsFromItem(Blocks.TERRACOTTA)).offerTo(this.exporter);
    }

    public void offerConcretePowderDyeingRecipe(ItemConvertible output, ItemConvertible input) {
        ((ShapelessRecipeJsonBuilder)this.createShapeless(RecipeCategory.BUILDING_BLOCKS, output, 8).input(input).input(Blocks.SAND, 4).input(Blocks.GRAVEL, 4).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group("concrete_powder").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_sand", (AdvancementCriterion)this.conditionsFromItem(Blocks.SAND))).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion("has_gravel", (AdvancementCriterion)this.conditionsFromItem(Blocks.GRAVEL)).offerTo(this.exporter);
    }

    public void offerCandleDyeingRecipe(ItemConvertible output, ItemConvertible input) {
        this.createShapeless(RecipeCategory.DECORATIONS, output).input(Blocks.CANDLE).input(input).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group("dyed_candle").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerWallRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.getWallRecipe(category, output, Ingredient.ofItem(input)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public final CraftingRecipeJsonBuilder getWallRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return this.createShaped(category, output, 6).input(Character.valueOf('#'), input).pattern("###").pattern("###");
    }

    public void offerPolishedStoneRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.createCondensingRecipe(category, output, Ingredient.ofItem(input)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public final CraftingRecipeJsonBuilder createCondensingRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return this.createShaped(category, output, 4).input(Character.valueOf('S'), input).pattern("SS").pattern("SS");
    }

    public void offerCutCopperRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.createCutCopperRecipe(category, output, Ingredient.ofItem(input)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public final ShapedRecipeJsonBuilder createCutCopperRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return this.createShaped(category, output, 4).input(Character.valueOf('#'), input).pattern("##").pattern("##");
    }

    public void offerChiseledBlockRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.createChiseledBlockRecipe(category, output, Ingredient.ofItem(input)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerMosaicRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.createShaped(category, output).input(Character.valueOf('#'), input).pattern("#").pattern("#").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public ShapedRecipeJsonBuilder createChiseledBlockRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return this.createShaped(category, output).input(Character.valueOf('#'), input).pattern("#").pattern("#");
    }

    public void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        this.offerStonecuttingRecipe(category, output, input, 1);
    }

    public void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
        StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(input), category, output, count).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter, RecipeGenerator.convertBetween(output, input) + "_stonecutting");
    }

    public final void offerCrackingRecipe(ItemConvertible output, ItemConvertible input) {
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(input), RecipeCategory.BUILDING_BLOCKS, output, 0.1f, 200).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerReversibleCompactingRecipes(RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem) {
        this.offerReversibleCompactingRecipes(reverseCategory, baseItem, compactingCategory, compactItem, RecipeGenerator.getRecipeName(compactItem), null, RecipeGenerator.getRecipeName(baseItem), null);
    }

    public void offerReversibleCompactingRecipesWithCompactingRecipeGroup(RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem, String compactingId, String compactingGroup) {
        this.offerReversibleCompactingRecipes(reverseCategory, baseItem, compactingCategory, compactItem, compactingId, compactingGroup, RecipeGenerator.getRecipeName(baseItem), null);
    }

    public void offerReversibleCompactingRecipesWithReverseRecipeGroup(RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem, String reverseId, String reverseGroup) {
        this.offerReversibleCompactingRecipes(reverseCategory, baseItem, compactingCategory, compactItem, RecipeGenerator.getRecipeName(compactItem), null, reverseId, reverseGroup);
    }

    public final void offerReversibleCompactingRecipes(RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem, String compactingId, @Nullable String compactingGroup, String reverseId, @Nullable String reverseGroup) {
        ((ShapelessRecipeJsonBuilder)this.createShapeless(reverseCategory, baseItem, 9).input(compactItem).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group(reverseGroup).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(compactItem), (AdvancementCriterion)this.conditionsFromItem(compactItem))).offerTo(this.exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(reverseId)));
        ((ShapedRecipeJsonBuilder)this.createShaped(compactingCategory, compactItem).input(Character.valueOf('#'), baseItem).pattern("###").pattern("###").pattern("###").net_minecraft_data_recipe_ShapedRecipeJsonBuilder_group(compactingGroup).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(baseItem), (AdvancementCriterion)this.conditionsFromItem(baseItem))).offerTo(this.exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(compactingId)));
    }

    public void offerSmithingTemplateCopyingRecipe(ItemConvertible template, ItemConvertible resource) {
        this.createShaped(RecipeCategory.MISC, template, 2).input(Character.valueOf('#'), Items.DIAMOND).input(Character.valueOf('C'), resource).input(Character.valueOf('S'), template).pattern("#S#").pattern("#C#").pattern("###").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(template), (AdvancementCriterion)this.conditionsFromItem(template)).offerTo(this.exporter);
    }

    public void offerSmithingTemplateCopyingRecipe(ItemConvertible template, Ingredient resource) {
        this.createShaped(RecipeCategory.MISC, template, 2).input(Character.valueOf('#'), Items.DIAMOND).input(Character.valueOf('C'), resource).input(Character.valueOf('S'), template).pattern("#S#").pattern("#C#").pattern("###").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(template), (AdvancementCriterion)this.conditionsFromItem(template)).offerTo(this.exporter);
    }

    public <T extends AbstractCookingRecipe> void generateCookingRecipes(String cooker, RecipeSerializer<T> serializer, AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime) {
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.COD, Items.COOKED_COD, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35f);
        this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35f);
    }

    public final <T extends AbstractCookingRecipe> void offerFoodCookingRecipe(String cooker, RecipeSerializer<T> serializer, AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime, ItemConvertible input, ItemConvertible output, float experience) {
        CookingRecipeJsonBuilder.create(Ingredient.ofItem(input), RecipeCategory.FOOD, output, experience, cookingTime, serializer, recipeFactory).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter, RecipeGenerator.getItemPath(output) + "_from_" + cooker);
    }

    public void offerWaxingRecipes(FeatureSet enabledFeatures) {
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().forEach((unwaxed, waxed) -> {
            if (!waxed.getRequiredFeatures().isSubsetOf(enabledFeatures)) {
                return;
            }
            this.createShapeless(RecipeCategory.BUILDING_BLOCKS, (ItemConvertible)waxed).input((ItemConvertible)unwaxed).input(Items.HONEYCOMB).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group(RecipeGenerator.getItemPath(waxed)).net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(unwaxed), (AdvancementCriterion)this.conditionsFromItem((ItemConvertible)unwaxed)).offerTo(this.exporter, RecipeGenerator.convertBetween(waxed, Items.HONEYCOMB));
        });
    }

    public void offerGrateRecipe(Block output, Block input) {
        this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 4).input(Character.valueOf('M'), input).pattern(" M ").pattern("M M").pattern(" M ").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerBulbRecipe(Block output, Block input) {
        this.createShaped(RecipeCategory.REDSTONE, output, 4).input(Character.valueOf('C'), input).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('B'), Items.BLAZE_ROD).pattern(" C ").pattern("CBC").pattern(" R ").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter);
    }

    public void offerSuspiciousStewRecipe(Item input, SuspiciousStewIngredient stewIngredient) {
        ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW.getRegistryEntry(), 1, ComponentChanges.builder().add(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, stewIngredient.getStewEffects()).build());
        this.createShapeless(RecipeCategory.FOOD, itemStack).input(Items.BOWL).input(Items.BROWN_MUSHROOM).input(Items.RED_MUSHROOM).input(input).net_minecraft_data_recipe_ShapelessRecipeJsonBuilder_group("suspicious_stew").net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(RecipeGenerator.hasItem(input), (AdvancementCriterion)this.conditionsFromItem(input)).offerTo(this.exporter, RecipeGenerator.getItemPath(itemStack.getItem()) + "_from_" + RecipeGenerator.getItemPath(input));
    }

    public void generateFamily(BlockFamily family, FeatureSet enabledFeatures) {
        family.getVariants().forEach((variant, block) -> {
            if (!block.getRequiredFeatures().isSubsetOf(enabledFeatures)) {
                return;
            }
            BlockFamilyRecipeFactory blockFamilyRecipeFactory = VARIANT_FACTORIES.get(variant);
            Block itemConvertible = this.getVariantRecipeInput(family, (BlockFamily.Variant)((Object)variant));
            if (blockFamilyRecipeFactory != null) {
                CraftingRecipeJsonBuilder craftingRecipeJsonBuilder = blockFamilyRecipeFactory.create(this, (ItemConvertible)block, itemConvertible);
                family.getGroup().ifPresent(group -> craftingRecipeJsonBuilder.net_minecraft_data_recipe_CraftingRecipeJsonBuilder_group(group + (String)(variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
                craftingRecipeJsonBuilder.net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(family.getUnlockCriterionName().orElseGet(() -> RecipeGenerator.hasItem(itemConvertible)), this.conditionsFromItem(itemConvertible));
                craftingRecipeJsonBuilder.offerTo(this.exporter);
            }
            if (variant == BlockFamily.Variant.CRACKED) {
                this.offerCrackingRecipe((ItemConvertible)block, itemConvertible);
            }
        });
    }

    public final Block getVariantRecipeInput(BlockFamily family, BlockFamily.Variant variant) {
        if (variant == BlockFamily.Variant.CHISELED) {
            if (!family.getVariants().containsKey((Object)BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            }
            return family.getVariant(BlockFamily.Variant.SLAB);
        }
        return family.getBaseBlock();
    }

    public static AdvancementCriterion<EnterBlockCriterion.Conditions> requireEnteringFluid(Block block) {
        return Criteria.ENTER_BLOCK.create(new EnterBlockCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
    }

    public final AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
        return RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(this.itemLookup, item).count(count));
    }

    public AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(ItemConvertible item) {
        return RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().items(this.itemLookup, item));
    }

    public AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromTag(TagKey<Item> tag) {
        return RecipeGenerator.conditionsFromPredicates(ItemPredicate.Builder.create().tag(this.itemLookup, tag));
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromPredicates(ItemPredicate.Builder ... predicates) {
        return RecipeGenerator.conditionsFromItemPredicates((ItemPredicate[])Arrays.stream(predicates).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItemPredicates(ItemPredicate ... predicates) {
        return Criteria.INVENTORY_CHANGED.create(new InventoryChangedCriterion.Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(predicates)));
    }

    public static String hasItem(ItemConvertible item) {
        return "has_" + RecipeGenerator.getItemPath(item);
    }

    public static String getItemPath(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem()).getPath();
    }

    public static String getRecipeName(ItemConvertible item) {
        return RecipeGenerator.getItemPath(item);
    }

    public static String convertBetween(ItemConvertible to, ItemConvertible from) {
        return RecipeGenerator.getItemPath(to) + "_from_" + RecipeGenerator.getItemPath(from);
    }

    public static String getSmeltingItemPath(ItemConvertible item) {
        return RecipeGenerator.getItemPath(item) + "_from_smelting";
    }

    public static String getBlastingItemPath(ItemConvertible item) {
        return RecipeGenerator.getItemPath(item) + "_from_blasting";
    }

    public Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.ofTag(this.itemLookup.getOrThrow(tag));
    }

    public ShapedRecipeJsonBuilder createShaped(RecipeCategory category, ItemConvertible output) {
        return ShapedRecipeJsonBuilder.create(this.itemLookup, category, output);
    }

    public ShapedRecipeJsonBuilder createShaped(RecipeCategory category, ItemConvertible output, int count) {
        return ShapedRecipeJsonBuilder.create(this.itemLookup, category, output, count);
    }

    public ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemStack output) {
        return ShapelessRecipeJsonBuilder.create(this.itemLookup, category, output);
    }

    public ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemConvertible output) {
        return ShapelessRecipeJsonBuilder.create(this.itemLookup, category, output);
    }

    public ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemConvertible output, int count) {
        return ShapelessRecipeJsonBuilder.create(this.itemLookup, category, output, count);
    }

    @FunctionalInterface
    static interface BlockFamilyRecipeFactory {
        public CraftingRecipeJsonBuilder create(RecipeGenerator var1, ItemConvertible var2, ItemConvertible var3);
    }

    public static abstract class RecipeProvider
    implements DataProvider {
        final private DataOutput output;
        final private CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

        protected RecipeProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        public CompletableFuture<?> run(final DataWriter writer) {
            return this.registriesFuture.thenCompose(registries -> {
                DataOutput.PathResolver pathResolver = this.output.getResolver(RegistryKeys.RECIPE);
                DataOutput.PathResolver pathResolver2 = this.output.getResolver(RegistryKeys.ADVANCEMENT);
                final HashSet set = Sets.newHashSet();
                final ArrayList list = new ArrayList();
                RecipeExporter recipeExporter = new RecipeExporter(){
                    final RegistryWrapper.WrapperLookup registries;
                    final DataOutput.PathResolver recipePathResolver;
                    final DataOutput.PathResolver recipeAdvancementPathResolver;
                    {
                        this.registries = wrapperLookup;
                        this.recipePathResolver = pathResolver;
                        this.recipeAdvancementPathResolver = pathResolver2;
                    }

                    @Override
                    public void accept(RegistryKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
                        if (!set.add(key)) {
                            throw new IllegalStateException("Duplicate recipe " + String.valueOf(key.getValue()));
                        }
                        this.addRecipe(key, recipe);
                        if (advancement != null) {
                            this.addRecipeAdvancement(advancement);
                        }
                    }

                    @Override
                    public Advancement.Builder getAdvancementBuilder() {
                        return Advancement.Builder.createUntelemetered().parent(CraftingRecipeJsonBuilder.ROOT);
                    }

                    @Override
                    public void addRootAdvancement() {
                        AdvancementEntry advancementEntry = Advancement.Builder.createUntelemetered().criterion("impossible", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())).build(CraftingRecipeJsonBuilder.ROOT);
                        this.addRecipeAdvancement(advancementEntry);
                    }

                    private void addRecipe(RegistryKey<Recipe<?>> key, Recipe<?> recipe) {
                        list.add(DataProvider.writeCodecToPath(writer, this.registries, Recipe.CODEC, recipe, this.recipePathResolver.resolveJson(key.getValue())));
                    }

                    private void addRecipeAdvancement(AdvancementEntry advancementEntry) {
                        list.add(DataProvider.writeCodecToPath(writer, this.registries, Advancement.CODEC, advancementEntry.value(), this.recipeAdvancementPathResolver.resolveJson(advancementEntry.id())));
                    }
                };
                this.getRecipeGenerator((RegistryWrapper.WrapperLookup)registries, recipeExporter).generate();
                return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
            });
        }

        protected abstract RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup var1, RecipeExporter var2);
    }
}

