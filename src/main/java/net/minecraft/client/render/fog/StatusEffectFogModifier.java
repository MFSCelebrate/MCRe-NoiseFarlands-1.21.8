/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class StatusEffectFogModifier
extends FogModifier {
    public abstract RegistryEntry<StatusEffect> getStatusEffect();

    @Override
    public boolean isColorSource() {
        return false;
    }

    @Override
    public boolean isDarknessModifier() {
        return true;
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        LivingEntity livingEntity;
        return cameraEntity instanceof LivingEntity && (livingEntity = (LivingEntity)cameraEntity).hasStatusEffect(this.getStatusEffect());
    }
}

