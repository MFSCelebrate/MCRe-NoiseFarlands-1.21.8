/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public final class SaplingGenerator {
    final static private Map<String, SaplingGenerator> GENERATORS = new Object2ObjectArrayMap();
    final static public Codec<SaplingGenerator> CODEC = Codec.stringResolver(generator -> generator.id, GENERATORS::get);
    final static public SaplingGenerator OAK = new SaplingGenerator("oak", 0.1f, Optional.empty(), Optional.empty(), Optional.of(TreeConfiguredFeatures.OAK), Optional.of(TreeConfiguredFeatures.FANCY_OAK), Optional.of(TreeConfiguredFeatures.OAK_BEES_005), Optional.of(TreeConfiguredFeatures.FANCY_OAK_BEES_005));
    final static public SaplingGenerator SPRUCE = new SaplingGenerator("spruce", 0.5f, Optional.of(TreeConfiguredFeatures.MEGA_SPRUCE), Optional.of(TreeConfiguredFeatures.MEGA_PINE), Optional.of(TreeConfiguredFeatures.SPRUCE), Optional.empty(), Optional.empty(), Optional.empty());
    final static public SaplingGenerator MANGROVE = new SaplingGenerator("mangrove", 0.85f, Optional.empty(), Optional.empty(), Optional.of(TreeConfiguredFeatures.MANGROVE), Optional.of(TreeConfiguredFeatures.TALL_MANGROVE), Optional.empty(), Optional.empty());
    final static public SaplingGenerator AZALEA = new SaplingGenerator("azalea", Optional.empty(), Optional.of(TreeConfiguredFeatures.AZALEA_TREE), Optional.empty());
    final static public SaplingGenerator BIRCH = new SaplingGenerator("birch", Optional.empty(), Optional.of(TreeConfiguredFeatures.BIRCH), Optional.of(TreeConfiguredFeatures.BIRCH_BEES_005));
    final static public SaplingGenerator JUNGLE = new SaplingGenerator("jungle", Optional.of(TreeConfiguredFeatures.MEGA_JUNGLE_TREE), Optional.of(TreeConfiguredFeatures.JUNGLE_TREE_NO_VINE), Optional.empty());
    final static public SaplingGenerator ACACIA = new SaplingGenerator("acacia", Optional.empty(), Optional.of(TreeConfiguredFeatures.ACACIA), Optional.empty());
    final static public SaplingGenerator CHERRY = new SaplingGenerator("cherry", Optional.empty(), Optional.of(TreeConfiguredFeatures.CHERRY), Optional.of(TreeConfiguredFeatures.CHERRY_BEES_005));
    final static public SaplingGenerator DARK_OAK = new SaplingGenerator("dark_oak", Optional.of(TreeConfiguredFeatures.DARK_OAK), Optional.empty(), Optional.empty());
    final static public SaplingGenerator PALE_OAK = new SaplingGenerator("pale_oak", Optional.of(TreeConfiguredFeatures.PALE_OAK_BONEMEAL), Optional.empty(), Optional.empty());
    final private String id;
    final private float rareChance;
    final private Optional<RegistryKey<ConfiguredFeature<?, ?>>> megaVariant;
    final private Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareMegaVariant;
    final private Optional<RegistryKey<ConfiguredFeature<?, ?>>> regularVariant;
    final private Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareRegularVariant;
    final private Optional<RegistryKey<ConfiguredFeature<?, ?>>> beesVariant;
    final private Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareBeesVariant;

    public SaplingGenerator(String id, Optional<RegistryKey<ConfiguredFeature<?, ?>>> megaVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> regularVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> beesVariant) {
        this(id, 0.0f, megaVariant, Optional.empty(), regularVariant, Optional.empty(), beesVariant, Optional.empty());
    }

    public SaplingGenerator(String id, float rareChance, Optional<RegistryKey<ConfiguredFeature<?, ?>>> megaVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareMegaVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> regularVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareRegularVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> beesVariant, Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareBeesVariant) {
        this.id = id;
        this.rareChance = rareChance;
        this.megaVariant = megaVariant;
        this.rareMegaVariant = rareMegaVariant;
        this.regularVariant = regularVariant;
        this.rareRegularVariant = rareRegularVariant;
        this.beesVariant = beesVariant;
        this.rareBeesVariant = rareBeesVariant;
        GENERATORS.put(id, this);
    }

    @Nullable
    private RegistryKey<ConfiguredFeature<?, ?>> getSmallTreeFeature(Random random, boolean flowersNearby) {
        if (random.nextFloat() < this.rareChance) {
            if (flowersNearby && this.rareBeesVariant.isPresent()) {
                return this.rareBeesVariant.get();
            }
            if (this.rareRegularVariant.isPresent()) {
                return this.rareRegularVariant.get();
            }
        }
        if (flowersNearby && this.beesVariant.isPresent()) {
            return this.beesVariant.get();
        }
        return this.regularVariant.orElse(null);
    }

    @Nullable
    private RegistryKey<ConfiguredFeature<?, ?>> getMegaTreeFeature(Random random) {
        if (this.rareMegaVariant.isPresent() && random.nextFloat() < this.rareChance) {
            return this.rareMegaVariant.get();
        }
        return this.megaVariant.orElse(null);
    }

    public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
        RegistryKey<ConfiguredFeature<?, ?>> registryKey2;
        RegistryEntry registryEntry;
        RegistryKey<ConfiguredFeature<?, ?>> registryKey = this.getMegaTreeFeature(random);
        if (registryKey != null && (registryEntry = (RegistryEntry)world.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(registryKey).orElse(null)) != null) {
            for (int i = 0; i >= -1; --i) {
                for (int j = 0; j >= -1; --j) {
                    if (!SaplingGenerator.canGenerateLargeTree(state, world, pos, i, j)) continue;
                    ConfiguredFeature configuredFeature = (ConfiguredFeature)registryEntry.value();
                    BlockState blockState = Blocks.AIR.getDefaultState();
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i, 0, j), blockState, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i + 1, 0, j), blockState, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i, 0, j + 1), blockState, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i + 1, 0, j + 1), blockState, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    if (configuredFeature.generate(world, chunkGenerator, random, pos.net_minecraft_util_math_BlockPos_add(i, 0, j))) {
                        return true;
                    }
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i, 0, j), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i + 1, 0, j), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i, 0, j + 1), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_add(i + 1, 0, j + 1), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                    return false;
                }
            }
        }
        if ((registryKey2 = this.getSmallTreeFeature(random, this.areFlowersNearby(world, pos))) == null) {
            return false;
        }
        RegistryEntry registryEntry2 = world.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(registryKey2).orElse(null);
        if (registryEntry2 == null) {
            return false;
        }
        ConfiguredFeature configuredFeature2 = (ConfiguredFeature)registryEntry2.value();
        BlockState blockState2 = world.getFluidState(pos).getBlockState();
        world.setBlockState(pos, blockState2, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        if (configuredFeature2.generate(world, chunkGenerator, random, pos)) {
            if (world.getBlockState(pos) == blockState2) {
                world.updateListeners(pos, state, blockState2, Block.NOTIFY_LISTENERS);
            }
            return true;
        }
        world.setBlockState(pos, state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        return false;
    }

    private static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
        Block block = state.getBlock();
        return world.getBlockState(pos.net_minecraft_util_math_BlockPos_add(x, 0, z)).isOf(block) && world.getBlockState(pos.net_minecraft_util_math_BlockPos_add(x + 1, 0, z)).isOf(block) && world.getBlockState(pos.net_minecraft_util_math_BlockPos_add(x, 0, z + 1)).isOf(block) && world.getBlockState(pos.net_minecraft_util_math_BlockPos_add(x + 1, 0, z + 1)).isOf(block);
    }

    private boolean areFlowersNearby(WorldAccess world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.Mutable.iterate(pos.net_minecraft_util_math_BlockPos_down().net_minecraft_util_math_BlockPos_north(2).net_minecraft_util_math_BlockPos_west(2), pos.net_minecraft_util_math_BlockPos_up().net_minecraft_util_math_BlockPos_south(2).net_minecraft_util_math_BlockPos_east(2))) {
            if (!world.getBlockState(blockPos).isIn(BlockTags.FLOWERS)) continue;
            return true;
        }
        return false;
    }
}

