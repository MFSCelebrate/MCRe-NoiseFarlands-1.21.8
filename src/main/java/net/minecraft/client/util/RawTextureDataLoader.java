/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class RawTextureDataLoader {
    /*
     * Loose catch block
     */
    @Deprecated
    public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier id)
            throws IOException {
        try (InputStream inputStream = resourceManager.open(id);
                NativeImage nativeImage = NativeImage.read(inputStream)) {
            return nativeImage.makePixelArray();
        }
    }
}
