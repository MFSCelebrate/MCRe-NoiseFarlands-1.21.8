/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.jpountz.lz4.LZ4BlockInputStream
 *  net.jpountz.lz4.LZ4BlockOutputStream
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.minecraft.util.FixedBufferInputStream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChunkCompressionFormat {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Int2ObjectMap<ChunkCompressionFormat> FORMATS = new Int2ObjectOpenHashMap();
    final static private Object2ObjectMap<String, ChunkCompressionFormat> FORMAT_BY_NAME = new Object2ObjectOpenHashMap();
    final static public ChunkCompressionFormat GZIP = ChunkCompressionFormat.add(new ChunkCompressionFormat(1, null, stream -> new FixedBufferInputStream(new GZIPInputStream((InputStream)stream)), stream -> new BufferedOutputStream(new GZIPOutputStream((OutputStream)stream))));
    final static public ChunkCompressionFormat DEFLATE = ChunkCompressionFormat.add(new ChunkCompressionFormat(2, "deflate", stream -> new FixedBufferInputStream(new InflaterInputStream((InputStream)stream)), stream -> new BufferedOutputStream(new DeflaterOutputStream((OutputStream)stream))));
    final static public ChunkCompressionFormat UNCOMPRESSED = ChunkCompressionFormat.add(new ChunkCompressionFormat(3, "none", FixedBufferInputStream::new, BufferedOutputStream::new));
    final static public ChunkCompressionFormat LZ4 = ChunkCompressionFormat.add(new ChunkCompressionFormat(4, "lz4", stream -> new FixedBufferInputStream((InputStream)new LZ4BlockInputStream(stream)), stream -> new BufferedOutputStream((OutputStream)new LZ4BlockOutputStream(stream))));
    final static public ChunkCompressionFormat CUSTOM = ChunkCompressionFormat.add(new ChunkCompressionFormat(127, null, stream -> {
        throw new UnsupportedOperationException();
    }, stream -> {
        throw new UnsupportedOperationException();
    }));
    final static public ChunkCompressionFormat DEFAULT_FORMAT;
    static private volatile ChunkCompressionFormat currentFormat;
    final private int id;
    @Nullable
    final private String name;
    final private Wrapper<InputStream> inputStreamWrapper;
    final private Wrapper<OutputStream> outputStreamWrapper;

    private ChunkCompressionFormat(int id, @Nullable String name, Wrapper<InputStream> inputStreamWrapper, Wrapper<OutputStream> outputStreamWrapper) {
        this.id = id;
        this.name = name;
        this.inputStreamWrapper = inputStreamWrapper;
        this.outputStreamWrapper = outputStreamWrapper;
    }

    private static ChunkCompressionFormat add(ChunkCompressionFormat version) {
        FORMATS.put(version.id, (Object)version);
        if (version.name != null) {
            FORMAT_BY_NAME.put((Object)version.name, (Object)version);
        }
        return version;
    }

    @Nullable
    public static ChunkCompressionFormat get(int id) {
        return (ChunkCompressionFormat)FORMATS.get(id);
    }

    public static void setCurrentFormat(String name) {
        ChunkCompressionFormat chunkCompressionFormat = (ChunkCompressionFormat)FORMAT_BY_NAME.get((Object)name);
        if (chunkCompressionFormat != null) {
            currentFormat = chunkCompressionFormat;
        } else {
            LOGGER.error("Invalid `region-file-compression` value `{}` in server.properties. Please use one of: {}", (Object)name, (Object)String.join((CharSequence)", ", (Iterable<? extends CharSequence>)FORMAT_BY_NAME.keySet()));
        }
    }

    public static ChunkCompressionFormat getCurrentFormat() {
        return currentFormat;
    }

    public static boolean exists(int id) {
        return FORMATS.containsKey(id);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream outputStream) throws IOException {
        return this.outputStreamWrapper.wrap(outputStream);
    }

    public InputStream wrap(InputStream inputStream) throws IOException {
        return this.inputStreamWrapper.wrap(inputStream);
    }

    static {
        currentFormat = DEFAULT_FORMAT = DEFLATE;
    }

    @FunctionalInterface
    static interface Wrapper<O> {
        public O wrap(O var1) throws IOException;
    }
}

