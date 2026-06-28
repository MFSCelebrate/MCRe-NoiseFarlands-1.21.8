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

@Environment(value=EnvType.CLIENT)
public class RawTextureDataLoader {
    /*
     * Loose catch block
     */
    @Deprecated
    public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier id) throws IOException {
        int[] nArray;
        NativeImage nativeImage;
        InputStream inputStream;
        block12: {
            inputStream = resourceManager.open(id);
            nativeImage = NativeImage.read(inputStream);
            nArray = nativeImage.makePixelArray();
            if (nativeImage != null) {
                nativeImage.close();
            }
            if (inputStream == null) break block12;
            inputStream.close();
        }
        return nArray;
        {
            catch (Throwable throwable) {
                try {
                    if (nativeImage != null) {
                        try {
                            nativeImage.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Throwable throwable3) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable4) {
                            throwable3.addSuppressed(throwable4);
                        }
                    }
                    throw throwable3;
                }
            }
        }
    }
}

