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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WaterFogModifier
extends FogModifier {
    final static private int field_60592 = 96;
    final static private float field_60593 = 5000.0f;
    static private int waterFogColor = -1;
    static private int lerpedWaterFogColor = -1;
    static private long updateTime = -1L;

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
        data.environmentalStart = -8.0f;
        data.environmentalEnd = 96.0f;
        if (cameraEntity instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)cameraEntity;
            data.environmentalEnd *= Math.max(0.25f, clientPlayerEntity.getUnderwaterVisibility());
            if (world.getBiome(cameraPos).isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                data.environmentalEnd *= 0.85f;
            }
        }
        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == CameraSubmersionType.WATER;
    }

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        long l = Util.getMeasuringTimeMs();
        int i = world.getBiome(camera.getBlockPos()).value().getWaterFogColor();
        if (updateTime < 0L) {
            waterFogColor = i;
            lerpedWaterFogColor = i;
            updateTime = l;
        }
        float f = MathHelper.clamp((float)(l - updateTime) / 5000.0f, 0.0f, 1.0f);
        int j = ColorHelper.lerp(f, lerpedWaterFogColor, waterFogColor);
        if (waterFogColor != i) {
            waterFogColor = i;
            lerpedWaterFogColor = j;
            updateTime = l;
        }
        return j;
    }

    @Override
    public void onSkipped() {
        updateTime = -1L;
    }
}

