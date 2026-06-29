/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.tint;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record FireworkTintSource(int defaultColor) implements TintSource
{
    final static public MapCodec<FireworkTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.RGB.fieldOf("default").forGetter(FireworkTintSource::defaultColor)).apply((Applicative)instance, FireworkTintSource::new));

    public FireworkTintSource() {
        this(-7697782);
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        FireworkExplosionComponent fireworkExplosionComponent = stack.get(DataComponentTypes.FIREWORK_EXPLOSION);
        IntList intList = fireworkExplosionComponent != null ? fireworkExplosionComponent.colors() : IntList.of();
        int i = intList.size();
        if (i == 0) {
            return this.defaultColor;
        }
        if (i == 1) {
            return ColorHelper.fullAlpha(intList.getInt(0));
        }
        int j = 0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < i; ++m) {
            int n = intList.getInt(m);
            j += ColorHelper.getRed(n);
            k += ColorHelper.getGreen(n);
            l += ColorHelper.getBlue(n);
        }
        return ColorHelper.getArgb(j / i, k / i, l / i);
    }

    public MapCodec<FireworkTintSource> getCodec() {
        return CODEC;
    }
}

