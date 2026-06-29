/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class CompiledShader
implements AutoCloseable {
    final static private int CLOSED = -1;
    final static public CompiledShader INVALID_SHADER = new CompiledShader(-1, Identifier.ofVanilla("invalid"), ShaderType.VERTEX);
    final private Identifier id;
    private int handle;
    final private ShaderType shaderType;

    public CompiledShader(int handle, Identifier id, ShaderType shaderType) {
        this.id = id;
        this.handle = handle;
        this.shaderType = shaderType;
    }

    public static CompiledShader compile(Identifier id, ShaderType type, String source) throws ShaderLoader.LoadException {
        RenderSystem.assertOnRenderThread();
        int i = GlStateManager.glCreateShader(GlConst.toGl(type));
        GlStateManager.glShaderSource(i, source);
        GlStateManager.glCompileShader(i);
        if (GlStateManager.glGetShaderi(i, GlConst.GL_COMPILE_STATUS) == 0) {
            String string = StringUtils.trim((String)GlStateManager.glGetShaderInfoLog(i, 32768));
            throw new ShaderLoader.LoadException("Couldn't compile " + type.getName() + " shader (" + String.valueOf(id) + ") : " + string);
        }
        return new CompiledShader(i, id, type);
    }

    @Override
    public void close() {
        if (this.handle == -1) {
            throw new IllegalStateException("Already closed");
        }
        RenderSystem.assertOnRenderThread();
        GlStateManager.glDeleteShader(this.handle);
        this.handle = -1;
    }

    public Identifier getId() {
        return this.id;
    }

    public int getHandle() {
        return this.handle;
    }

    public String getDebugLabel() {
        return this.shaderType.idConverter().toResourcePath(this.id).toString();
    }
}

