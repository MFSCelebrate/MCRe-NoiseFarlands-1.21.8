/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Set;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BlockEntityType<T extends BlockEntity>
implements FabricBlockEntityType {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public BlockEntityType<FurnaceBlockEntity> FURNACE = BlockEntityType.create("furnace", FurnaceBlockEntity::new, Blocks.FURNACE);
    final static public BlockEntityType<ChestBlockEntity> CHEST = BlockEntityType.create("chest", ChestBlockEntity::new, Blocks.CHEST);
    final static public BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = BlockEntityType.create("trapped_chest", TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST);
    final static public BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = BlockEntityType.create("ender_chest", EnderChestBlockEntity::new, Blocks.ENDER_CHEST);
    final static public BlockEntityType<JukeboxBlockEntity> JUKEBOX = BlockEntityType.create("jukebox", JukeboxBlockEntity::new, Blocks.JUKEBOX);
    final static public BlockEntityType<DispenserBlockEntity> DISPENSER = BlockEntityType.create("dispenser", DispenserBlockEntity::new, Blocks.DISPENSER);
    final static public BlockEntityType<DropperBlockEntity> DROPPER = BlockEntityType.create("dropper", DropperBlockEntity::new, Blocks.DROPPER);
    final static public BlockEntityType<SignBlockEntity> SIGN = BlockEntityType.create("sign", SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN);
    final static public BlockEntityType<HangingSignBlockEntity> HANGING_SIGN = BlockEntityType.create("hanging_sign", HangingSignBlockEntity::new, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
    final static public BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = BlockEntityType.create("mob_spawner", MobSpawnerBlockEntity::new, Blocks.SPAWNER);
    final static public BlockEntityType<CreakingHeartBlockEntity> CREAKING_HEART = BlockEntityType.create("creaking_heart", CreakingHeartBlockEntity::new, Blocks.CREAKING_HEART);
    final static public BlockEntityType<PistonBlockEntity> PISTON = BlockEntityType.create("piston", PistonBlockEntity::new, Blocks.MOVING_PISTON);
    final static public BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = BlockEntityType.create("brewing_stand", BrewingStandBlockEntity::new, Blocks.BREWING_STAND);
    final static public BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = BlockEntityType.create("enchanting_table", EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE);
    final static public BlockEntityType<EndPortalBlockEntity> END_PORTAL = BlockEntityType.create("end_portal", EndPortalBlockEntity::new, Blocks.END_PORTAL);
    final static public BlockEntityType<BeaconBlockEntity> BEACON = BlockEntityType.create("beacon", BeaconBlockEntity::new, Blocks.BEACON);
    final static public BlockEntityType<SkullBlockEntity> SKULL = BlockEntityType.create("skull", SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD);
    final static public BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = BlockEntityType.create("daylight_detector", DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR);
    final static public BlockEntityType<HopperBlockEntity> HOPPER = BlockEntityType.create("hopper", HopperBlockEntity::new, Blocks.HOPPER);
    final static public BlockEntityType<ComparatorBlockEntity> COMPARATOR = BlockEntityType.create("comparator", ComparatorBlockEntity::new, Blocks.COMPARATOR);
    final static public BlockEntityType<BannerBlockEntity> BANNER = BlockEntityType.create("banner", BannerBlockEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
    final static public BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = BlockEntityType.create("structure_block", StructureBlockBlockEntity::new, Blocks.STRUCTURE_BLOCK);
    final static public BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = BlockEntityType.create("end_gateway", EndGatewayBlockEntity::new, Blocks.END_GATEWAY);
    final static public BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = BlockEntityType.create("command_block", CommandBlockBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK);
    final static public BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = BlockEntityType.create("shulker_box", ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
    final static public BlockEntityType<BedBlockEntity> BED = BlockEntityType.create("bed", BedBlockEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED);
    final static public BlockEntityType<ConduitBlockEntity> CONDUIT = BlockEntityType.create("conduit", ConduitBlockEntity::new, Blocks.CONDUIT);
    final static public BlockEntityType<BarrelBlockEntity> BARREL = BlockEntityType.create("barrel", BarrelBlockEntity::new, Blocks.BARREL);
    final static public BlockEntityType<SmokerBlockEntity> SMOKER = BlockEntityType.create("smoker", SmokerBlockEntity::new, Blocks.SMOKER);
    final static public BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = BlockEntityType.create("blast_furnace", BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE);
    final static public BlockEntityType<LecternBlockEntity> LECTERN = BlockEntityType.create("lectern", LecternBlockEntity::new, Blocks.LECTERN);
    final static public BlockEntityType<BellBlockEntity> BELL = BlockEntityType.create("bell", BellBlockEntity::new, Blocks.BELL);
    final static public BlockEntityType<JigsawBlockEntity> JIGSAW = BlockEntityType.create("jigsaw", JigsawBlockEntity::new, Blocks.JIGSAW);
    final static public BlockEntityType<CampfireBlockEntity> CAMPFIRE = BlockEntityType.create("campfire", CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
    final static public BlockEntityType<BeehiveBlockEntity> BEEHIVE = BlockEntityType.create("beehive", BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE);
    final static public BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR = BlockEntityType.create("sculk_sensor", SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR);
    final static public BlockEntityType<CalibratedSculkSensorBlockEntity> CALIBRATED_SCULK_SENSOR = BlockEntityType.create("calibrated_sculk_sensor", CalibratedSculkSensorBlockEntity::new, Blocks.CALIBRATED_SCULK_SENSOR);
    final static public BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST = BlockEntityType.create("sculk_catalyst", SculkCatalystBlockEntity::new, Blocks.SCULK_CATALYST);
    final static public BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER = BlockEntityType.create("sculk_shrieker", SculkShriekerBlockEntity::new, Blocks.SCULK_SHRIEKER);
    final static public BlockEntityType<ChiseledBookshelfBlockEntity> CHISELED_BOOKSHELF = BlockEntityType.create("chiseled_bookshelf", ChiseledBookshelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF);
    final static public BlockEntityType<BrushableBlockEntity> BRUSHABLE_BLOCK = BlockEntityType.create("brushable_block", BrushableBlockEntity::new, Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL);
    final static public BlockEntityType<DecoratedPotBlockEntity> DECORATED_POT = BlockEntityType.create("decorated_pot", DecoratedPotBlockEntity::new, Blocks.DECORATED_POT);
    final static public BlockEntityType<CrafterBlockEntity> CRAFTER = BlockEntityType.create("crafter", CrafterBlockEntity::new, Blocks.CRAFTER);
    final static public BlockEntityType<TrialSpawnerBlockEntity> TRIAL_SPAWNER = BlockEntityType.create("trial_spawner", TrialSpawnerBlockEntity::new, Blocks.TRIAL_SPAWNER);
    final static public BlockEntityType<VaultBlockEntity> VAULT = BlockEntityType.create("vault", VaultBlockEntity::new, Blocks.VAULT);
    final static public BlockEntityType<TestBlockEntity> TEST_BLOCK = BlockEntityType.create("test_block", TestBlockEntity::new, Blocks.TEST_BLOCK);
    final static public BlockEntityType<TestInstanceBlockEntity> TEST_INSTANCE_BLOCK = BlockEntityType.create("test_instance_block", TestInstanceBlockEntity::new, Blocks.TEST_INSTANCE_BLOCK);
    final static private Set<BlockEntityType<?>> POTENTIALLY_EXECUTES_COMMANDS = Set.of(COMMAND_BLOCK, LECTERN, SIGN, HANGING_SIGN, MOB_SPAWNER, TRIAL_SPAWNER);
    final private BlockEntityFactory<? extends T> factory;
    final private Set<Block> blocks;
    final private RegistryEntry.Reference<BlockEntityType<?>> registryEntry = Registries.BLOCK_ENTITY_TYPE.createEntry(this);

    @Nullable
    public static Identifier getId(BlockEntityType<?> type) {
        return Registries.BLOCK_ENTITY_TYPE.getId(type);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityFactory<? extends T> factory, Block ... blocks) {
        if (blocks.length == 0) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", (Object)id);
        }
        Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, new BlockEntityType<T>(factory, Set.of(blocks)));
    }

    private BlockEntityType(BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
        this.factory = factory;
        this.blocks = blocks;
    }

    public T instantiate(BlockPos pos, BlockState state) {
        return this.factory.create(pos, state);
    }

    public boolean supports(BlockState state) {
        return this.blocks.contains(state.getBlock());
    }

    @Deprecated
    public RegistryEntry.Reference<BlockEntityType<?>> getRegistryEntry() {
        return this.registryEntry;
    }

    @Nullable
    public T get(BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getType() != this) {
            return null;
        }
        return (T)blockEntity;
    }

    public boolean canPotentiallyExecuteCommands() {
        return POTENTIALLY_EXECUTES_COMMANDS.contains(this);
    }

    @FunctionalInterface
    static interface BlockEntityFactory<T extends BlockEntity> {
        public T create(BlockPos var1, BlockState var2);
    }
}

