/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.block.DriedGhastBlock;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.LightBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.PaleMossCarpetBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SnifferEggBlock;
import net.minecraft.block.TestBlock;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.CreakingHeartState;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.enums.Tilt;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.enums.VaultState;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.BannerModelRenderer;
import net.minecraft.client.render.item.model.special.BedModelRenderer;
import net.minecraft.client.render.item.model.special.ChestModelRenderer;
import net.minecraft.client.render.item.model.special.ConduitModelRenderer;
import net.minecraft.client.render.item.model.special.DecoratedPotModelRenderer;
import net.minecraft.client.render.item.model.special.HeadModelRenderer;
import net.minecraft.client.render.item.model.special.PlayerHeadModelRenderer;
import net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.tint.GrassTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.MultipartModelCombinedCondition;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.State;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockStateModelGenerator {
    final public Consumer<BlockModelDefinitionCreator> blockStateCollector;
    final public ItemModelOutput itemModelOutput;
    final public BiConsumer<Identifier, ModelSupplier> modelCollector;
    final static List<Block> UNORIENTABLE_TRAPDOORS = List.of(Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
    final static public ModelVariantOperator NO_OP = variant -> variant;
    final static public ModelVariantOperator UV_LOCK = ModelVariantOperator.UV_LOCK.withValue(true);
    final static public ModelVariantOperator ROTATE_X_90 = ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90);
    final static public ModelVariantOperator ROTATE_X_180 = ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180);
    final static public ModelVariantOperator ROTATE_X_270 = ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270);
    final static public ModelVariantOperator ROTATE_Y_90 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90);
    final static public ModelVariantOperator ROTATE_Y_180 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180);
    final static public ModelVariantOperator ROTATE_Y_270 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270);
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_1_CONDITION_FUNCTION = builder -> builder;
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_2_CONDITION_FUNCTION = builder -> builder.put(Properties.FLOWER_AMOUNT, Integer.valueOf(2), new Integer[]{3, 4});
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_3_CONDITION_FUNCTION = builder -> builder.put(Properties.FLOWER_AMOUNT, Integer.valueOf(3), new Integer[]{4});
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_4_CONDITION_FUNCTION = builder -> builder.put(Properties.FLOWER_AMOUNT, 4);
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_1_CONDITION_FUNCTION = builder -> builder.put(Properties.SEGMENT_AMOUNT, 1);
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_2_CONDITION_FUNCTION = builder -> builder.put(Properties.SEGMENT_AMOUNT, Integer.valueOf(2), new Integer[]{3});
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_3_CONDITION_FUNCTION = builder -> builder.put(Properties.SEGMENT_AMOUNT, 3);
    final static private Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_4_CONDITION_FUNCTION = builder -> builder.put(Properties.SEGMENT_AMOUNT, 4);
    final static Map<Block, StateFactory> BASE_WITH_CUSTOM_GENERATOR = Map.of(Blocks.STONE, BlockStateModelGenerator::createStoneState, Blocks.DEEPSLATE, BlockStateModelGenerator::createDeepslateState, Blocks.MUD_BRICKS, BlockStateModelGenerator::createMudBrickState);
    final static private BlockStateVariantMap<ModelVariantOperator> NORTH_DEFAULT_ROTATION_OPERATIONS = BlockStateVariantMap.operations(Properties.FACING).register(Direction.DOWN, ROTATE_X_90).register(Direction.UP, ROTATE_X_270).register(Direction.NORTH, NO_OP).register(Direction.SOUTH, ROTATE_Y_180).register(Direction.WEST, ROTATE_Y_270).register(Direction.EAST, ROTATE_Y_90);
    final static private BlockStateVariantMap<ModelVariantOperator> UP_DEFAULT_ROTATION_OPERATIONS = BlockStateVariantMap.operations(Properties.FACING).register(Direction.DOWN, ROTATE_X_180).register(Direction.UP, NO_OP).register(Direction.NORTH, ROTATE_X_90).register(Direction.SOUTH, ROTATE_X_90.then(ROTATE_Y_180)).register(Direction.WEST, ROTATE_X_90.then(ROTATE_Y_270)).register(Direction.EAST, ROTATE_X_90.then(ROTATE_Y_90));
    final static private BlockStateVariantMap<ModelVariantOperator> EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).register(Direction.EAST, NO_OP).register(Direction.SOUTH, ROTATE_Y_90).register(Direction.WEST, ROTATE_Y_180).register(Direction.NORTH, ROTATE_Y_270);
    final static private BlockStateVariantMap<ModelVariantOperator> SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).register(Direction.SOUTH, NO_OP).register(Direction.WEST, ROTATE_Y_90).register(Direction.NORTH, ROTATE_Y_180).register(Direction.EAST, ROTATE_Y_270);
    final static private BlockStateVariantMap<ModelVariantOperator> NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).register(Direction.EAST, ROTATE_Y_90).register(Direction.SOUTH, ROTATE_Y_180).register(Direction.WEST, ROTATE_Y_270).register(Direction.NORTH, NO_OP);
    final static Map<Block, TexturedModel> TEXTURED_MODELS = ImmutableMap.builder().put((Object)Blocks.SANDSTONE, (Object)TexturedModel.SIDE_TOP_BOTTOM_WALL.get(Blocks.SANDSTONE)).put((Object)Blocks.RED_SANDSTONE, (Object)TexturedModel.SIDE_TOP_BOTTOM_WALL.get(Blocks.RED_SANDSTONE)).put((Object)Blocks.SMOOTH_SANDSTONE, (Object)TexturedModel.getCubeAll(TextureMap.getSubId(Blocks.SANDSTONE, "_top"))).put((Object)Blocks.SMOOTH_RED_SANDSTONE, (Object)TexturedModel.getCubeAll(TextureMap.getSubId(Blocks.RED_SANDSTONE, "_top"))).put((Object)Blocks.CUT_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.SANDSTONE).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CUT_SANDSTONE)))).put((Object)Blocks.CUT_RED_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.RED_SANDSTONE).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CUT_RED_SANDSTONE)))).put((Object)Blocks.QUARTZ_BLOCK, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.QUARTZ_BLOCK)).put((Object)Blocks.SMOOTH_QUARTZ, (Object)TexturedModel.getCubeAll(TextureMap.getSubId(Blocks.QUARTZ_BLOCK, "_bottom"))).put((Object)Blocks.BLACKSTONE, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.BLACKSTONE)).put((Object)Blocks.DEEPSLATE, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.DEEPSLATE)).put((Object)Blocks.CHISELED_QUARTZ_BLOCK, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_QUARTZ_BLOCK).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CHISELED_QUARTZ_BLOCK)))).put((Object)Blocks.CHISELED_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_SANDSTONE).textures(textureMap -> {
        textureMap.put(TextureKey.END, TextureMap.getSubId(Blocks.SANDSTONE, "_top"));
        textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CHISELED_SANDSTONE));
    })).put((Object)Blocks.CHISELED_RED_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).textures(textureMap -> {
        textureMap.put(TextureKey.END, TextureMap.getSubId(Blocks.RED_SANDSTONE, "_top"));
        textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CHISELED_RED_SANDSTONE));
    })).put((Object)Blocks.CHISELED_TUFF_BRICKS, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.CHISELED_TUFF_BRICKS)).put((Object)Blocks.CHISELED_TUFF, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.CHISELED_TUFF)).build();
    final static Map<BlockFamily.Variant, BiConsumer<BlockTexturePool, Block>> VARIANT_POOL_FUNCTIONS = ImmutableMap.builder().put((Object)BlockFamily.Variant.BUTTON, BlockTexturePool::button).put((Object)BlockFamily.Variant.DOOR, BlockTexturePool::door).put((Object)BlockFamily.Variant.CHISELED, BlockTexturePool::block).put((Object)BlockFamily.Variant.CRACKED, BlockTexturePool::block).put((Object)BlockFamily.Variant.CUSTOM_FENCE, BlockTexturePool::customFence).put((Object)BlockFamily.Variant.FENCE, BlockTexturePool::fence).put((Object)BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockTexturePool::customFenceGate).put((Object)BlockFamily.Variant.FENCE_GATE, BlockTexturePool::fenceGate).put((Object)BlockFamily.Variant.SIGN, BlockTexturePool::sign).put((Object)BlockFamily.Variant.SLAB, BlockTexturePool::slab).put((Object)BlockFamily.Variant.STAIRS, BlockTexturePool::stairs).put((Object)BlockFamily.Variant.PRESSURE_PLATE, BlockTexturePool::pressurePlate).put((Object)BlockFamily.Variant.TRAPDOOR, BlockTexturePool::registerTrapdoor).put((Object)BlockFamily.Variant.WALL, BlockTexturePool::wall).build();
    final static private Map<Direction, ModelVariantOperator> CONNECTION_VARIANT_FUNCTIONS = ImmutableMap.of((Object)Direction.NORTH, (Object)NO_OP, (Object)Direction.EAST, (Object)ROTATE_Y_90.then(UV_LOCK), (Object)Direction.SOUTH, (Object)ROTATE_Y_180.then(UV_LOCK), (Object)Direction.WEST, (Object)ROTATE_Y_270.then(UV_LOCK), (Object)Direction.UP, (Object)ROTATE_X_270.then(UV_LOCK), (Object)Direction.DOWN, (Object)ROTATE_X_90.then(UV_LOCK));
    final static private Map<ChiseledBookshelfModelCacheKey, Identifier> CHISELED_BOOKSHELF_MODEL_CACHE = new HashMap<ChiseledBookshelfModelCacheKey, Identifier>();

    public static ModelVariant createModelVariant(Identifier id) {
        return new ModelVariant(id);
    }

    public static WeightedVariant createWeightedVariant(ModelVariant variant) {
        return new WeightedVariant(Pool.of(variant));
    }

    public static WeightedVariant createWeightedVariant(ModelVariant ... variants) {
        return new WeightedVariant(Pool.of(Arrays.stream(variants).map(variant -> new Weighted<ModelVariant>((ModelVariant)variant, 1)).toList()));
    }

    public static WeightedVariant createWeightedVariant(Identifier id) {
        return BlockStateModelGenerator.createWeightedVariant(BlockStateModelGenerator.createModelVariant(id));
    }

    public static MultipartModelConditionBuilder createMultipartConditionBuilder() {
        return new MultipartModelConditionBuilder();
    }

    public static MultipartModelCondition or(MultipartModelConditionBuilder ... conditionBuilders) {
        return new MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.OR, Stream.of(conditionBuilders).map(MultipartModelConditionBuilder::build).toList());
    }

    public static BlockModelDefinitionCreator createStoneState(Block block, ModelVariant variant, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(Models.CUBE_MIRRORED_ALL.upload(block, textures, modelCollector));
        return VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.modelWithMirroring(variant, modelVariant));
    }

    public static BlockModelDefinitionCreator createMudBrickState(Block block, ModelVariant variant, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_NORTH_WEST_MIRRORED_ALL.upload(block, textures, modelCollector));
        return BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant);
    }

    public static BlockModelDefinitionCreator createDeepslateState(Block block, ModelVariant variant, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(Models.CUBE_COLUMN_MIRRORED.upload(block, textures, modelCollector));
        return VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.modelWithMirroring(variant, modelVariant)).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap());
    }

    public BlockStateModelGenerator(Consumer<BlockModelDefinitionCreator> blockStateCollector, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        this.blockStateCollector = blockStateCollector;
        this.itemModelOutput = itemModelOutput;
        this.modelCollector = modelCollector;
    }

    public final void registerItemModel(Item item, Identifier modelId) {
        this.itemModelOutput.accept(item, ItemModels.basic(modelId));
    }

    public void registerParentedItemModel(Block block, Identifier parentModelId) {
        this.itemModelOutput.accept(block.asItem(), ItemModels.basic(parentModelId));
    }

    public final void registerTintedItemModel(Block block, Identifier modelId, TintSource tint) {
        this.itemModelOutput.accept(block.asItem(), ItemModels.tinted(modelId, tint));
    }

    public final Identifier uploadItemModel(Item item) {
        return Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), this.modelCollector);
    }

    public Identifier uploadBlockItemModel(Item item, Block block) {
        return Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(block), this.modelCollector);
    }

    public final Identifier uploadBlockItemModel(Item item, Block block, String textureSuffix) {
        return Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(TextureMap.getSubId(block, textureSuffix)), this.modelCollector);
    }

    public Identifier uploadTwoLayerBlockItemModel(Item item, Block block, String layer1Suffix) {
        Identifier identifier = TextureMap.getId(block);
        Identifier identifier2 = TextureMap.getSubId(block, layer1Suffix);
        return Models.GENERATED_TWO_LAYERS.upload(ModelIds.getItemModelId(item), TextureMap.layered(identifier, identifier2), this.modelCollector);
    }

    public void registerItemModel(Item item) {
        this.registerItemModel(item, this.uploadItemModel(item));
    }

    public final void registerItemModel(Block block) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            this.registerItemModel(item, this.uploadBlockItemModel(item, block));
        }
    }

    public final void registerItemModel(Block block, String textureSuffix) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            this.registerItemModel(item, this.uploadBlockItemModel(item, block, textureSuffix));
        }
    }

    public final void registerTwoLayerItemModel(Block block, String layer1Suffix) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            Identifier identifier = this.uploadTwoLayerBlockItemModel(item, block, layer1Suffix);
            this.registerItemModel(item, identifier);
        }
    }

    public static WeightedVariant modelWithYRotation(ModelVariant variant) {
        return BlockStateModelGenerator.createWeightedVariant(variant, variant.with(ROTATE_Y_90), variant.with(ROTATE_Y_180), variant.with(ROTATE_Y_270));
    }

    public static WeightedVariant modelWithMirroring(ModelVariant variant, ModelVariant mirroredVariant) {
        return BlockStateModelGenerator.createWeightedVariant(variant, mirroredVariant, variant.with(ROTATE_Y_180), mirroredVariant.with(ROTATE_Y_180));
    }

    public static BlockStateVariantMap<WeightedVariant> createBooleanModelMap(BooleanProperty property, WeightedVariant trueModel, WeightedVariant falseModel) {
        return BlockStateVariantMap.models(property).register(true, trueModel).register(false, falseModel);
    }

    public final void registerMirrorable(Block block) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(TexturedModel.CUBE_ALL.upload(block, this.modelCollector));
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant(TexturedModel.CUBE_MIRRORED_ALL.upload(block, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.modelWithMirroring(modelVariant, modelVariant2)));
    }

    public final void registerRotatable(Block block) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(TexturedModel.CUBE_ALL.upload(block, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.modelWithYRotation(modelVariant)));
    }

    public final void registerBrushableBlock(Block block) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.DUSTED).generate(dusted -> {
            String string = "_" + dusted;
            Identifier identifier = TextureMap.getSubId(block, string);
            Identifier identifier2 = Models.CUBE_ALL.upload(block, string, new TextureMap().put(TextureKey.ALL, identifier), this.modelCollector);
            return BlockStateModelGenerator.createWeightedVariant(identifier2);
        })));
        this.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_0"));
    }

    public static BlockModelDefinitionCreator createButtonBlockState(Block buttonBlock, WeightedVariant unpressedModel, WeightedVariant pressedModel) {
        return VariantsBlockModelDefinitionCreator.of(buttonBlock).with(BlockStateVariantMap.models(Properties.POWERED).register(false, unpressedModel).register(true, pressedModel)).coordinate(BlockStateVariantMap.operations(Properties.BLOCK_FACE, Properties.HORIZONTAL_FACING).register(BlockFace.FLOOR, Direction.EAST, ROTATE_Y_90).register(BlockFace.FLOOR, Direction.WEST, ROTATE_Y_270).register(BlockFace.FLOOR, Direction.SOUTH, ROTATE_Y_180).register(BlockFace.FLOOR, Direction.NORTH, NO_OP).register(BlockFace.WALL, Direction.EAST, ROTATE_Y_90.then(ROTATE_X_90).then(UV_LOCK)).register(BlockFace.WALL, Direction.WEST, ROTATE_Y_270.then(ROTATE_X_90).then(UV_LOCK)).register(BlockFace.WALL, Direction.SOUTH, ROTATE_Y_180.then(ROTATE_X_90).then(UV_LOCK)).register(BlockFace.WALL, Direction.NORTH, ROTATE_X_90.then(UV_LOCK)).register(BlockFace.CEILING, Direction.EAST, ROTATE_Y_270.then(ROTATE_X_180)).register(BlockFace.CEILING, Direction.WEST, ROTATE_Y_90.then(ROTATE_X_180)).register(BlockFace.CEILING, Direction.SOUTH, ROTATE_X_180).register(BlockFace.CEILING, Direction.NORTH, ROTATE_Y_180.then(ROTATE_X_180)));
    }

    public static BlockModelDefinitionCreator createDoorBlockState(Block doorBlock, WeightedVariant bottomLeftClosedModel, WeightedVariant bottomLeftOpenModel, WeightedVariant bottomRightClosedModel, WeightedVariant bottomRightOpenModel, WeightedVariant topLeftClosedModel, WeightedVariant topLeftOpenModel, WeightedVariant topRightClosedModel, WeightedVariant topRightOpenModel) {
        return VariantsBlockModelDefinitionCreator.of(doorBlock).with(BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF, Properties.DOOR_HINGE, Properties.OPEN).register(Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false, bottomLeftClosedModel).register(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false, bottomLeftClosedModel.apply(ROTATE_Y_90)).register(Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false, bottomLeftClosedModel.apply(ROTATE_Y_180)).register(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false, bottomLeftClosedModel.apply(ROTATE_Y_270)).register(Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false, bottomRightClosedModel).register(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false, bottomRightClosedModel.apply(ROTATE_Y_90)).register(Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false, bottomRightClosedModel.apply(ROTATE_Y_180)).register(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false, bottomRightClosedModel.apply(ROTATE_Y_270)).register(Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true, bottomLeftOpenModel.apply(ROTATE_Y_90)).register(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true, bottomLeftOpenModel.apply(ROTATE_Y_180)).register(Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true, bottomLeftOpenModel.apply(ROTATE_Y_270)).register(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true, bottomLeftOpenModel).register(Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true, bottomRightOpenModel.apply(ROTATE_Y_270)).register(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true, bottomRightOpenModel).register(Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true, bottomRightOpenModel.apply(ROTATE_Y_90)).register(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true, bottomRightOpenModel.apply(ROTATE_Y_180)).register(Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false, topLeftClosedModel).register(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false, topLeftClosedModel.apply(ROTATE_Y_90)).register(Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false, topLeftClosedModel.apply(ROTATE_Y_180)).register(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false, topLeftClosedModel.apply(ROTATE_Y_270)).register(Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false, topRightClosedModel).register(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false, topRightClosedModel.apply(ROTATE_Y_90)).register(Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false, topRightClosedModel.apply(ROTATE_Y_180)).register(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false, topRightClosedModel.apply(ROTATE_Y_270)).register(Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true, topLeftOpenModel.apply(ROTATE_Y_90)).register(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true, topLeftOpenModel.apply(ROTATE_Y_180)).register(Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true, topLeftOpenModel.apply(ROTATE_Y_270)).register(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true, topLeftOpenModel).register(Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true, topRightOpenModel.apply(ROTATE_Y_270)).register(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true, topRightOpenModel).register(Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true, topRightOpenModel.apply(ROTATE_Y_90)).register(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true, topRightOpenModel.apply(ROTATE_Y_180)));
    }

    public static BlockModelDefinitionCreator createCustomFenceBlockState(Block customFenceBlock, WeightedVariant postModel, WeightedVariant northSideModel, WeightedVariant eastSideModel, WeightedVariant southSideModel, WeightedVariant westSideModel) {
        return MultipartBlockModelDefinitionCreator.create(customFenceBlock).with(postModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), northSideModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), eastSideModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), southSideModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), westSideModel);
    }

    public static BlockModelDefinitionCreator createFenceBlockState(Block fenceBlock, WeightedVariant postModel, WeightedVariant sideModel) {
        return MultipartBlockModelDefinitionCreator.create(fenceBlock).with(postModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), sideModel.apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), sideModel.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), sideModel.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), sideModel.apply(ROTATE_Y_270).apply(UV_LOCK));
    }

    public static BlockModelDefinitionCreator createWallBlockState(Block wallBlock, WeightedVariant postModel, WeightedVariant lowSideModel, WeightedVariant tallSideModel) {
        return MultipartBlockModelDefinitionCreator.create(wallBlock).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.UP, true), postModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH_WALL_SHAPE, WallShape.LOW), lowSideModel.apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST_WALL_SHAPE, WallShape.LOW), lowSideModel.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH_WALL_SHAPE, WallShape.LOW), lowSideModel.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST_WALL_SHAPE, WallShape.LOW), lowSideModel.apply(ROTATE_Y_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH_WALL_SHAPE, WallShape.TALL), tallSideModel.apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST_WALL_SHAPE, WallShape.TALL), tallSideModel.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH_WALL_SHAPE, WallShape.TALL), tallSideModel.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST_WALL_SHAPE, WallShape.TALL), tallSideModel.apply(ROTATE_Y_270).apply(UV_LOCK));
    }

    public static BlockModelDefinitionCreator createFenceGateBlockState(Block fenceGateBlock, WeightedVariant openModel, WeightedVariant closedModel, WeightedVariant openWallModel, WeightedVariant closedWallModel, boolean uvlock) {
        return VariantsBlockModelDefinitionCreator.of(fenceGateBlock).with(BlockStateVariantMap.models(Properties.IN_WALL, Properties.OPEN).register(false, false, closedModel).register(true, false, closedWallModel).register(false, true, openModel).register(true, true, openWallModel)).apply(uvlock ? UV_LOCK : NO_OP).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS);
    }

    public static BlockModelDefinitionCreator createStairsBlockState(Block stairsBlock, WeightedVariant innerModel, WeightedVariant straightModel, WeightedVariant outerModel) {
        return VariantsBlockModelDefinitionCreator.of(stairsBlock).with(BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, straightModel).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT, straightModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, straightModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, straightModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, outerModel).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, outerModel).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, innerModel).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, innerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, innerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, innerModel).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, innerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT, straightModel.apply(ROTATE_X_180).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT, straightModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT, straightModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT, straightModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, outerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT, outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT, innerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT, innerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT, innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT, innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT, innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)));
    }

    public static BlockModelDefinitionCreator createOrientableTrapdoorBlockState(Block trapdoorBlock, WeightedVariant topModel, WeightedVariant bottomModel, WeightedVariant openModel) {
        return VariantsBlockModelDefinitionCreator.of(trapdoorBlock).with(BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN).register(Direction.NORTH, BlockHalf.BOTTOM, false, bottomModel).register(Direction.SOUTH, BlockHalf.BOTTOM, false, bottomModel.apply(ROTATE_Y_180)).register(Direction.EAST, BlockHalf.BOTTOM, false, bottomModel.apply(ROTATE_Y_90)).register(Direction.WEST, BlockHalf.BOTTOM, false, bottomModel.apply(ROTATE_Y_270)).register(Direction.NORTH, BlockHalf.TOP, false, topModel).register(Direction.SOUTH, BlockHalf.TOP, false, topModel.apply(ROTATE_Y_180)).register(Direction.EAST, BlockHalf.TOP, false, topModel.apply(ROTATE_Y_90)).register(Direction.WEST, BlockHalf.TOP, false, topModel.apply(ROTATE_Y_270)).register(Direction.NORTH, BlockHalf.BOTTOM, true, openModel).register(Direction.SOUTH, BlockHalf.BOTTOM, true, openModel.apply(ROTATE_Y_180)).register(Direction.EAST, BlockHalf.BOTTOM, true, openModel.apply(ROTATE_Y_90)).register(Direction.WEST, BlockHalf.BOTTOM, true, openModel.apply(ROTATE_Y_270)).register(Direction.NORTH, BlockHalf.TOP, true, openModel.apply(ROTATE_X_180).apply(ROTATE_Y_180)).register(Direction.SOUTH, BlockHalf.TOP, true, openModel.apply(ROTATE_X_180)).register(Direction.EAST, BlockHalf.TOP, true, openModel.apply(ROTATE_X_180).apply(ROTATE_Y_270)).register(Direction.WEST, BlockHalf.TOP, true, openModel.apply(ROTATE_X_180).apply(ROTATE_Y_90)));
    }

    public static BlockModelDefinitionCreator createTrapdoorBlockState(Block trapdoorBlock, WeightedVariant topModel, WeightedVariant bottomModel, WeightedVariant openModel) {
        return VariantsBlockModelDefinitionCreator.of(trapdoorBlock).with(BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN).register(Direction.NORTH, BlockHalf.BOTTOM, false, bottomModel).register(Direction.SOUTH, BlockHalf.BOTTOM, false, bottomModel).register(Direction.EAST, BlockHalf.BOTTOM, false, bottomModel).register(Direction.WEST, BlockHalf.BOTTOM, false, bottomModel).register(Direction.NORTH, BlockHalf.TOP, false, topModel).register(Direction.SOUTH, BlockHalf.TOP, false, topModel).register(Direction.EAST, BlockHalf.TOP, false, topModel).register(Direction.WEST, BlockHalf.TOP, false, topModel).register(Direction.NORTH, BlockHalf.BOTTOM, true, openModel).register(Direction.SOUTH, BlockHalf.BOTTOM, true, openModel.apply(ROTATE_Y_180)).register(Direction.EAST, BlockHalf.BOTTOM, true, openModel.apply(ROTATE_Y_90)).register(Direction.WEST, BlockHalf.BOTTOM, true, openModel.apply(ROTATE_Y_270)).register(Direction.NORTH, BlockHalf.TOP, true, openModel).register(Direction.SOUTH, BlockHalf.TOP, true, openModel.apply(ROTATE_Y_180)).register(Direction.EAST, BlockHalf.TOP, true, openModel.apply(ROTATE_Y_90)).register(Direction.WEST, BlockHalf.TOP, true, openModel.apply(ROTATE_Y_270)));
    }

    public static VariantsBlockModelDefinitionCreator createSingletonBlockState(Block block, WeightedVariant model) {
        return VariantsBlockModelDefinitionCreator.of(block, model);
    }

    public static BlockStateVariantMap<ModelVariantOperator> createAxisRotatedVariantMap() {
        return BlockStateVariantMap.operations(Properties.AXIS).register(Direction.Axis.Y, NO_OP).register(Direction.Axis.Z, ROTATE_X_90).register(Direction.Axis.X, ROTATE_X_90.then(ROTATE_Y_90));
    }

    public static BlockModelDefinitionCreator createUvLockedColumnBlockState(Block block, TextureMap textureMap, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN_UV_LOCKED_X.upload(block, textureMap, modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN_UV_LOCKED_Y.upload(block, textureMap, modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN_UV_LOCKED_Z.upload(block, textureMap, modelCollector));
        return VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.AXIS).register(Direction.Axis.X, weightedVariant).register(Direction.Axis.Y, weightedVariant2).register(Direction.Axis.Z, weightedVariant3));
    }

    public static BlockModelDefinitionCreator createAxisRotatedBlockState(Block block, WeightedVariant model) {
        return VariantsBlockModelDefinitionCreator.of(block, model).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap());
    }

    public final void registerAxisRotated(Block block, WeightedVariant model) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(block, model));
    }

    public void registerAxisRotated(Block block, TexturedModel.Factory modelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(modelFactory.upload(block, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(block, weightedVariant));
    }

    public final void registerNorthDefaultHorizontalRotated(Block block, TexturedModel.Factory modelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(modelFactory.upload(block, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, weightedVariant).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public static BlockModelDefinitionCreator createAxisRotatedBlockState(Block block, WeightedVariant verticalModel, WeightedVariant horizontalModel) {
        return VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.AXIS).register(Direction.Axis.Y, verticalModel).register(Direction.Axis.Z, horizontalModel.apply(ROTATE_X_90)).register(Direction.Axis.X, horizontalModel.apply(ROTATE_X_90).apply(ROTATE_Y_90)));
    }

    public final void registerAxisRotated(Block block, TexturedModel.Factory verticalModelFactory, TexturedModel.Factory horizontalModelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(verticalModelFactory.upload(block, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(horizontalModelFactory.upload(block, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(block, weightedVariant, weightedVariant2));
    }

    public final void registerCreakingHeart(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.END_FOR_TOP_CUBE_COLUMN.upload(block, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL.upload(block, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN, block, "_awake"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL, block, "_awake"));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN, block, "_dormant"));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL, block, "_dormant"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.AXIS, CreakingHeartBlock.ACTIVE).register(Direction.Axis.Y, CreakingHeartState.UPROOTED, weightedVariant).register(Direction.Axis.Z, CreakingHeartState.UPROOTED, weightedVariant2.apply(ROTATE_X_90)).register(Direction.Axis.X, CreakingHeartState.UPROOTED, weightedVariant2.apply(ROTATE_X_90).apply(ROTATE_Y_90)).register(Direction.Axis.Y, CreakingHeartState.DORMANT, weightedVariant5).register(Direction.Axis.Z, CreakingHeartState.DORMANT, weightedVariant6.apply(ROTATE_X_90)).register(Direction.Axis.X, CreakingHeartState.DORMANT, weightedVariant6.apply(ROTATE_X_90).apply(ROTATE_Y_90)).register(Direction.Axis.Y, CreakingHeartState.AWAKE, weightedVariant3).register(Direction.Axis.Z, CreakingHeartState.AWAKE, weightedVariant4.apply(ROTATE_X_90)).register(Direction.Axis.X, CreakingHeartState.AWAKE, weightedVariant4.apply(ROTATE_X_90).apply(ROTATE_Y_90))));
    }

    public final Identifier createCreakingHeartModel(TexturedModel.Factory texturedModelFactory, Block block, String suffix) {
        return texturedModelFactory.andThen(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getSubId(block, suffix)).put(TextureKey.END, TextureMap.getSubId(block, "_top" + suffix))).upload(block, suffix, this.modelCollector);
    }

    public final Identifier createSubModel(Block block, String suffix, Model model, Function<Identifier, TextureMap> texturesFactory) {
        return model.upload(block, suffix, texturesFactory.apply(TextureMap.getSubId(block, suffix)), this.modelCollector);
    }

    public static BlockModelDefinitionCreator createPressurePlateBlockState(Block pressurePlateBlock, WeightedVariant upModel, WeightedVariant downModel) {
        return VariantsBlockModelDefinitionCreator.of(pressurePlateBlock).with(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, downModel, upModel));
    }

    public static BlockModelDefinitionCreator createSlabBlockState(Block slabBlock, WeightedVariant bottomModel, WeightedVariant topModel, WeightedVariant doubleModel) {
        return VariantsBlockModelDefinitionCreator.of(slabBlock).with(BlockStateVariantMap.models(Properties.SLAB_TYPE).register(SlabType.BOTTOM, bottomModel).register(SlabType.TOP, topModel).register(SlabType.DOUBLE, doubleModel));
    }

    public void registerSimpleCubeAll(Block block) {
        this.registerSingleton(block, TexturedModel.CUBE_ALL);
    }

    public void registerSingleton(Block block, TexturedModel.Factory modelFactory) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(modelFactory.upload(block, this.modelCollector))));
    }

    public void registerTintedBlockAndItem(Block block, TexturedModel.Factory texturedModelFactory, int tintColor) {
        Identifier identifier = texturedModelFactory.upload(block, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(identifier)));
        this.registerTintedItemModel(block, identifier, ItemModels.constantTintSource(tintColor));
    }

    private void registerVine() {
        this.registerMultifaceBlockModel(Blocks.VINE);
        Identifier identifier = this.uploadBlockItemModel(Items.VINE, Blocks.VINE);
        this.registerTintedItemModel(Blocks.VINE, identifier, ItemModels.constantTintSource(-12012264));
    }

    public final void registerGrassTinted(Block block) {
        Identifier identifier = this.uploadBlockItemModel(block.asItem(), block);
        this.registerTintedItemModel(block, identifier, new GrassTintSource());
    }

    public final BlockTexturePool registerCubeAllModelTexturePool(Block block) {
        TexturedModel texturedModel = TEXTURED_MODELS.getOrDefault(block, TexturedModel.CUBE_ALL.get(block));
        return new BlockTexturePool(texturedModel.getTextures()).base(block, texturedModel.getModel());
    }

    public void registerHangingSign(Block base, Block hangingSign, Block wallHangingSign) {
        WeightedVariant weightedVariant = this.uploadParticleModel(hangingSign, base);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(hangingSign, weightedVariant));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(wallHangingSign, weightedVariant));
        this.registerItemModel(hangingSign.asItem());
    }

    public void registerDoor(Block doorBlock) {
        TextureMap textureMap = TextureMap.topBottom(doorBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_LEFT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_LEFT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_RIGHT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_RIGHT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_LEFT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_LEFT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_RIGHT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_RIGHT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        this.registerItemModel(doorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(doorBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, weightedVariant5, weightedVariant6, weightedVariant7, weightedVariant8));
    }

    public final void registerParentedDoor(Block parent, Block doorBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_LEFT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_LEFT_OPEN.getBlockSubModelId(parent));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_RIGHT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_BOTTOM_RIGHT_OPEN.getBlockSubModelId(parent));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_LEFT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_LEFT_OPEN.getBlockSubModelId(parent));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_RIGHT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant(Models.DOOR_TOP_RIGHT_OPEN.getBlockSubModelId(parent));
        this.itemModelOutput.acceptAlias(parent.asItem(), doorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(doorBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, weightedVariant5, weightedVariant6, weightedVariant7, weightedVariant8));
    }

    public void registerOrientableTrapdoor(Block trapdoorBlock) {
        TextureMap textureMap = TextureMap.texture(trapdoorBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(trapdoorBlock, textureMap, this.modelCollector));
        Identifier identifier = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, textureMap, this.modelCollector);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(trapdoorBlock, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createOrientableTrapdoorBlockState(trapdoorBlock, weightedVariant, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant2));
        this.registerParentedItemModel(trapdoorBlock, identifier);
    }

    public void registerTrapdoor(Block trapdoorBlock) {
        TextureMap textureMap = TextureMap.texture(trapdoorBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TRAPDOOR_TOP.upload(trapdoorBlock, textureMap, this.modelCollector));
        Identifier identifier = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, textureMap, this.modelCollector);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TRAPDOOR_OPEN.upload(trapdoorBlock, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState(trapdoorBlock, weightedVariant, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant2));
        this.registerParentedItemModel(trapdoorBlock, identifier);
    }

    public final void registerParentedTrapdoor(Block parent, Block trapdoorBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TRAPDOOR_TOP.getBlockSubModelId(parent));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TRAPDOOR_BOTTOM.getBlockSubModelId(parent));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TRAPDOOR_OPEN.getBlockSubModelId(parent));
        this.itemModelOutput.acceptAlias(parent.asItem(), trapdoorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState(trapdoorBlock, weightedVariant, weightedVariant2, weightedVariant3));
    }

    private void registerBigDripleaf() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.BIG_DRIPLEAF));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BIG_DRIPLEAF, "_partial_tilt"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BIG_DRIPLEAF, "_full_tilt"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.BIG_DRIPLEAF).with(BlockStateVariantMap.models(Properties.TILT).register(Tilt.NONE, weightedVariant).register(Tilt.UNSTABLE, weightedVariant).register(Tilt.PARTIAL, weightedVariant2).register(Tilt.FULL, weightedVariant3)).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final LogTexturePool createLogTexturePool(Block logBlock) {
        return new LogTexturePool(TextureMap.sideAndEndForTop(logBlock));
    }

    public final void registerSimpleState(Block block) {
        this.registerStateWithModelReference(block, block);
    }

    public final void registerStateWithModelReference(Block block, Block modelReference) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(modelReference))));
    }

    public final void registerTintableCross(Block block, CrossType crossType) {
        this.registerItemModel(block.asItem(), crossType.registerItemModel(this, block));
        this.registerTintableCrossBlockState(block, crossType);
    }

    public final void registerTintableCross(Block block, CrossType tintType, TextureMap texture) {
        this.registerItemModel(block);
        this.registerTintableCrossBlockState(block, tintType, texture);
    }

    public final void registerTintableCrossBlockState(Block block, CrossType tintType) {
        TextureMap textureMap = tintType.getTextureMap(block);
        this.registerTintableCrossBlockState(block, tintType, textureMap);
    }

    public final void registerTintableCrossBlockState(Block block, CrossType tintType, TextureMap crossTexture) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(tintType.getCrossModel().upload(block, crossTexture, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
    }

    public final void registerTintableCrossBlockStateWithStages(Block block, CrossType tintType, Property<Integer> stageProperty, int ... stages) {
        if (stageProperty.getValues().size() != stages.length) {
            throw new IllegalArgumentException("missing values for property: " + String.valueOf(stageProperty));
        }
        this.registerItemModel(block.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(stageProperty).generate(stage -> {
            String string = "_stage" + stages[stage];
            TextureMap textureMap = TextureMap.cross(TextureMap.getSubId(block, string));
            return BlockStateModelGenerator.createWeightedVariant(tintType.getCrossModel().upload(block, string, textureMap, this.modelCollector));
        })));
    }

    public final void registerFlowerPotPlantAndItem(Block block, Block flowerPotBlock, CrossType crossType) {
        this.registerItemModel(block.asItem(), crossType.registerItemModel(this, block));
        this.registerFlowerPotPlant(block, flowerPotBlock, crossType);
    }

    public final void registerFlowerPotPlant(Block plantBlock, Block flowerPotBlock, CrossType tintType) {
        this.registerTintableCrossBlockState(plantBlock, tintType);
        TextureMap textureMap = tintType.getFlowerPotTextureMap(plantBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(tintType.getFlowerPotCrossModel().upload(flowerPotBlock, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(flowerPotBlock, weightedVariant));
    }

    public final void registerCoralFan(Block coralFanBlock, Block coralWallFanBlock) {
        TexturedModel texturedModel = TexturedModel.CORAL_FAN.get(coralFanBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(texturedModel.upload(coralFanBlock, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(coralFanBlock, weightedVariant));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.CORAL_WALL_FAN.upload(coralWallFanBlock, texturedModel.getTextures(), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(coralWallFanBlock, weightedVariant2).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        this.registerItemModel(coralFanBlock);
    }

    public final void registerGourd(Block stemBlock, Block attachedStemBlock) {
        this.registerItemModel(stemBlock.asItem());
        TextureMap textureMap = TextureMap.stem(stemBlock);
        TextureMap textureMap2 = TextureMap.stemAndUpper(stemBlock, attachedStemBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.STEM_FRUIT.upload(attachedStemBlock, textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(attachedStemBlock, weightedVariant).coordinate(BlockStateVariantMap.operations(Properties.HORIZONTAL_FACING).register(Direction.WEST, NO_OP).register(Direction.SOUTH, ROTATE_Y_270).register(Direction.NORTH, ROTATE_Y_90).register(Direction.EAST, ROTATE_Y_180)));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(stemBlock).with(BlockStateVariantMap.models(Properties.AGE_7).generate(age -> BlockStateModelGenerator.createWeightedVariant(Models.STEM_GROWTH_STAGES[age].upload(stemBlock, textureMap, this.modelCollector)))));
    }

    private void registerPitcherPlant() {
        Block block = Blocks.PITCHER_PLANT;
        this.registerItemModel(block.asItem());
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_top"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_bottom"));
        this.registerDoubleBlock(block, weightedVariant, weightedVariant2);
    }

    private void registerPitcherCrop() {
        Block block = Blocks.PITCHER_CROP;
        this.registerItemModel(block.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(PitcherCropBlock.AGE, Properties.DOUBLE_BLOCK_HALF).generate((age, half) -> switch (half) {
            default -> throw new MatchException(null, null);
            case DoubleBlockHalf.UPPER -> BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_top_stage_" + age));
            case DoubleBlockHalf.LOWER -> BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_bottom_stage_" + age));
        })));
    }

    public final void registerCoral(Block coral, Block deadCoral, Block coralBlock, Block deadCoralBlock, Block coralFan, Block deadCoralFan, Block coralWallFan, Block deadCoralWallFan) {
        this.registerTintableCross(coral, CrossType.NOT_TINTED);
        this.registerTintableCross(deadCoral, CrossType.NOT_TINTED);
        this.registerSimpleCubeAll(coralBlock);
        this.registerSimpleCubeAll(deadCoralBlock);
        this.registerCoralFan(coralFan, coralWallFan);
        this.registerCoralFan(deadCoralFan, deadCoralWallFan);
    }

    public final void registerDoubleBlock(Block doubleBlock, CrossType tintType) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(doubleBlock, "_top", tintType.getCrossModel(), TextureMap::cross));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(doubleBlock, "_bottom", tintType.getCrossModel(), TextureMap::cross));
        this.registerDoubleBlock(doubleBlock, weightedVariant, weightedVariant2);
    }

    public final void registerDoubleBlockAndItem(Block block, CrossType crossType) {
        this.registerItemModel(block, "_top");
        this.registerDoubleBlock(block, crossType);
    }

    public final void registerGrassTintedDoubleBlockAndItem(Block block) {
        Identifier identifier = this.uploadBlockItemModel(block.asItem(), block, "_top");
        this.registerTintedItemModel(block, identifier, new GrassTintSource());
        this.registerDoubleBlock(block, CrossType.TINTED);
    }

    private void registerSunflower() {
        this.registerItemModel(Blocks.SUNFLOWER, "_front");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.SUNFLOWER, "_top"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.SUNFLOWER, "_bottom", CrossType.NOT_TINTED.getCrossModel(), TextureMap::cross));
        this.registerDoubleBlock(Blocks.SUNFLOWER, weightedVariant, weightedVariant2);
    }

    private void registerTallSeagrass() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.TALL_SEAGRASS, "_top", Models.TEMPLATE_SEAGRASS, TextureMap::texture));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.TALL_SEAGRASS, "_bottom", Models.TEMPLATE_SEAGRASS, TextureMap::texture));
        this.registerDoubleBlock(Blocks.TALL_SEAGRASS, weightedVariant, weightedVariant2);
    }

    private void registerSmallDripleaf() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.SMALL_DRIPLEAF, "_top"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.SMALL_DRIPLEAF, "_bottom"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SMALL_DRIPLEAF).with(BlockStateVariantMap.models(Properties.DOUBLE_BLOCK_HALF).register(DoubleBlockHalf.LOWER, weightedVariant2).register(DoubleBlockHalf.UPPER, weightedVariant)).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final void registerDoubleBlock(Block block, WeightedVariant upperModel, WeightedVariant lowerModel) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.DOUBLE_BLOCK_HALF).register(DoubleBlockHalf.LOWER, lowerModel).register(DoubleBlockHalf.UPPER, upperModel)));
    }

    public final void registerTurnableRail(Block rail) {
        TextureMap textureMap = TextureMap.rail(rail);
        TextureMap textureMap2 = TextureMap.rail(TextureMap.getSubId(rail, "_corner"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.RAIL_FLAT.upload(rail, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.RAIL_CURVED.upload(rail, textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_RAIL_RAISED_NE.upload(rail, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_RAIL_RAISED_SW.upload(rail, textureMap, this.modelCollector));
        this.registerItemModel(rail);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(rail).with(BlockStateVariantMap.models(Properties.RAIL_SHAPE).register(RailShape.NORTH_SOUTH, weightedVariant).register(RailShape.EAST_WEST, weightedVariant.apply(ROTATE_Y_90)).register(RailShape.ASCENDING_EAST, weightedVariant3.apply(ROTATE_Y_90)).register(RailShape.ASCENDING_WEST, weightedVariant4.apply(ROTATE_Y_90)).register(RailShape.ASCENDING_NORTH, weightedVariant3).register(RailShape.ASCENDING_SOUTH, weightedVariant4).register(RailShape.SOUTH_EAST, weightedVariant2).register(RailShape.SOUTH_WEST, weightedVariant2.apply(ROTATE_Y_90)).register(RailShape.NORTH_WEST, weightedVariant2.apply(ROTATE_Y_180)).register(RailShape.NORTH_EAST, weightedVariant2.apply(ROTATE_Y_270))));
    }

    public final void registerStraightRail(Block rail) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(rail, "", Models.RAIL_FLAT, TextureMap::rail));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_NE, TextureMap::rail));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_SW, TextureMap::rail));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(rail, "_on", Models.RAIL_FLAT, TextureMap::rail));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_NE, TextureMap::rail));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_SW, TextureMap::rail));
        this.registerItemModel(rail);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(rail).with(BlockStateVariantMap.models(Properties.POWERED, Properties.STRAIGHT_RAIL_SHAPE).generate((powered, shape) -> switch (shape) {
            case RailShape.NORTH_SOUTH -> {
                if (powered.booleanValue()) {
                    yield weightedVariant4;
                }
                yield weightedVariant;
            }
            case RailShape.EAST_WEST -> (powered != false ? weightedVariant4 : weightedVariant).apply(ROTATE_Y_90);
            case RailShape.ASCENDING_EAST -> (powered != false ? weightedVariant5 : weightedVariant2).apply(ROTATE_Y_90);
            case RailShape.ASCENDING_WEST -> (powered != false ? weightedVariant6 : weightedVariant3).apply(ROTATE_Y_90);
            case RailShape.ASCENDING_NORTH -> {
                if (powered.booleanValue()) {
                    yield weightedVariant5;
                }
                yield weightedVariant2;
            }
            case RailShape.ASCENDING_SOUTH -> {
                if (powered.booleanValue()) {
                    yield weightedVariant6;
                }
                yield weightedVariant3;
            }
            default -> throw new UnsupportedOperationException("Fix you generator!");
        })));
    }

    public final void registerBuiltinWithParticle(Block block, Item particleSource) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PARTICLE.upload(block, TextureMap.particle(particleSource), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
    }

    public final void registerBuiltinWithParticle(Block block, Identifier particleSource) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PARTICLE.upload(block, TextureMap.particle(particleSource), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
    }

    public final WeightedVariant uploadParticleModel(Block block, Block particleSource) {
        return BlockStateModelGenerator.createWeightedVariant(Models.PARTICLE.upload(block, TextureMap.particle(particleSource), this.modelCollector));
    }

    public void registerBuiltinWithParticle(Block block, Block particleSource) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, this.uploadParticleModel(block, particleSource)));
    }

    public final void registerBuiltin(Block block) {
        this.registerBuiltinWithParticle(block, block);
    }

    public final void registerWoolAndCarpet(Block wool, Block carpet) {
        this.registerSimpleCubeAll(wool);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.CARPET.get(wool).upload(carpet, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(carpet, weightedVariant));
    }

    public final void registerLeafLitter(Block leafLitter) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_LEAF_LITTER_1.upload(leafLitter, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_LEAF_LITTER_2.upload(leafLitter, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_LEAF_LITTER_3.upload(leafLitter, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_LEAF_LITTER_4.upload(leafLitter, this.modelCollector));
        this.registerItemModel(leafLitter.asItem());
        this.registerSegmentedBlock(leafLitter, weightedVariant, LEAF_LITTER_MODEL_1_CONDITION_FUNCTION, weightedVariant2, LEAF_LITTER_MODEL_2_CONDITION_FUNCTION, weightedVariant3, LEAF_LITTER_MODEL_3_CONDITION_FUNCTION, weightedVariant4, LEAF_LITTER_MODEL_4_CONDITION_FUNCTION);
    }

    public final void registerFlowerbed(Block flowerbed) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.FLOWERBED_1.upload(flowerbed, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.FLOWERBED_2.upload(flowerbed, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.FLOWERBED_3.upload(flowerbed, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.FLOWERBED_4.upload(flowerbed, this.modelCollector));
        this.registerItemModel(flowerbed.asItem());
        this.registerSegmentedBlock(flowerbed, weightedVariant, FLOWERBED_MODEL_1_CONDITION_FUNCTION, weightedVariant2, FLOWERBED_MODEL_2_CONDITION_FUNCTION, weightedVariant3, FLOWERBED_MODEL_3_CONDITION_FUNCTION, weightedVariant4, FLOWERBED_MODEL_4_CONDITION_FUNCTION);
    }

    public final void registerSegmentedBlock(Block block, WeightedVariant model1, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model1ConditionFunction, WeightedVariant model2, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model2ConditionFunction, WeightedVariant model3, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model3ConditionFunction, WeightedVariant model4, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model4ConditionFunction) {
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(block).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.NORTH)), model1).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.EAST)), model1.apply(ROTATE_Y_90)).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.SOUTH)), model1.apply(ROTATE_Y_180)).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.WEST)), model1.apply(ROTATE_Y_270)).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.NORTH)), model2).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.EAST)), model2.apply(ROTATE_Y_90)).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.SOUTH)), model2.apply(ROTATE_Y_180)).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.WEST)), model2.apply(ROTATE_Y_270)).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.NORTH)), model3).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.EAST)), model3.apply(ROTATE_Y_90)).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.SOUTH)), model3.apply(ROTATE_Y_180)).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.WEST)), model3.apply(ROTATE_Y_270)).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.NORTH)), model4).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.EAST)), model4.apply(ROTATE_Y_90)).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.SOUTH)), model4.apply(ROTATE_Y_180)).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, Direction.WEST)), model4.apply(ROTATE_Y_270)));
    }

    public final void registerRandomHorizontalRotations(TexturedModel.Factory modelFactory, Block ... blocks) {
        for (Block block : blocks) {
            ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(modelFactory.upload(block, this.modelCollector));
            this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.modelWithYRotation(modelVariant)));
        }
    }

    public final void registerSouthDefaultHorizontalFacing(TexturedModel.Factory modelFactory, Block ... blocks) {
        for (Block block : blocks) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(modelFactory.upload(block, this.modelCollector));
            this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, weightedVariant).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        }
    }

    public final void registerGlassAndPane(Block glassBlock, Block glassPane) {
        this.registerSimpleCubeAll(glassBlock);
        TextureMap textureMap = TextureMap.paneAndTopForEdge(glassBlock, glassPane);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_GLASS_PANE_POST.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_GLASS_PANE_SIDE.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(glassPane, textureMap, this.modelCollector));
        Item item = glassPane.asItem();
        this.registerItemModel(item, this.uploadBlockItemModel(item, glassBlock));
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(glassPane).with(weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), weightedVariant2).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), weightedVariant2.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), weightedVariant3).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), weightedVariant3.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false), weightedVariant4).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, false), weightedVariant5).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, false), weightedVariant5.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, false), weightedVariant4.apply(ROTATE_Y_270)));
    }

    public final void registerCommandBlock(Block commandBlock) {
        TextureMap textureMap = TextureMap.sideFrontBack(commandBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_COMMAND_BLOCK.upload(commandBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(commandBlock, "_conditional", Models.TEMPLATE_COMMAND_BLOCK, id -> textureMap.copyAndAdd(TextureKey.SIDE, (Identifier)id)));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(commandBlock).with(BlockStateModelGenerator.createBooleanModelMap(Properties.CONDITIONAL, weightedVariant2, weightedVariant)).coordinate(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    public final void registerAnvil(Block anvil) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_ANVIL.upload(anvil, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(anvil, weightedVariant).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public static WeightedVariant getBambooBlockStateVariants(int age) {
        String string = "_age" + age;
        return new WeightedVariant(Pool.of(IntStream.range(1, 5).mapToObj(i -> new Weighted<ModelVariant>(BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.BAMBOO, i + string)), 1)).collect(Collectors.toList())));
    }

    private void registerBamboo() {
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.BAMBOO).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.AGE_1, 0), BlockStateModelGenerator.getBambooBlockStateVariants(0)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.AGE_1, 1), BlockStateModelGenerator.getBambooBlockStateVariants(1)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.BAMBOO_LEAVES, BambooLeaves.SMALL), BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BAMBOO, "_small_leaves"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.BAMBOO_LEAVES, BambooLeaves.LARGE), BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BAMBOO, "_large_leaves"))));
    }

    private void registerBarrel() {
        Identifier identifier = TextureMap.getSubId(Blocks.BARREL, "_top_open");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.CUBE_BOTTOM_TOP.upload(Blocks.BARREL, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.BARREL).textures(textureMap -> textureMap.put(TextureKey.TOP, identifier)).upload(Blocks.BARREL, "_open", this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.BARREL).with(BlockStateVariantMap.models(Properties.OPEN).register(false, weightedVariant).register(true, weightedVariant2)).coordinate(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    public static <T extends Comparable<T>> BlockStateVariantMap<WeightedVariant> createValueFencedModelMap(Property<T> property, T fence, WeightedVariant aboveFenceModel, WeightedVariant belowFenceModel) {
        return BlockStateVariantMap.models(property).generate(value -> {
            boolean bl = value.compareTo(fence) >= 0;
            return bl ? aboveFenceModel : belowFenceModel;
        });
    }

    public final void registerBeehive(Block beehive, Function<Block, TextureMap> texturesFactory) {
        TextureMap textureMap = texturesFactory.apply(beehive).inherit(TextureKey.SIDE, TextureKey.PARTICLE);
        TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.FRONT, TextureMap.getSubId(beehive, "_front_honey"));
        Identifier identifier = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, "_empty", textureMap, this.modelCollector);
        Identifier identifier2 = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, "_honey", textureMap2, this.modelCollector);
        this.itemModelOutput.accept(beehive.asItem(), ItemModels.select(BeehiveBlock.HONEY_LEVEL, ItemModels.basic(identifier), Map.of(5, ItemModels.basic(identifier2))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(beehive).with(BlockStateModelGenerator.createValueFencedModelMap(BeehiveBlock.HONEY_LEVEL, 5, BlockStateModelGenerator.createWeightedVariant(identifier2), BlockStateModelGenerator.createWeightedVariant(identifier))).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final void registerCrop(Block crop, Property<Integer> ageProperty, int ... ageTextureIndices) {
        this.registerItemModel(crop.asItem());
        if (ageProperty.getValues().size() != ageTextureIndices.length) {
            throw new IllegalArgumentException();
        }
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(crop).with(BlockStateVariantMap.models(ageProperty).generate(arg_0 -> this.method_67830(ageTextureIndices, (Int2ObjectMap)int2ObjectMap, crop, arg_0))));
    }

    private void registerBell() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BELL, "_floor"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BELL, "_ceiling"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BELL, "_wall"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.BELL, "_between_walls"));
        this.registerItemModel(Items.BELL);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.BELL).with(BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.ATTACHMENT).register(Direction.NORTH, Attachment.FLOOR, weightedVariant).register(Direction.SOUTH, Attachment.FLOOR, weightedVariant.apply(ROTATE_Y_180)).register(Direction.EAST, Attachment.FLOOR, weightedVariant.apply(ROTATE_Y_90)).register(Direction.WEST, Attachment.FLOOR, weightedVariant.apply(ROTATE_Y_270)).register(Direction.NORTH, Attachment.CEILING, weightedVariant2).register(Direction.SOUTH, Attachment.CEILING, weightedVariant2.apply(ROTATE_Y_180)).register(Direction.EAST, Attachment.CEILING, weightedVariant2.apply(ROTATE_Y_90)).register(Direction.WEST, Attachment.CEILING, weightedVariant2.apply(ROTATE_Y_270)).register(Direction.NORTH, Attachment.SINGLE_WALL, weightedVariant3.apply(ROTATE_Y_270)).register(Direction.SOUTH, Attachment.SINGLE_WALL, weightedVariant3.apply(ROTATE_Y_90)).register(Direction.EAST, Attachment.SINGLE_WALL, weightedVariant3).register(Direction.WEST, Attachment.SINGLE_WALL, weightedVariant3.apply(ROTATE_Y_180)).register(Direction.SOUTH, Attachment.DOUBLE_WALL, weightedVariant4.apply(ROTATE_Y_90)).register(Direction.NORTH, Attachment.DOUBLE_WALL, weightedVariant4.apply(ROTATE_Y_270)).register(Direction.EAST, Attachment.DOUBLE_WALL, weightedVariant4).register(Direction.WEST, Attachment.DOUBLE_WALL, weightedVariant4.apply(ROTATE_Y_180))));
    }

    private void registerGrindstone() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.GRINDSTONE, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.GRINDSTONE))).coordinate(BlockStateVariantMap.operations(Properties.BLOCK_FACE, Properties.HORIZONTAL_FACING).register(BlockFace.FLOOR, Direction.NORTH, NO_OP).register(BlockFace.FLOOR, Direction.EAST, ROTATE_Y_90).register(BlockFace.FLOOR, Direction.SOUTH, ROTATE_Y_180).register(BlockFace.FLOOR, Direction.WEST, ROTATE_Y_270).register(BlockFace.WALL, Direction.NORTH, ROTATE_X_90).register(BlockFace.WALL, Direction.EAST, ROTATE_X_90.then(ROTATE_Y_90)).register(BlockFace.WALL, Direction.SOUTH, ROTATE_X_90.then(ROTATE_Y_180)).register(BlockFace.WALL, Direction.WEST, ROTATE_X_90.then(ROTATE_Y_270)).register(BlockFace.CEILING, Direction.SOUTH, ROTATE_X_180).register(BlockFace.CEILING, Direction.WEST, ROTATE_X_180.then(ROTATE_Y_90)).register(BlockFace.CEILING, Direction.NORTH, ROTATE_X_180.then(ROTATE_Y_180)).register(BlockFace.CEILING, Direction.EAST, ROTATE_X_180.then(ROTATE_Y_270))));
    }

    public final void registerCooker(Block cooker, TexturedModel.Factory modelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(modelFactory.upload(cooker, this.modelCollector));
        Identifier identifier = TextureMap.getSubId(cooker, "_front_on");
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(modelFactory.get(cooker).textures(textures -> textures.put(TextureKey.FRONT, identifier)).upload(cooker, "_on", this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(cooker).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, weightedVariant2, weightedVariant)).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final void registerCampfire(Block ... blocks) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("campfire_off"));
        for (Block block : blocks) {
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAMPFIRE.upload(block, TextureMap.campfire(block), this.modelCollector));
            this.registerItemModel(block.asItem());
            this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, weightedVariant2, weightedVariant)).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        }
    }

    public final void registerAzalea(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_AZALEA.upload(block, TextureMap.sideAndTop(block), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
    }

    public final void registerPottedAzaleaBush(Block block) {
        WeightedVariant weightedVariant = block == Blocks.POTTED_FLOWERING_AZALEA_BUSH ? BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_POTTED_FLOWERING_AZALEA_BUSH.upload(block, TextureMap.pottedAzaleaBush(block), this.modelCollector)) : BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_POTTED_AZALEA_BUSH.upload(block, TextureMap.pottedAzaleaBush(block), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
    }

    private void registerBookshelf() {
        TextureMap textureMap = TextureMap.sideEnd(TextureMap.getId(Blocks.BOOKSHELF), TextureMap.getId(Blocks.OAK_PLANKS));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN.upload(Blocks.BOOKSHELF, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.BOOKSHELF, weightedVariant));
    }

    private void registerRedstone() {
        this.registerItemModel(Items.REDSTONE);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.REDSTONE_WIRE).with(BlockStateModelGenerator.or(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE).put(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE).put(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE).put(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE), BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).put(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).put(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).put(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).put(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP})), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_dot"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1")).apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_side1")).apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST_WIRE_CONNECTION, WireConnection.UP), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).apply(ROTATE_Y_180)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST_WIRE_CONNECTION, WireConnection.UP), BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).apply(ROTATE_Y_270)));
    }

    private void registerComparator() {
        this.registerItemModel(Items.COMPARATOR);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.COMPARATOR).with(BlockStateVariantMap.models(Properties.COMPARATOR_MODE, Properties.POWERED).register(ComparatorMode.COMPARE, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.COMPARATOR))).register(ComparatorMode.COMPARE, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_on"))).register(ComparatorMode.SUBTRACT, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_subtract"))).register(ComparatorMode.SUBTRACT, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_on_subtract")))).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSmoothStone() {
        TextureMap textureMap = TextureMap.all(Blocks.SMOOTH_STONE);
        TextureMap textureMap2 = TextureMap.sideEnd(TextureMap.getSubId(Blocks.SMOOTH_STONE_SLAB, "_side"), textureMap.getTexture(TextureKey.TOP));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.SLAB.upload(Blocks.SMOOTH_STONE_SLAB, textureMap2, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.SLAB_TOP.upload(Blocks.SMOOTH_STONE_SLAB, textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN.uploadWithoutVariant(Blocks.SMOOTH_STONE_SLAB, "_double", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(Blocks.SMOOTH_STONE_SLAB, weightedVariant, weightedVariant2, weightedVariant3));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.SMOOTH_STONE, BlockStateModelGenerator.createWeightedVariant(Models.CUBE_ALL.upload(Blocks.SMOOTH_STONE, textureMap, this.modelCollector))));
    }

    private void registerBrewingStand() {
        this.registerItemModel(Items.BREWING_STAND);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.BREWING_STAND).with(BlockStateModelGenerator.createWeightedVariant(TextureMap.getId(Blocks.BREWING_STAND))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HAS_BOTTLE_0, true), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.BREWING_STAND, "_bottle0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HAS_BOTTLE_1, true), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.BREWING_STAND, "_bottle1"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HAS_BOTTLE_2, true), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.BREWING_STAND, "_bottle2"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HAS_BOTTLE_0, false), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.BREWING_STAND, "_empty0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HAS_BOTTLE_1, false), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.BREWING_STAND, "_empty1"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HAS_BOTTLE_2, false), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.BREWING_STAND, "_empty2"))));
    }

    public final void registerMushroomBlock(Block mushroomBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_SINGLE_FACE.upload(mushroomBlock, TextureMap.texture(mushroomBlock), this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("mushroom_block_inside"));
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(mushroomBlock).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), weightedVariant.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), weightedVariant.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), weightedVariant.apply(ROTATE_Y_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.UP, true), weightedVariant.apply(ROTATE_X_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.DOWN, true), weightedVariant.apply(ROTATE_X_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false), weightedVariant2).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, false), weightedVariant2.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, false), weightedVariant2.apply(ROTATE_Y_180)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, false), weightedVariant2.apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.UP, false), weightedVariant2.apply(ROTATE_X_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.DOWN, false), weightedVariant2.apply(ROTATE_X_90)));
        this.registerParentedItemModel(mushroomBlock, TexturedModel.CUBE_ALL.upload(mushroomBlock, "_inventory", this.modelCollector));
    }

    private void registerCake() {
        this.registerItemModel(Items.CAKE);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.CAKE).with(BlockStateVariantMap.models(Properties.BITES).register(0, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.CAKE))).register(1, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice1"))).register(2, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice2"))).register(3, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice3"))).register(4, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice4"))).register(5, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice5"))).register(6, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice6")))));
    }

    private void registerCartographyTable() {
        TextureMap textureMap = new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureKey.DOWN, TextureMap.getId(Blocks.DARK_OAK_PLANKS)).put(TextureKey.UP, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_top")).put(TextureKey.NORTH, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureKey.EAST, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureKey.SOUTH, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(TextureKey.WEST, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side2"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.CARTOGRAPHY_TABLE, BlockStateModelGenerator.createWeightedVariant(Models.CUBE.upload(Blocks.CARTOGRAPHY_TABLE, textureMap, this.modelCollector))));
    }

    private void registerSmithingTable() {
        TextureMap textureMap = new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_front")).put(TextureKey.DOWN, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_bottom")).put(TextureKey.UP, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_top")).put(TextureKey.NORTH, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_front")).put(TextureKey.SOUTH, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_front")).put(TextureKey.EAST, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_side")).put(TextureKey.WEST, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.SMITHING_TABLE, BlockStateModelGenerator.createWeightedVariant(Models.CUBE.upload(Blocks.SMITHING_TABLE, textureMap, this.modelCollector))));
    }

    public final void registerCubeWithCustomTextures(Block block, Block otherTextureSource, BiFunction<Block, Block, TextureMap> texturesFactory) {
        TextureMap textureMap = texturesFactory.apply(block, otherTextureSource);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(Models.CUBE.upload(block, textureMap, this.modelCollector))));
    }

    public void registerGeneric(Block block) {
        TextureMap textureMap = new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_particle")).put(TextureKey.DOWN, TextureMap.getSubId(block, "_down")).put(TextureKey.UP, TextureMap.getSubId(block, "_up")).put(TextureKey.NORTH, TextureMap.getSubId(block, "_north")).put(TextureKey.SOUTH, TextureMap.getSubId(block, "_south")).put(TextureKey.EAST, TextureMap.getSubId(block, "_east")).put(TextureKey.WEST, TextureMap.getSubId(block, "_west"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(Models.CUBE.upload(block, textureMap, this.modelCollector))));
    }

    private void registerPumpkins() {
        TextureMap textureMap = TextureMap.sideEnd(Blocks.PUMPKIN);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.PUMPKIN, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.PUMPKIN))));
        this.registerNorthDefaultHorizontalRotatable(Blocks.CARVED_PUMPKIN, textureMap);
        this.registerNorthDefaultHorizontalRotatable(Blocks.JACK_O_LANTERN, textureMap);
    }

    public final void registerNorthDefaultHorizontalRotatable(Block block, TextureMap texture) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.ORIENTABLE.upload(block, texture.copyAndAdd(TextureKey.FRONT, TextureMap.getId(block)), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, weightedVariant).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerCauldrons() {
        this.registerItemModel(Items.CAULDRON);
        this.registerSimpleState(Blocks.CAULDRON);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.LAVA_CAULDRON, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.LAVA_CAULDRON, TextureMap.cauldron(TextureMap.getSubId(Blocks.LAVA, "_still")), this.modelCollector))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.WATER_CAULDRON).with(BlockStateVariantMap.models(LeveledCauldronBlock.LEVEL).register(1, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_LEVEL1.upload(Blocks.WATER_CAULDRON, "_level1", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector))).register(2, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_LEVEL2.upload(Blocks.WATER_CAULDRON, "_level2", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector))).register(3, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.WATER_CAULDRON, "_full", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector)))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.POWDER_SNOW_CAULDRON).with(BlockStateVariantMap.models(LeveledCauldronBlock.LEVEL).register(1, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_LEVEL1.upload(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMap.cauldron(TextureMap.getId(Blocks.POWDER_SNOW)), this.modelCollector))).register(2, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_LEVEL2.upload(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMap.cauldron(TextureMap.getId(Blocks.POWDER_SNOW)), this.modelCollector))).register(3, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMap.cauldron(TextureMap.getId(Blocks.POWDER_SNOW)), this.modelCollector)))));
    }

    private void registerChorusFlower() {
        TextureMap textureMap = TextureMap.texture(Blocks.CHORUS_FLOWER);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CHORUS_FLOWER.upload(Blocks.CHORUS_FLOWER, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.CHORUS_FLOWER, "_dead", Models.TEMPLATE_CHORUS_FLOWER, id -> textureMap.copyAndAdd(TextureKey.TEXTURE, (Identifier)id)));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.CHORUS_FLOWER).with(BlockStateModelGenerator.createValueFencedModelMap(Properties.AGE_5, 5, weightedVariant2, weightedVariant)));
    }

    private void registerCrafter() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.CRAFTER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CRAFTER, "_triggered"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CRAFTER, "_crafting"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CRAFTER, "_crafting_triggered"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.CRAFTER).with(BlockStateVariantMap.models(Properties.TRIGGERED, CrafterBlock.CRAFTING).register(false, false, weightedVariant).register(true, true, weightedVariant4).register(true, false, weightedVariant2).register(false, true, weightedVariant3)).coordinate(BlockStateVariantMap.operations(Properties.ORIENTATION).generate(BlockStateModelGenerator::addJigsawOrientationToVariant)));
    }

    public final void registerDispenserLikeOrientable(Block block) {
        TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(Blocks.FURNACE, "_top")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.FURNACE, "_side")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front"));
        TextureMap textureMap2 = new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(Blocks.FURNACE, "_top")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front_vertical"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.ORIENTABLE.upload(block, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.ORIENTABLE_VERTICAL.upload(block, textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.FACING).register(Direction.DOWN, weightedVariant2.apply(ROTATE_X_180)).register(Direction.UP, weightedVariant2).register(Direction.NORTH, weightedVariant).register(Direction.EAST, weightedVariant.apply(ROTATE_Y_90)).register(Direction.SOUTH, weightedVariant.apply(ROTATE_Y_180)).register(Direction.WEST, weightedVariant.apply(ROTATE_Y_270))));
    }

    private void registerEndPortalFrame() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.END_PORTAL_FRAME));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.END_PORTAL_FRAME, "_filled"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.END_PORTAL_FRAME).with(BlockStateVariantMap.models(Properties.EYE).register(false, weightedVariant).register(true, weightedVariant2)).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerChorusPlant() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_side"));
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside"));
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside1"));
        ModelVariant modelVariant3 = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside2"));
        ModelVariant modelVariant4 = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside3"));
        ModelVariant modelVariant5 = modelVariant.with(UV_LOCK);
        ModelVariant modelVariant6 = modelVariant2.with(UV_LOCK);
        ModelVariant modelVariant7 = modelVariant3.with(UV_LOCK);
        ModelVariant modelVariant8 = modelVariant4.with(UV_LOCK);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.CHORUS_PLANT).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), weightedVariant.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), weightedVariant.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), weightedVariant.apply(ROTATE_Y_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.UP, true), weightedVariant.apply(ROTATE_X_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.DOWN, true), weightedVariant.apply(ROTATE_X_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false), new WeightedVariant(Pool.of(new Weighted<ModelVariant>(modelVariant, 2), new Weighted<ModelVariant>(modelVariant2, 1), new Weighted<ModelVariant>(modelVariant3, 1), new Weighted<ModelVariant>(modelVariant4, 1)))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, false), new WeightedVariant(Pool.of(new Weighted<ModelVariant>(modelVariant6.with(ROTATE_Y_90), 1), new Weighted<ModelVariant>(modelVariant7.with(ROTATE_Y_90), 1), new Weighted<ModelVariant>(modelVariant8.with(ROTATE_Y_90), 1), new Weighted<ModelVariant>(modelVariant5.with(ROTATE_Y_90), 2)))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, false), new WeightedVariant(Pool.of(new Weighted<ModelVariant>(modelVariant7.with(ROTATE_Y_180), 1), new Weighted<ModelVariant>(modelVariant8.with(ROTATE_Y_180), 1), new Weighted<ModelVariant>(modelVariant5.with(ROTATE_Y_180), 2), new Weighted<ModelVariant>(modelVariant6.with(ROTATE_Y_180), 1)))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, false), new WeightedVariant(Pool.of(new Weighted<ModelVariant>(modelVariant8.with(ROTATE_Y_270), 1), new Weighted<ModelVariant>(modelVariant5.with(ROTATE_Y_270), 2), new Weighted<ModelVariant>(modelVariant6.with(ROTATE_Y_270), 1), new Weighted<ModelVariant>(modelVariant7.with(ROTATE_Y_270), 1)))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.UP, false), new WeightedVariant(Pool.of(new Weighted<ModelVariant>(modelVariant5.with(ROTATE_X_270), 2), new Weighted<ModelVariant>(modelVariant8.with(ROTATE_X_270), 1), new Weighted<ModelVariant>(modelVariant6.with(ROTATE_X_270), 1), new Weighted<ModelVariant>(modelVariant7.with(ROTATE_X_270), 1)))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.DOWN, false), new WeightedVariant(Pool.of(new Weighted<ModelVariant>(modelVariant8.with(ROTATE_X_90), 1), new Weighted<ModelVariant>(modelVariant7.with(ROTATE_X_90), 1), new Weighted<ModelVariant>(modelVariant6.with(ROTATE_X_90), 1), new Weighted<ModelVariant>(modelVariant5.with(ROTATE_X_90), 2)))));
    }

    private void registerComposter() {
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.COMPOSTER).with(BlockStateModelGenerator.createWeightedVariant(TextureMap.getId(Blocks.COMPOSTER))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 1), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents1"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 2), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents2"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 3), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents3"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 4), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents4"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 5), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents5"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 6), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents6"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 7), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents7"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.LEVEL_8, 8), BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.COMPOSTER, "_contents_ready"))));
    }

    public final void registerCopperBulb(Block copperBulbBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_ALL.upload(copperBulbBlock, TextureMap.all(copperBulbBlock), this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(copperBulbBlock, "_powered", Models.CUBE_ALL, TextureMap::all));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(copperBulbBlock, "_lit", Models.CUBE_ALL, TextureMap::all));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(copperBulbBlock, "_lit_powered", Models.CUBE_ALL, TextureMap::all));
        this.blockStateCollector.accept(BlockStateModelGenerator.createCopperBulbBlockState(copperBulbBlock, weightedVariant, weightedVariant3, weightedVariant2, weightedVariant4));
    }

    public static BlockModelDefinitionCreator createCopperBulbBlockState(Block block, WeightedVariant unlitUnpoweredModel, WeightedVariant litUnpoweredModel, WeightedVariant unlitPoweredModel, WeightedVariant litPoweredModel) {
        return VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.LIT, Properties.POWERED).generate((lit, powered) -> {
            if (lit.booleanValue()) {
                return powered != false ? litPoweredModel : litUnpoweredModel;
            }
            return powered != false ? unlitPoweredModel : unlitUnpoweredModel;
        }));
    }

    public final void registerWaxedCopperBulb(Block unwaxedCopperBulbBlock, Block waxedCopperBulbBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(unwaxedCopperBulbBlock));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(unwaxedCopperBulbBlock, "_powered"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(unwaxedCopperBulbBlock, "_lit"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(unwaxedCopperBulbBlock, "_lit_powered"));
        this.itemModelOutput.acceptAlias(unwaxedCopperBulbBlock.asItem(), waxedCopperBulbBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createCopperBulbBlockState(waxedCopperBulbBlock, weightedVariant, weightedVariant3, weightedVariant2, weightedVariant4));
    }

    public final void registerAmethyst(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CROSS.upload(block, TextureMap.cross(block), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, weightedVariant).coordinate(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerAmethysts() {
        this.registerAmethyst(Blocks.SMALL_AMETHYST_BUD);
        this.registerAmethyst(Blocks.MEDIUM_AMETHYST_BUD);
        this.registerAmethyst(Blocks.LARGE_AMETHYST_BUD);
        this.registerAmethyst(Blocks.AMETHYST_CLUSTER);
    }

    private void registerPointedDripstone() {
        BlockStateVariantMap.DoubleProperty<WeightedVariant, Direction, Thickness> doubleProperty = BlockStateVariantMap.models(Properties.VERTICAL_DIRECTION, Properties.THICKNESS);
        for (Thickness thickness : Thickness.values()) {
            doubleProperty.register(Direction.UP, thickness, this.getDripstoneVariant(Direction.UP, thickness));
        }
        for (Thickness thickness : Thickness.values()) {
            doubleProperty.register(Direction.DOWN, thickness, this.getDripstoneVariant(Direction.DOWN, thickness));
        }
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.POINTED_DRIPSTONE).with(doubleProperty));
    }

    public final WeightedVariant getDripstoneVariant(Direction direction, Thickness thickness) {
        String string = "_" + direction.asString() + "_" + thickness.asString();
        TextureMap textureMap = TextureMap.cross(TextureMap.getSubId(Blocks.POINTED_DRIPSTONE, string));
        return BlockStateModelGenerator.createWeightedVariant(Models.POINTED_DRIPSTONE.upload(Blocks.POINTED_DRIPSTONE, string, textureMap, this.modelCollector));
    }

    public final void registerNetherrackBottomCustomTop(Block block) {
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, TextureMap.getId(Blocks.NETHERRACK)).put(TextureKey.TOP, TextureMap.getId(block)).put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP.upload(block, textureMap, this.modelCollector))));
    }

    private void registerDaylightDetector() {
        Identifier identifier = TextureMap.getSubId(Blocks.DAYLIGHT_DETECTOR, "_side");
        TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureKey.SIDE, identifier);
        TextureMap textureMap2 = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureKey.SIDE, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.DAYLIGHT_DETECTOR).with(BlockStateVariantMap.models(Properties.INVERTED).register(false, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(Blocks.DAYLIGHT_DETECTOR, textureMap, this.modelCollector))).register(true, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(ModelIds.getBlockSubModelId(Blocks.DAYLIGHT_DETECTOR, "_inverted"), textureMap2, this.modelCollector)))));
    }

    public final void registerRod(Block block) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block))).coordinate(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerLightningRod() {
        Block block = Blocks.LIGHTNING_ROD;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_on"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, weightedVariant, weightedVariant2)).coordinate(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerFarmland() {
        TextureMap textureMap = new TextureMap().put(TextureKey.DIRT, TextureMap.getId(Blocks.DIRT)).put(TextureKey.TOP, TextureMap.getId(Blocks.FARMLAND));
        TextureMap textureMap2 = new TextureMap().put(TextureKey.DIRT, TextureMap.getId(Blocks.DIRT)).put(TextureKey.TOP, TextureMap.getSubId(Blocks.FARMLAND, "_moist"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FARMLAND.upload(Blocks.FARMLAND, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FARMLAND.upload(TextureMap.getSubId(Blocks.FARMLAND, "_moist"), textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.FARMLAND).with(BlockStateModelGenerator.createValueFencedModelMap(Properties.MOISTURE, 7, weightedVariant2, weightedVariant)));
    }

    public final WeightedVariant getFireFloorModels(Block texture) {
        return BlockStateModelGenerator.createWeightedVariant(BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(texture, "_floor0"), TextureMap.fire0(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(texture, "_floor1"), TextureMap.fire1(texture), this.modelCollector)));
    }

    public final WeightedVariant getFireSideModels(Block texture) {
        return BlockStateModelGenerator.createWeightedVariant(BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(texture, "_side0"), TextureMap.fire0(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(texture, "_side1"), TextureMap.fire1(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(texture, "_side_alt0"), TextureMap.fire0(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(texture, "_side_alt1"), TextureMap.fire1(texture), this.modelCollector)));
    }

    public final WeightedVariant getFireUpModels(Block texture) {
        return BlockStateModelGenerator.createWeightedVariant(BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(texture, "_up0"), TextureMap.fire0(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(texture, "_up1"), TextureMap.fire1(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(texture, "_up_alt0"), TextureMap.fire0(texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(texture, "_up_alt1"), TextureMap.fire1(texture), this.modelCollector)));
    }

    private void registerFire() {
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false).put(Properties.EAST, false).put(Properties.SOUTH, false).put(Properties.WEST, false).put(Properties.UP, false);
        WeightedVariant weightedVariant = this.getFireFloorModels(Blocks.FIRE);
        WeightedVariant weightedVariant2 = this.getFireSideModels(Blocks.FIRE);
        WeightedVariant weightedVariant3 = this.getFireUpModels(Blocks.FIRE);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.FIRE).with(multipartModelConditionBuilder, weightedVariant).with(BlockStateModelGenerator.or(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), multipartModelConditionBuilder), weightedVariant2).with(BlockStateModelGenerator.or(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), multipartModelConditionBuilder), weightedVariant2.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.or(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), multipartModelConditionBuilder), weightedVariant2.apply(ROTATE_Y_180)).with(BlockStateModelGenerator.or(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), multipartModelConditionBuilder), weightedVariant2.apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.UP, true), weightedVariant3));
    }

    private void registerSoulFire() {
        WeightedVariant weightedVariant = this.getFireFloorModels(Blocks.SOUL_FIRE);
        WeightedVariant weightedVariant2 = this.getFireSideModels(Blocks.SOUL_FIRE);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.SOUL_FIRE).with(weightedVariant).with(weightedVariant2).with(weightedVariant2.apply(ROTATE_Y_90)).with(weightedVariant2.apply(ROTATE_Y_180)).with(weightedVariant2.apply(ROTATE_Y_270)));
    }

    public final void registerLantern(Block lantern) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_LANTERN.upload(lantern, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.TEMPLATE_HANGING_LANTERN.upload(lantern, this.modelCollector));
        this.registerItemModel(lantern.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(lantern).with(BlockStateModelGenerator.createBooleanModelMap(Properties.HANGING, weightedVariant2, weightedVariant)));
    }

    private void registerMuddyMangroveRoots() {
        TextureMap textureMap = TextureMap.sideEnd(TextureMap.getSubId(Blocks.MUDDY_MANGROVE_ROOTS, "_side"), TextureMap.getSubId(Blocks.MUDDY_MANGROVE_ROOTS, "_top"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN.upload(Blocks.MUDDY_MANGROVE_ROOTS, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(Blocks.MUDDY_MANGROVE_ROOTS, weightedVariant));
    }

    private void registerMangrovePropagule() {
        this.registerItemModel(Items.MANGROVE_PROPAGULE);
        Block block = Blocks.MANGROVE_PROPAGULE;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.MANGROVE_PROPAGULE).with(BlockStateVariantMap.models(PropaguleBlock.HANGING, PropaguleBlock.AGE).generate((hanging, age) -> hanging != false ? BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_hanging_" + age)) : weightedVariant)));
    }

    private void registerFrostedIce() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.FROSTED_ICE).with(BlockStateVariantMap.models(Properties.AGE_3).register(0, BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.FROSTED_ICE, "_0", Models.CUBE_ALL, TextureMap::all))).register(1, BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.FROSTED_ICE, "_1", Models.CUBE_ALL, TextureMap::all))).register(2, BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.FROSTED_ICE, "_2", Models.CUBE_ALL, TextureMap::all))).register(3, BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.FROSTED_ICE, "_3", Models.CUBE_ALL, TextureMap::all)))));
    }

    private void registerTopSoils() {
        Identifier identifier = TextureMap.getId(Blocks.DIRT);
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, identifier).inherit(TextureKey.BOTTOM, TextureKey.PARTICLE).put(TextureKey.TOP, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_top")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_snow"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", textureMap, this.modelCollector));
        Identifier identifier2 = ModelIds.getBlockModelId(Blocks.GRASS_BLOCK);
        this.registerTopSoil(Blocks.GRASS_BLOCK, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(identifier2)), weightedVariant);
        this.registerTintedItemModel(Blocks.GRASS_BLOCK, identifier2, new GrassTintSource());
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.MYCELIUM).textures(textures -> textures.put(TextureKey.BOTTOM, identifier)).upload(Blocks.MYCELIUM, this.modelCollector)));
        this.registerTopSoil(Blocks.MYCELIUM, weightedVariant2, weightedVariant);
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.PODZOL).textures(textures -> textures.put(TextureKey.BOTTOM, identifier)).upload(Blocks.PODZOL, this.modelCollector)));
        this.registerTopSoil(Blocks.PODZOL, weightedVariant3, weightedVariant);
    }

    public final void registerTopSoil(Block topSoil, WeightedVariant weightedVariant, WeightedVariant snowyVariant) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(topSoil).with(BlockStateVariantMap.models(Properties.SNOWY).register(true, snowyVariant).register(false, weightedVariant)));
    }

    private void registerCocoa() {
        this.registerItemModel(Items.COCOA_BEANS);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.COCOA).with(BlockStateVariantMap.models(Properties.AGE_2).register(0, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage0"))).register(1, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage1"))).register(2, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage2")))).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerDirtPath() {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockModelId(Blocks.DIRT_PATH));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.DIRT_PATH, BlockStateModelGenerator.modelWithYRotation(modelVariant)));
    }

    public final void registerWeightedPressurePlate(Block weightedPressurePlate, Block textureSource) {
        TextureMap textureMap = TextureMap.texture(textureSource);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PRESSURE_PLATE_UP.upload(weightedPressurePlate, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.PRESSURE_PLATE_DOWN.upload(weightedPressurePlate, textureMap, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(weightedPressurePlate).with(BlockStateModelGenerator.createValueFencedModelMap(Properties.POWER, 1, weightedVariant2, weightedVariant)));
    }

    private void registerHopper() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.HOPPER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.HOPPER, "_side"));
        this.registerItemModel(Items.HOPPER);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.HOPPER).with(BlockStateVariantMap.models(Properties.HOPPER_FACING).register(Direction.DOWN, weightedVariant).register(Direction.NORTH, weightedVariant2).register(Direction.EAST, weightedVariant2.apply(ROTATE_Y_90)).register(Direction.SOUTH, weightedVariant2.apply(ROTATE_Y_180)).register(Direction.WEST, weightedVariant2.apply(ROTATE_Y_270))));
    }

    public final void registerParented(Block modelSource, Block child) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(modelSource));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(child, weightedVariant));
        this.itemModelOutput.acceptAlias(modelSource.asItem(), child.asItem());
    }

    private void registerIronBars() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post_ends"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap_alt"));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side"));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side_alt"));
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create(Blocks.IRON_BARS).with(weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false).put(Properties.EAST, false).put(Properties.SOUTH, false).put(Properties.WEST, false), weightedVariant2).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true).put(Properties.EAST, false).put(Properties.SOUTH, false).put(Properties.WEST, false), weightedVariant3).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false).put(Properties.EAST, true).put(Properties.SOUTH, false).put(Properties.WEST, false), weightedVariant3.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false).put(Properties.EAST, false).put(Properties.SOUTH, true).put(Properties.WEST, false), weightedVariant4).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, false).put(Properties.EAST, false).put(Properties.SOUTH, false).put(Properties.WEST, true), weightedVariant4.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.NORTH, true), weightedVariant5).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.EAST, true), weightedVariant5.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.SOUTH, true), weightedVariant6).with(BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.WEST, true), weightedVariant6.apply(ROTATE_Y_90)));
        this.registerItemModel(Blocks.IRON_BARS);
    }

    public final void registerNorthDefaultHorizontalRotation(Block block) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block))).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerLever() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.LEVER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.LEVER, "_on"));
        this.registerItemModel(Blocks.LEVER);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.LEVER).with(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, weightedVariant, weightedVariant2)).coordinate(BlockStateVariantMap.operations(Properties.BLOCK_FACE, Properties.HORIZONTAL_FACING).register(BlockFace.CEILING, Direction.NORTH, ROTATE_X_180.then(ROTATE_Y_180)).register(BlockFace.CEILING, Direction.EAST, ROTATE_X_180.then(ROTATE_Y_270)).register(BlockFace.CEILING, Direction.SOUTH, ROTATE_X_180).register(BlockFace.CEILING, Direction.WEST, ROTATE_X_180.then(ROTATE_Y_90)).register(BlockFace.FLOOR, Direction.NORTH, NO_OP).register(BlockFace.FLOOR, Direction.EAST, ROTATE_Y_90).register(BlockFace.FLOOR, Direction.SOUTH, ROTATE_Y_180).register(BlockFace.FLOOR, Direction.WEST, ROTATE_Y_270).register(BlockFace.WALL, Direction.NORTH, ROTATE_X_90).register(BlockFace.WALL, Direction.EAST, ROTATE_X_90.then(ROTATE_Y_90)).register(BlockFace.WALL, Direction.SOUTH, ROTATE_X_90.then(ROTATE_Y_180)).register(BlockFace.WALL, Direction.WEST, ROTATE_X_90.then(ROTATE_Y_270))));
    }

    private void registerLilyPad() {
        Identifier identifier = this.uploadBlockItemModel(Items.LILY_PAD, Blocks.LILY_PAD);
        this.registerTintedItemModel(Blocks.LILY_PAD, identifier, ItemModels.constantTintSource(-9321636));
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockModelId(Blocks.LILY_PAD));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.LILY_PAD, BlockStateModelGenerator.modelWithYRotation(modelVariant)));
    }

    private void registerFrogspawn() {
        this.registerItemModel(Blocks.FROGSPAWN);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.FROGSPAWN, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.FROGSPAWN))));
    }

    private void registerNetherPortal() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.NETHER_PORTAL).with(BlockStateVariantMap.models(Properties.HORIZONTAL_AXIS).register(Direction.Axis.X, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.NETHER_PORTAL, "_ns"))).register(Direction.Axis.Z, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.NETHER_PORTAL, "_ew")))));
    }

    private void registerNetherrack() {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(TexturedModel.CUBE_ALL.upload(Blocks.NETHERRACK, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.NETHERRACK, BlockStateModelGenerator.createWeightedVariant(modelVariant, modelVariant.with(ROTATE_X_90), modelVariant.with(ROTATE_X_180), modelVariant.with(ROTATE_X_270), modelVariant.with(ROTATE_Y_90), modelVariant.with(ROTATE_Y_90.then(ROTATE_X_90)), modelVariant.with(ROTATE_Y_90.then(ROTATE_X_180)), modelVariant.with(ROTATE_Y_90.then(ROTATE_X_270)), modelVariant.with(ROTATE_Y_180), modelVariant.with(ROTATE_Y_180.then(ROTATE_X_90)), modelVariant.with(ROTATE_Y_180.then(ROTATE_X_180)), modelVariant.with(ROTATE_Y_180.then(ROTATE_X_270)), modelVariant.with(ROTATE_Y_270), modelVariant.with(ROTATE_Y_270.then(ROTATE_X_90)), modelVariant.with(ROTATE_Y_270.then(ROTATE_X_180)), modelVariant.with(ROTATE_Y_270.then(ROTATE_X_270)))));
    }

    private void registerObserver() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.OBSERVER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.OBSERVER, "_on"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.OBSERVER).with(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, weightedVariant2, weightedVariant)).coordinate(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerPistons() {
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, TextureMap.getSubId(Blocks.PISTON, "_bottom")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.PISTON, "_side"));
        Identifier identifier = TextureMap.getSubId(Blocks.PISTON, "_top_sticky");
        Identifier identifier2 = TextureMap.getSubId(Blocks.PISTON, "_top");
        TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.PLATFORM, identifier);
        TextureMap textureMap3 = textureMap.copyAndAdd(TextureKey.PLATFORM, identifier2);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.PISTON, "_base"));
        this.registerPiston(Blocks.PISTON, weightedVariant, textureMap3);
        this.registerPiston(Blocks.STICKY_PISTON, weightedVariant, textureMap2);
        Identifier identifier3 = Models.CUBE_BOTTOM_TOP.upload(Blocks.PISTON, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, identifier2), this.modelCollector);
        Identifier identifier4 = Models.CUBE_BOTTOM_TOP.upload(Blocks.STICKY_PISTON, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, identifier), this.modelCollector);
        this.registerParentedItemModel(Blocks.PISTON, identifier3);
        this.registerParentedItemModel(Blocks.STICKY_PISTON, identifier4);
    }

    public final void registerPiston(Block piston, WeightedVariant weightedVariant, TextureMap textures) {
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_PISTON.upload(piston, textures, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(piston).with(BlockStateModelGenerator.createBooleanModelMap(Properties.EXTENDED, weightedVariant, weightedVariant2)).coordinate(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerPistonHead() {
        TextureMap textureMap = new TextureMap().put(TextureKey.UNSTICKY, TextureMap.getSubId(Blocks.PISTON, "_top")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.PISTON, "_side"));
        TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(Blocks.PISTON, "_top_sticky"));
        TextureMap textureMap3 = textureMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(Blocks.PISTON, "_top"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.PISTON_HEAD).with(BlockStateVariantMap.models(Properties.SHORT, Properties.PISTON_TYPE).register(false, PistonType.DEFAULT, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head", textureMap3, this.modelCollector))).register(false, PistonType.STICKY, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head_sticky", textureMap2, this.modelCollector))).register(true, PistonType.DEFAULT, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short", textureMap3, this.modelCollector))).register(true, PistonType.STICKY, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short_sticky", textureMap2, this.modelCollector)))).coordinate(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerTrialSpawner() {
        Block block = Blocks.TRIAL_SPAWNER;
        TextureMap textureMap = TextureMap.trialSpawner(block, "_side_inactive", "_top_inactive");
        TextureMap textureMap2 = TextureMap.trialSpawner(block, "_side_active", "_top_active");
        TextureMap textureMap3 = TextureMap.trialSpawner(block, "_side_active", "_top_ejecting_reward");
        TextureMap textureMap4 = TextureMap.trialSpawner(block, "_side_inactive_ominous", "_top_inactive_ominous");
        TextureMap textureMap5 = TextureMap.trialSpawner(block, "_side_active_ominous", "_top_active_ominous");
        TextureMap textureMap6 = TextureMap.trialSpawner(block, "_side_active_ominous", "_top_ejecting_reward_ominous");
        Identifier identifier = Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, textureMap, this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_active", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_ejecting_reward", textureMap3, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_inactive_ominous", textureMap4, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_active_ominous", textureMap5, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_ejecting_reward_ominous", textureMap6, this.modelCollector));
        this.registerParentedItemModel(block, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.TRIAL_SPAWNER_STATE, Properties.OMINOUS).generate((state, ominous) -> switch (state) {
            default -> throw new MatchException(null, null);
            case TrialSpawnerState.INACTIVE, TrialSpawnerState.COOLDOWN -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant4;
                }
                yield weightedVariant;
            }
            case TrialSpawnerState.WAITING_FOR_PLAYERS, TrialSpawnerState.ACTIVE, TrialSpawnerState.WAITING_FOR_REWARD_EJECTION -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant5;
                }
                yield weightedVariant2;
            }
            case TrialSpawnerState.EJECTING_REWARD -> ominous != false ? weightedVariant6 : weightedVariant3;
        })));
    }

    private void registerVault() {
        Block block = Blocks.VAULT;
        TextureMap textureMap = TextureMap.vault(block, "_front_off", "_side_off", "_top", "_bottom");
        TextureMap textureMap2 = TextureMap.vault(block, "_front_on", "_side_on", "_top", "_bottom");
        TextureMap textureMap3 = TextureMap.vault(block, "_front_ejecting", "_side_on", "_top", "_bottom");
        TextureMap textureMap4 = TextureMap.vault(block, "_front_ejecting", "_side_on", "_top_ejecting", "_bottom");
        Identifier identifier = Models.TEMPLATE_VAULT.upload(block, textureMap, this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_active", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_unlocking", textureMap3, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_ejecting_reward", textureMap4, this.modelCollector));
        TextureMap textureMap5 = TextureMap.vault(block, "_front_off_ominous", "_side_off_ominous", "_top_ominous", "_bottom_ominous");
        TextureMap textureMap6 = TextureMap.vault(block, "_front_on_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
        TextureMap textureMap7 = TextureMap.vault(block, "_front_ejecting_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
        TextureMap textureMap8 = TextureMap.vault(block, "_front_ejecting_ominous", "_side_on_ominous", "_top_ejecting_ominous", "_bottom_ominous");
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_ominous", textureMap5, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_active_ominous", textureMap6, this.modelCollector));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_unlocking_ominous", textureMap7, this.modelCollector));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_VAULT.upload(block, "_ejecting_reward_ominous", textureMap8, this.modelCollector));
        this.registerParentedItemModel(block, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(VaultBlock.VAULT_STATE, VaultBlock.OMINOUS).generate((state, ominous) -> switch (state) {
            default -> throw new MatchException(null, null);
            case VaultState.INACTIVE -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant5;
                }
                yield weightedVariant;
            }
            case VaultState.ACTIVE -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant6;
                }
                yield weightedVariant2;
            }
            case VaultState.UNLOCKING -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant7;
                }
                yield weightedVariant3;
            }
            case VaultState.EJECTING -> ominous != false ? weightedVariant8 : weightedVariant4;
        })).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSculkSensor() {
        Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SCULK_SENSOR, "_inactive");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.SCULK_SENSOR, "_active"));
        this.registerParentedItemModel(Blocks.SCULK_SENSOR, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SCULK_SENSOR).with(BlockStateVariantMap.models(Properties.SCULK_SENSOR_PHASE).generate(phase -> phase == SculkSensorPhase.ACTIVE || phase == SculkSensorPhase.COOLDOWN ? weightedVariant2 : weightedVariant)));
    }

    private void registerCalibratedSculkSensor() {
        Identifier identifier = ModelIds.getBlockSubModelId(Blocks.CALIBRATED_SCULK_SENSOR, "_inactive");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.CALIBRATED_SCULK_SENSOR, "_active"));
        this.registerParentedItemModel(Blocks.CALIBRATED_SCULK_SENSOR, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.CALIBRATED_SCULK_SENSOR).with(BlockStateVariantMap.models(Properties.SCULK_SENSOR_PHASE).generate(phase -> phase == SculkSensorPhase.ACTIVE || phase == SculkSensorPhase.COOLDOWN ? weightedVariant2 : weightedVariant)).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSculkShrieker() {
        Identifier identifier = Models.TEMPLATE_SCULK_SHRIEKER.upload(Blocks.SCULK_SHRIEKER, TextureMap.sculkShrieker(false), this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_SCULK_SHRIEKER.upload(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMap.sculkShrieker(true), this.modelCollector));
        this.registerParentedItemModel(Blocks.SCULK_SHRIEKER, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SCULK_SHRIEKER).with(BlockStateModelGenerator.createBooleanModelMap(Properties.CAN_SUMMON, weightedVariant2, weightedVariant)));
    }

    private void registerScaffolding() {
        Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_stable");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_unstable"));
        this.registerParentedItemModel(Blocks.SCAFFOLDING, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SCAFFOLDING).with(BlockStateModelGenerator.createBooleanModelMap(Properties.BOTTOM, weightedVariant2, weightedVariant)));
    }

    private void registerCaveVines() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.CAVE_VINES, "", Models.CROSS, TextureMap::cross));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.CAVE_VINES, "_lit", Models.CROSS, TextureMap::cross));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.CAVE_VINES).with(BlockStateModelGenerator.createBooleanModelMap(Properties.BERRIES, weightedVariant2, weightedVariant)));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.CAVE_VINES_PLANT, "", Models.CROSS, TextureMap::cross));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.CAVE_VINES_PLANT, "_lit", Models.CROSS, TextureMap::cross));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.CAVE_VINES_PLANT).with(BlockStateModelGenerator.createBooleanModelMap(Properties.BERRIES, weightedVariant4, weightedVariant3)));
    }

    private void registerRedstoneLamp() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.CUBE_ALL.upload(Blocks.REDSTONE_LAMP, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.REDSTONE_LAMP, "_on", Models.CUBE_ALL, TextureMap::all));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.REDSTONE_LAMP).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, weightedVariant2, weightedVariant)));
    }

    public final void registerTorch(Block torch, Block wallTorch) {
        TextureMap textureMap = TextureMap.torch(torch);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(torch, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TORCH.upload(torch, textureMap, this.modelCollector))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(wallTorch, BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TORCH_WALL.upload(wallTorch, textureMap, this.modelCollector))).coordinate(EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        this.registerItemModel(torch);
    }

    private void registerRedstoneTorch() {
        TextureMap textureMap = TextureMap.torch(Blocks.REDSTONE_TORCH);
        TextureMap textureMap2 = TextureMap.torch(TextureMap.getSubId(Blocks.REDSTONE_TORCH, "_off"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_REDSTONE_TORCH.upload(Blocks.REDSTONE_TORCH, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TORCH_UNLIT.upload(Blocks.REDSTONE_TORCH, "_off", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.REDSTONE_TORCH).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, weightedVariant, weightedVariant2)));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_REDSTONE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TORCH_WALL_UNLIT.upload(Blocks.REDSTONE_WALL_TORCH, "_off", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.REDSTONE_WALL_TORCH).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, weightedVariant3, weightedVariant4)).coordinate(EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        this.registerItemModel(Blocks.REDSTONE_TORCH);
    }

    private void registerRepeater() {
        this.registerItemModel(Items.REPEATER);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.REPEATER).with(BlockStateVariantMap.models(Properties.DELAY, Properties.LOCKED, Properties.POWERED).generate((Function3<Integer, Boolean, Boolean, WeightedVariant>)((Function3)(tick, locked, on) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('_').append(tick).append("tick");
            if (on.booleanValue()) {
                stringBuilder.append("_on");
            }
            if (locked.booleanValue()) {
                stringBuilder.append("_locked");
            }
            return BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(Blocks.REPEATER, stringBuilder.toString()));
        }))).coordinate(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSeaPickle() {
        this.registerItemModel(Items.SEA_PICKLE);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SEA_PICKLE).with(BlockStateVariantMap.models(Properties.PICKLES, Properties.WATERLOGGED).register(1, false, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("dead_sea_pickle")))).register(2, false, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("two_dead_sea_pickles")))).register(3, false, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("three_dead_sea_pickles")))).register(4, false, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("four_dead_sea_pickles")))).register(1, true, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("sea_pickle")))).register(2, true, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("two_sea_pickles")))).register(3, true, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("three_sea_pickles")))).register(4, true, BlockStateModelGenerator.modelWithYRotation(BlockStateModelGenerator.createModelVariant(ModelIds.getMinecraftNamespacedBlock("four_sea_pickles"))))));
    }

    private void registerSnows() {
        TextureMap textureMap = TextureMap.all(Blocks.SNOW);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_ALL.upload(Blocks.SNOW_BLOCK, textureMap, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SNOW).with(BlockStateVariantMap.models(Properties.LAYERS).generate(layers -> layers < 8 ? BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.SNOW, "_height" + layers * 2)) : weightedVariant)));
        this.registerParentedItemModel(Blocks.SNOW, ModelIds.getBlockSubModelId(Blocks.SNOW, "_height2"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.SNOW_BLOCK, weightedVariant));
    }

    private void registerStonecutter() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.STONECUTTER, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.STONECUTTER))).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerStructureBlock() {
        Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.STRUCTURE_BLOCK, this.modelCollector);
        this.registerParentedItemModel(Blocks.STRUCTURE_BLOCK, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.STRUCTURE_BLOCK).with(BlockStateVariantMap.models(Properties.STRUCTURE_BLOCK_MODE).generate(mode -> BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.STRUCTURE_BLOCK, "_" + mode.asString(), Models.CUBE_ALL, TextureMap::all)))));
    }

    private void registerTestBlock() {
        HashMap<TestBlockMode, Identifier> map = new HashMap<TestBlockMode, Identifier>();
        for (TestBlockMode testBlockMode : TestBlockMode.values()) {
            map.put(testBlockMode, this.createSubModel(Blocks.TEST_BLOCK, "_" + testBlockMode.asString(), Models.CUBE_ALL, TextureMap::all));
        }
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.TEST_BLOCK).with(BlockStateVariantMap.models(Properties.TEST_BLOCK_MODE).generate(mode -> BlockStateModelGenerator.createWeightedVariant((Identifier)map.get(mode)))));
        this.itemModelOutput.accept(Items.TEST_BLOCK, ItemModels.select(TestBlock.MODE, ItemModels.basic((Identifier)map.get(TestBlockMode.START)), Map.of(TestBlockMode.FAIL, ItemModels.basic((Identifier)map.get(TestBlockMode.FAIL)), TestBlockMode.LOG, ItemModels.basic((Identifier)map.get(TestBlockMode.LOG)), TestBlockMode.ACCEPT, ItemModels.basic((Identifier)map.get(TestBlockMode.ACCEPT)))));
    }

    private void registerSweetBerryBush() {
        this.registerItemModel(Items.SWEET_BERRIES);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SWEET_BERRY_BUSH).with(BlockStateVariantMap.models(Properties.AGE_3).generate(stage -> BlockStateModelGenerator.createWeightedVariant(this.createSubModel(Blocks.SWEET_BERRY_BUSH, "_stage" + stage, Models.CROSS, TextureMap::cross)))));
    }

    private void registerTripwire() {
        this.registerItemModel(Items.STRING);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.TRIPWIRE).with(BlockStateVariantMap.models(Properties.ATTACHED, Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.WEST).register(false, false, false, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns"))).register(false, true, false, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")).apply(ROTATE_Y_90)).register(false, false, true, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n"))).register(false, false, false, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")).apply(ROTATE_Y_180)).register(false, false, false, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")).apply(ROTATE_Y_270)).register(false, true, true, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne"))).register(false, true, false, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")).apply(ROTATE_Y_90)).register(false, false, false, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")).apply(ROTATE_Y_180)).register(false, false, true, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")).apply(ROTATE_Y_270)).register(false, false, true, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns"))).register(false, true, false, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns")).apply(ROTATE_Y_90)).register(false, true, true, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse"))).register(false, true, false, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")).apply(ROTATE_Y_90)).register(false, false, true, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")).apply(ROTATE_Y_180)).register(false, true, true, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")).apply(ROTATE_Y_270)).register(false, true, true, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nsew"))).register(true, false, false, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))).register(true, false, true, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n"))).register(true, false, false, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n")).apply(ROTATE_Y_180)).register(true, true, false, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n")).apply(ROTATE_Y_90)).register(true, false, false, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n")).apply(ROTATE_Y_270)).register(true, true, true, false, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne"))).register(true, true, false, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne")).apply(ROTATE_Y_90)).register(true, false, false, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne")).apply(ROTATE_Y_180)).register(true, false, true, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne")).apply(ROTATE_Y_270)).register(true, false, true, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))).register(true, true, false, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns")).apply(ROTATE_Y_90)).register(true, true, true, true, false, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse"))).register(true, true, false, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse")).apply(ROTATE_Y_90)).register(true, false, true, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse")).apply(ROTATE_Y_180)).register(true, true, true, false, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse")).apply(ROTATE_Y_270)).register(true, true, true, true, true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nsew")))));
    }

    private void registerTripwireHook() {
        this.registerItemModel(Blocks.TRIPWIRE_HOOK);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.TRIPWIRE_HOOK).with(BlockStateVariantMap.models(Properties.ATTACHED, Properties.POWERED).generate((attached, on) -> BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(Blocks.TRIPWIRE_HOOK, (attached != false ? "_attached" : "") + (on != false ? "_on" : ""))))).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final ModelVariant getTurtleEggModel(int eggs, String prefix, TextureMap textures) {
        return switch (eggs) {
            case 1 -> BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_TURTLE_EGG.upload(ModelIds.getMinecraftNamespacedBlock(prefix + "turtle_egg"), textures, this.modelCollector));
            case 2 -> BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_TWO_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("two_" + prefix + "turtle_eggs"), textures, this.modelCollector));
            case 3 -> BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_THREE_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("three_" + prefix + "turtle_eggs"), textures, this.modelCollector));
            case 4 -> BlockStateModelGenerator.createModelVariant(Models.TEMPLATE_FOUR_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("four_" + prefix + "turtle_eggs"), textures, this.modelCollector));
            default -> throw new UnsupportedOperationException();
        };
    }

    public final ModelVariant getTurtleEggModel(int eggs, int cracks) {
        return switch (cracks) {
            case 0 -> this.getTurtleEggModel(eggs, "", TextureMap.all(TextureMap.getId(Blocks.TURTLE_EGG)));
            case 1 -> this.getTurtleEggModel(eggs, "slightly_cracked_", TextureMap.all(TextureMap.getSubId(Blocks.TURTLE_EGG, "_slightly_cracked")));
            case 2 -> this.getTurtleEggModel(eggs, "very_cracked_", TextureMap.all(TextureMap.getSubId(Blocks.TURTLE_EGG, "_very_cracked")));
            default -> throw new UnsupportedOperationException();
        };
    }

    private void registerTurtleEgg() {
        this.registerItemModel(Items.TURTLE_EGG);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.TURTLE_EGG).with(BlockStateVariantMap.models(Properties.EGGS, Properties.HATCH).generate((eggs, hatch) -> BlockStateModelGenerator.modelWithYRotation(this.getTurtleEggModel((int)eggs, (int)hatch)))));
    }

    private void registerDriedGhast() {
        Identifier identifier = ModelIds.getBlockSubModelId(Blocks.DRIED_GHAST, "_hydration_0");
        this.registerParentedItemModel(Blocks.DRIED_GHAST, identifier);
        Function<Integer, Identifier> function = hydration -> {
            String string = switch (hydration) {
                case 1 -> "_hydration_1";
                case 2 -> "_hydration_2";
                case 3 -> "_hydration_3";
                default -> "_hydration_0";
            };
            TextureMap textureMap = TextureMap.driedGhast(string);
            return Models.DRIED_GHAST.upload(Blocks.DRIED_GHAST, string, textureMap, this.modelCollector);
        };
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.DRIED_GHAST).with(BlockStateVariantMap.models(DriedGhastBlock.HYDRATION).generate(hydration -> BlockStateModelGenerator.createWeightedVariant((Identifier)function.apply((Integer)hydration)))).coordinate(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSnifferEgg() {
        this.registerItemModel(Items.SNIFFER_EGG);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SNIFFER_EGG).with(BlockStateVariantMap.models(SnifferEggBlock.HATCH).generate(hatch -> {
            String string = switch (hatch) {
                case 1 -> "_slightly_cracked";
                case 2 -> "_very_cracked";
                default -> "_not_cracked";
            };
            TextureMap textureMap = TextureMap.snifferEgg(string);
            return BlockStateModelGenerator.createWeightedVariant(Models.SNIFFER_EGG.upload(Blocks.SNIFFER_EGG, string, textureMap, this.modelCollector));
        })));
    }

    public final void registerMultifaceBlock(Block block) {
        this.registerItemModel(block);
        this.registerMultifaceBlockModel(block);
    }

    public final void registerMultifaceBlock(Block block, Item item) {
        this.registerItemModel(item);
        this.registerMultifaceBlockModel(block);
    }

    public static <T extends Property<?>> Map<T, ModelVariantOperator> collectMultifaceOperators(State<?, ?> state, Function<Direction, T> propertyGetter) {
        ImmutableMap.Builder builder = ImmutableMap.builderWithExpectedSize((int)CONNECTION_VARIANT_FUNCTIONS.size());
        CONNECTION_VARIANT_FUNCTIONS.forEach((direction, operator) -> {
            Property property = (Property)propertyGetter.apply((Direction)direction);
            if (state.contains(property)) {
                builder.put((Object)property, operator);
            }
        });
        return builder.build();
    }

    public final void registerMultifaceBlockModel(Block block) {
        Map<Property, ModelVariantOperator> map = BlockStateModelGenerator.collectMultifaceOperators(block.getDefaultState(), MultifaceBlock::getProperty);
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder();
        map.forEach((property, operator) -> multipartModelConditionBuilder.put(property, false));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create(block);
        map.forEach((property, operator) -> {
            multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(property, true), weightedVariant.apply((ModelVariantOperator)operator));
            multipartBlockModelDefinitionCreator.with(multipartModelConditionBuilder, weightedVariant.apply((ModelVariantOperator)operator));
        });
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
    }

    public final void registerPaleMossCarpet(Block block) {
        Map<Property, ModelVariantOperator> map = BlockStateModelGenerator.collectMultifaceOperators(block.getDefaultState(), PaleMossCarpetBlock::getWallShape);
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder().put(PaleMossCarpetBlock.BOTTOM, false);
        map.forEach((property, operator) -> multipartModelConditionBuilder.put(property, WallShape.NONE));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(TexturedModel.CARPET.upload(block, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.MOSSY_CARPET_SIDE.get(block).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getSubId(block, "_side_tall"))).upload(block, "_side_tall", this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(TexturedModel.MOSSY_CARPET_SIDE.get(block).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getSubId(block, "_side_small"))).upload(block, "_side_small", this.modelCollector));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create(block);
        multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(PaleMossCarpetBlock.BOTTOM, true), weightedVariant);
        multipartBlockModelDefinitionCreator.with(multipartModelConditionBuilder, weightedVariant);
        map.forEach((property, operator) -> {
            multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(property, WallShape.TALL), weightedVariant2.apply((ModelVariantOperator)operator));
            multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(property, WallShape.LOW), weightedVariant3.apply((ModelVariantOperator)operator));
            multipartBlockModelDefinitionCreator.with(multipartModelConditionBuilder, weightedVariant2.apply((ModelVariantOperator)operator));
        });
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
    }

    public final void registerHangingMoss(Block block) {
        this.registerItemModel(block);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(HangingMossBlock.TIP).generate(tip -> {
            String string = tip != false ? "_tip" : "";
            TextureMap textureMap = TextureMap.cross(TextureMap.getSubId(block, string));
            return BlockStateModelGenerator.createWeightedVariant(CrossType.NOT_TINTED.getCrossModel().upload(block, string, textureMap, this.modelCollector));
        })));
    }

    private void registerSculkCatalyst() {
        Identifier identifier = TextureMap.getSubId(Blocks.SCULK_CATALYST, "_bottom");
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, identifier).put(TextureKey.TOP, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_top")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_side"));
        TextureMap textureMap2 = new TextureMap().put(TextureKey.BOTTOM, identifier).put(TextureKey.TOP, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_top_bloom")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_side_bloom"));
        Identifier identifier2 = Models.CUBE_BOTTOM_TOP.upload(Blocks.SCULK_CATALYST, textureMap, this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(identifier2);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_BOTTOM_TOP.upload(Blocks.SCULK_CATALYST, "_bloom", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.SCULK_CATALYST).with(BlockStateVariantMap.models(Properties.BLOOM).generate(bloom -> bloom != false ? weightedVariant2 : weightedVariant)));
        this.registerParentedItemModel(Blocks.SCULK_CATALYST, identifier2);
    }

    private void registerChiseledBookshelf() {
        Block block = Blocks.CHISELED_BOOKSHELF;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create(block);
        List.of(Pair.of((Object)Direction.NORTH, (Object)NO_OP), Pair.of((Object)Direction.EAST, (Object)ROTATE_Y_90), Pair.of((Object)Direction.SOUTH, (Object)ROTATE_Y_180), Pair.of((Object)Direction.WEST, (Object)ROTATE_Y_270)).forEach(pair -> {
            Direction direction = (Direction)pair.getFirst();
            ModelVariantOperator modelVariantOperator = (ModelVariantOperator)pair.getSecond();
            MultipartModelCondition multipartModelCondition = BlockStateModelGenerator.createMultipartConditionBuilder().put(Properties.HORIZONTAL_FACING, direction).build();
            multipartBlockModelDefinitionCreator.with(multipartModelCondition, weightedVariant.apply(modelVariantOperator).apply(UV_LOCK));
            this.supplyChiseledBookshelfModels(multipartBlockModelDefinitionCreator, multipartModelCondition, modelVariantOperator);
        });
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
        this.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_inventory"));
        CHISELED_BOOKSHELF_MODEL_CACHE.clear();
    }

    public final void supplyChiseledBookshelfModels(MultipartBlockModelDefinitionCreator blockStateSupplier, MultipartModelCondition facingCondition, ModelVariantOperator rotation) {
        List.of(Pair.of((Object)Properties.SLOT_0_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT), Pair.of((Object)Properties.SLOT_1_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID), Pair.of((Object)Properties.SLOT_2_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT), Pair.of((Object)Properties.SLOT_3_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT), Pair.of((Object)Properties.SLOT_4_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID), Pair.of((Object)Properties.SLOT_5_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT)).forEach(pair -> {
            BooleanProperty booleanProperty = (BooleanProperty)pair.getFirst();
            Model model = (Model)pair.getSecond();
            this.supplyChiseledBookshelfModel(blockStateSupplier, facingCondition, rotation, booleanProperty, model, true);
            this.supplyChiseledBookshelfModel(blockStateSupplier, facingCondition, rotation, booleanProperty, model, false);
        });
    }

    public final void supplyChiseledBookshelfModel(MultipartBlockModelDefinitionCreator blockStateSupplier, MultipartModelCondition facingCondition, ModelVariantOperator rotation, BooleanProperty property, Model model, boolean occupied) {
        String string = occupied ? "_occupied" : "_empty";
        TextureMap textureMap = new TextureMap().put(TextureKey.TEXTURE, TextureMap.getSubId(Blocks.CHISELED_BOOKSHELF, string));
        ChiseledBookshelfModelCacheKey chiseledBookshelfModelCacheKey = new ChiseledBookshelfModelCacheKey(model, string);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(CHISELED_BOOKSHELF_MODEL_CACHE.computeIfAbsent(chiseledBookshelfModelCacheKey, key -> model.upload(Blocks.CHISELED_BOOKSHELF, string, textureMap, this.modelCollector)));
        blockStateSupplier.with(new MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.AND, List.of(facingCondition, BlockStateModelGenerator.createMultipartConditionBuilder().put(property, occupied).build())), weightedVariant.apply(rotation));
    }

    private void registerMagmaBlock() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_ALL.upload(Blocks.MAGMA_BLOCK, TextureMap.all(ModelIds.getMinecraftNamespacedBlock("magma")), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.MAGMA_BLOCK, weightedVariant));
    }

    public final void registerShulkerBox(Block shulkerBox, @Nullable DyeColor color) {
        this.registerBuiltin(shulkerBox);
        Item item = shulkerBox.asItem();
        Identifier identifier = Models.TEMPLATE_SHULKER_BOX.upload(item, TextureMap.particle(shulkerBox), this.modelCollector);
        ItemModel.Unbaked unbaked = color != null ? ItemModels.special(identifier, new ShulkerBoxModelRenderer.Unbaked(color)) : ItemModels.special(identifier, new ShulkerBoxModelRenderer.Unbaked());
        this.itemModelOutput.accept(item, unbaked);
    }

    public final void registerPlantPart(Block plant, Block plantStem, CrossType tintType) {
        this.registerTintableCrossBlockState(plant, tintType);
        this.registerTintableCrossBlockState(plantStem, tintType);
    }

    private void registerInfestedStone() {
        Identifier identifier = ModelIds.getBlockModelId(Blocks.STONE);
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(identifier);
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.STONE, "_mirrored"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.INFESTED_STONE, BlockStateModelGenerator.modelWithMirroring(modelVariant, modelVariant2)));
        this.registerParentedItemModel(Blocks.INFESTED_STONE, identifier);
    }

    private void registerInfestedDeepslate() {
        Identifier identifier = ModelIds.getBlockModelId(Blocks.DEEPSLATE);
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant(identifier);
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant(ModelIds.getBlockSubModelId(Blocks.DEEPSLATE, "_mirrored"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.INFESTED_DEEPSLATE, BlockStateModelGenerator.modelWithMirroring(modelVariant, modelVariant2)).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap()));
        this.registerParentedItemModel(Blocks.INFESTED_DEEPSLATE, identifier);
    }

    public final void registerRoots(Block root, Block pottedRoot) {
        this.registerTintableCross(root, CrossType.NOT_TINTED);
        TextureMap textureMap = TextureMap.plant(TextureMap.getSubId(root, "_pot"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(CrossType.NOT_TINTED.getFlowerPotCrossModel().upload(pottedRoot, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(pottedRoot, weightedVariant));
    }

    private void registerRespawnAnchor() {
        Identifier identifier = TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_bottom");
        Identifier identifier2 = TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_top_off");
        Identifier identifier3 = TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_top");
        Identifier[] identifiers = new Identifier[5];
        for (int i = 0; i < 5; ++i) {
            TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, identifier).put(TextureKey.TOP, i == 0 ? identifier2 : identifier3).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_side" + i));
            identifiers[i] = Models.CUBE_BOTTOM_TOP.upload(Blocks.RESPAWN_ANCHOR, "_" + i, textureMap, this.modelCollector);
        }
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.RESPAWN_ANCHOR).with(BlockStateVariantMap.models(Properties.CHARGES).generate(charges -> BlockStateModelGenerator.createWeightedVariant(identifiers[charges]))));
        this.registerParentedItemModel(Blocks.RESPAWN_ANCHOR, identifiers[0]);
    }

    public static ModelVariantOperator addJigsawOrientationToVariant(Orientation orientation) {
        return switch (orientation) {
            default -> throw new MatchException(null, null);
            case Orientation.DOWN_NORTH -> ROTATE_X_90;
            case Orientation.DOWN_SOUTH -> ROTATE_X_90.then(ROTATE_Y_180);
            case Orientation.DOWN_WEST -> ROTATE_X_90.then(ROTATE_Y_270);
            case Orientation.DOWN_EAST -> ROTATE_X_90.then(ROTATE_Y_90);
            case Orientation.UP_NORTH -> ROTATE_X_270.then(ROTATE_Y_180);
            case Orientation.UP_SOUTH -> ROTATE_X_270;
            case Orientation.UP_WEST -> ROTATE_X_270.then(ROTATE_Y_90);
            case Orientation.UP_EAST -> ROTATE_X_270.then(ROTATE_Y_270);
            case Orientation.NORTH_UP -> NO_OP;
            case Orientation.SOUTH_UP -> ROTATE_Y_180;
            case Orientation.WEST_UP -> ROTATE_Y_270;
            case Orientation.EAST_UP -> ROTATE_Y_90;
        };
    }

    private void registerJigsaw() {
        Identifier identifier = TextureMap.getSubId(Blocks.JIGSAW, "_top");
        Identifier identifier2 = TextureMap.getSubId(Blocks.JIGSAW, "_bottom");
        Identifier identifier3 = TextureMap.getSubId(Blocks.JIGSAW, "_side");
        Identifier identifier4 = TextureMap.getSubId(Blocks.JIGSAW, "_lock");
        TextureMap textureMap = new TextureMap().put(TextureKey.DOWN, identifier3).put(TextureKey.WEST, identifier3).put(TextureKey.EAST, identifier3).put(TextureKey.PARTICLE, identifier).put(TextureKey.NORTH, identifier).put(TextureKey.SOUTH, identifier2).put(TextureKey.UP, identifier4);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.JIGSAW, BlockStateModelGenerator.createWeightedVariant(Models.CUBE_DIRECTIONAL.upload(Blocks.JIGSAW, textureMap, this.modelCollector))).coordinate(BlockStateVariantMap.operations(Properties.ORIENTATION).generate(BlockStateModelGenerator::addJigsawOrientationToVariant)));
    }

    private void registerPetrifiedOakSlab() {
        Block block = Blocks.OAK_PLANKS;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block));
        TextureMap textureMap = TextureMap.all(block);
        Block block2 = Blocks.PETRIFIED_OAK_SLAB;
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.SLAB.upload(block2, textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.SLAB_TOP.upload(block2, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(block2, weightedVariant2, weightedVariant3, weightedVariant));
    }

    public final void registerSkull(Block block, Block wallBlock, SkullBlock.SkullType type, Identifier baseModelId) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("skull"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(wallBlock, weightedVariant));
        if (type == SkullBlock.Type.PLAYER) {
            this.itemModelOutput.accept(block.asItem(), ItemModels.special(baseModelId, new PlayerHeadModelRenderer.Unbaked()));
        } else {
            this.itemModelOutput.accept(block.asItem(), ItemModels.special(baseModelId, new HeadModelRenderer.Unbaked(type)));
        }
    }

    private void registerSkulls() {
        Identifier identifier = ModelIds.getMinecraftNamespacedItem("template_skull");
        this.registerSkull(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, SkullBlock.Type.CREEPER, identifier);
        this.registerSkull(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, SkullBlock.Type.PLAYER, identifier);
        this.registerSkull(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, SkullBlock.Type.ZOMBIE, identifier);
        this.registerSkull(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, SkullBlock.Type.SKELETON, identifier);
        this.registerSkull(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, SkullBlock.Type.WITHER_SKELETON, identifier);
        this.registerSkull(Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, SkullBlock.Type.PIGLIN, identifier);
        this.registerSkull(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, SkullBlock.Type.DRAGON, ModelIds.getItemModelId(Items.DRAGON_HEAD));
    }

    public final void registerBanner(Block block, Block wallBlock, DyeColor color) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("banner"));
        Identifier identifier = ModelIds.getMinecraftNamespacedItem("template_banner");
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(wallBlock, weightedVariant));
        Item item = block.asItem();
        this.itemModelOutput.accept(item, ItemModels.special(identifier, new BannerModelRenderer.Unbaked(color)));
    }

    private void registerBanners() {
        this.registerBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, DyeColor.WHITE);
        this.registerBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, DyeColor.ORANGE);
        this.registerBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, DyeColor.MAGENTA);
        this.registerBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, DyeColor.LIGHT_BLUE);
        this.registerBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, DyeColor.YELLOW);
        this.registerBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, DyeColor.LIME);
        this.registerBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, DyeColor.PINK);
        this.registerBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, DyeColor.GRAY);
        this.registerBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, DyeColor.LIGHT_GRAY);
        this.registerBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, DyeColor.CYAN);
        this.registerBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, DyeColor.PURPLE);
        this.registerBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, DyeColor.BLUE);
        this.registerBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, DyeColor.BROWN);
        this.registerBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, DyeColor.GREEN);
        this.registerBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, DyeColor.RED);
        this.registerBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, DyeColor.BLACK);
    }

    public final void registerChest(Block block, Block particleSource, Identifier texture, boolean christmas) {
        this.registerBuiltinWithParticle(block, particleSource);
        Item item = block.asItem();
        Identifier identifier = Models.TEMPLATE_CHEST.upload(item, TextureMap.particle(particleSource), this.modelCollector);
        ItemModel.Unbaked unbaked = ItemModels.special(identifier, new ChestModelRenderer.Unbaked(texture));
        if (christmas) {
            ItemModel.Unbaked unbaked2 = ItemModels.special(identifier, new ChestModelRenderer.Unbaked(ChestModelRenderer.CHRISTMAS_ID));
            this.itemModelOutput.accept(item, ItemModels.christmasSelect(unbaked2, unbaked));
        } else {
            this.itemModelOutput.accept(item, unbaked);
        }
    }

    private void registerChests() {
        this.registerChest(Blocks.CHEST, Blocks.OAK_PLANKS, ChestModelRenderer.NORMAL_ID, true);
        this.registerChest(Blocks.TRAPPED_CHEST, Blocks.OAK_PLANKS, ChestModelRenderer.TRAPPED_ID, true);
        this.registerChest(Blocks.ENDER_CHEST, Blocks.OBSIDIAN, ChestModelRenderer.ENDER_ID, false);
    }

    public final void registerBed(Block block, Block particleSource, DyeColor color) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getMinecraftNamespacedBlock("bed"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
        Item item = block.asItem();
        Identifier identifier = Models.TEMPLATE_BED.upload(ModelIds.getItemModelId(item), TextureMap.particle(particleSource), this.modelCollector);
        this.itemModelOutput.accept(item, ItemModels.special(identifier, new BedModelRenderer.Unbaked(color)));
    }

    private void registerBeds() {
        this.registerBed(Blocks.WHITE_BED, Blocks.WHITE_WOOL, DyeColor.WHITE);
        this.registerBed(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL, DyeColor.ORANGE);
        this.registerBed(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL, DyeColor.MAGENTA);
        this.registerBed(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE);
        this.registerBed(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL, DyeColor.YELLOW);
        this.registerBed(Blocks.LIME_BED, Blocks.LIME_WOOL, DyeColor.LIME);
        this.registerBed(Blocks.PINK_BED, Blocks.PINK_WOOL, DyeColor.PINK);
        this.registerBed(Blocks.GRAY_BED, Blocks.GRAY_WOOL, DyeColor.GRAY);
        this.registerBed(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY);
        this.registerBed(Blocks.CYAN_BED, Blocks.CYAN_WOOL, DyeColor.CYAN);
        this.registerBed(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL, DyeColor.PURPLE);
        this.registerBed(Blocks.BLUE_BED, Blocks.BLUE_WOOL, DyeColor.BLUE);
        this.registerBed(Blocks.BROWN_BED, Blocks.BROWN_WOOL, DyeColor.BROWN);
        this.registerBed(Blocks.GREEN_BED, Blocks.GREEN_WOOL, DyeColor.GREEN);
        this.registerBed(Blocks.RED_BED, Blocks.RED_WOOL, DyeColor.RED);
        this.registerBed(Blocks.BLACK_BED, Blocks.BLACK_WOOL, DyeColor.BLACK);
    }

    public final void registerSpecialItemModel(Block block, SpecialModelRenderer.Unbaked specialModel) {
        Item item = block.asItem();
        Identifier identifier = ModelIds.getItemModelId(item);
        this.itemModelOutput.accept(item, ItemModels.special(identifier, specialModel));
    }

    public void register() {
        BlockFamilies.getFamilies().filter(BlockFamily::shouldGenerateModels).forEach(family -> this.registerCubeAllModelTexturePool(family.getBaseBlock()).family((BlockFamily)family));
        this.registerCubeAllModelTexturePool(Blocks.CUT_COPPER).family(BlockFamilies.CUT_COPPER).parented(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER).parented(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER).family(BlockFamilies.WAXED_CUT_COPPER);
        this.registerCubeAllModelTexturePool(Blocks.EXPOSED_CUT_COPPER).family(BlockFamilies.EXPOSED_CUT_COPPER).parented(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER).parented(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER).family(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
        this.registerCubeAllModelTexturePool(Blocks.WEATHERED_CUT_COPPER).family(BlockFamilies.WEATHERED_CUT_COPPER).parented(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER).parented(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER).family(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
        this.registerCubeAllModelTexturePool(Blocks.OXIDIZED_CUT_COPPER).family(BlockFamilies.OXIDIZED_CUT_COPPER).parented(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).parented(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).family(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
        this.registerCopperBulb(Blocks.COPPER_BULB);
        this.registerCopperBulb(Blocks.EXPOSED_COPPER_BULB);
        this.registerCopperBulb(Blocks.WEATHERED_COPPER_BULB);
        this.registerCopperBulb(Blocks.OXIDIZED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB);
        this.registerSimpleState(Blocks.AIR);
        this.registerStateWithModelReference(Blocks.CAVE_AIR, Blocks.AIR);
        this.registerStateWithModelReference(Blocks.VOID_AIR, Blocks.AIR);
        this.registerSimpleState(Blocks.BEACON);
        this.registerSimpleState(Blocks.CACTUS);
        this.registerStateWithModelReference(Blocks.BUBBLE_COLUMN, Blocks.WATER);
        this.registerSimpleState(Blocks.DRAGON_EGG);
        this.registerSimpleState(Blocks.DRIED_KELP_BLOCK);
        this.registerSimpleState(Blocks.ENCHANTING_TABLE);
        this.registerSimpleState(Blocks.FLOWER_POT);
        this.registerItemModel(Items.FLOWER_POT);
        this.registerSimpleState(Blocks.HONEY_BLOCK);
        this.registerSimpleState(Blocks.WATER);
        this.registerSimpleState(Blocks.LAVA);
        this.registerSimpleState(Blocks.SLIME_BLOCK);
        this.registerItemModel(Items.CHAIN);
        this.registerCandle(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
        this.registerCandle(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
        this.registerCandle(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
        this.registerCandle(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
        this.registerCandle(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
        this.registerCandle(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
        this.registerCandle(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
        this.registerCandle(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
        this.registerCandle(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
        this.registerCandle(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
        this.registerCandle(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
        this.registerCandle(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
        this.registerCandle(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
        this.registerCandle(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
        this.registerCandle(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
        this.registerCandle(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
        this.registerCandle(Blocks.CANDLE, Blocks.CANDLE_CAKE);
        this.registerSimpleState(Blocks.POTTED_BAMBOO);
        this.registerSimpleState(Blocks.POTTED_CACTUS);
        this.registerSimpleState(Blocks.POWDER_SNOW);
        this.registerSimpleState(Blocks.SPORE_BLOSSOM);
        this.registerAzalea(Blocks.AZALEA);
        this.registerAzalea(Blocks.FLOWERING_AZALEA);
        this.registerPottedAzaleaBush(Blocks.POTTED_AZALEA_BUSH);
        this.registerPottedAzaleaBush(Blocks.POTTED_FLOWERING_AZALEA_BUSH);
        this.registerCaveVines();
        this.registerWoolAndCarpet(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
        this.registerPaleMossCarpet(Blocks.PALE_MOSS_CARPET);
        this.registerHangingMoss(Blocks.PALE_HANGING_MOSS);
        this.registerSimpleCubeAll(Blocks.PALE_MOSS_BLOCK);
        this.registerFlowerbed(Blocks.PINK_PETALS);
        this.registerFlowerbed(Blocks.WILDFLOWERS);
        this.registerLeafLitter(Blocks.LEAF_LITTER);
        this.registerTintableCrossBlockState(Blocks.FIREFLY_BUSH, CrossType.EMISSIVE_NOT_TINTED);
        this.registerItemModel(Items.FIREFLY_BUSH);
        this.registerBuiltinWithParticle(Blocks.BARRIER, Items.BARRIER);
        this.registerItemModel(Items.BARRIER);
        this.registerLightBlock();
        this.registerBuiltinWithParticle(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
        this.registerItemModel(Items.STRUCTURE_VOID);
        this.registerBuiltinWithParticle(Blocks.MOVING_PISTON, TextureMap.getSubId(Blocks.PISTON, "_side"));
        this.registerSimpleCubeAll(Blocks.COAL_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_COAL_ORE);
        this.registerSimpleCubeAll(Blocks.COAL_BLOCK);
        this.registerSimpleCubeAll(Blocks.DIAMOND_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_DIAMOND_ORE);
        this.registerSimpleCubeAll(Blocks.DIAMOND_BLOCK);
        this.registerSimpleCubeAll(Blocks.EMERALD_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_EMERALD_ORE);
        this.registerSimpleCubeAll(Blocks.EMERALD_BLOCK);
        this.registerSimpleCubeAll(Blocks.GOLD_ORE);
        this.registerSimpleCubeAll(Blocks.NETHER_GOLD_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_GOLD_ORE);
        this.registerSimpleCubeAll(Blocks.GOLD_BLOCK);
        this.registerSimpleCubeAll(Blocks.IRON_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_IRON_ORE);
        this.registerSimpleCubeAll(Blocks.IRON_BLOCK);
        this.registerSingleton(Blocks.ANCIENT_DEBRIS, TexturedModel.CUBE_COLUMN);
        this.registerSimpleCubeAll(Blocks.NETHERITE_BLOCK);
        this.registerSimpleCubeAll(Blocks.LAPIS_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_LAPIS_ORE);
        this.registerSimpleCubeAll(Blocks.LAPIS_BLOCK);
        this.registerSimpleCubeAll(Blocks.RESIN_BLOCK);
        this.registerSimpleCubeAll(Blocks.NETHER_QUARTZ_ORE);
        this.registerSimpleCubeAll(Blocks.REDSTONE_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_REDSTONE_ORE);
        this.registerSimpleCubeAll(Blocks.REDSTONE_BLOCK);
        this.registerSimpleCubeAll(Blocks.GILDED_BLACKSTONE);
        this.registerSimpleCubeAll(Blocks.BLUE_ICE);
        this.registerSimpleCubeAll(Blocks.CLAY);
        this.registerSimpleCubeAll(Blocks.COARSE_DIRT);
        this.registerSimpleCubeAll(Blocks.CRYING_OBSIDIAN);
        this.registerSimpleCubeAll(Blocks.END_STONE);
        this.registerSimpleCubeAll(Blocks.GLOWSTONE);
        this.registerSimpleCubeAll(Blocks.GRAVEL);
        this.registerSimpleCubeAll(Blocks.HONEYCOMB_BLOCK);
        this.registerSimpleCubeAll(Blocks.ICE);
        this.registerSingleton(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
        this.registerSingleton(Blocks.LODESTONE, TexturedModel.CUBE_COLUMN);
        this.registerSingleton(Blocks.MELON, TexturedModel.CUBE_COLUMN);
        this.registerSimpleState(Blocks.MANGROVE_ROOTS);
        this.registerSimpleState(Blocks.POTTED_MANGROVE_PROPAGULE);
        this.registerSimpleCubeAll(Blocks.NETHER_WART_BLOCK);
        this.registerSimpleCubeAll(Blocks.NOTE_BLOCK);
        this.registerSimpleCubeAll(Blocks.PACKED_ICE);
        this.registerSimpleCubeAll(Blocks.OBSIDIAN);
        this.registerSimpleCubeAll(Blocks.QUARTZ_BRICKS);
        this.registerSimpleCubeAll(Blocks.SEA_LANTERN);
        this.registerSimpleCubeAll(Blocks.SHROOMLIGHT);
        this.registerSimpleCubeAll(Blocks.SOUL_SAND);
        this.registerSimpleCubeAll(Blocks.SOUL_SOIL);
        this.registerSingleton(Blocks.SPAWNER, TexturedModel.CUBE_ALL_INNER_FACES);
        this.registerCreakingHeart(Blocks.CREAKING_HEART);
        this.registerSimpleCubeAll(Blocks.SPONGE);
        this.registerSingleton(Blocks.SEAGRASS, TexturedModel.TEMPLATE_SEAGRASS);
        this.registerItemModel(Items.SEAGRASS);
        this.registerSingleton(Blocks.TNT, TexturedModel.CUBE_BOTTOM_TOP);
        this.registerSingleton(Blocks.TARGET, TexturedModel.CUBE_COLUMN);
        this.registerSimpleCubeAll(Blocks.WARPED_WART_BLOCK);
        this.registerSimpleCubeAll(Blocks.WET_SPONGE);
        this.registerSimpleCubeAll(Blocks.AMETHYST_BLOCK);
        this.registerSimpleCubeAll(Blocks.BUDDING_AMETHYST);
        this.registerSimpleCubeAll(Blocks.CALCITE);
        this.registerSimpleCubeAll(Blocks.DRIPSTONE_BLOCK);
        this.registerSimpleCubeAll(Blocks.RAW_IRON_BLOCK);
        this.registerSimpleCubeAll(Blocks.RAW_COPPER_BLOCK);
        this.registerSimpleCubeAll(Blocks.RAW_GOLD_BLOCK);
        this.registerMirrorable(Blocks.SCULK);
        this.registerSimpleState(Blocks.HEAVY_CORE);
        this.registerPetrifiedOakSlab();
        this.registerSimpleCubeAll(Blocks.COPPER_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_COPPER_ORE);
        this.registerSimpleCubeAll(Blocks.COPPER_BLOCK);
        this.registerSimpleCubeAll(Blocks.EXPOSED_COPPER);
        this.registerSimpleCubeAll(Blocks.WEATHERED_COPPER);
        this.registerSimpleCubeAll(Blocks.OXIDIZED_COPPER);
        this.registerParented(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
        this.registerParented(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
        this.registerParented(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
        this.registerParented(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
        this.registerDoor(Blocks.COPPER_DOOR);
        this.registerDoor(Blocks.EXPOSED_COPPER_DOOR);
        this.registerDoor(Blocks.WEATHERED_COPPER_DOOR);
        this.registerDoor(Blocks.OXIDIZED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR);
        this.registerTrapdoor(Blocks.COPPER_TRAPDOOR);
        this.registerTrapdoor(Blocks.EXPOSED_COPPER_TRAPDOOR);
        this.registerTrapdoor(Blocks.WEATHERED_COPPER_TRAPDOOR);
        this.registerTrapdoor(Blocks.OXIDIZED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.COPPER_TRAPDOOR, Blocks.WAXED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.OXIDIZED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        this.registerSimpleCubeAll(Blocks.COPPER_GRATE);
        this.registerSimpleCubeAll(Blocks.EXPOSED_COPPER_GRATE);
        this.registerSimpleCubeAll(Blocks.WEATHERED_COPPER_GRATE);
        this.registerSimpleCubeAll(Blocks.OXIDIZED_COPPER_GRATE);
        this.registerParented(Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE);
        this.registerParented(Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE);
        this.registerParented(Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE);
        this.registerParented(Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
        this.registerWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
        this.registerWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
        this.registerAmethysts();
        this.registerBookshelf();
        this.registerChiseledBookshelf();
        this.registerBrewingStand();
        this.registerCake();
        this.registerCampfire(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
        this.registerCartographyTable();
        this.registerCauldrons();
        this.registerChorusFlower();
        this.registerChorusPlant();
        this.registerComposter();
        this.registerDaylightDetector();
        this.registerEndPortalFrame();
        this.registerRod(Blocks.END_ROD);
        this.registerLightningRod();
        this.registerFarmland();
        this.registerFire();
        this.registerSoulFire();
        this.registerFrostedIce();
        this.registerTopSoils();
        this.registerCocoa();
        this.registerDirtPath();
        this.registerGrindstone();
        this.registerHopper();
        this.registerIronBars();
        this.registerLever();
        this.registerLilyPad();
        this.registerNetherPortal();
        this.registerNetherrack();
        this.registerObserver();
        this.registerPistons();
        this.registerPistonHead();
        this.registerScaffolding();
        this.registerRedstoneTorch();
        this.registerRedstoneLamp();
        this.registerRepeater();
        this.registerSeaPickle();
        this.registerSmithingTable();
        this.registerSnows();
        this.registerStonecutter();
        this.registerStructureBlock();
        this.registerSweetBerryBush();
        this.registerTestBlock();
        this.registerSimpleCubeAll(Blocks.TEST_INSTANCE_BLOCK);
        this.registerTripwire();
        this.registerTripwireHook();
        this.registerTurtleEgg();
        this.registerSnifferEgg();
        this.registerDriedGhast();
        this.registerVine();
        this.registerMultifaceBlock(Blocks.GLOW_LICHEN);
        this.registerMultifaceBlock(Blocks.SCULK_VEIN);
        this.registerMultifaceBlock(Blocks.RESIN_CLUMP, Items.RESIN_CLUMP);
        this.registerMagmaBlock();
        this.registerJigsaw();
        this.registerSculkSensor();
        this.registerCalibratedSculkSensor();
        this.registerSculkShrieker();
        this.registerFrogspawn();
        this.registerMangrovePropagule();
        this.registerMuddyMangroveRoots();
        this.registerTrialSpawner();
        this.registerVault();
        this.registerNorthDefaultHorizontalRotation(Blocks.LADDER);
        this.registerItemModel(Blocks.LADDER);
        this.registerNorthDefaultHorizontalRotation(Blocks.LECTERN);
        this.registerBigDripleaf();
        this.registerNorthDefaultHorizontalRotation(Blocks.BIG_DRIPLEAF_STEM);
        this.registerTorch(Blocks.TORCH, Blocks.WALL_TORCH);
        this.registerTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
        this.registerCubeWithCustomTextures(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMap::frontSideWithCustomBottom);
        this.registerCubeWithCustomTextures(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMap::frontTopSide);
        this.registerNetherrackBottomCustomTop(Blocks.CRIMSON_NYLIUM);
        this.registerNetherrackBottomCustomTop(Blocks.WARPED_NYLIUM);
        this.registerDispenserLikeOrientable(Blocks.DISPENSER);
        this.registerDispenserLikeOrientable(Blocks.DROPPER);
        this.registerCrafter();
        this.registerLantern(Blocks.LANTERN);
        this.registerLantern(Blocks.SOUL_LANTERN);
        this.registerAxisRotated(Blocks.CHAIN, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(Blocks.CHAIN)));
        this.registerAxisRotated(Blocks.BASALT, TexturedModel.CUBE_COLUMN);
        this.registerAxisRotated(Blocks.POLISHED_BASALT, TexturedModel.CUBE_COLUMN);
        this.registerSimpleCubeAll(Blocks.SMOOTH_BASALT);
        this.registerAxisRotated(Blocks.BONE_BLOCK, TexturedModel.CUBE_COLUMN);
        this.registerRotatable(Blocks.DIRT);
        this.registerRotatable(Blocks.ROOTED_DIRT);
        this.registerRotatable(Blocks.SAND);
        this.registerBrushableBlock(Blocks.SUSPICIOUS_SAND);
        this.registerBrushableBlock(Blocks.SUSPICIOUS_GRAVEL);
        this.registerRotatable(Blocks.RED_SAND);
        this.registerMirrorable(Blocks.BEDROCK);
        this.registerSingleton(Blocks.REINFORCED_DEEPSLATE, TexturedModel.CUBE_BOTTOM_TOP);
        this.registerAxisRotated(Blocks.HAY_BLOCK, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.PURPUR_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.QUARTZ_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.OCHRE_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.VERDANT_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.PEARLESCENT_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerNorthDefaultHorizontalRotated(Blocks.LOOM, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        this.registerPumpkins();
        this.registerBeehive(Blocks.BEE_NEST, TextureMap::sideFrontTopBottom);
        this.registerBeehive(Blocks.BEEHIVE, TextureMap::sideFrontEnd);
        this.registerCrop(Blocks.BEETROOTS, Properties.AGE_3, 0, 1, 2, 3);
        this.registerCrop(Blocks.CARROTS, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.registerCrop(Blocks.NETHER_WART, Properties.AGE_3, 0, 1, 1, 2);
        this.registerCrop(Blocks.POTATOES, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.registerCrop(Blocks.WHEAT, Properties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
        this.registerTintableCrossBlockStateWithStages(Blocks.TORCHFLOWER_CROP, CrossType.NOT_TINTED, Properties.AGE_1, 0, 1);
        this.registerPitcherCrop();
        this.registerPitcherPlant();
        this.registerBanners();
        this.registerBeds();
        this.registerSkulls();
        this.registerChests();
        this.registerShulkerBox(Blocks.SHULKER_BOX, null);
        this.registerShulkerBox(Blocks.WHITE_SHULKER_BOX, DyeColor.WHITE);
        this.registerShulkerBox(Blocks.ORANGE_SHULKER_BOX, DyeColor.ORANGE);
        this.registerShulkerBox(Blocks.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA);
        this.registerShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE);
        this.registerShulkerBox(Blocks.YELLOW_SHULKER_BOX, DyeColor.YELLOW);
        this.registerShulkerBox(Blocks.LIME_SHULKER_BOX, DyeColor.LIME);
        this.registerShulkerBox(Blocks.PINK_SHULKER_BOX, DyeColor.PINK);
        this.registerShulkerBox(Blocks.GRAY_SHULKER_BOX, DyeColor.GRAY);
        this.registerShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY);
        this.registerShulkerBox(Blocks.CYAN_SHULKER_BOX, DyeColor.CYAN);
        this.registerShulkerBox(Blocks.PURPLE_SHULKER_BOX, DyeColor.PURPLE);
        this.registerShulkerBox(Blocks.BLUE_SHULKER_BOX, DyeColor.BLUE);
        this.registerShulkerBox(Blocks.BROWN_SHULKER_BOX, DyeColor.BROWN);
        this.registerShulkerBox(Blocks.GREEN_SHULKER_BOX, DyeColor.GREEN);
        this.registerShulkerBox(Blocks.RED_SHULKER_BOX, DyeColor.RED);
        this.registerShulkerBox(Blocks.BLACK_SHULKER_BOX, DyeColor.BLACK);
        this.registerBuiltin(Blocks.CONDUIT);
        this.registerSpecialItemModel(Blocks.CONDUIT, new ConduitModelRenderer.Unbaked());
        this.registerBuiltinWithParticle(Blocks.DECORATED_POT, Blocks.TERRACOTTA);
        this.registerSpecialItemModel(Blocks.DECORATED_POT, new DecoratedPotModelRenderer.Unbaked());
        this.registerBuiltinWithParticle(Blocks.END_PORTAL, Blocks.OBSIDIAN);
        this.registerBuiltinWithParticle(Blocks.END_GATEWAY, Blocks.OBSIDIAN);
        this.registerSimpleCubeAll(Blocks.AZALEA_LEAVES);
        this.registerSimpleCubeAll(Blocks.FLOWERING_AZALEA_LEAVES);
        this.registerSimpleCubeAll(Blocks.WHITE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.ORANGE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.MAGENTA_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.YELLOW_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIME_CONCRETE);
        this.registerSimpleCubeAll(Blocks.PINK_CONCRETE);
        this.registerSimpleCubeAll(Blocks.GRAY_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_CONCRETE);
        this.registerSimpleCubeAll(Blocks.CYAN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.PURPLE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BLUE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BROWN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.GREEN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.RED_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BLACK_CONCRETE);
        this.registerRandomHorizontalRotations(TexturedModel.CUBE_ALL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
        this.registerSimpleCubeAll(Blocks.TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.WHITE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.ORANGE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.MAGENTA_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.YELLOW_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIME_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.PINK_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.GRAY_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.CYAN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.PURPLE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BLUE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BROWN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.GREEN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.RED_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BLACK_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.TINTED_GLASS);
        this.registerGlassAndPane(Blocks.GLASS, Blocks.GLASS_PANE);
        this.registerGlassAndPane(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
        this.registerSouthDefaultHorizontalFacing(TexturedModel.TEMPLATE_GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        this.registerWoolAndCarpet(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
        this.registerWoolAndCarpet(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
        this.registerWoolAndCarpet(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
        this.registerWoolAndCarpet(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
        this.registerWoolAndCarpet(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
        this.registerWoolAndCarpet(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
        this.registerWoolAndCarpet(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
        this.registerWoolAndCarpet(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
        this.registerWoolAndCarpet(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
        this.registerWoolAndCarpet(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
        this.registerWoolAndCarpet(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
        this.registerWoolAndCarpet(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
        this.registerWoolAndCarpet(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
        this.registerWoolAndCarpet(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
        this.registerWoolAndCarpet(Blocks.RED_WOOL, Blocks.RED_CARPET);
        this.registerWoolAndCarpet(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
        this.registerSimpleCubeAll(Blocks.MUD);
        this.registerSimpleCubeAll(Blocks.PACKED_MUD);
        this.registerFlowerPotPlant(Blocks.FERN, Blocks.POTTED_FERN, CrossType.TINTED);
        this.registerGrassTinted(Blocks.FERN);
        this.registerFlowerPotPlantAndItem(Blocks.DANDELION, Blocks.POTTED_DANDELION, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.POPPY, Blocks.POTTED_POPPY, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.OPEN_EYEBLOSSOM, Blocks.POTTED_OPEN_EYEBLOSSOM, CrossType.EMISSIVE_NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.CLOSED_EYEBLOSSOM, Blocks.POTTED_CLOSED_EYEBLOSSOM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER, CrossType.NOT_TINTED);
        this.registerPointedDripstone();
        this.registerMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        this.registerMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
        this.registerMushroomBlock(Blocks.MUSHROOM_STEM);
        this.registerTintableCrossBlockState(Blocks.SHORT_GRASS, CrossType.TINTED);
        this.registerGrassTinted(Blocks.SHORT_GRASS);
        this.registerTintableCross(Blocks.SHORT_DRY_GRASS, CrossType.NOT_TINTED);
        this.registerTintableCross(Blocks.TALL_DRY_GRASS, CrossType.NOT_TINTED);
        this.registerTintableCrossBlockState(Blocks.BUSH, CrossType.TINTED);
        this.registerGrassTinted(Blocks.BUSH);
        this.registerTintableCrossBlockState(Blocks.SUGAR_CANE, CrossType.TINTED);
        this.registerItemModel(Items.SUGAR_CANE);
        this.registerPlantPart(Blocks.KELP, Blocks.KELP_PLANT, CrossType.NOT_TINTED);
        this.registerItemModel(Items.KELP);
        this.registerTintableCrossBlockState(Blocks.HANGING_ROOTS, CrossType.NOT_TINTED);
        this.registerPlantPart(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, CrossType.NOT_TINTED);
        this.registerPlantPart(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, CrossType.NOT_TINTED);
        this.registerItemModel(Blocks.WEEPING_VINES, "_plant");
        this.registerItemModel(Blocks.TWISTING_VINES, "_plant");
        this.registerTintableCross(Blocks.BAMBOO_SAPLING, CrossType.TINTED, TextureMap.cross(TextureMap.getSubId(Blocks.BAMBOO, "_stage0")));
        this.registerBamboo();
        this.registerTintableCross(Blocks.CACTUS_FLOWER, CrossType.NOT_TINTED);
        this.registerTintableCross(Blocks.COBWEB, CrossType.NOT_TINTED);
        this.registerDoubleBlockAndItem(Blocks.LILAC, CrossType.NOT_TINTED);
        this.registerDoubleBlockAndItem(Blocks.ROSE_BUSH, CrossType.NOT_TINTED);
        this.registerDoubleBlockAndItem(Blocks.PEONY, CrossType.NOT_TINTED);
        this.registerGrassTintedDoubleBlockAndItem(Blocks.TALL_GRASS);
        this.registerGrassTintedDoubleBlockAndItem(Blocks.LARGE_FERN);
        this.registerSunflower();
        this.registerTallSeagrass();
        this.registerSmallDripleaf();
        this.registerCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
        this.registerCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
        this.registerGourd(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
        this.registerGourd(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        this.createLogTexturePool(Blocks.MANGROVE_LOG).log(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_MANGROVE_LOG).log(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
        this.registerTintedBlockAndItem(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES, -7158200);
        this.createLogTexturePool(Blocks.ACACIA_LOG).log(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_ACACIA_LOG).log(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.CHERRY_LOG).uvLockedLog(Blocks.CHERRY_LOG).wood(Blocks.CHERRY_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_CHERRY_LOG).uvLockedLog(Blocks.STRIPPED_CHERRY_LOG).wood(Blocks.STRIPPED_CHERRY_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.CHERRY_SAPLING, Blocks.POTTED_CHERRY_SAPLING, CrossType.NOT_TINTED);
        this.registerSingleton(Blocks.CHERRY_LEAVES, TexturedModel.LEAVES);
        this.createLogTexturePool(Blocks.BIRCH_LOG).log(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_BIRCH_LOG).log(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES, -8345771);
        this.createLogTexturePool(Blocks.OAK_LOG).log(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_OAK_LOG).log(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.OAK_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.SPRUCE_LOG).log(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_SPRUCE_LOG).log(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES, -10380959);
        this.createLogTexturePool(Blocks.DARK_OAK_LOG).log(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_DARK_OAK_LOG).log(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.PALE_OAK_LOG).log(Blocks.PALE_OAK_LOG).wood(Blocks.PALE_OAK_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_PALE_OAK_LOG).log(Blocks.STRIPPED_PALE_OAK_LOG).wood(Blocks.STRIPPED_PALE_OAK_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_PALE_OAK_LOG, Blocks.PALE_OAK_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.PALE_OAK_SAPLING, Blocks.POTTED_PALE_OAK_SAPLING, CrossType.NOT_TINTED);
        this.registerSingleton(Blocks.PALE_OAK_LEAVES, TexturedModel.LEAVES);
        this.createLogTexturePool(Blocks.JUNGLE_LOG).log(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_JUNGLE_LOG).log(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.CRIMSON_STEM).stem(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
        this.createLogTexturePool(Blocks.STRIPPED_CRIMSON_STEM).stem(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.registerHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, CrossType.NOT_TINTED);
        this.registerRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
        this.createLogTexturePool(Blocks.WARPED_STEM).stem(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
        this.createLogTexturePool(Blocks.STRIPPED_WARPED_STEM).stem(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
        this.registerHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, CrossType.NOT_TINTED);
        this.registerRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
        this.createLogTexturePool(Blocks.BAMBOO_BLOCK).uvLockedLog(Blocks.BAMBOO_BLOCK);
        this.createLogTexturePool(Blocks.STRIPPED_BAMBOO_BLOCK).uvLockedLog(Blocks.STRIPPED_BAMBOO_BLOCK);
        this.registerHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        this.registerTintableCrossBlockState(Blocks.NETHER_SPROUTS, CrossType.NOT_TINTED);
        this.registerItemModel(Items.NETHER_SPROUTS);
        this.registerDoor(Blocks.IRON_DOOR);
        this.registerTrapdoor(Blocks.IRON_TRAPDOOR);
        this.registerSmoothStone();
        this.registerTurnableRail(Blocks.RAIL);
        this.registerStraightRail(Blocks.POWERED_RAIL);
        this.registerStraightRail(Blocks.DETECTOR_RAIL);
        this.registerStraightRail(Blocks.ACTIVATOR_RAIL);
        this.registerComparator();
        this.registerCommandBlock(Blocks.COMMAND_BLOCK);
        this.registerCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
        this.registerCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
        this.registerAnvil(Blocks.ANVIL);
        this.registerAnvil(Blocks.CHIPPED_ANVIL);
        this.registerAnvil(Blocks.DAMAGED_ANVIL);
        this.registerBarrel();
        this.registerBell();
        this.registerCooker(Blocks.FURNACE, TexturedModel.ORIENTABLE);
        this.registerCooker(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE);
        this.registerCooker(Blocks.SMOKER, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        this.registerRedstone();
        this.registerRespawnAnchor();
        this.registerSculkCatalyst();
        this.registerParented(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
        this.registerParented(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
        this.registerParented(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        this.registerParented(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
        this.registerInfestedStone();
        this.registerParented(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
        this.registerInfestedDeepslate();
    }

    private void registerLightBlock() {
        ItemModel.Unbaked unbaked = ItemModels.basic(this.uploadItemModel(Items.LIGHT));
        HashMap<Integer, ItemModel.Unbaked> map = new HashMap<Integer, ItemModel.Unbaked>(16);
        BlockStateVariantMap.SingleProperty<WeightedVariant, Integer> singleProperty = BlockStateVariantMap.models(Properties.LEVEL_15);
        for (int i = 0; i <= 15; ++i) {
            String string = String.format(Locale.ROOT, "_%02d", i);
            Identifier identifier = TextureMap.getSubId(Items.LIGHT, string);
            singleProperty.register(i, BlockStateModelGenerator.createWeightedVariant(Models.PARTICLE.upload(Blocks.LIGHT, string, TextureMap.particle(identifier), this.modelCollector)));
            ItemModel.Unbaked unbaked2 = ItemModels.basic(Models.GENERATED.upload(ModelIds.getItemSubModelId(Items.LIGHT, string), TextureMap.layer0(identifier), this.modelCollector));
            map.put(i, unbaked2);
        }
        this.itemModelOutput.accept(Items.LIGHT, ItemModels.select(LightBlock.LEVEL_15, unbaked, map));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(Blocks.LIGHT).with(singleProperty));
    }

    public final void registerCandle(Block candle, Block cake) {
        this.registerItemModel(candle.asItem());
        TextureMap textureMap = TextureMap.all(TextureMap.getId(candle));
        TextureMap textureMap2 = TextureMap.all(TextureMap.getSubId(candle, "_lit"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CANDLE.upload(candle, "_one_candle", textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TWO_CANDLES.upload(candle, "_two_candles", textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_THREE_CANDLES.upload(candle, "_three_candles", textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FOUR_CANDLES.upload(candle, "_four_candles", textureMap, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CANDLE.upload(candle, "_one_candle_lit", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_TWO_CANDLES.upload(candle, "_two_candles_lit", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_THREE_CANDLES.upload(candle, "_three_candles_lit", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FOUR_CANDLES.upload(candle, "_four_candles_lit", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(candle).with(BlockStateVariantMap.models(Properties.CANDLES, Properties.LIT).register(1, false, weightedVariant).register(2, false, weightedVariant2).register(3, false, weightedVariant3).register(4, false, weightedVariant4).register(1, true, weightedVariant5).register(2, true, weightedVariant6).register(3, true, weightedVariant7).register(4, true, weightedVariant8)));
        WeightedVariant weightedVariant9 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAKE_WITH_CANDLE.upload(cake, TextureMap.candleCake(candle, false), this.modelCollector));
        WeightedVariant weightedVariant10 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CAKE_WITH_CANDLE.upload(cake, "_lit", TextureMap.candleCake(candle, true), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(cake).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, weightedVariant10, weightedVariant9)));
    }

    private WeightedVariant method_67830(int[] is, Int2ObjectMap int2ObjectMap, Block block, Integer age) {
        int i = is[age];
        return BlockStateModelGenerator.createWeightedVariant((Identifier)int2ObjectMap.computeIfAbsent(i, stage -> this.createSubModel(block, "_stage" + stage, Models.CROP, TextureMap::crop)));
    }

    @Environment(value=EnvType.CLIENT)
    public class BlockTexturePool {
        final private TextureMap textures;
        final private Map<Model, Identifier> knownModels = new HashMap<Model, Identifier>();
        @Nullable
        private BlockFamily family;
        @Nullable
        private ModelVariant baseModelId;
        final private Set<Block> children = new HashSet<Block>();

        public BlockTexturePool(TextureMap textures) {
            this.textures = textures;
        }

        public BlockTexturePool base(Block block, Model model) {
            this.baseModelId = BlockStateModelGenerator.createModelVariant(model.upload(block, this.textures, BlockStateModelGenerator.this.modelCollector));
            if (BASE_WITH_CUSTOM_GENERATOR.containsKey(block)) {
                BlockStateModelGenerator.this.blockStateCollector.accept(BASE_WITH_CUSTOM_GENERATOR.get(block).create(block, this.baseModelId, this.textures, BlockStateModelGenerator.this.modelCollector));
            } else {
                BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(this.baseModelId)));
            }
            return this;
        }

        public BlockTexturePool parented(Block parent, Block child) {
            Identifier identifier = ModelIds.getBlockModelId(parent);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(child, BlockStateModelGenerator.createWeightedVariant(identifier)));
            BlockStateModelGenerator.this.itemModelOutput.acceptAlias(parent.asItem(), child.asItem());
            this.children.add(child);
            return this;
        }

        public BlockTexturePool button(Block buttonBlock) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.BUTTON.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.BUTTON_PRESSED.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(buttonBlock, weightedVariant, weightedVariant2));
            Identifier identifier = Models.BUTTON_INVENTORY.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(buttonBlock, identifier);
            return this;
        }

        public BlockTexturePool wall(Block wallBlock) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_WALL_POST.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_WALL_SIDE.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_WALL_SIDE_TALL.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(wallBlock, weightedVariant, weightedVariant2, weightedVariant3));
            Identifier identifier = Models.WALL_INVENTORY.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(wallBlock, identifier);
            return this;
        }

        public BlockTexturePool customFence(Block customFenceBlock) {
            TextureMap textureMap = TextureMap.textureParticle(customFenceBlock);
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_POST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_NORTH.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_EAST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_SOUTH.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_WEST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createCustomFenceBlockState(customFenceBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, weightedVariant5));
            Identifier identifier = Models.CUSTOM_FENCE_INVENTORY.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(customFenceBlock, identifier);
            return this;
        }

        public BlockTexturePool fence(Block fenceBlock) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.FENCE_POST.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.FENCE_SIDE.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(fenceBlock, weightedVariant, weightedVariant2));
            Identifier identifier = Models.FENCE_INVENTORY.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(fenceBlock, identifier);
            return this;
        }

        public BlockTexturePool customFenceGate(Block customFenceGateBlock) {
            TextureMap textureMap = TextureMap.textureParticle(customFenceGateBlock);
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE_OPEN.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(customFenceGateBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, false));
            return this;
        }

        public BlockTexturePool fenceGate(Block fenceGateBlock) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE_OPEN.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE_WALL.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(fenceGateBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, true));
            return this;
        }

        public BlockTexturePool pressurePlate(Block pressurePlateBlock) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PRESSURE_PLATE_UP.upload(pressurePlateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.PRESSURE_PLATE_DOWN.upload(pressurePlateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createPressurePlateBlockState(pressurePlateBlock, weightedVariant, weightedVariant2));
            return this;
        }

        public BlockTexturePool sign(Block signBlock) {
            if (this.family == null) {
                throw new IllegalStateException("Family not defined");
            }
            Block block = this.family.getVariants().get((Object)BlockFamily.Variant.WALL_SIGN);
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PARTICLE.upload(signBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(signBlock, weightedVariant));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
            BlockStateModelGenerator.this.registerItemModel(signBlock.asItem());
            return this;
        }

        public BlockTexturePool slab(Block block) {
            if (this.baseModelId == null) {
                throw new IllegalStateException("Full block not generated yet");
            }
            Identifier identifier = this.ensureModel(Models.SLAB, block);
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.ensureModel(Models.SLAB_TOP, block));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(block, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant, BlockStateModelGenerator.createWeightedVariant(this.baseModelId)));
            BlockStateModelGenerator.this.registerParentedItemModel(block, identifier);
            return this;
        }

        public BlockTexturePool stairs(Block block) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.ensureModel(Models.INNER_STAIRS, block));
            Identifier identifier = this.ensureModel(Models.STAIRS, block);
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.ensureModel(Models.OUTER_STAIRS, block));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(block, weightedVariant, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant2));
            BlockStateModelGenerator.this.registerParentedItemModel(block, identifier);
            return this;
        }

        private BlockTexturePool block(Block block) {
            TexturedModel texturedModel = TEXTURED_MODELS.getOrDefault(block, TexturedModel.CUBE_ALL.get(block));
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(texturedModel.upload(block, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
            return this;
        }

        private BlockTexturePool door(Block block) {
            BlockStateModelGenerator.this.registerDoor(block);
            return this;
        }

        private void registerTrapdoor(Block block) {
            if (UNORIENTABLE_TRAPDOORS.contains(block)) {
                BlockStateModelGenerator.this.registerTrapdoor(block);
            } else {
                BlockStateModelGenerator.this.registerOrientableTrapdoor(block);
            }
        }

        private Identifier ensureModel(Model model, Block block) {
            return this.knownModels.computeIfAbsent(model, newModel -> newModel.upload(block, this.textures, BlockStateModelGenerator.this.modelCollector));
        }

        public BlockTexturePool family(BlockFamily family) {
            this.family = family;
            family.getVariants().forEach((variant, block) -> {
                if (this.children.contains(block)) {
                    return;
                }
                BiConsumer<BlockTexturePool, Block> biConsumer = VARIANT_POOL_FUNCTIONS.get(variant);
                if (biConsumer != null) {
                    biConsumer.accept(this, (Block)block);
                }
            });
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class LogTexturePool {
        final private TextureMap textures;

        public LogTexturePool(TextureMap textures) {
            this.textures = textures;
        }

        public LogTexturePool wood(Block woodBlock) {
            TextureMap textureMap = this.textures.copyAndAdd(TextureKey.END, this.textures.getTexture(TextureKey.SIDE));
            Identifier identifier = Models.CUBE_COLUMN.upload(woodBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(woodBlock, BlockStateModelGenerator.createWeightedVariant(identifier)));
            BlockStateModelGenerator.this.registerParentedItemModel(woodBlock, identifier);
            return this;
        }

        public LogTexturePool stem(Block stemBlock) {
            Identifier identifier = Models.CUBE_COLUMN.upload(stemBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(stemBlock, BlockStateModelGenerator.createWeightedVariant(identifier)));
            BlockStateModelGenerator.this.registerParentedItemModel(stemBlock, identifier);
            return this;
        }

        public LogTexturePool log(Block logBlock) {
            Identifier identifier = Models.CUBE_COLUMN.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN_HORIZONTAL.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(logBlock, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant));
            BlockStateModelGenerator.this.registerParentedItemModel(logBlock, identifier);
            return this;
        }

        public LogTexturePool uvLockedLog(Block logBlock) {
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createUvLockedColumnBlockState(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            BlockStateModelGenerator.this.registerParentedItemModel(logBlock, Models.CUBE_COLUMN.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class CrossType
    extends Enum<CrossType> {
        final static public CrossType TINTED = new CrossType(Models.TINTED_CROSS, Models.TINTED_FLOWER_POT_CROSS, false);
        final static public CrossType NOT_TINTED = new CrossType(Models.CROSS, Models.FLOWER_POT_CROSS, false);
        final static public CrossType EMISSIVE_NOT_TINTED = new CrossType(Models.CROSS_EMISSIVE, Models.FLOWER_POT_CROSS_EMISSIVE, true);
        final private Model model;
        final private Model flowerPotModel;
        final private boolean emissive;
        final static private CrossType[] field_22841;

        public static CrossType[] values() {
            return (CrossType[])field_22841.clone();
        }

        public static CrossType valueOf(String string) {
            return Enum.valueOf(CrossType.class, string);
        }

        private CrossType(Model model, Model flowerPotModel, boolean emissive) {
            this.model = model;
            this.flowerPotModel = flowerPotModel;
            this.emissive = emissive;
        }

        public Model getCrossModel() {
            return this.model;
        }

        public Model getFlowerPotCrossModel() {
            return this.flowerPotModel;
        }

        public Identifier registerItemModel(BlockStateModelGenerator modelGenerator, Block block) {
            Item item = block.asItem();
            if (this.emissive) {
                return modelGenerator.uploadTwoLayerBlockItemModel(item, block, "_emissive");
            }
            return modelGenerator.uploadBlockItemModel(item, block);
        }

        public TextureMap getTextureMap(Block block) {
            return this.emissive ? TextureMap.crossAndCrossEmissive(block) : TextureMap.cross(block);
        }

        public TextureMap getFlowerPotTextureMap(Block block) {
            return this.emissive ? TextureMap.plantAndCrossEmissive(block) : TextureMap.plant(block);
        }

        private static CrossType[] method_36939() {
            return new CrossType[]{TINTED, NOT_TINTED, EMISSIVE_NOT_TINTED};
        }

        static {
            field_22841 = CrossType.method_36939();
        }
    }

    @Environment(value=EnvType.CLIENT)
    record ChiseledBookshelfModelCacheKey(Model template, String modelSuffix) {
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface StateFactory {
        public BlockModelDefinitionCreator create(Block var1, ModelVariant var2, TextureMap var3, BiConsumer<Identifier, ModelSupplier> var4);
    }
}

