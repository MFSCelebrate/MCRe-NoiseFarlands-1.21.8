/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.rule.blockentity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class AppendStaticRuleBlockEntityModifier
implements RuleBlockEntityModifier {
    final static public MapCodec<AppendStaticRuleBlockEntityModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)NbtCompound.CODEC.fieldOf("data").forGetter(modifier -> modifier.nbt)).apply((Applicative)instance, AppendStaticRuleBlockEntityModifier::new));
    final private NbtCompound nbt;

    public AppendStaticRuleBlockEntityModifier(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
        return nbt == null ? this.nbt.net_minecraft_nbt_NbtCompound_copy() : nbt.copyFrom(this.nbt);
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.APPEND_STATIC;
    }
}

