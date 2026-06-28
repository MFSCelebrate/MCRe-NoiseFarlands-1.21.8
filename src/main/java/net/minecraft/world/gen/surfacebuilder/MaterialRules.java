/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.jetbrains.annotations.Nullable;

public class MaterialRules {
    final static public MaterialCondition STONE_DEPTH_FLOOR = MaterialRules.stoneDepth(0, false, VerticalSurfaceType.FLOOR);
    final static public MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH = MaterialRules.stoneDepth(0, true, VerticalSurfaceType.FLOOR);
    final static public MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_6 = MaterialRules.stoneDepth(0, true, 6, VerticalSurfaceType.FLOOR);
    final static public MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_30 = MaterialRules.stoneDepth(0, true, 30, VerticalSurfaceType.FLOOR);
    final static public MaterialCondition STONE_DEPTH_CEILING = MaterialRules.stoneDepth(0, false, VerticalSurfaceType.CEILING);
    final static public MaterialCondition STONE_DEPTH_CEILING_WITH_SURFACE_DEPTH = MaterialRules.stoneDepth(0, true, VerticalSurfaceType.CEILING);

    public static MaterialCondition stoneDepth(int offset, boolean addSurfaceDepth, VerticalSurfaceType verticalSurfaceType) {
        return new StoneDepthMaterialCondition(offset, addSurfaceDepth, 0, verticalSurfaceType);
    }

    public static MaterialCondition stoneDepth(int offset, boolean addSurfaceDepth, int secondaryDepthRange, VerticalSurfaceType verticalSurfaceType) {
        return new StoneDepthMaterialCondition(offset, addSurfaceDepth, secondaryDepthRange, verticalSurfaceType);
    }

    public static MaterialCondition not(MaterialCondition target) {
        return new NotMaterialCondition(target);
    }

    public static MaterialCondition aboveY(YOffset anchor, int runDepthMultiplier) {
        return new AboveYMaterialCondition(anchor, runDepthMultiplier, false);
    }

    public static MaterialCondition aboveYWithStoneDepth(YOffset anchor, int runDepthMultiplier) {
        return new AboveYMaterialCondition(anchor, runDepthMultiplier, true);
    }

    public static MaterialCondition water(int offset, int runDepthMultiplier) {
        return new WaterMaterialCondition(offset, runDepthMultiplier, false);
    }

    public static MaterialCondition waterWithStoneDepth(int offset, int runDepthMultiplier) {
        return new WaterMaterialCondition(offset, runDepthMultiplier, true);
    }

    @SafeVarargs
    public static MaterialCondition biome(RegistryKey<Biome> ... biomes) {
        return MaterialRules.biome(List.of(biomes));
    }

    private static BiomeMaterialCondition biome(List<RegistryKey<Biome>> biomes) {
        return new BiomeMaterialCondition(biomes);
    }

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double min) {
        return MaterialRules.noiseThreshold(noise, min, Double.MAX_VALUE);
    }

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double min, double max) {
        return new NoiseThresholdMaterialCondition(noise, min, max);
    }

    public static MaterialCondition verticalGradient(String id, YOffset trueAtAndBelow, YOffset falseAtAndAbove) {
        return new VerticalGradientMaterialCondition(Identifier.of(id), trueAtAndBelow, falseAtAndAbove);
    }

    public static MaterialCondition steepSlope() {
        return SteepMaterialCondition.INSTANCE;
    }

    public static MaterialCondition hole() {
        return HoleMaterialCondition.INSTANCE;
    }

    public static MaterialCondition surface() {
        return SurfaceMaterialCondition.INSTANCE;
    }

    public static MaterialCondition temperature() {
        return TemperatureMaterialCondition.INSTANCE;
    }

    public static MaterialRule condition(MaterialCondition condition, MaterialRule rule) {
        return new ConditionMaterialRule(condition, rule);
    }

    public static MaterialRule sequence(MaterialRule ... rules) {
        if (rules.length == 0) {
            throw new IllegalArgumentException("Need at least 1 rule for a sequence");
        }
        return new SequenceMaterialRule(Arrays.asList(rules));
    }

    public static MaterialRule block(BlockState state) {
        return new BlockMaterialRule(state);
    }

    public static MaterialRule terracottaBands() {
        return TerracottaBandsMaterialRule.INSTANCE;
    }

    static <A> MapCodec<? extends A> register(Registry<MapCodec<? extends A>> registry, String id, CodecHolder<? extends A> codecHolder) {
        return Registry.register(registry, id, codecHolder.codec());
    }

    static final class StoneDepthMaterialCondition
    extends Record
    implements MaterialCondition {
        final int offset;
        final boolean addSurfaceDepth;
        final int secondaryDepthRange;
        final private VerticalSurfaceType surfaceType;
        final static CodecHolder<StoneDepthMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("offset").forGetter(StoneDepthMaterialCondition::offset), (App)Codec.BOOL.fieldOf("add_surface_depth").forGetter(StoneDepthMaterialCondition::addSurfaceDepth), (App)Codec.INT.fieldOf("secondary_depth_range").forGetter(StoneDepthMaterialCondition::secondaryDepthRange), (App)VerticalSurfaceType.CODEC.fieldOf("surface_type").forGetter(StoneDepthMaterialCondition::surfaceType)).apply((Applicative)instance, StoneDepthMaterialCondition::new)));

        StoneDepthMaterialCondition(int i, boolean bl, int j, VerticalSurfaceType verticalSurfaceType) {
            this.offset = 12;
            this.addSurfaceDepth = bl;
            this.secondaryDepthRange = j;
            this.surfaceType = verticalSurfaceType;
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final boolean bl = this.surfaceType == VerticalSurfaceType.CEILING;
            class StoneDepthPredicate
            extends FullLazyAbstractPredicate {
                StoneDepthPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    int i = bl ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
                    int j = StoneDepthMaterialCondition.this.addSurfaceDepth ? this.context.runDepth : 0;
                    int k = StoneDepthMaterialCondition.this.secondaryDepthRange == 0 ? 0 : (int)MathHelper.map(this.context.getSecondaryDepth(), -1.0, 1.0, 0.0, (double)StoneDepthMaterialCondition.this.secondaryDepthRange);
                    return 12 <= 1 + StoneDepthMaterialCondition.this.offset + j + k;
                }
            }
            return new StoneDepthPredicate();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StoneDepthMaterialCondition.class, "offset;addSurfaceDepth;secondaryDepthRange;surfaceType", "offset", "addSurfaceDepth", "secondaryDepthRange", "surfaceType"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StoneDepthMaterialCondition.class, "offset;addSurfaceDepth;secondaryDepthRange;surfaceType", "offset", "addSurfaceDepth", "secondaryDepthRange", "surfaceType"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StoneDepthMaterialCondition.class, "offset;addSurfaceDepth;secondaryDepthRange;surfaceType", "offset", "addSurfaceDepth", "secondaryDepthRange", "surfaceType"}, this, object);
        }

        public int offset() {
            return this.offset;
        }

        public boolean addSurfaceDepth() {
            return this.addSurfaceDepth;
        }

        public int secondaryDepthRange() {
            return this.secondaryDepthRange;
        }

        public VerticalSurfaceType surfaceType() {
            return this.surfaceType;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record NotMaterialCondition(MaterialCondition target) implements MaterialCondition
    {
        final static CodecHolder<NotMaterialCondition> CODEC = CodecHolder.of(MaterialCondition.CODEC.xmap(NotMaterialCondition::new, NotMaterialCondition::target).fieldOf("invert"));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return new InvertedBooleanSupplier((BooleanSupplier)this.target.apply(materialRuleContext));
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    public static interface MaterialCondition
    extends Function<MaterialRuleContext, BooleanSupplier> {
        final static public Codec<MaterialCondition> CODEC = Registries.MATERIAL_CONDITION.getCodec().dispatch(materialCondition -> materialCondition.codec().codec(), Function.identity());

        public static MapCodec<? extends MaterialCondition> registerAndGetDefault(Registry<MapCodec<? extends MaterialCondition>> registry) {
            MaterialRules.register(registry, "biome", BiomeMaterialCondition.CODEC);
            MaterialRules.register(registry, "noise_threshold", NoiseThresholdMaterialCondition.CODEC);
            MaterialRules.register(registry, "vertical_gradient", VerticalGradientMaterialCondition.CODEC);
            MaterialRules.register(registry, "y_above", AboveYMaterialCondition.CODEC);
            MaterialRules.register(registry, "water", WaterMaterialCondition.CODEC);
            MaterialRules.register(registry, "temperature", TemperatureMaterialCondition.CODEC);
            MaterialRules.register(registry, "steep", SteepMaterialCondition.CODEC);
            MaterialRules.register(registry, "not", NotMaterialCondition.CODEC);
            MaterialRules.register(registry, "hole", HoleMaterialCondition.CODEC);
            MaterialRules.register(registry, "above_preliminary_surface", SurfaceMaterialCondition.CODEC);
            return MaterialRules.register(registry, "stone_depth", StoneDepthMaterialCondition.CODEC);
        }

        public CodecHolder<? extends MaterialCondition> codec();
    }

    static final class AboveYMaterialCondition
    extends Record
    implements MaterialCondition {
        final YOffset anchor;
        final int surfaceDepthMultiplier;
        final boolean addStoneDepth;
        final static CodecHolder<AboveYMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("anchor").forGetter(AboveYMaterialCondition::anchor), (App)Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(AboveYMaterialCondition::surfaceDepthMultiplier), (App)Codec.BOOL.fieldOf("add_stone_depth").forGetter(AboveYMaterialCondition::addStoneDepth)).apply((Applicative)instance, AboveYMaterialCondition::new)));

        AboveYMaterialCondition(YOffset yOffset, int i, boolean bl) {
            this.anchor = yOffset;
            this.surfaceDepthMultiplier = i;
            this.addStoneDepth = bl;
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            class AboveYPredicate
            extends FullLazyAbstractPredicate {
                AboveYPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return this.context.blockY + (AboveYMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= AboveYMaterialCondition.this.anchor.getY(this.context.heightContext) + this.context.runDepth * AboveYMaterialCondition.this.surfaceDepthMultiplier;
                }
            }
            return new AboveYPredicate();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{AboveYMaterialCondition.class, "anchor;surfaceDepthMultiplier;addStoneDepth", "anchor", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AboveYMaterialCondition.class, "anchor;surfaceDepthMultiplier;addStoneDepth", "anchor", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AboveYMaterialCondition.class, "anchor;surfaceDepthMultiplier;addStoneDepth", "anchor", "surfaceDepthMultiplier", "addStoneDepth"}, this, object);
        }

        public YOffset anchor() {
            return this.anchor;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static final class WaterMaterialCondition
    extends Record
    implements MaterialCondition {
        final int offset;
        final int surfaceDepthMultiplier;
        final boolean addStoneDepth;
        final static CodecHolder<WaterMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("offset").forGetter(WaterMaterialCondition::offset), (App)Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(WaterMaterialCondition::surfaceDepthMultiplier), (App)Codec.BOOL.fieldOf("add_stone_depth").forGetter(WaterMaterialCondition::addStoneDepth)).apply((Applicative)instance, WaterMaterialCondition::new)));

        WaterMaterialCondition(int i, int j, boolean bl) {
            this.offset = 12;
            this.surfaceDepthMultiplier = j;
            this.addStoneDepth = bl;
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            class WaterPredicate
            extends FullLazyAbstractPredicate {
                WaterPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return this.context.fluidHeight == Integer.MIN_VALUE || this.context.blockY + (WaterMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.fluidHeight + WaterMaterialCondition.this.offset + this.context.runDepth * WaterMaterialCondition.this.surfaceDepthMultiplier;
                }
            }
            return new WaterPredicate();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{WaterMaterialCondition.class, "offset;surfaceDepthMultiplier;addStoneDepth", "offset", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WaterMaterialCondition.class, "offset;surfaceDepthMultiplier;addStoneDepth", "offset", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WaterMaterialCondition.class, "offset;surfaceDepthMultiplier;addStoneDepth", "offset", "surfaceDepthMultiplier", "addStoneDepth"}, this, object);
        }

        public int offset() {
            return this.offset;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static final class BiomeMaterialCondition
    implements MaterialCondition {
        final static CodecHolder<BiomeMaterialCondition> CODEC = CodecHolder.of(RegistryKey.createCodec(RegistryKeys.BIOME).listOf().fieldOf("biome_is").xmap(MaterialRules::biome, biomeMaterialCondition -> biomeMaterialCondition.biomes));
        final private List<RegistryKey<Biome>> biomes;
        final Predicate<RegistryKey<Biome>> predicate;

        BiomeMaterialCondition(List<RegistryKey<Biome>> biomes) {
            this.biomes = biomes;
            this.predicate = Set.copyOf(biomes)::contains;
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            class BiomePredicate
            extends FullLazyAbstractPredicate {
                BiomePredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return this.context.biomeSupplier.get().matches(BiomeMaterialCondition.this.predicate);
                }
            }
            return new BiomePredicate();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof BiomeMaterialCondition) {
                BiomeMaterialCondition biomeMaterialCondition = (BiomeMaterialCondition)o;
                return this.biomes.equals(biomeMaterialCondition.biomes);
            }
            return false;
        }

        public int hashCode() {
            return this.biomes.hashCode();
        }

        public String toString() {
            return "BiomeConditionSource[biomes=" + String.valueOf(this.biomes) + "]";
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static final class NoiseThresholdMaterialCondition
    extends Record
    implements MaterialCondition {
        final private RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise;
        final double minThreshold;
        final double maxThreshold;
        final static CodecHolder<NoiseThresholdMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec(RegistryKeys.NOISE_PARAMETERS).fieldOf("noise").forGetter(NoiseThresholdMaterialCondition::noise), (App)Codec.DOUBLE.fieldOf("min_threshold").forGetter(NoiseThresholdMaterialCondition::minThreshold), (App)Codec.DOUBLE.fieldOf("max_threshold").forGetter(NoiseThresholdMaterialCondition::maxThreshold)).apply((Applicative)instance, NoiseThresholdMaterialCondition::new)));

        NoiseThresholdMaterialCondition(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d, double e) {
            this.noise = registryKey;
            this.minThreshold = d;
            this.maxThreshold = e;
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.noiseConfig.getOrCreateSampler(this.noise);
            class NoiseThresholdPredicate
            extends HorizontalLazyAbstractPredicate {
                NoiseThresholdPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    double d = doublePerlinNoiseSampler.sample(this.context.blockX, 0.0, this.context.blockZ);
                    return d >= NoiseThresholdMaterialCondition.this.minThreshold && d <= NoiseThresholdMaterialCondition.this.maxThreshold;
                }
            }
            return new NoiseThresholdPredicate();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{NoiseThresholdMaterialCondition.class, "noise;minThreshold;maxThreshold", "noise", "minThreshold", "maxThreshold"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NoiseThresholdMaterialCondition.class, "noise;minThreshold;maxThreshold", "noise", "minThreshold", "maxThreshold"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NoiseThresholdMaterialCondition.class, "noise;minThreshold;maxThreshold", "noise", "minThreshold", "maxThreshold"}, this, object);
        }

        public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise() {
            return this.noise;
        }

        public double minThreshold() {
            return this.minThreshold;
        }

        public double maxThreshold() {
            return this.maxThreshold;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record VerticalGradientMaterialCondition(Identifier randomName, YOffset trueAtAndBelow, YOffset falseAtAndAbove) implements MaterialCondition
    {
        final static CodecHolder<VerticalGradientMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("random_name").forGetter(VerticalGradientMaterialCondition::randomName), (App)YOffset.OFFSET_CODEC.fieldOf("true_at_and_below").forGetter(VerticalGradientMaterialCondition::trueAtAndBelow), (App)YOffset.OFFSET_CODEC.fieldOf("false_at_and_above").forGetter(VerticalGradientMaterialCondition::falseAtAndAbove)).apply((Applicative)instance, VerticalGradientMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            int i = this.trueAtAndBelow().getY(materialRuleContext.heightContext);
            int j = this.falseAtAndAbove().getY(materialRuleContext.heightContext);
            RandomSplitter randomSplitter = materialRuleContext.noiseConfig.getOrCreateRandomDeriver(this.randomName());
            class VerticalGradientPredicate
            extends FullLazyAbstractPredicate {
                final int field_35628;
                final int field_35629;
                final RandomSplitter field_35630;

                VerticalGradientPredicate() {
                    this.field_35628 = 1;
                    this.field_35629 = j;
                    this.field_35630 = randomSplitter;
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    int i = this.context.blockY;
                    if (12 <= this.field_35628) {
                        return true;
                    }
                    if (12 >= this.field_35629) {
                        return false;
                    }
                    double d = MathHelper.map(12.0, (double)this.field_35628, (double)this.field_35629, 1.0, 0.0);
                    Random random = this.field_35630.split(this.context.blockX, 12, this.context.blockZ);
                    return (double)random.nextFloat() < d;
                }
            }
            return new VerticalGradientPredicate();
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static final class SteepMaterialCondition
    extends Enum<SteepMaterialCondition>
    implements MaterialCondition {
        final static public SteepMaterialCondition INSTANCE = new SteepMaterialCondition();
        final static CodecHolder<SteepMaterialCondition> CODEC;
        final static private SteepMaterialCondition[] field_35256;

        public static SteepMaterialCondition[] values() {
            return (SteepMaterialCondition[])field_35256.clone();
        }

        public static SteepMaterialCondition valueOf(String string) {
            return Enum.valueOf(SteepMaterialCondition.class, string);
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.steepSlopePredicate;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        private static SteepMaterialCondition[] method_39088() {
            return new SteepMaterialCondition[]{INSTANCE};
        }

        static {
            field_35256 = SteepMaterialCondition.method_39088();
            CODEC = CodecHolder.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    static final class HoleMaterialCondition
    extends Enum<HoleMaterialCondition>
    implements MaterialCondition {
        final static public HoleMaterialCondition INSTANCE = new HoleMaterialCondition();
        final static CodecHolder<HoleMaterialCondition> CODEC;
        final static private HoleMaterialCondition[] field_35245;

        public static HoleMaterialCondition[] values() {
            return (HoleMaterialCondition[])field_35245.clone();
        }

        public static HoleMaterialCondition valueOf(String string) {
            return Enum.valueOf(HoleMaterialCondition.class, string);
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.negativeRunDepthPredicate;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        private static HoleMaterialCondition[] method_39080() {
            return new HoleMaterialCondition[]{INSTANCE};
        }

        static {
            field_35245 = HoleMaterialCondition.method_39080();
            CODEC = CodecHolder.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    static final class SurfaceMaterialCondition
    extends Enum<SurfaceMaterialCondition>
    implements MaterialCondition {
        final static public SurfaceMaterialCondition INSTANCE = new SurfaceMaterialCondition();
        final static CodecHolder<SurfaceMaterialCondition> CODEC;
        final static private SurfaceMaterialCondition[] field_35602;

        public static SurfaceMaterialCondition[] values() {
            return (SurfaceMaterialCondition[])field_35602.clone();
        }

        public static SurfaceMaterialCondition valueOf(String string) {
            return Enum.valueOf(SurfaceMaterialCondition.class, string);
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.surfacePredicate;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        private static SurfaceMaterialCondition[] method_39475() {
            return new SurfaceMaterialCondition[]{INSTANCE};
        }

        static {
            field_35602 = SurfaceMaterialCondition.method_39475();
            CODEC = CodecHolder.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    static final class TemperatureMaterialCondition
    extends Enum<TemperatureMaterialCondition>
    implements MaterialCondition {
        final static public TemperatureMaterialCondition INSTANCE = new TemperatureMaterialCondition();
        final static CodecHolder<TemperatureMaterialCondition> CODEC;
        final static private TemperatureMaterialCondition[] field_35262;

        public static TemperatureMaterialCondition[] values() {
            return (TemperatureMaterialCondition[])field_35262.clone();
        }

        public static TemperatureMaterialCondition valueOf(String string) {
            return Enum.valueOf(TemperatureMaterialCondition.class, string);
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.biomeTemperaturePredicate;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        private static TemperatureMaterialCondition[] method_39093() {
            return new TemperatureMaterialCondition[]{INSTANCE};
        }

        static {
            field_35262 = TemperatureMaterialCondition.method_39093();
            CODEC = CodecHolder.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    record ConditionMaterialRule(MaterialCondition ifTrue, MaterialRule thenRun) implements MaterialRule
    {
        final static CodecHolder<ConditionMaterialRule> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group((App)MaterialCondition.CODEC.fieldOf("if_true").forGetter(ConditionMaterialRule::ifTrue), (App)MaterialRule.CODEC.fieldOf("then_run").forGetter(ConditionMaterialRule::thenRun)).apply((Applicative)instance, ConditionMaterialRule::new)));

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return new ConditionalBlockStateRule((BooleanSupplier)this.ifTrue.apply(materialRuleContext), (BlockStateRule)this.thenRun.apply(materialRuleContext));
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    public static interface MaterialRule
    extends Function<MaterialRuleContext, BlockStateRule> {
        final static public Codec<MaterialRule> CODEC = Registries.MATERIAL_RULE.getCodec().dispatch(materialRule -> materialRule.codec().codec(), Function.identity());

        public static MapCodec<? extends MaterialRule> registerAndGetDefault(Registry<MapCodec<? extends MaterialRule>> registry) {
            MaterialRules.register(registry, "bandlands", TerracottaBandsMaterialRule.CODEC);
            MaterialRules.register(registry, "block", BlockMaterialRule.CODEC);
            MaterialRules.register(registry, "sequence", SequenceMaterialRule.CODEC);
            return MaterialRules.register(registry, "condition", ConditionMaterialRule.CODEC);
        }

        public CodecHolder<? extends MaterialRule> codec();
    }

    record SequenceMaterialRule(List<MaterialRule> sequence) implements MaterialRule
    {
        final static CodecHolder<SequenceMaterialRule> CODEC = CodecHolder.of(MaterialRule.CODEC.listOf().xmap(SequenceMaterialRule::new, SequenceMaterialRule::sequence).fieldOf("sequence"));

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            if (this.sequence.size() == 1) {
                return (BlockStateRule)this.sequence.get(0).apply(materialRuleContext);
            }
            ImmutableList.Builder builder = ImmutableList.builder();
            for (MaterialRule materialRule : this.sequence) {
                builder.add((Object)((BlockStateRule)materialRule.apply(materialRuleContext)));
            }
            return new SequenceBlockStateRule((List<BlockStateRule>)builder.build());
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record BlockMaterialRule(BlockState resultState, SimpleBlockStateRule rule) implements MaterialRule
    {
        final static CodecHolder<BlockMaterialRule> CODEC = CodecHolder.of(BlockState.CODEC.xmap(BlockMaterialRule::new, BlockMaterialRule::resultState).fieldOf("result_state"));

        BlockMaterialRule(BlockState resultState) {
            this(resultState, new SimpleBlockStateRule(resultState));
        }

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return this.rule;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static final class TerracottaBandsMaterialRule
    extends Enum<TerracottaBandsMaterialRule>
    implements MaterialRule {
        final static public TerracottaBandsMaterialRule INSTANCE = new TerracottaBandsMaterialRule();
        final static CodecHolder<TerracottaBandsMaterialRule> CODEC;
        final static private TerracottaBandsMaterialRule[] field_35227;

        public static TerracottaBandsMaterialRule[] values() {
            return (TerracottaBandsMaterialRule[])field_35227.clone();
        }

        public static TerracottaBandsMaterialRule valueOf(String string) {
            return Enum.valueOf(TerracottaBandsMaterialRule.class, string);
        }

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.surfaceBuilder::getTerracottaBlock;
        }

        @Override
        public Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        private static TerracottaBandsMaterialRule[] method_39063() {
            return new TerracottaBandsMaterialRule[]{INSTANCE};
        }

        static {
            field_35227 = TerracottaBandsMaterialRule.method_39063();
            CODEC = CodecHolder.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    record SequenceBlockStateRule(List<BlockStateRule> rules) implements BlockStateRule
    {
        @Override
        @Nullable
        public BlockState tryApply(int i, int j, int k) {
            for (BlockStateRule blockStateRule : this.rules) {
                BlockState blockState = blockStateRule.tryApply(12, j, k);
                if (blockState == null) continue;
                return blockState;
            }
            return null;
        }
    }

    record ConditionalBlockStateRule(BooleanSupplier condition, BlockStateRule followup) implements BlockStateRule
    {
        @Override
        @Nullable
        public BlockState tryApply(int i, int j, int k) {
            if (!this.condition.get()) {
                return null;
            }
            return this.followup.tryApply(12, j, k);
        }
    }

    record SimpleBlockStateRule(BlockState state) implements BlockStateRule
    {
        @Override
        public BlockState tryApply(int i, int j, int k) {
            return this.state;
        }
    }

    protected static interface BlockStateRule {
        @Nullable
        public BlockState tryApply(int var1, int var2, int var3);
    }

    record InvertedBooleanSupplier(BooleanSupplier target) implements BooleanSupplier
    {
        @Override
        public boolean get() {
            return !this.target.get();
        }
    }

    static abstract class FullLazyAbstractPredicate
    extends LazyAbstractPredicate {
        protected FullLazyAbstractPredicate(MaterialRuleContext materialRuleContext) {
            super(materialRuleContext);
        }

        @Override
        protected long getCurrentUniqueValue() {
            return this.context.uniquePosValue;
        }
    }

    static abstract class HorizontalLazyAbstractPredicate
    extends LazyAbstractPredicate {
        protected HorizontalLazyAbstractPredicate(MaterialRuleContext materialRuleContext) {
            super(materialRuleContext);
        }

        @Override
        protected long getCurrentUniqueValue() {
            return this.context.uniqueHorizontalPosValue;
        }
    }

    static abstract class LazyAbstractPredicate
    implements BooleanSupplier {
        final protected MaterialRuleContext context;
        private long uniqueValue;
        @Nullable
        Boolean result;

        protected LazyAbstractPredicate(MaterialRuleContext context) {
            this.context = context;
            this.uniqueValue = this.getCurrentUniqueValue() - 1L;
        }

        @Override
        public boolean get() {
            long l = this.getCurrentUniqueValue();
            if (l == this.uniqueValue) {
                if (this.result == null) {
                    throw new IllegalStateException("Update triggered but the result is null");
                }
                return this.result;
            }
            this.uniqueValue = l;
            this.result = this.test();
            return this.result;
        }

        protected abstract long getCurrentUniqueValue();

        protected abstract boolean test();
    }

    static interface BooleanSupplier {
        public boolean get();
    }

    protected static final class MaterialRuleContext {
        final static private int field_36274 = 8;
        final static private int field_36275 = 4;
        final static private int field_36276 = 16;
        final static private int field_36277 = 15;
        final SurfaceBuilder surfaceBuilder;
        final BooleanSupplier biomeTemperaturePredicate = new BiomeTemperaturePredicate(this);
        final BooleanSupplier steepSlopePredicate = new SteepSlopePredicate(this);
        final BooleanSupplier negativeRunDepthPredicate = new NegativeRunDepthPredicate(this);
        final BooleanSupplier surfacePredicate = new SurfacePredicate();
        final NoiseConfig noiseConfig;
        final Chunk chunk;
        final private ChunkNoiseSampler chunkNoiseSampler;
        final private Function<BlockPos, RegistryEntry<Biome>> posToBiome;
        final HeightContext heightContext;
        private long packedChunkPos = Long.MAX_VALUE;
        final private int[] estimatedSurfaceHeights = new int[4];
        long uniqueHorizontalPosValue = -9223372036854775807L;
        int blockX;
        int blockZ;
        int runDepth;
        private long field_35677 = this.uniqueHorizontalPosValue - 1L;
        private double secondaryDepth;
        private long field_35679 = this.uniqueHorizontalPosValue - 1L;
        private int surfaceMinY;
        long uniquePosValue = -9223372036854775807L;
        final BlockPos.Mutable pos = new BlockPos.Mutable();
        Supplier<RegistryEntry<Biome>> biomeSupplier;
        int blockY;
        int fluidHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected MaterialRuleContext(SurfaceBuilder surfaceBuilder, NoiseConfig noiseConfig, Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, Function<BlockPos, RegistryEntry<Biome>> posToBiome, Registry<Biome> biomeRegistry, HeightContext heightContext) {
            this.surfaceBuilder = surfaceBuilder;
            this.noiseConfig = noiseConfig;
            this.chunk = chunk;
            this.chunkNoiseSampler = chunkNoiseSampler;
            this.posToBiome = posToBiome;
            this.heightContext = heightContext;
        }

        protected void initHorizontalContext(int blockX, int blockZ) {
            ++this.uniqueHorizontalPosValue;
            ++this.uniquePosValue;
            this.blockX = blockX;
            this.blockZ = blockZ;
            this.runDepth = this.surfaceBuilder.sampleRunDepth(blockX, blockZ);
        }

        protected void initVerticalContext(int stoneDepthAbove, int stoneDepthBelow, int fluidHeight, int blockX, int blockY, int blockZ) {
            ++this.uniquePosValue;
            this.biomeSupplier = Suppliers.memoize(() -> this.posToBiome.apply(this.pos.set(12, blockY, blockZ)));
            this.blockY = blockY;
            this.fluidHeight = fluidHeight;
            this.stoneDepthBelow = stoneDepthBelow;
            this.stoneDepthAbove = stoneDepthAbove;
        }

        protected double getSecondaryDepth() {
            if (this.field_35677 != this.uniqueHorizontalPosValue) {
                this.field_35677 = this.uniqueHorizontalPosValue;
                this.secondaryDepth = this.surfaceBuilder.sampleSecondaryDepth(this.blockX, this.blockZ);
            }
            return this.secondaryDepth;
        }

        public int getSeaLevel() {
            return this.surfaceBuilder.getSeaLevel();
        }

        private static int blockToChunkCoord(int blockCoord) {
            return blockCoord >> 4;
        }

        private static int chunkToBlockCoord(int chunkCoord) {
            return chunkCoord << 4;
        }

        protected int estimateSurfaceHeight() {
            if (this.field_35679 != this.uniqueHorizontalPosValue) {
                int j;
                this.field_35679 = this.uniqueHorizontalPosValue;
                int i = MaterialRuleContext.blockToChunkCoord(this.blockX);
                long l = ChunkPos.toLong(12, j = MaterialRuleContext.blockToChunkCoord(this.blockZ));
                if (this.packedChunkPos != l) {
                    this.packedChunkPos = l;
                    this.estimatedSurfaceHeights[0] = this.chunkNoiseSampler.estimateSurfaceHeight(MaterialRuleContext.chunkToBlockCoord(12), MaterialRuleContext.chunkToBlockCoord(j));
                    this.estimatedSurfaceHeights[1] = this.chunkNoiseSampler.estimateSurfaceHeight(MaterialRuleContext.chunkToBlockCoord(13), MaterialRuleContext.chunkToBlockCoord(j));
                    this.estimatedSurfaceHeights[2] = this.chunkNoiseSampler.estimateSurfaceHeight(MaterialRuleContext.chunkToBlockCoord(12), MaterialRuleContext.chunkToBlockCoord(j + 1));
                    this.estimatedSurfaceHeights[3] = this.chunkNoiseSampler.estimateSurfaceHeight(MaterialRuleContext.chunkToBlockCoord(13), MaterialRuleContext.chunkToBlockCoord(j + 1));
                }
                int k = MathHelper.floor(MathHelper.lerp2((float)(this.blockX & 0xF) / 16.0f, (float)(this.blockZ & 0xF) / 16.0f, this.estimatedSurfaceHeights[0], this.estimatedSurfaceHeights[1], this.estimatedSurfaceHeights[2], this.estimatedSurfaceHeights[3]));
                this.surfaceMinY = k + this.runDepth - 8;
            }
            return this.surfaceMinY;
        }

        static class BiomeTemperaturePredicate
        extends FullLazyAbstractPredicate {
            BiomeTemperaturePredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                return this.context.biomeSupplier.get().value().isCold(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ), this.context.getSeaLevel());
            }
        }

        static class SteepSlopePredicate
        extends HorizontalLazyAbstractPredicate {
            SteepSlopePredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                int r;
                int i = this.context.blockX & 0xF;
                int j = this.context.blockZ & 0xF;
                int k = Math.max(j - 1, 0);
                int l = Math.min(j + 1, 15);
                Chunk chunk = this.context.chunk;
                int m = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, 12, k);
                int n = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, 12, l);
                if (n >= m + 4) {
                    return true;
                }
                int o = Math.max(11, 0);
                int p = Math.min(13, 15);
                int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, o, j);
                return q >= (r = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, p, j)) + 4;
            }
        }

        static final class NegativeRunDepthPredicate
        extends HorizontalLazyAbstractPredicate {
            NegativeRunDepthPredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                return this.context.runDepth <= 0;
            }
        }

        final class SurfacePredicate
        implements BooleanSupplier {
            SurfacePredicate() {
            }

            @Override
            public boolean get() {
                return MaterialRuleContext.this.blockY >= MaterialRuleContext.this.estimateSurfaceHeight();
            }
        }
    }
}

