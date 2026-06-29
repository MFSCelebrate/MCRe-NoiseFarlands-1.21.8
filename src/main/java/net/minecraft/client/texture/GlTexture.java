/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlTexture
extends GpuTexture {
    final protected int glId;
    final private Int2IntMap depthTexToFramebufferIdCache = new Int2IntOpenHashMap();
    protected boolean closed;
    protected boolean needsReinit = true;
    private int refCount;

    protected GlTexture(int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int glId) {
        super(usage, label, format, width, height, depthOrLayers, mipLevels);
        this.glId = glId;
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.refCount == 0) {
            this.free();
        }
    }

    private void free() {
        GlStateManager._deleteTexture(this.glId);
        IntIterator intIterator = this.depthTexToFramebufferIdCache.values().iterator();
        while (intIterator.hasNext()) {
            int i = (Integer)intIterator.next();
            GlStateManager._glDeleteFramebuffers(i);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    public int getOrCreateFramebuffer(BufferManager manager, @Nullable GpuTexture depthTexture) {
        int i = depthTexture == null ? 0 : ((GlTexture)depthTexture).glId;
        return this.depthTexToFramebufferIdCache.computeIfAbsent(1, unused -> {
            int j = manager.createFramebuffer();
            manager.setupFramebuffer(j, this.glId, 1, 0, 0);
            return j;
        });
    }

    public void checkDirty(int target) {
        if (this.needsReinit) {
            GlStateManager._texParameter(target, GlConst.GL_TEXTURE_WRAP_S, GlConst.toGl(this.addressModeU));
            GlStateManager._texParameter(target, GlConst.GL_TEXTURE_WRAP_T, GlConst.toGl(this.addressModeV));
            switch (this.minFilter) {
                case NEAREST: {
                    GlStateManager._texParameter(target, GlConst.GL_TEXTURE_MIN_FILTER, this.useMipmaps ? GlConst.GL_NEAREST_MIPMAP_LINEAR : GlConst.GL_NEAREST);
                    break;
                }
                case LINEAR: {
                    GlStateManager._texParameter(target, GlConst.GL_TEXTURE_MIN_FILTER, this.useMipmaps ? GlConst.GL_LINEAR_MIPMAP_LINEAR : GlConst.GL_LINEAR);
                }
            }
            switch (this.magFilter) {
                case NEAREST: {
                    GlStateManager._texParameter(target, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
                    break;
                }
                case LINEAR: {
                    GlStateManager._texParameter(target, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
                }
            }
            this.needsReinit = false;
        }
    }

    public int getGlId() {
        return this.glId;
    }

    @Override
    public void setAddressMode(AddressMode addressMode, AddressMode addressMode2) {
        super.setAddressMode(addressMode, addressMode2);
        this.needsReinit = true;
    }

    @Override
    public void setTextureFilter(FilterMode filterMode, FilterMode filterMode2, boolean bl) {
        super.setTextureFilter(filterMode, filterMode2, bl);
        this.needsReinit = true;
    }

    @Override
    public void setUseMipmaps(boolean bl) {
        super.setUseMipmaps(bl);
        this.needsReinit = true;
    }

    public void incrementRefCount() {
        ++this.refCount;
    }

    public void decrementRefCount() {
        --this.refCount;
        if (this.closed && this.refCount == 0) {
            this.free();
        }
    }
}

