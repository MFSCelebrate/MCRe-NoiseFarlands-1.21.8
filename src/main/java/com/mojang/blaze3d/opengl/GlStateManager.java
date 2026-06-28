/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.mojang.jtracy.Plot
 *  com.mojang.jtracy.TracyClient
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL32
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.opengl;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class GlStateManager {
    final static private boolean ON_LINUX = Util.getOperatingSystem() == Util.OperatingSystem.LINUX;
    final static private Plot PLOT_TEXTURES = TracyClient.createPlot((String)"GPU Textures");
    static private int numTextures = 0;
    final static private Plot PLOT_BUFFERS = TracyClient.createPlot((String)"GPU Buffers");
    static private int numBuffers = 0;
    final static private BlendFuncState BLEND = new BlendFuncState();
    final static private DepthTestState DEPTH = new DepthTestState();
    final static private CullFaceState CULL = new CullFaceState();
    final static private PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    final static private LogicOpState COLOR_LOGIC = new LogicOpState();
    final static private ScissorTestState SCISSOR = new ScissorTestState();
    static private int activeTexture;
    final static private Texture2DState[] TEXTURES;
    final static private ColorMask COLOR_MASK;
    static private int readFbo;
    static private int writeFbo;

    public static void _disableScissorTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.SCISSOR.capState.disable();
    }

    public static void _enableScissorTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.SCISSOR.capState.enable();
    }

    public static void _scissorBox(int x, int y, int width, int height) {
        RenderSystem.assertOnRenderThread();
        GL20.glScissor((int)x, (int)y, (int)width, (int)height);
    }

    public static void _disableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.DEPTH.capState.disable();
    }

    public static void _enableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.DEPTH.capState.enable();
    }

    public static void _depthFunc(int func) {
        RenderSystem.assertOnRenderThread();
        if (func != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = func;
            GL11.glDepthFunc((int)func);
        }
    }

    public static void _depthMask(boolean mask) {
        RenderSystem.assertOnRenderThread();
        if (mask != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = mask;
            GL11.glDepthMask((boolean)mask);
        }
    }

    public static void _disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.capState.disable();
    }

    public static void _enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.capState.enable();
    }

    public static void _blendFuncSeparate(int srcFactorRGB, int dstFactorRgb, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        if (srcFactorRGB != GlStateManager.BLEND.srcFactorRgb || dstFactorRgb != GlStateManager.BLEND.dstFactorRgb || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRgb = srcFactorRGB;
            GlStateManager.BLEND.dstFactorRgb = dstFactorRgb;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            GlStateManager.glBlendFuncSeparate(srcFactorRGB, dstFactorRgb, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static int glGetProgrami(int program, int pname) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgrami((int)program, (int)pname);
    }

    public static void glAttachShader(int program, int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glAttachShader((int)program, (int)shader);
    }

    public static void glDeleteShader(int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteShader((int)shader);
    }

    public static int glCreateShader(int type) {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateShader((int)type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public static void glShaderSource(int shader, String source) {
        ByteBuffer byteBuffer;
        block6: {
            RenderSystem.assertOnRenderThread();
            byte[] bs = source.getBytes(Charsets.UTF_8);
            byteBuffer = MemoryUtil.memAlloc((int)(bs.length + 1));
            byteBuffer.put(bs);
            byteBuffer.put(0);
            byteBuffer.flip();
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                pointerBuffer.put(byteBuffer);
                GL20C.nglShaderSource((int)shader, 1, (long)pointerBuffer.address0(), 0L);
                if (memoryStack == null) break block6;
            }
            catch (Throwable throwable) {
                MemoryUtil.memFree((Buffer)byteBuffer);
                throw throwable;
            }
        }
        MemoryUtil.memFree((Buffer)byteBuffer);
    }

    public static void glCompileShader(int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glCompileShader((int)shader);
    }

    public static int glGetShaderi(int shader, int pname) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderi((int)shader, (int)pname);
    }

    public static void _glUseProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glUseProgram((int)program);
    }

    public static int glCreateProgram() {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteProgram((int)program);
    }

    public static void glLinkProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glLinkProgram((int)program);
    }

    public static int _glGetUniformLocation(int program, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetUniformLocation((int)program, (CharSequence)name);
    }

    public static void _glUniform1(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1iv((int)location, (IntBuffer)value);
    }

    public static void _glUniform1i(int location, int value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1i((int)location, (int)value);
    }

    public static void _glUniform1(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniform2(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniform3(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3iv((int)location, (IntBuffer)value);
    }

    public static void _glUniform3(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniform4(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniformMatrix4(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)value);
    }

    public static void _glBindAttribLocation(int program, int index, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        GL20.glBindAttribLocation((int)program, (int)index, (CharSequence)name);
    }

    public static int _glGenBuffers() {
        RenderSystem.assertOnRenderThread();
        PLOT_BUFFERS.setValue((double)(++numBuffers));
        return GL15.glGenBuffers();
    }

    public static int _glGenVertexArrays() {
        RenderSystem.assertOnRenderThread();
        return GL30.glGenVertexArrays();
    }

    public static void _glBindBuffer(int target, int buffer) {
        RenderSystem.assertOnRenderThread();
        GL15.glBindBuffer((int)target, (int)buffer);
    }

    public static void _glBindVertexArray(int array) {
        RenderSystem.assertOnRenderThread();
        GL30.glBindVertexArray((int)array);
    }

    public static void _glBufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData((int)target, (ByteBuffer)data, (int)usage);
    }

    public static void _glBufferSubData(int target, int offset, ByteBuffer data) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferSubData((int)target, (long)offset, (ByteBuffer)data);
    }

    public static void _glBufferData(int target, long size, int usage) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData((int)target, (long)size, (int)usage);
    }

    @Nullable
    public static ByteBuffer _glMapBufferRange(int target, int offset, int range, int access) {
        RenderSystem.assertOnRenderThread();
        return GL30.glMapBufferRange((int)target, (long)offset, (long)range, (int)access);
    }

    public static void _glUnmapBuffer(int target) {
        RenderSystem.assertOnRenderThread();
        GL15.glUnmapBuffer((int)target);
    }

    public static void _glDeleteBuffers(int buffer) {
        RenderSystem.assertOnRenderThread();
        PLOT_BUFFERS.setValue((double)(--numBuffers));
        GL15.glDeleteBuffers((int)buffer);
    }

    public static void _glBindFramebuffer(int target, int framebuffer) {
        if ((target == GlConst.GL_READ_FRAMEBUFFER || target == GlConst.GL_FRAMEBUFFER) && readFbo != framebuffer) {
            GL30.glBindFramebuffer((int)GlConst.GL_READ_FRAMEBUFFER, (int)framebuffer);
            readFbo = framebuffer;
        }
        if ((target == GlConst.GL_DRAW_FRAMEBUFFER || target == GlConst.GL_FRAMEBUFFER) && writeFbo != framebuffer) {
            GL30.glBindFramebuffer((int)GlConst.GL_DRAW_FRAMEBUFFER, (int)framebuffer);
            writeFbo = framebuffer;
        }
    }

    public static int getFrameBuffer(int target) {
        if (target == GlConst.GL_READ_FRAMEBUFFER) {
            return readFbo;
        }
        if (target == GlConst.GL_DRAW_FRAMEBUFFER) {
            return writeFbo;
        }
        return 0;
    }

    public static void _glBlitFrameBuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        RenderSystem.assertOnRenderThread();
        GL30.glBlitFramebuffer((int)srcX0, (int)srcY0, (int)srcX1, (int)srcY1, (int)dstX0, (int)dstY0, (int)dstX1, (int)dstY1, (int)mask, (int)filter);
    }

    public static void _glDeleteFramebuffers(int framebuffer) {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteFramebuffers((int)framebuffer);
    }

    public static int glGenFramebuffers() {
        RenderSystem.assertOnRenderThread();
        return GL30.glGenFramebuffers();
    }

    public static void _glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        RenderSystem.assertOnRenderThread();
        GL30.glFramebufferTexture2D((int)target, (int)attachment, (int)textureTarget, (int)texture, (int)level);
    }

    public static void glActiveTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        GL13.glActiveTexture((int)texture);
    }

    public static void glBlendFuncSeparate(int srcFactorRgb, int dstFactorRgb, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendFuncSeparate((int)srcFactorRgb, (int)dstFactorRgb, (int)srcFactorAlpha, (int)dstFactorAlpha);
    }

    public static String glGetShaderInfoLog(int shader, int maxLength) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderInfoLog((int)shader, (int)maxLength);
    }

    public static String glGetProgramInfoLog(int program, int maxLength) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgramInfoLog((int)program, (int)maxLength);
    }

    public static void _enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.capState.enable();
    }

    public static void _disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.capState.disable();
    }

    public static void _polygonMode(int face, int mode) {
        RenderSystem.assertOnRenderThread();
        GL11.glPolygonMode((int)face, (int)mode);
    }

    public static void _enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.capFill.enable();
    }

    public static void _disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.capFill.disable();
    }

    public static void _polygonOffset(float factor, float units) {
        RenderSystem.assertOnRenderThread();
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset((float)factor, (float)units);
        }
    }

    public static void _enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.capState.enable();
    }

    public static void _disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.capState.disable();
    }

    public static void _logicOp(int op) {
        RenderSystem.assertOnRenderThread();
        if (op != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = op;
            GL11.glLogicOp((int)op);
        }
    }

    public static void _activeTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        if (activeTexture != texture - GlConst.GL_TEXTURE0) {
            activeTexture = texture - GlConst.GL_TEXTURE0;
            GlStateManager.glActiveTexture(texture);
        }
    }

    public static void _texParameter(int target, int pname, int param) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexParameteri((int)target, (int)pname, (int)param);
    }

    public static int _getTexLevelParameter(int target, int level, int pname) {
        return GL11.glGetTexLevelParameteri((int)target, (int)level, (int)pname);
    }

    public static int _genTexture() {
        RenderSystem.assertOnRenderThread();
        PLOT_TEXTURES.setValue((double)(++numTextures));
        return GL11.glGenTextures();
    }

    public static void _deleteTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        GL11.glDeleteTextures((int)texture);
        for (Texture2DState texture2DState : TEXTURES) {
            if (texture2DState.boundTexture != texture) continue;
            texture2DState.boundTexture = -1;
        }
        PLOT_TEXTURES.setValue((double)(--numTextures));
    }

    public static void _bindTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture((int)GlConst.GL_TEXTURE_2D, (int)texture);
        }
    }

    public static int _getActiveTexture() {
        return activeTexture + 33984;
    }

    public static void _texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexImage2D((int)target, (int)level, (int)internalFormat, (int)width, (int)height, (int)border, (int)format, (int)type, (IntBuffer)pixels);
    }

    public static void _texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D((int)target, (int)level, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static void _texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, IntBuffer pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D((int)target, (int)level, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)format, (int)type, (IntBuffer)pixels);
    }

    public static void _viewport(int x, int y, int width, int height) {
        GL11.glViewport((int)x, (int)y, (int)width, (int)height);
    }

    public static void _colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertOnRenderThread();
        if (red != GlStateManager.COLOR_MASK.red || green != GlStateManager.COLOR_MASK.green || blue != GlStateManager.COLOR_MASK.blue || alpha != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = red;
            GlStateManager.COLOR_MASK.green = green;
            GlStateManager.COLOR_MASK.blue = blue;
            GlStateManager.COLOR_MASK.alpha = alpha;
            GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
        }
    }

    public static void _clear(int mask) {
        RenderSystem.assertOnRenderThread();
        GL11.glClear((int)mask);
        if (MacWindowUtil.IS_MAC) {
            GlStateManager._getError();
        }
    }

    public static void _vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        RenderSystem.assertOnRenderThread();
        GL20.glVertexAttribPointer((int)index, (int)size, (int)type, (boolean)normalized, (int)stride, (long)pointer);
    }

    public static void _vertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
        RenderSystem.assertOnRenderThread();
        GL30.glVertexAttribIPointer((int)index, (int)size, (int)type, (int)stride, (long)pointer);
    }

    public static void _enableVertexAttribArray(int index) {
        RenderSystem.assertOnRenderThread();
        GL20.glEnableVertexAttribArray((int)index);
    }

    public static void _drawElements(int mode, int type, int count, long indices) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawElements((int)mode, (int)type, (int)count, (long)indices);
    }

    public static void _drawArrays(int mode, int first, int count) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawArrays((int)mode, (int)first, (int)count);
    }

    public static void _pixelStore(int pname, int param) {
        RenderSystem.assertOnRenderThread();
        GL11.glPixelStorei((int)pname, (int)param);
    }

    public static void _readPixels(int x, int y, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static int _getError() {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetError();
    }

    public static void clearGlErrors() {
        RenderSystem.assertOnRenderThread();
        while (GL11.glGetError() != 0) {
        }
    }

    public static String _getString(int name) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetString((int)name);
    }

    public static int _getInteger(int pname) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetInteger((int)pname);
    }

    public static long _glFenceSync(int condition, int flags) {
        RenderSystem.assertOnRenderThread();
        return GL32.glFenceSync((int)condition, (int)flags);
    }

    public static int _glClientWaitSync(long sync, int flags, long timeout) {
        RenderSystem.assertOnRenderThread();
        return GL32.glClientWaitSync((long)sync, (int)flags, (long)timeout);
    }

    public static void _glDeleteSync(long sync) {
        RenderSystem.assertOnRenderThread();
        GL32.glDeleteSync((long)sync);
    }

    static {
        TEXTURES = (Texture2DState[])IntStream.range(0, 12).mapToObj(index -> new Texture2DState()).toArray(Texture2DState[]::new);
        COLOR_MASK = new ColorMask();
    }

    @Environment(value=EnvType.CLIENT)
    static class ScissorTestState {
        final public CapabilityTracker capState = new CapabilityTracker(GL11.GL_SCISSOR_TEST);

        ScissorTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CapabilityTracker {
        final private int cap;
        private boolean state;

        public CapabilityTracker(int cap) {
            this.cap = cap;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean state) {
            RenderSystem.assertOnRenderThread();
            if (state != this.state) {
                this.state = state;
                if (state) {
                    GL11.glEnable((int)this.cap);
                } else {
                    GL11.glDisable((int)this.cap);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DepthTestState {
        final public CapabilityTracker capState = new CapabilityTracker(GL11.GL_DEPTH_TEST);
        public boolean mask = true;
        public int func = 513;

        DepthTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlendFuncState {
        final public CapabilityTracker capState = new CapabilityTracker(GL11.GL_BLEND);
        public int srcFactorRgb = 1;
        public int dstFactorRgb = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        BlendFuncState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CullFaceState {
        final public CapabilityTracker capState = new CapabilityTracker(GL11.GL_CULL_FACE);

        CullFaceState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PolygonOffsetState {
        final public CapabilityTracker capFill = new CapabilityTracker(32823);
        public float factor;
        public float units;

        PolygonOffsetState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LogicOpState {
        final public CapabilityTracker capState = new CapabilityTracker(GL11.GL_COLOR_LOGIC_OP);
        public int op = 5379;

        LogicOpState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture2DState {
        public int boundTexture;

        Texture2DState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        ColorMask() {
        }
    }
}

