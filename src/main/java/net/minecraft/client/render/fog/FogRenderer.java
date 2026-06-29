/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render.fog;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.BlindnessEffectFogModifier;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.client.render.fog.DimensionOrBossFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.LavaFogModifier;
import net.minecraft.client.render.fog.PowderSnowFogModifier;
import net.minecraft.client.render.fog.WaterFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class FogRenderer
implements AutoCloseable {
    final static public int FOG_UBO_SIZE = new Std140SizeCalculator().putVec4().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().get();
    final static private List<FogModifier> FOG_MODIFIERS = Lists.newArrayList((Object[])new FogModifier[]{new LavaFogModifier(), new PowderSnowFogModifier(), new BlindnessEffectFogModifier(), new DarknessEffectFogModifier(), new WaterFogModifier(), new DimensionOrBossFogModifier(), new AtmosphericFogModifier()});
    static private boolean fogEnabled = true;
    final private GpuBuffer emptyBuffer;
    final private MappableRingBuffer fogBuffer;

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public FogRenderer() {
        block4: {
            GpuDevice gpuDevice = RenderSystem.getDevice();
            this.fogBuffer = new MappableRingBuffer(() -> "Fog UBO", 130, FOG_UBO_SIZE);
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                ByteBuffer byteBuffer = memoryStack.malloc(FOG_UBO_SIZE);
                this.applyFog(byteBuffer, 0, new Vector4f(0.0f), Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
                this.emptyBuffer = gpuDevice.createBuffer(() -> "Empty fog", 128, byteBuffer.flip());
                if (memoryStack == null) break block4;
            }
        }
        RenderSystem.setShaderFog(this.getFogBuffer(FogType.NONE));
    }

    @Override
    public void close() {
        this.emptyBuffer.close();
        this.fogBuffer.close();
    }

    public void rotate() {
        this.fogBuffer.rotate();
    }

    public GpuBufferSlice getFogBuffer(FogType fogType) {
        if (!fogEnabled) {
            return this.emptyBuffer.slice(0, FOG_UBO_SIZE);
        }
        return switch (fogType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.emptyBuffer.slice(0, FOG_UBO_SIZE);
            case 1 -> this.fogBuffer.getBlocking().slice(0, FOG_UBO_SIZE);
        };
    }

    private Vector4f getFogColor(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, boolean thick) {
        LivingEntity livingEntity2;
        float l;
        CameraSubmersionType cameraSubmersionType = this.getCameraSubmersionType(camera, thick);
        Entity entity = camera.getFocusedEntity();
        FogModifier fogModifier = null;
        FogModifier fogModifier2 = null;
        for (FogModifier fogModifier3 : FOG_MODIFIERS) {
            if (fogModifier3.shouldApply(cameraSubmersionType, entity)) {
                if (fogModifier == null && fogModifier3.isColorSource()) {
                    fogModifier = fogModifier3;
                }
                if (fogModifier2 != null || !fogModifier3.isDarknessModifier()) continue;
                fogModifier2 = fogModifier3;
                continue;
            }
            fogModifier3.onSkipped();
        }
        if (fogModifier == null) {
            throw new IllegalStateException("No color source environment found");
        }
        int i = fogModifier.getFogColor(world, camera, viewDistance, skyDarkness);
        float f = world.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties().getVoidDarknessRange();
        float g = MathHelper.clamp((f + (float)world.getBottomY() - (float)camera.getPos().y) / f, 0.0f, 1.0f);
        if (fogModifier2 != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            g = fogModifier2.applyDarknessModifier(livingEntity, g, tickProgress);
        }
        float h = ColorHelper.getRedFloat(i);
        float j = ColorHelper.getGreenFloat(i);
        float k = ColorHelper.getBlueFloat(i);
        if (g > 0.0f && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            l = MathHelper.square(1.0f - g);
            h *= l;
            j *= l;
            k *= l;
        }
        if (skyDarkness > 0.0f) {
            h = MathHelper.lerp(skyDarkness, h, h * 0.7f);
            j = MathHelper.lerp(skyDarkness, j, j * 0.6f);
            k = MathHelper.lerp(skyDarkness, k, k * 0.6f);
        }
        l = cameraSubmersionType == CameraSubmersionType.WATER ? (entity instanceof ClientPlayerEntity ? ((ClientPlayerEntity)entity).getUnderwaterVisibility() : 1.0f) : (entity instanceof LivingEntity && (livingEntity2 = (LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION) && !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS) ? GameRenderer.getNightVisionStrength(livingEntity2, tickProgress) : 0.0f);
        if (h != 0.0f && j != 0.0f && k != 0.0f) {
            float m = 1.0f / Math.max(h, Math.max(j, k));
            h = MathHelper.lerp(l, h, h * m);
            j = MathHelper.lerp(l, j, j * m);
            k = MathHelper.lerp(l, k, k * m);
        }
        return new Vector4f(h, j, k, 1.0f);
    }

    public static boolean toggleFog() {
        fogEnabled = !fogEnabled;
        return fogEnabled;
    }

    public Vector4f applyFog(Camera camera, int viewDistance, boolean thick, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world) {
        float f = tickCounter.getTickProgress(false);
        Vector4f vector4f = this.getFogColor(camera, f, world, viewDistance, skyDarkness, thick);
        float g = viewDistance * 16;
        CameraSubmersionType cameraSubmersionType = this.getCameraSubmersionType(camera, thick);
        Entity entity = camera.getFocusedEntity();
        FogData fogData = new FogData();
        for (FogModifier fogModifier : FOG_MODIFIERS) {
            if (!fogModifier.shouldApply(cameraSubmersionType, entity)) continue;
            fogModifier.applyStartEndModifier(fogData, entity, camera.getBlockPos(), world, g, tickCounter);
            break;
        }
        float h = MathHelper.clamp(g / 10.0f, 4.0f, 64.0f);
        fogData.renderDistanceStart = g - h;
        fogData.renderDistanceEnd = g;
        GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.fogBuffer.getBlocking(), false, true);
        try {
            this.applyFog(mappedView.data(), 0, vector4f, fogData.environmentalStart, fogData.environmentalEnd, fogData.renderDistanceStart, fogData.renderDistanceEnd, fogData.skyEnd, fogData.cloudEnd);
            if (mappedView != null) {
                mappedView.close();
            }
        }
        catch (Throwable throwable) {
            if (mappedView != null) {
                try {
                    mappedView.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        return vector4f;
    }

    private CameraSubmersionType getCameraSubmersionType(Camera camera, boolean thick) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.NONE) {
            if (thick) {
                return CameraSubmersionType.DIMENSION_OR_BOSS;
            }
            return CameraSubmersionType.ATMOSPHERIC;
        }
        return cameraSubmersionType;
    }

    private void applyFog(ByteBuffer buffer, int bufPos, Vector4f fogColor, float environmentalStart, float environmentalEnd, float renderDistanceStart, float renderDistanceEnd, float skyEnd, float cloudEnd) {
        buffer.position(bufPos);
        Std140Builder.intoBuffer(buffer).putVec4((Vector4fc)fogColor).putFloat(environmentalStart).putFloat(environmentalEnd).putFloat(renderDistanceStart).putFloat(renderDistanceEnd).putFloat(skyEnd).putFloat(cloudEnd);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class FogType
    extends Enum<FogType> {
        final static public FogType NONE = new FogType();
        final static public FogType WORLD = new FogType();
        final static private FogType[] field_20947;

        public static FogType[] values() {
            return (FogType[])field_20947.clone();
        }

        public static FogType valueOf(String string) {
            return Enum.valueOf(FogType.class, string);
        }

        private static FogType[] method_36914() {
            return new FogType[]{NONE, WORLD};
        }

        static {
            field_20947 = FogType.method_36914();
        }
    }
}

