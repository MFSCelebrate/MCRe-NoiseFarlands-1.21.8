/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Animator;
import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteContents
implements TextureStitcher.Stitchable,
AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Identifier id;
    final int width;
    final int height;
    final private NativeImage image;
    NativeImage[] mipmapLevelsImages;
    @Nullable
    final private Animation animation;
    final private ResourceMetadata metadata;

    public SpriteContents(Identifier id, SpriteDimensions dimensions, NativeImage image, ResourceMetadata metadata) {
        this.id = id;
        this.width = dimensions.width();
        this.height = dimensions.height();
        this.metadata = metadata;
        this.animation = metadata.decode(AnimationResourceMetadata.SERIALIZER).map(animationMetadata -> this.createAnimation(dimensions, image.getWidth(), image.getHeight(), (AnimationResourceMetadata)animationMetadata)).orElse(null);
        this.image = image;
        this.mipmapLevelsImages = new NativeImage[]{this.image};
    }

    public void generateMipmaps(int mipmapLevels) {
        try {
            this.mipmapLevelsImages = MipmapHelper.getMipmapLevelsImages(this.mipmapLevelsImages, mipmapLevels);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Generating mipmaps for frame");
            CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
            crashReportSection.add("First frame", () -> {
                StringBuilder stringBuilder = new StringBuilder();
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(this.image.getWidth()).append("x").append(this.image.getHeight());
                return stringBuilder.toString();
            });
            CrashReportSection crashReportSection2 = crashReport.addElement("Frame being iterated");
            crashReportSection2.add("Sprite name", this.id);
            crashReportSection2.add("Sprite size", () -> this.width + " x " + this.height);
            crashReportSection2.add("Sprite frames", () -> this.getFrameCount() + " frames");
            crashReportSection2.add("Mipmap levels", mipmapLevels);
            throw new CrashException(crashReport);
        }
    }

    private int getFrameCount() {
        return this.animation != null ? this.animation.frames.size() : 1;
    }

    @Nullable
    private Animation createAnimation(SpriteDimensions dimensions, int imageWidth, int imageHeight, AnimationResourceMetadata metadata) {
        ArrayList<AnimationFrame> list;
        int i = imageWidth / dimensions.width();
        int j = imageHeight / dimensions.height();
        int k = i * j;
        int l = metadata.defaultFrameTime();
        if (metadata.frames().isEmpty()) {
            list = new ArrayList<AnimationFrame>(k);
            for (int m = 0; m < k; ++m) {
                list.add(new AnimationFrame(m, l));
            }
        } else {
            List<AnimationFrameResourceMetadata> list2 = metadata.frames().get();
            list = new ArrayList(list2.size());
            for (AnimationFrameResourceMetadata animationFrameResourceMetadata : list2) {
                list.add(new AnimationFrame(animationFrameResourceMetadata.index(), animationFrameResourceMetadata.getTime(l)));
            }
            int n = 0;
            IntOpenHashSet intSet = new IntOpenHashSet();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                AnimationFrame animationFrame = (AnimationFrame)iterator.next();
                boolean bl = true;
                if (animationFrame.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.id, n, animationFrame.time});
                    bl = false;
                }
                if (animationFrame.index < 0 || animationFrame.index >= k) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.id, n, animationFrame.index});
                    bl = false;
                }
                if (bl) {
                    intSet.add(animationFrame.index);
                } else {
                    iterator.remove();
                }
                ++n;
            }
            int[] is = IntStream.range(0, k).filter(arg_0 -> SpriteContents.method_45813((IntSet)intSet, arg_0)).toArray();
            if (is.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", (Object)this.id, (Object)Arrays.toString(is));
            }
        }
        if (list.size() <= 1) {
            return null;
        }
        return new Animation(List.copyOf(list), i, metadata.interpolate());
    }

    void upload(int x, int y, int unpackSkipPixels, int unpackSkipRows, NativeImage[] images, GpuTexture texture) {
        for (int i = 0; i < this.mipmapLevelsImages.length; ++i) {
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, images[i], i, 0, x >> i, y >> i, this.width >> i, this.height >> i, unpackSkipPixels >> i, unpackSkipRows >> i);
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public IntStream getDistinctFrameCount() {
        return this.animation != null ? this.animation.getDistinctFrameCount() : IntStream.of(1);
    }

    @Nullable
    public Animator createAnimator() {
        return this.animation != null ? this.animation.createAnimator() : null;
    }

    public ResourceMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public void close() {
        for (NativeImage nativeImage : this.mipmapLevelsImages) {
            nativeImage.close();
        }
    }

    public String toString() {
        return "SpriteContents{name=" + String.valueOf(this.id) + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
    }

    public boolean isPixelTransparent(int frame, int x, int y) {
        int i = x;
        int j = y;
        if (this.animation != null) {
            i += this.animation.getFrameX(frame) * this.width;
            j += this.animation.getFrameY(frame) * this.height;
        }
        return ColorHelper.getAlpha(this.image.getColorArgb(1, j)) == 0;
    }

    public void upload(int x, int y, GpuTexture texture) {
        if (this.animation != null) {
            this.animation.upload(x, y, texture);
        } else {
            this.upload(x, y, 0, 0, this.mipmapLevelsImages, texture);
        }
    }

    private static boolean method_45813(IntSet frameIndex, int i) {
        return !frameIndex.contains(i);
    }

    @Environment(value=EnvType.CLIENT)
    class Animation {
        final List<AnimationFrame> frames;
        final private int frameCount;
        final private boolean interpolation;

        Animation(List<AnimationFrame> frames, int frameCount, boolean interpolation) {
            this.frames = frames;
            this.frameCount = frameCount;
            this.interpolation = interpolation;
        }

        int getFrameX(int frame) {
            return frame % this.frameCount;
        }

        int getFrameY(int frame) {
            return frame / this.frameCount;
        }

        void upload(int x, int y, int frame, GpuTexture texture) {
            int i = this.getFrameX(frame) * SpriteContents.this.width;
            int j = this.getFrameY(frame) * SpriteContents.this.height;
            SpriteContents.this.upload(x, y, i, j, SpriteContents.this.mipmapLevelsImages, texture);
        }

        public Animator createAnimator() {
            return new AnimatorImpl(SpriteContents.this, this, this.interpolation ? new Interpolation() : null);
        }

        public void upload(int x, int y, GpuTexture texture) {
            this.upload(x, y, this.frames.get(0).index, texture);
        }

        public IntStream getDistinctFrameCount() {
            return this.frames.stream().mapToInt(frame -> frame.index).distinct();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class AnimationFrame
    extends Record {
        final int index;
        final int time;

        AnimationFrame(int index, int time) {
            this.index = index;
            this.time = time;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{AnimationFrame.class, "index;time", "index", "time"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AnimationFrame.class, "index;time", "index", "time"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AnimationFrame.class, "index;time", "index", "time"}, this, object);
        }

        public int index() {
            return this.index;
        }

        public int time() {
            return this.time;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class AnimatorImpl
    implements Animator {
        int frame;
        int currentTime;
        final Animation animation;
        @Nullable
        final private Interpolation interpolation;

        AnimatorImpl(SpriteContents spriteContents, @Nullable Animation animation, Interpolation interpolation) {
            this.animation = animation;
            this.interpolation = interpolation;
        }

        @Override
        public void tick(int x, int y, GpuTexture texture) {
            ++this.currentTime;
            AnimationFrame animationFrame = this.animation.frames.get(this.frame);
            if (this.currentTime >= animationFrame.time) {
                int i = animationFrame.index;
                this.frame = (this.frame + 1) % this.animation.frames.size();
                this.currentTime = 0;
                int j = this.animation.frames.get((int)this.frame).index;
                if (i != j) {
                    this.animation.upload(x, y, j, texture);
                }
            } else if (this.interpolation != null) {
                this.interpolation.method_24128(x, y, this, texture);
            }
        }

        @Override
        public void close() {
            if (this.interpolation != null) {
                this.interpolation.close();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class Interpolation
    implements AutoCloseable {
        final private NativeImage[] images;

        Interpolation() {
            this.images = new NativeImage[SpriteContents.this.mipmapLevelsImages.length];
            for (int i = 0; i < this.images.length; ++i) {
                int j = SpriteContents.this.width >> i;
                int k = SpriteContents.this.height >> i;
                this.images[i] = new NativeImage(j, k, false);
            }
        }

        void method_24128(int i, int j, AnimatorImpl animatorImpl, GpuTexture gpuTexture) {
            Animation animation = animatorImpl.animation;
            List<AnimationFrame> list = animation.frames;
            AnimationFrame animationFrame = list.get(animatorImpl.frame);
            float f = (float)animatorImpl.currentTime / (float)animationFrame.time;
            int k = animationFrame.index;
            int l = list.get((int)((animatorImpl.frame + 1) % list.size())).index;
            if (k != l) {
                for (int m = 0; m < this.images.length; ++m) {
                    int n = SpriteContents.this.width >> m;
                    int o = SpriteContents.this.height >> m;
                    for (int p = 0; p < o; ++p) {
                        for (int q = 0; q < n; ++q) {
                            int r = this.getPixelColor(animation, k, m, q, p);
                            int s = this.getPixelColor(animation, l, m, q, p);
                            this.images[m].setColorArgb(q, p, ColorHelper.lerp(f, r, s));
                        }
                    }
                }
                SpriteContents.this.upload(i, j, 0, 0, this.images, gpuTexture);
            }
        }

        private int getPixelColor(Animation animation, int frameIndex, int layer, int x, int y) {
            return SpriteContents.this.mipmapLevelsImages[layer].getColorArgb(x + (animation.getFrameX(frameIndex) * SpriteContents.this.width >> layer), y + (animation.getFrameY(frameIndex) * SpriteContents.this.height >> layer));
        }

        @Override
        public void close() {
            for (NativeImage nativeImage : this.images) {
                nativeImage.close();
            }
        }
    }
}

