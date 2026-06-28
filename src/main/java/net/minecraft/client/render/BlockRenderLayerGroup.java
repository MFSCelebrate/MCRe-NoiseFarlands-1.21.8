/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BlockRenderLayer;

@Environment(value=EnvType.CLIENT)
public final class BlockRenderLayerGroup
extends Enum<BlockRenderLayerGroup> {
    final static public BlockRenderLayerGroup OPAQUE = new BlockRenderLayerGroup(BlockRenderLayer.SOLID, BlockRenderLayer.CUTOUT_MIPPED, BlockRenderLayer.CUTOUT);
    final static public BlockRenderLayerGroup TRANSLUCENT = new BlockRenderLayerGroup(BlockRenderLayer.TRANSLUCENT);
    final static public BlockRenderLayerGroup TRIPWIRE = new BlockRenderLayerGroup(BlockRenderLayer.TRIPWIRE);
    final private String name;
    final private BlockRenderLayer[] layers;
    final static private BlockRenderLayerGroup[] field_61027;

    public static BlockRenderLayerGroup[] values() {
        return (BlockRenderLayerGroup[])field_61027.clone();
    }

    public static BlockRenderLayerGroup valueOf(String string) {
        return Enum.valueOf(BlockRenderLayerGroup.class, string);
    }

    private BlockRenderLayerGroup(BlockRenderLayer ... layers) {
        this.layers = layers;
        this.name = this.toString().toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return this.name;
    }

    public BlockRenderLayer[] getLayers() {
        return this.layers;
    }

    public Framebuffer getFramebuffer() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Framebuffer framebuffer = switch (this.ordinal()) {
            case 2 -> minecraftClient.worldRenderer.getWeatherFramebuffer();
            case 1 -> minecraftClient.worldRenderer.getTranslucentFramebuffer();
            default -> minecraftClient.getFramebuffer();
        };
        return framebuffer != null ? framebuffer : minecraftClient.getFramebuffer();
    }

    private static BlockRenderLayerGroup[] method_72169() {
        return new BlockRenderLayerGroup[]{OPAQUE, TRANSLUCENT, TRIPWIRE};
    }

    static {
        field_61027 = BlockRenderLayerGroup.method_72169();
    }
}

