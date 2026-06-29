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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class FogModifier {
    public abstract void applyStartEndModifier(FogData var1, Entity var2, BlockPos var3, ClientWorld var4, float var5, RenderTickCounter var6);

    public boolean isColorSource() {
        return true;
    }

    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        return -1;
    }

    public boolean isDarknessModifier() {
        return false;
    }

    public float applyDarknessModifier(LivingEntity cameraEntity, float darkness, float tickProgress) {
        return darkness;
    }

    public abstract boolean shouldApply(@Nullable CameraSubmersionType var1, Entity var2);

    public void onSkipped() {
    }
}

