/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(value=EnvType.CLIENT)
public final class BlockRenderLayer
extends Enum<BlockRenderLayer> {
    final static public BlockRenderLayer SOLID = new BlockRenderLayer(RenderPipelines.SOLID, 0x400000, true, false);
    final static public BlockRenderLayer CUTOUT_MIPPED = new BlockRenderLayer(RenderPipelines.CUTOUT_MIPPED, 0x400000, true, false);
    final static public BlockRenderLayer CUTOUT = new BlockRenderLayer(RenderPipelines.CUTOUT, 786432, false, false);
    final static public BlockRenderLayer TRANSLUCENT = new BlockRenderLayer(RenderPipelines.TRANSLUCENT, 786432, true, true);
    final static public BlockRenderLayer TRIPWIRE = new BlockRenderLayer(RenderPipelines.TRIPWIRE, 1536, true, true);
    final private RenderPipeline pipeline;
    final private int size;
    final private boolean mipmap;
    final private boolean translucent;
    final private String name;
    final static private BlockRenderLayer[] field_60933;

    public static BlockRenderLayer[] values() {
        return (BlockRenderLayer[])field_60933.clone();
    }

    public static BlockRenderLayer valueOf(String string) {
        return Enum.valueOf(BlockRenderLayer.class, string);
    }

    private BlockRenderLayer(RenderPipeline pipeline, int size, boolean mipmap, boolean translucent) {
        this.pipeline = pipeline;
        this.size = size;
        this.mipmap = mipmap;
        this.translucent = translucent;
        this.name = this.toString().toLowerCase(Locale.ROOT);
    }

    public RenderPipeline getPipeline() {
        return this.pipeline;
    }

    public int getBufferSize() {
        return this.size;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTranslucent() {
        return this.translucent;
    }

    public GpuTextureView getTextureView() {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        abstractTexture.setUseMipmaps(this.mipmap);
        return abstractTexture.getGlTextureView();
    }

    public Framebuffer getFramebuffer() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        switch (this.ordinal()) {
            case 4: {
                Framebuffer framebuffer = minecraftClient.worldRenderer.getWeatherFramebuffer();
                return framebuffer != null ? framebuffer : minecraftClient.getFramebuffer();
            }
            case 3: {
                Framebuffer framebuffer = minecraftClient.worldRenderer.getTranslucentFramebuffer();
                return framebuffer != null ? framebuffer : minecraftClient.getFramebuffer();
            }
        }
        return minecraftClient.getFramebuffer();
    }

    private static BlockRenderLayer[] method_72026() {
        return new BlockRenderLayer[]{SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT, TRIPWIRE};
    }

    static {
        field_60933 = BlockRenderLayer.method_72026();
    }
}

