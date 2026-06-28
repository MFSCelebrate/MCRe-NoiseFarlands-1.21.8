/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class SkyRendering
implements AutoCloseable {
    final static private Identifier SUN_TEXTURE = Identifier.ofVanilla("textures/environment/sun.png");
    final static private Identifier MOON_PHASES_TEXTURE = Identifier.ofVanilla("textures/environment/moon_phases.png");
    final static public Identifier END_SKY_TEXTURE = Identifier.ofVanilla("textures/environment/end_sky.png");
    final static private float field_53144 = 512.0f;
    final static private int field_57932 = 10;
    final static private int field_57933 = 1500;
    final static private int field_57934 = 6;
    final private GpuBuffer starVertexBuffer;
    final private RenderSystem.ShapeIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
    final private GpuBuffer topSkyVertexBuffer;
    final private GpuBuffer bottomSkyVertexBuffer;
    final private GpuBuffer endSkyVertexBuffer;
    private int starIndexCount;

    public SkyRendering() {
        this.starVertexBuffer = this.createStars();
        this.endSkyVertexBuffer = SkyRendering.createEndSky();
        BufferAllocator bufferAllocator = BufferAllocator.method_72201(10 * VertexFormats.POSITION.getVertexSize());
        try {
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
            this.createSky(bufferBuilder, 16.0f);
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                this.topSkyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Top sky vertex buffer", 32, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
            this.createSky(bufferBuilder, -16.0f);
            builtBuffer = bufferBuilder.end();
            try {
                this.bottomSkyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Bottom sky vertex buffer", 32, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                }
                throw throwable;
            }
            if (bufferAllocator != null) {
                bufferAllocator.close();
            }
        }
        catch (Throwable throwable) {
            if (bufferAllocator != null) {
                try {
                    bufferAllocator.close();
                }
                catch (Throwable throwable4) {
                    throwable.addSuppressed(throwable4);
                }
            }
            throw throwable;
        }
    }

    private GpuBuffer createStars() {
        Random random = Random.create(10842L);
        float f = 100.0f;
        BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION.getVertexSize() * 1500 * 4);
        BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            float g = random.nextFloat() * 2.0f - 1.0f;
            float h = random.nextFloat() * 2.0f - 1.0f;
            float j = random.nextFloat() * 2.0f - 1.0f;
            float k = 0.15f + random.nextFloat() * 0.1f;
            float l = MathHelper.magnitude(g, h, j);
            if (l <= 0.010000001f || l >= 1.0f) continue;
            Vector3f vector3f = new Vector3f(g, h, j).normalize(100.0f);
            float m = (float)(random.nextDouble() * 3.1415927410125732 * 2.0);
            Matrix3f matrix3f = new Matrix3f().rotateTowards((Vector3fc)new Vector3f((Vector3fc)vector3f).negate(), (Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f)).rotateZ(-m);
            bufferBuilder.vertex(new Vector3f(k, -k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
            bufferBuilder.vertex(new Vector3f(k, k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
            bufferBuilder.vertex(new Vector3f(-k, k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
            bufferBuilder.vertex(new Vector3f(-k, -k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
        }
        BuiltBuffer builtBuffer = bufferBuilder.end();
        try {
            this.starIndexCount = builtBuffer.getDrawParameters().indexCount();
            GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Stars vertex buffer", 40, builtBuffer.getBuffer());
            if (builtBuffer != null) {
                builtBuffer.close();
            }
            if (bufferAllocator != null) {
                bufferAllocator.close();
            }
            return gpuBuffer;
        }
        catch (Throwable throwable) {
            try {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (Throwable throwable3) {
                if (bufferAllocator != null) {
                    try {
                        bufferAllocator.close();
                    }
                    catch (Throwable throwable4) {
                        throwable3.addSuppressed(throwable4);
                    }
                }
                throw throwable3;
            }
        }
    }

    private void createSky(VertexConsumer vertexConsumer, float height) {
        float f = Math.signum(height) * 512.0f;
        vertexConsumer.vertex(0.0f, height, 0.0f);
        for (int i = -180; 1 <= 180; i += 45) {
            vertexConsumer.vertex(f * MathHelper.cos((float)Math.PI / 180), height, 512.0f * MathHelper.sin((float)Math.PI / 180));
        }
    }

    public void renderTopSky(float red, float green, float blue) {
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(red, green, blue, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky disc", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());
        try {
            renderPass.setPipeline(RenderPipelines.POSITION_SKY);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.topSkyVertexBuffer);
            renderPass.draw(0, 10);
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    public void renderSkyDark() {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.translate(0.0f, 12.0f, 0.0f);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky dark", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());
        try {
            renderPass.setPipeline(RenderPipelines.POSITION_SKY);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.bottomSkyVertexBuffer);
            renderPass.draw(0, 10);
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        matrix4fStack.popMatrix();
    }

    public void renderCelestialBodies(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float rot, int phase, float alpha, float starBrightness) {
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(rot * 360.0f));
        this.renderSun(alpha, vertexConsumers, matrices);
        this.renderMoon(phase, alpha, vertexConsumers, matrices);
        vertexConsumers.draw();
        if (starBrightness > 0.0f) {
            this.renderStars(starBrightness, matrices);
        }
        matrices.pop();
    }

    private void renderSun(float alpha, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        float f = 30.0f;
        float g = 100.0f;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCelestial(SUN_TEXTURE));
        int i = ColorHelper.getWhite(alpha);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, -30.0f, 100.0f, -30.0f).texture(0.0f, 0.0f).color(i);
        vertexConsumer.vertex(matrix4f, 30.0f, 100.0f, -30.0f).texture(1.0f, 0.0f).color(i);
        vertexConsumer.vertex(matrix4f, 30.0f, 100.0f, 30.0f).texture(1.0f, 1.0f).color(i);
        vertexConsumer.vertex(matrix4f, -30.0f, 100.0f, 30.0f).texture(0.0f, 1.0f).color(i);
    }

    private void renderMoon(int phase, float alpha, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        float f = 20.0f;
        int i = phase % 4;
        int j = phase / 4 % 2;
        float g = (float)(i + 0) / 4.0f;
        float h = (float)(j + 0) / 2.0f;
        float k = (float)(i + 1) / 4.0f;
        float l = (float)(j + 1) / 2.0f;
        float m = 100.0f;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCelestial(MOON_PHASES_TEXTURE));
        int n = ColorHelper.getWhite(alpha);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, -20.0f, -100.0f, 20.0f).texture(k, l).color(n);
        vertexConsumer.vertex(matrix4f, 20.0f, -100.0f, 20.0f).texture(g, l).color(n);
        vertexConsumer.vertex(matrix4f, 20.0f, -100.0f, -20.0f).texture(g, h).color(n);
        vertexConsumer.vertex(matrix4f, -20.0f, -100.0f, -20.0f).texture(k, h).color(n);
    }

    private void renderStars(float brightness, MatrixStack matrices) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)matrices.peek().getPositionMatrix());
        RenderPipeline renderPipeline = RenderPipelines.POSITION_STARS;
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBuffer gpuBuffer = this.indexBuffer.getIndexBuffer(this.starIndexCount);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(brightness, brightness, brightness, brightness), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Stars", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());
        try {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.starVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer.getIndexType());
            renderPass.drawIndexed(0, 0, this.starIndexCount, 1);
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        matrix4fStack.popMatrix();
    }

    public void renderGlowingSky(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float angleRadians, int color) {
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        float f = MathHelper.sin(angleRadians) < 0.0f ? 180.0f : 0.0f;
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getSunriseSunset());
        float g = ColorHelper.getAlphaFloat(color);
        vertexConsumer.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(color);
        int i = ColorHelper.zeroAlpha(color);
        int j = 16;
        for (int k = 0; k <= 16; ++k) {
            float h = (float)k * ((float)Math.PI * 2) / 16.0f;
            float l = MathHelper.sin(h);
            float m = MathHelper.cos(h);
            vertexConsumer.vertex(matrix4f, l * 120.0f, m * 120.0f, -m * 40.0f * g).color(i);
        }
        matrices.pop();
    }

    private static GpuBuffer createEndSky() {
        BufferAllocator bufferAllocator = BufferAllocator.method_72201(24 * VertexFormats.POSITION_TEXTURE_COLOR.getVertexSize());
        BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        for (int i = 0; i < 6; ++i) {
            Matrix4f matrix4f = new Matrix4f();
            switch (i) {
                case 1: {
                    matrix4f.rotationX(1.5707964f);
                    break;
                }
                case 2: {
                    matrix4f.rotationX(-1.5707964f);
                    break;
                }
                case 3: {
                    matrix4f.rotationX((float)Math.PI);
                    break;
                }
                case 4: {
                    matrix4f.rotationZ(1.5707964f);
                    break;
                }
                case 5: {
                    matrix4f.rotationZ(-1.5707964f);
                }
            }
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(-14145496);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(-14145496);
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(-14145496);
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(-14145496);
        }
        BuiltBuffer builtBuffer = bufferBuilder.end();
        try {
            GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "End sky vertex buffer", 40, builtBuffer.getBuffer());
            if (builtBuffer != null) {
                builtBuffer.close();
            }
            if (bufferAllocator != null) {
                bufferAllocator.close();
            }
            return gpuBuffer;
        }
        catch (Throwable throwable) {
            try {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (Throwable throwable3) {
                if (bufferAllocator != null) {
                    try {
                        bufferAllocator.close();
                    }
                    catch (Throwable throwable4) {
                        throwable3.addSuppressed(throwable4);
                    }
                }
                throw throwable3;
            }
        }
    }

    public void renderEndSky() {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(END_SKY_TEXTURE);
        abstractTexture.setUseMipmaps(false);
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "End sky", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());
        try {
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_END_SKY);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindSampler("Sampler0", abstractTexture.getGlTextureView());
            renderPass.setVertexBuffer(0, this.endSkyVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.drawIndexed(0, 0, 36, 1);
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    @Override
    public void close() {
        this.starVertexBuffer.close();
        this.topSkyVertexBuffer.close();
        this.bottomSkyVertexBuffer.close();
        this.endSkyVertexBuffer.close();
    }
}

