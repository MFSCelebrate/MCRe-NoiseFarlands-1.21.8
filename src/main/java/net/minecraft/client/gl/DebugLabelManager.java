/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.EXTDebugLabel
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL43
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.KHRDebug
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBufferManager;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.StringHelper;
import org.lwjgl.opengl.EXTDebugLabel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class DebugLabelManager {
    final static private Logger LOGGER = LogUtils.getLogger();

    public void labelGlGpuBuffer(GlGpuBuffer buffer) {
    }

    public void labelGlTexture(GlTexture texture) {
    }

    public void labelCompiledShader(CompiledShader shader) {
    }

    public void labelShaderProgram(ShaderProgram program) {
    }

    public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
    }

    public void pushDebugGroup(Supplier<String> labelGetter) {
    }

    public void popDebugGroup() {
    }

    public static DebugLabelManager create(GLCapabilities capabilities, boolean debugEnabled, Set<String> usedCapabilities) {
        if (debugEnabled) {
            if (capabilities.GL_KHR_debug && GlBackend.allowGlKhrDebug) {
                usedCapabilities.add("GL_KHR_debug");
                return new KHRDebugLabelManager();
            }
            if (capabilities.GL_EXT_debug_label && GlBackend.allowExtDebugLabel) {
                usedCapabilities.add("GL_EXT_debug_label");
                return new EXTDebugLabelManager();
            }
            LOGGER.warn("Debug labels unavailable: neither KHR_debug nor EXT_debug_label are supported");
        }
        return new NoOpDebugLabelManager();
    }

    public boolean isUsable() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static class KHRDebugLabelManager
    extends DebugLabelManager {
        final private int maxLabelLength = GL11.glGetInteger((int)KHRDebug.GL_MAX_LABEL_LENGTH);

        KHRDebugLabelManager() {
        }

        @Override
        public void labelGlGpuBuffer(GlGpuBuffer buffer) {
            Supplier<String> supplier = buffer.debugLabelSupplier;
            if (supplier != null) {
                KHRDebug.glObjectLabel((int)KHRDebug.GL_BUFFER, (int)buffer.id, (CharSequence)StringHelper.truncate(supplier.get(), this.maxLabelLength, true));
            }
        }

        @Override
        public void labelGlTexture(GlTexture texture) {
            KHRDebug.glObjectLabel((int)GL11.GL_TEXTURE, (int)texture.glId, (CharSequence)StringHelper.truncate(texture.getLabel(), this.maxLabelLength, true));
        }

        @Override
        public void labelCompiledShader(CompiledShader shader) {
            KHRDebug.glObjectLabel((int)KHRDebug.GL_SHADER, (int)shader.getHandle(), (CharSequence)StringHelper.truncate(shader.getDebugLabel(), this.maxLabelLength, true));
        }

        @Override
        public void labelShaderProgram(ShaderProgram program) {
            KHRDebug.glObjectLabel((int)KHRDebug.GL_PROGRAM, (int)program.getGlRef(), (CharSequence)StringHelper.truncate(program.getDebugLabel(), this.maxLabelLength, true));
        }

        @Override
        public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
            KHRDebug.glObjectLabel((int)GL11.GL_VERTEX_ARRAY, (int)buffer.glId, (CharSequence)StringHelper.truncate(buffer.vertexFormat.toString(), this.maxLabelLength, true));
        }

        @Override
        public void pushDebugGroup(Supplier<String> labelGetter) {
            KHRDebug.glPushDebugGroup((int)GL43.GL_DEBUG_SOURCE_APPLICATION, 0, (CharSequence)labelGetter.get());
        }

        @Override
        public void popDebugGroup() {
            KHRDebug.glPopDebugGroup();
        }

        @Override
        public boolean isUsable() {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class EXTDebugLabelManager
    extends DebugLabelManager {
        EXTDebugLabelManager() {
        }

        @Override
        public void labelGlGpuBuffer(GlGpuBuffer buffer) {
            Supplier<String> supplier = buffer.debugLabelSupplier;
            if (supplier != null) {
                EXTDebugLabel.glLabelObjectEXT((int)EXTDebugLabel.GL_BUFFER_OBJECT_EXT, (int)buffer.id, (CharSequence)StringHelper.truncate(supplier.get(), 256, true));
            }
        }

        @Override
        public void labelGlTexture(GlTexture texture) {
            EXTDebugLabel.glLabelObjectEXT((int)GL11.GL_TEXTURE, (int)texture.glId, (CharSequence)StringHelper.truncate(texture.getLabel(), 256, true));
        }

        @Override
        public void labelCompiledShader(CompiledShader shader) {
            EXTDebugLabel.glLabelObjectEXT((int)EXTDebugLabel.GL_SHADER_OBJECT_EXT, (int)shader.getHandle(), (CharSequence)StringHelper.truncate(shader.getDebugLabel(), 256, true));
        }

        @Override
        public void labelShaderProgram(ShaderProgram program) {
            EXTDebugLabel.glLabelObjectEXT((int)EXTDebugLabel.GL_PROGRAM_OBJECT_EXT, (int)program.getGlRef(), (CharSequence)StringHelper.truncate(program.getDebugLabel(), 256, true));
        }

        @Override
        public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
            EXTDebugLabel.glLabelObjectEXT((int)GL11.GL_VERTEX_ARRAY, (int)buffer.glId, (CharSequence)StringHelper.truncate(buffer.vertexFormat.toString(), 256, true));
        }

        @Override
        public boolean isUsable() {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class NoOpDebugLabelManager
    extends DebugLabelManager {
        NoOpDebugLabelManager() {
        }
    }
}

