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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LavaFogModifier
extends FogModifier {
    final static private int COLOR = -6743808;

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        return -6743808;
    }

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
        LivingEntity livingEntity;
        if (cameraEntity.isSpectator()) {
            data.environmentalStart = -8.0f;
            data.environmentalEnd = viewDistance * 0.5f;
        } else if (cameraEntity instanceof LivingEntity && (livingEntity = (LivingEntity)cameraEntity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            data.environmentalStart = 0.0f;
            data.environmentalEnd = 5.0f;
        } else {
            data.environmentalStart = 0.25f;
            data.environmentalEnd = 1.0f;
        }
        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == CameraSubmersionType.LAVA;
    }
}

