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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.StandardFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AtmosphericFogModifier
extends StandardFogModifier {
    final static private int field_60795 = 8;
    final static private float field_60587 = -160.0f;
    final static private float field_60588 = -256.0f;
    private float fogMultiplier;

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
        Biome biome = world.getBiome(cameraPos).value();
        float f = tickCounter.getDynamicDeltaTicks();
        boolean bl = biome.hasPrecipitation();
        float g = MathHelper.clamp(((float)world.getLightingProvider().get(LightType.SKY).getLightLevel(cameraPos) - 8.0f) / 7.0f, 0.0f, 1.0f);
        float h = world.getRainGradient(tickCounter.getTickProgress(false)) * g * (bl ? 1.0f : 0.5f);
        this.fogMultiplier += (h - this.fogMultiplier) * f * 0.2f;
        data.environmentalStart = this.fogMultiplier * -160.0f;
        data.environmentalEnd = 1024.0f + -256.0f * this.fogMultiplier;
        data.skyEnd = viewDistance;
        data.cloudEnd = MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16;
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == CameraSubmersionType.ATMOSPHERIC;
    }
}

