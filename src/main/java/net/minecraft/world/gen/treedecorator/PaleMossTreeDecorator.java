/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.mutable.Mutable
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class PaleMossTreeDecorator
extends TreeDecorator {
    final static public MapCodec<PaleMossTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.floatRange(0.0f, 1.0f).fieldOf("leaves_probability").forGetter(treeDecorator -> Float.valueOf(treeDecorator.leavesProbability)), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("trunk_probability").forGetter(treeDecorator -> Float.valueOf(treeDecorator.trunkProbability)), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("ground_probability").forGetter(treeDecorator -> Float.valueOf(treeDecorator.groundProbability))).apply((Applicative)instance, PaleMossTreeDecorator::new));
    final private float leavesProbability;
    final private float trunkProbability;
    final private float groundProbability;

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.PALE_MOSS;
    }

    public PaleMossTreeDecorator(float leavesProbability, float trunkProbability, float groundProbability) {
        this.leavesProbability = leavesProbability;
        this.trunkProbability = trunkProbability;
        this.groundProbability = groundProbability;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random random = generator.getRandom();
        StructureWorldAccess structureWorldAccess = (StructureWorldAccess)generator.getWorld();
        List<BlockPos> list = Util.copyShuffled(generator.getLogPositions(), random);
        if (list.isEmpty()) {
            return;
        }
        MutableObject mutable = new MutableObject((Object)list.getFirst());
        list.forEach(arg_0 -> PaleMossTreeDecorator.method_64816((Mutable)mutable, arg_0));
        BlockPos blockPos = (BlockPos)mutable.getValue();
        if (random.nextFloat() < this.groundProbability) {
            structureWorldAccess.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap(registry -> registry.getOptional(VegetationConfiguredFeatures.PALE_MOSS_PATCH)).ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate(structureWorldAccess, structureWorldAccess.toServerWorld().net_minecraft_server_world_ServerChunkManager_getChunkManager().getChunkGenerator(), random, blockPos.net_minecraft_util_math_BlockPos_up()));
        }
        generator.getLogPositions().forEach(pos -> {
            BlockPos blockPos;
            if (random.nextFloat() < this.trunkProbability && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_down())) {
                PaleMossTreeDecorator.decorate(blockPos, generator);
            }
        });
        generator.getLeavesPositions().forEach(pos -> {
            BlockPos blockPos;
            if (random.nextFloat() < this.leavesProbability && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_down())) {
                PaleMossTreeDecorator.decorate(blockPos, generator);
            }
        });
    }

    private static void decorate(BlockPos pos, TreeDecorator.Generator generator) {
        while (generator.isAir(pos.net_minecraft_util_math_BlockPos_down()) && !((double)generator.getRandom().nextFloat() < 0.5)) {
            generator.replace(pos, (BlockState)Blocks.PALE_HANGING_MOSS.getDefaultState().with(HangingMossBlock.TIP, false));
            pos = pos.net_minecraft_util_math_BlockPos_down();
        }
        generator.replace(pos, (BlockState)Blocks.PALE_HANGING_MOSS.getDefaultState().with(HangingMossBlock.TIP, true));
    }

    private static void method_64816(Mutable mutable, BlockPos pos) {
        if (pos.getY() < ((BlockPos)mutable.getValue()).getY()) {
            mutable.setValue((Object)pos);
        }
    }
}

