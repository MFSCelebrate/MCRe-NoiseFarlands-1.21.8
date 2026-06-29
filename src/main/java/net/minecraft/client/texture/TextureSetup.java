/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record TextureSetup(@Nullable GpuTextureView texure0, @Nullable GpuTextureView texure1, @Nullable GpuTextureView texure2) {
    final static private TextureSetup EMPTY = new TextureSetup(null, null, null);
    static private int field_60313;

    public static TextureSetup withoutGlTexture(GpuTextureView texture) {
        return new TextureSetup(texture, null, null);
    }

    public static TextureSetup of(GpuTextureView texture) {
        return new TextureSetup(texture, null, MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().getGlTextureView());
    }

    public static TextureSetup of(GpuTextureView texture0, GpuTextureView texture1) {
        return new TextureSetup(texture0, texture1, null);
    }

    public static TextureSetup empty() {
        return EMPTY;
    }

    public int getSortKey() {
        return this.hashCode();
    }

    public static void method_71298() {
        field_60313 = Math.round(100000.0f * (float)Math.random());
    }
}

