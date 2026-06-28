/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrateBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableGrateBlock
extends GrateBlock
implements Oxidizable {
    final static public MapCodec<OxidizableGrateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableGrateBlock::getDegradationLevel), OxidizableGrateBlock.createSettingsCodec()).apply((Applicative)instance, OxidizableGrateBlock::new));
    final private Oxidizable.OxidationLevel oxidationLevel;

    protected MapCodec<OxidizableGrateBlock> getCodec() {
        return CODEC;
    }

    public OxidizableGrateBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public Oxidizable.OxidationLevel net_minecraft_block_Oxidizable$OxidationLevel_getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public Enum java_lang_Enum_getDegradationLevel() {
        return this.net_minecraft_block_Oxidizable$OxidationLevel_getDegradationLevel();
    }
}

