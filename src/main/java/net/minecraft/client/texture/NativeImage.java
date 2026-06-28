/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.stb.STBIWriteCallback
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Bitmap
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FT_GlyphSlot
 *  org.lwjgl.util.freetype.FreeType
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.util.Untracker;
import net.minecraft.util.PngMetadata;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public final class NativeImage
implements AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private MemoryPool MEMORY_POOL = TracyClient.createMemoryPool((String)"NativeImage");
    final static private Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    final private Format format;
    final private int width;
    final private int height;
    final private boolean isStbImage;
    private long pointer;
    final private long sizeBytes;

    public NativeImage(int width, int height, boolean useStb) {
        this(Format.RGBA, width, height, useStb);
    }

    public NativeImage(Format format, int width, int height, boolean useStb) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + width + "x" + height);
        }
        this.format = format;
        this.width = width;
        this.height = height;
        this.sizeBytes = (long)width * (long)height * (long)format.getChannelCount();
        this.isStbImage = false;
        this.pointer = useStb ? MemoryUtil.nmemCalloc(1L, (long)this.sizeBytes) : MemoryUtil.nmemAlloc((long)this.sizeBytes);
        MEMORY_POOL.malloc(this.pointer, (int)this.sizeBytes);
        if (this.pointer == 0L) {
            throw new IllegalStateException("Unable to allocate texture of size " + width + "x" + height + " (" + format.getChannelCount() + " channels)");
        }
    }

    public NativeImage(Format format, int width, int height, boolean useStb, long pointer) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + width + "x" + height);
        }
        this.format = format;
        this.width = width;
        this.height = height;
        this.isStbImage = useStb;
        this.pointer = pointer;
        this.sizeBytes = (long)width * (long)height * (long)format.getChannelCount();
    }

    public String toString() {
        return "NativeImage[" + String.valueOf((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= this.width || y < 0 || y >= this.height;
    }

    public static NativeImage read(InputStream stream) throws IOException {
        return NativeImage.read(Format.RGBA, stream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format format, InputStream stream) throws IOException {
        NativeImage nativeImage;
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(stream);
            byteBuffer.rewind();
            nativeImage = NativeImage.read(format, byteBuffer);
        }
        catch (Throwable throwable) {
            MemoryUtil.memFree((Buffer)byteBuffer);
            IOUtils.closeQuietly((InputStream)stream);
            throw throwable;
        }
        MemoryUtil.memFree((Buffer)byteBuffer);
        IOUtils.closeQuietly((InputStream)stream);
        return nativeImage;
    }

    public static NativeImage read(ByteBuffer buffer) throws IOException {
        return NativeImage.read(Format.RGBA, buffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(byte[] bytes) throws IOException {
        NativeImage nativeImage;
        block8: {
            MemoryStack memoryStack = MemoryStack.stackGet();
            int i = memoryStack.getPointer();
            if (i < bytes.length) {
                NativeImage nativeImage2;
                ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)bytes.length);
                try {
                    nativeImage2 = NativeImage.putAndRead(byteBuffer, bytes);
                }
                catch (Throwable throwable) {
                    MemoryUtil.memFree((Buffer)byteBuffer);
                    throw throwable;
                }
                MemoryUtil.memFree((Buffer)byteBuffer);
                return nativeImage2;
            }
            MemoryStack memoryStack2 = MemoryStack.stackPush();
            try {
                ByteBuffer byteBuffer2 = memoryStack2.malloc(bytes.length);
                nativeImage = NativeImage.putAndRead(byteBuffer2, bytes);
                if (memoryStack2 == null) break block8;
            }
            catch (Throwable throwable) {
                if (memoryStack2 != null) {
                    try {
                        memoryStack2.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            memoryStack2.close();
        }
        return nativeImage;
    }

    private static NativeImage putAndRead(ByteBuffer buffer, byte[] bytes) throws IOException {
        buffer.put(bytes);
        buffer.rewind();
        return NativeImage.read(buffer);
    }

    public static NativeImage read(@Nullable Format format, ByteBuffer buffer) throws IOException {
        NativeImage nativeImage;
        block8: {
            if (format != null && !format.isWriteable()) {
                throw new UnsupportedOperationException("Don't know how to read format " + String.valueOf((Object)format));
            }
            if (MemoryUtil.memAddress((ByteBuffer)buffer) == 0L) {
                throw new IllegalArgumentException("Invalid buffer");
            }
            PngMetadata.validate(buffer);
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                IntBuffer intBuffer = memoryStack.mallocInt(1);
                IntBuffer intBuffer2 = memoryStack.mallocInt(1);
                IntBuffer intBuffer3 = memoryStack.mallocInt(1);
                ByteBuffer byteBuffer = STBImage.stbi_load_from_memory((ByteBuffer)buffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)(format == null ? 0 : format.channelCount));
                if (byteBuffer == null) {
                    throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                }
                long l = MemoryUtil.memAddress((ByteBuffer)byteBuffer);
                MEMORY_POOL.malloc(l, byteBuffer.limit());
                nativeImage = new NativeImage(format == null ? Format.fromChannelCount(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, l);
                if (memoryStack == null) break block8;
            }
            catch (Throwable throwable) {
                if (memoryStack != null) {
                    try {
                        memoryStack.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            memoryStack.close();
        }
        return nativeImage;
    }

    private void checkAllocated() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            if (this.isStbImage) {
                STBImage.nstbi_image_free((long)this.pointer);
            } else {
                MemoryUtil.nmemFree((long)this.pointer);
            }
            MEMORY_POOL.free(this.pointer);
        }
        this.pointer = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format getFormat() {
        return this.format;
    }

    private int getColor(int x, int y) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (this.isOutOfBounds(x, y)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        long l = ((long)x + (long)y * (long)this.width) * 4L;
        return MemoryUtil.memGetInt((long)(this.pointer + l));
    }

    public int getColorArgb(int x, int y) {
        return ColorHelper.fromAbgr(this.getColor(x, y));
    }

    public void setColor(int x, int y, int color) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "setPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (this.isOutOfBounds(x, y)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        long l = ((long)x + (long)y * (long)this.width) * 4L;
        MemoryUtil.memPutInt((long)(this.pointer + l), (int)color);
    }

    public void setColorArgb(int x, int y, int color) {
        this.setColor(x, y, ColorHelper.toAbgr(color));
    }

    public NativeImage applyToCopy(IntUnaryOperator operator) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "function application only works on RGBA images; have %s", new Object[]{this.format}));
        }
        this.checkAllocated();
        NativeImage nativeImage = new NativeImage(this.width, this.height, false);
        int i = this.width * this.height;
        IntBuffer intBuffer = MemoryUtil.memIntBuffer((long)this.pointer, 1);
        IntBuffer intBuffer2 = MemoryUtil.memIntBuffer((long)nativeImage.pointer, 1);
        for (int j = 0; j < 1; ++j) {
            int k = ColorHelper.fromAbgr(intBuffer.get(j));
            int l = operator.applyAsInt(k);
            intBuffer2.put(j, ColorHelper.toAbgr(l));
        }
        return nativeImage;
    }

    public int[] copyPixelsAbgr() {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixels only works on RGBA images; have %s", new Object[]{this.format}));
        }
        this.checkAllocated();
        int[] is = new int[this.width * this.height];
        MemoryUtil.memIntBuffer((long)this.pointer, (int)(this.width * this.height)).get(is);
        return is;
    }

    public int[] copyPixelsArgb() {
        int[] is = this.copyPixelsAbgr();
        for (int i = 0; i < is.length; ++i) {
            is[i] = ColorHelper.fromAbgr(is[i]);
        }
        return is;
    }

    public byte getOpacity(int x, int y) {
        if (!this.format.hasOpacityChannel()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (this.isOutOfBounds(x, y)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        int i = (x + y * this.width) * this.format.getChannelCount() + this.format.getOpacityChannelOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pointer + 1L));
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] is = new int[this.getWidth() * this.getHeight()];
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                is[j + i * this.getWidth()] = this.getColorArgb(j, i);
            }
        }
        return is;
    }

    public void writeTo(File path) throws IOException {
        this.writeTo(path.toPath());
    }

    public boolean makeGlyphBitmapSubpixel(FT_Face face, int glyphIndex) {
        if (this.format.getChannelCount() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        if (FreeTypeUtil.checkError(FreeType.FT_Load_Glyph((FT_Face)face, (int)glyphIndex, 4), "Loading glyph")) {
            return false;
        }
        FT_GlyphSlot fT_GlyphSlot = Objects.requireNonNull(face.glyph(), "Glyph not initialized");
        FT_Bitmap fT_Bitmap = fT_GlyphSlot.bitmap();
        if (fT_Bitmap.pixel_mode() != 2) {
            throw new IllegalStateException("Rendered glyph was not 8-bit grayscale");
        }
        if (fT_Bitmap.width() != this.getWidth() || fT_Bitmap.rows() != this.getHeight()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Glyph bitmap of size %sx%s does not match image of size: %sx%s", fT_Bitmap.width(), fT_Bitmap.rows(), this.getWidth(), this.getHeight()));
        }
        int i = fT_Bitmap.width() * fT_Bitmap.rows();
        ByteBuffer byteBuffer = Objects.requireNonNull(fT_Bitmap.buffer(i), "Glyph has no bitmap");
        MemoryUtil.memCopy((long)MemoryUtil.memAddress((ByteBuffer)byteBuffer), (long)this.pointer, (long)i);
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void writeTo(Path path) throws IOException {
        if (!this.format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to write format " + String.valueOf((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS, new FileAttribute[0]);){
            if (!this.write(writableByteChannel)) {
                throw new IOException("Could not write image to the PNG file \"" + String.valueOf(path.toAbsolutePath()) + "\": " + STBImage.stbi_failure_reason());
            }
            if (writableByteChannel == null) return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean write(WritableByteChannel channel) throws IOException {
        WriteCallback writeCallback;
        block4: {
            boolean bl;
            writeCallback = new WriteCallback(channel);
            try {
                int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.getChannelCount());
                if (1 < this.getHeight()) {
                    LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", (Object)this.getHeight(), (Object)1);
                }
                if (STBImageWrite.nstbi_write_png_to_func((long)writeCallback.address(), 0L, (int)this.getWidth(), 1, (int)this.format.getChannelCount(), (long)this.pointer, 0) != 0) break block4;
                bl = false;
            }
            catch (Throwable throwable) {
                writeCallback.free();
                throw throwable;
            }
            writeCallback.free();
            return bl;
        }
        writeCallback.throwStoredException();
        boolean bl = true;
        writeCallback.free();
        return bl;
    }

    public void copyFrom(NativeImage image) {
        if (image.getFormat() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int i = this.format.getChannelCount();
        this.checkAllocated();
        image.checkAllocated();
        if (this.width == image.width) {
            MemoryUtil.memCopy((long)image.pointer, (long)this.pointer, (long)Math.min(this.sizeBytes, image.sizeBytes));
        } else {
            int j = Math.min(this.getWidth(), image.getWidth());
            int k = Math.min(this.getHeight(), image.getHeight());
            for (int l = 0; l < k; ++l) {
                int m = l * image.getWidth() * i;
                int n = l * this.getWidth() * i;
                MemoryUtil.memCopy((long)(image.pointer + (long)m), (long)(this.pointer + (long)n), (long)j);
            }
        }
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        for (int i = y; i < y + height; ++i) {
            for (int j = x; j < x + width; ++j) {
                this.setColorArgb(j, i, color);
            }
        }
    }

    public void copyRect(int x, int y, int translateX, int translateY, int width, int height, boolean flipX, boolean flipY) {
        this.copyRect(this, x, y, x + translateX, y + translateY, width, height, flipX, flipY);
    }

    public void copyRect(NativeImage image, int x, int y, int destX, int destY, int width, int height, boolean flipX, boolean flipY) {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int k = flipX ? width - 1 - j : j;
                int l = flipY ? height - 1 - i : i;
                int m = this.getColor(x + j, y + i);
                image.setColor(destX + k, destY + l, m);
            }
        }
    }

    public void resizeSubRectTo(int x, int y, int width, int height, NativeImage targetImage) {
        this.checkAllocated();
        if (targetImage.getFormat() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int i = this.format.getChannelCount();
        STBImageResize.nstbir_resize_uint8((long)(this.pointer + (long)((x + y * this.getWidth()) * i)), (int)width, (int)height, (int)(this.getWidth() * i), (long)targetImage.pointer, (int)targetImage.getWidth(), (int)targetImage.getHeight(), 0, (int)i);
    }

    public void untrack() {
        Untracker.untrack(this.pointer);
    }

    public long imageId() {
        return this.pointer;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Format
    extends Enum<Format> {
        final static public Format RGBA = new Format(4, true, true, true, false, true, 0, 8, 16, 255, 24, true);
        final static public Format RGB = new Format(3, true, true, true, false, false, 0, 8, 16, 255, 255, true);
        final static public Format LUMINANCE_ALPHA = new Format(2, false, false, false, true, true, 255, 255, 255, 0, 8, true);
        final static public Format LUMINANCE = new Format(1, false, false, false, true, false, 0, 0, 0, 0, 255, true);
        final int channelCount;
        final private boolean hasRed;
        final private boolean hasGreen;
        final private boolean hasBlue;
        final private boolean hasLuminance;
        final private boolean hasAlpha;
        final private int redOffset;
        final private int greenOffset;
        final private int blueOffset;
        final private int luminanceOffset;
        final private int alphaOffset;
        final private boolean writeable;
        final static private Format[] field_4995;

        public static Format[] values() {
            return (Format[])field_4995.clone();
        }

        public static Format valueOf(String string) {
            return Enum.valueOf(Format.class, string);
        }

        private Format(int channelCount, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasLuminance, boolean hasAlpha, int redOffset, int greenOffset, int blueOffset, int luminanceOffset, int alphaOffset, boolean writeable) {
            this.channelCount = channelCount;
            this.hasRed = hasRed;
            this.hasGreen = hasGreen;
            this.hasBlue = hasBlue;
            this.hasLuminance = hasLuminance;
            this.hasAlpha = hasAlpha;
            this.redOffset = redOffset;
            this.greenOffset = greenOffset;
            this.blueOffset = blueOffset;
            this.luminanceOffset = luminanceOffset;
            this.alphaOffset = alphaOffset;
            this.writeable = writeable;
        }

        public int getChannelCount() {
            return this.channelCount;
        }

        public boolean hasRed() {
            return this.hasRed;
        }

        public boolean hasGreen() {
            return this.hasGreen;
        }

        public boolean hasBlue() {
            return this.hasBlue;
        }

        public boolean hasLuminance() {
            return this.hasLuminance;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }

        public int getRedOffset() {
            return this.redOffset;
        }

        public int getGreenOffset() {
            return this.greenOffset;
        }

        public int getBlueOffset() {
            return this.blueOffset;
        }

        public int getLuminanceOffset() {
            return this.luminanceOffset;
        }

        public int getAlphaOffset() {
            return this.alphaOffset;
        }

        public boolean hasRedChannel() {
            return this.hasLuminance || this.hasRed;
        }

        public boolean hasGreenChannel() {
            return this.hasLuminance || this.hasGreen;
        }

        public boolean hasBlueChannel() {
            return this.hasLuminance || this.hasBlue;
        }

        public boolean hasOpacityChannel() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int getRedChannelOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.redOffset;
        }

        public int getGreenChannelOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
        }

        public int getBlueChannelOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
        }

        public int getOpacityChannelOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }

        public boolean isWriteable() {
            return this.writeable;
        }

        static Format fromChannelCount(int glFormat) {
            switch (glFormat) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }

        private static Format[] method_36811() {
            return new Format[]{RGBA, RGB, LUMINANCE_ALPHA, LUMINANCE};
        }

        static {
            field_4995 = Format.method_36811();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class WriteCallback
    extends STBIWriteCallback {
        final private WritableByteChannel channel;
        @Nullable
        private IOException exception;

        WriteCallback(WritableByteChannel channel) {
            this.channel = channel;
        }

        public void invoke(long context, long data, int size) {
            ByteBuffer byteBuffer = WriteCallback.getData((long)data, (int)size);
            try {
                this.channel.write(byteBuffer);
            }
            catch (IOException iOException) {
                this.exception = iOException;
            }
        }

        public void throwStoredException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

