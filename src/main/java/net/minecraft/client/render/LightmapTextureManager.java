/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    final static public int MAX_LIGHT_COORDINATE = 0xF000F0;
    final static public int MAX_SKY_LIGHT_COORDINATE = 0xF00000;
    final static public int MAX_BLOCK_LIGHT_COORDINATE = 240;
    final static private int field_53098 = 16;
    final static private int UBO_SIZE = new Std140SizeCalculator().putFloat().putFloat().putFloat().putInt().putFloat().putFloat().putFloat().putFloat().putVec3().get();
    final private GpuTexture glTexture;
    final private GpuTextureView glTextureView;
    private boolean dirty;
    private float flickerIntensity;
    final private GameRenderer renderer;
    final private MinecraftClient client;
    final private MappableRingBuffer buffer;

    public LightmapTextureManager(GameRenderer renderer, MinecraftClient client) {
        this.renderer = renderer;
        this.client = client;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.glTexture = gpuDevice.createTexture("Light Texture", 12, TextureFormat.RGBA8, 16, 16, 1, 1);
        this.glTexture.setTextureFilter(FilterMode.LINEAR, false);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
        gpuDevice.createCommandEncoder().clearColorTexture(this.glTexture, -1);
        this.buffer = new MappableRingBuffer(() -> "Lightmap UBO", 130, UBO_SIZE);
    }

    public GpuTextureView getGlTextureView() {
        return this.glTextureView;
    }

    @Override
    public void close() {
        this.glTexture.close();
        this.glTextureView.close();
        this.buffer.close();
    }

    public void tick() {
        this.flickerIntensity += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.flickerIntensity *= 0.9f;
        this.dirty = true;
    }

    public void disable() {
        RenderSystem.setShaderTexture(2, null);
    }

    public void enable() {
        RenderSystem.setShaderTexture(2, this.glTextureView);
    }

    private float getDarkness(LivingEntity entity, float factor, float tickProgress) {
        float f = 0.45f * factor;
        return Math.max(0.0f, MathHelper.cos(((float)entity.age - tickProgress) * (float)Math.PI * 0.025f) * f);
    }

    public void update(float tickProgress) {
        if (!this.dirty) {
            return;
        }
        this.dirty = false;
        Profiler profiler = Profilers.get();
        profiler.push("lightTex");
        ClientWorld clientWorld = this.client.world;
        if (clientWorld == null) {
            return;
        }
        float f = clientWorld.getSkyBrightness(1.0f);
        float g = clientWorld.getLightningTicksLeft() > 0 ? 1.0f : f * 0.95f + 0.05f;
        float h = this.client.options.getDarknessEffectScale().getValue().floatValue();
        float i = this.client.player.getEffectFadeFactor(StatusEffects.DARKNESS, tickProgress) * h;
        float j = this.getDarkness(this.client.player, i, tickProgress) * h;
        float k = this.client.player.getUnderwaterVisibility();
        float l = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength(this.client.player, tickProgress) : (k > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? k : 0.0f);
        Vector3f vector3f = new Vector3f(f, f, 1.0f).lerp((Vector3fc)new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float m = this.flickerIntensity + 1.5f;
        float n = clientWorld.getDimension().ambientLight();
        boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
        float o = this.client.options.getGamma().getValue().floatValue();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6);
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.buffer.getBlocking(), false, true);
        try {
            Std140Builder.intoBuffer(mappedView.data()).putFloat(n).putFloat(g).putFloat(m).putInt(bl ? 1 : 0).putFloat(l).putFloat(j).putFloat(this.renderer.getSkyDarkness(tickProgress)).putFloat(Math.max(0.0f, o - i)).putVec3((Vector3fc)vector3f);
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
        RenderPass renderPass = commandEncoder.createRenderPass(() -> "Update light", this.glTextureView, OptionalInt.empty());
        try {
            renderPass.setPipeline(RenderPipelines.BILT_SCREEN_LIGHTMAP);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("LightmapInfo", this.buffer.getBlocking());
            renderPass.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.drawIndexed(0, 0, 6, 1);
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable3) {
                    throwable.addSuppressed(throwable3);
                }
            }
            throw throwable;
        }
        this.buffer.rotate();
        profiler.pop();
    }

    public static float getBrightness(DimensionType type, int lightLevel) {
        return LightmapTextureManager.getBrightness(type.ambientLight(), lightLevel);
    }

    public static float getBrightness(float ambientLight, int lightLevel) {
        float f = (float)lightLevel / 15.0f;
        float g = f / (4.0f - 3.0f * f);
        return MathHelper.lerp(ambientLight, g, 1.0f);
    }

    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    public static int getBlockLightCoordinates(int light) {
        return light >>> 4 & 0xF;
    }

    public static int getSkyLightCoordinates(int light) {
        return light >>> 20 & 0xF;
    }

    public static int applyEmission(int light, int lightEmission) {
        if (lightEmission == 0) {
            return light;
        }
        int i = Math.max(LightmapTextureManager.getSkyLightCoordinates(light), lightEmission);
        int j = Math.max(LightmapTextureManager.getBlockLightCoordinates(light), lightEmission);
        return LightmapTextureManager.pack(j, i);
    }
}

