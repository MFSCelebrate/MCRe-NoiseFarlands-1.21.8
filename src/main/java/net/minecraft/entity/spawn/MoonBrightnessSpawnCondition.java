/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.spawn;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.predicate.NumberRange;

public record MoonBrightnessSpawnCondition(NumberRange.DoubleRange range) implements SpawnCondition
{
    final static public MapCodec<MoonBrightnessSpawnCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)NumberRange.DoubleRange.CODEC.fieldOf("range").forGetter(MoonBrightnessSpawnCondition::range)).apply((Applicative)instance, MoonBrightnessSpawnCondition::new));

    @Override
    public boolean test(SpawnContext spawnContext) {
        return this.range.test(spawnContext.world().toServerWorld().getMoonSize());
    }

    public MapCodec<MoonBrightnessSpawnCondition> getCodec() {
        return CODEC;
    }

    @Override
    public boolean test(Object context) {
        return this.test((SpawnContext)context);
    }
}

