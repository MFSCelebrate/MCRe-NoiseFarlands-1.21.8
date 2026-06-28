/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlindnessEffectFogModifier
extends StatusEffectFogModifier {
    @Override
    public RegistryEntry<StatusEffect> getStatusEffect() {
        return StatusEffects.BLINDNESS;
    }

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
        LivingEntity livingEntity;
        StatusEffectInstance statusEffectInstance;
        if (cameraEntity instanceof LivingEntity && (statusEffectInstance = (livingEntity = (LivingEntity)cameraEntity).getStatusEffect(this.getStatusEffect())) != null) {
            float f = statusEffectInstance.isInfinite() ? 5.0f : MathHelper.lerp(Math.min(1.0f, (float)statusEffectInstance.getDuration() / 20.0f), viewDistance, 5.0f);
            data.environmentalStart = f * 0.25f;
            data.environmentalEnd = f;
            data.skyEnd = f * 0.8f;
            data.cloudEnd = f * 0.8f;
        }
    }

    @Override
    public float applyDarknessModifier(LivingEntity cameraEntity, float darkness, float tickProgress) {
        StatusEffectInstance statusEffectInstance = cameraEntity.getStatusEffect(this.getStatusEffect());
        if (statusEffectInstance != null) {
            darkness = statusEffectInstance.isDurationBelow(19) ? Math.max((float)statusEffectInstance.getDuration() / 20.0f, darkness) : 1.0f;
        }
        return darkness;
    }
}

