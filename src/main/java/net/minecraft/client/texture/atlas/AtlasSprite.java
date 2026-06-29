/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AtlasSprite {
    final private Identifier id;
    final private Resource resource;
    final private AtomicReference<NativeImage> image = new AtomicReference();
    final private AtomicInteger regionCount;

    public AtlasSprite(Identifier id, Resource resource, int regionCount) {
        this.id = id;
        this.resource = resource;
        this.regionCount = new AtomicInteger(regionCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public NativeImage read() throws IOException {
        NativeImage nativeImage = this.image.get();
        if (nativeImage != null) return nativeImage;
        AtlasSprite atlasSprite = this;
        synchronized (atlasSprite) {
            nativeImage = this.image.get();
            if (nativeImage != null) return nativeImage;
            try {
                try (InputStream inputStream = this.resource.getInputStream();){
                    nativeImage = NativeImage.read(inputStream);
                    this.image.set(nativeImage);
                    if (inputStream == null) return nativeImage;
                }
                {
                }
            }
            catch (IOException iOException) {
                throw new IOException("Failed to load image " + String.valueOf(this.id), iOException);
            }
            return nativeImage;
        }
    }

    public void close() {
        NativeImage nativeImage;
        int i = this.regionCount.decrementAndGet();
        if (i <= 0 && (nativeImage = (NativeImage)this.image.getAndSet(null)) != null) {
            nativeImage.close();
        }
    }
}

