/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.MemoryUtil$MemoryAllocator
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BufferAllocator
implements AutoCloseable {
    final static private MemoryPool MEMORY_POOL = TracyClient.createMemoryPool((String)"ByteBufferBuilder");
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator((boolean)false);
    final static private long field_61052 = 0xFFFFFFFFL;
    final static private int MIN_GROWTH = 0x200000;
    final static private int CLOSED = -1;
    long pointer;
    private long size;
    final private long field_61053;
    private long offset;
    private long lastOffset;
    private int refCount;
    private int clearCount;

    public BufferAllocator(int i, long l) {
        this.size = i;
        this.field_61053 = l;
        this.pointer = ALLOCATOR.malloc((long)i);
        MEMORY_POOL.malloc(this.pointer, i);
        if (this.pointer == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + i + " bytes");
        }
    }

    public BufferAllocator(int size) {
        this(size, 0xFFFFFFFFL);
    }

    public static BufferAllocator method_72201(int i) {
        return new BufferAllocator(i, i);
    }

    public long allocate(int size) {
        long l = this.offset;
        long m = Math.addExact(l, (long)size);
        this.growIfNecessary(m);
        this.offset = m;
        return Math.addExact(this.pointer, l);
    }

    private void growIfNecessary(long l) {
        if (l > this.size) {
            if (l > this.field_61053) {
                throw new IllegalArgumentException("Maximum capacity of ByteBufferBuilder (" + this.field_61053 + ") exceeded, required " + l);
            }
            long m = Math.min(this.size, 0x200000L);
            long n = MathHelper.clamp(this.size + m, l, this.field_61053);
            this.grow(n);
        }
    }

    private void grow(long l) {
        MEMORY_POOL.free(this.pointer);
        this.pointer = ALLOCATOR.realloc(this.pointer, l);
        MEMORY_POOL.malloc(this.pointer, (int)Math.min(l, Integer.MAX_VALUE));
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)this.size, (Object)l);
        if (this.pointer == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + this.size + " bytes to " + l + " bytes");
        }
        this.size = l;
    }

    @Nullable
    public CloseableBuffer getAllocated() {
        this.ensureNotFreed();
        long l = this.lastOffset;
        long m = this.offset - l;
        if (m == 0L) {
            return null;
        }
        if (m > Integer.MAX_VALUE) {
            throw new IllegalStateException("Cannot build buffer larger than 2147483647 bytes (was " + m + ")");
        }
        this.lastOffset = this.offset;
        ++this.refCount;
        return new CloseableBuffer(l, (int)m, this.clearCount);
    }

    public void clear() {
        if (this.refCount > 0) {
            LOGGER.warn("Clearing BufferBuilder with unused batches");
        }
        this.reset();
    }

    public void reset() {
        this.ensureNotFreed();
        if (this.refCount > 0) {
            this.forceClear();
            this.refCount = 0;
        }
    }

    boolean clearCountEquals(int clearCount) {
        return clearCount == this.clearCount;
    }

    void clearIfUnreferenced() {
        if (--this.refCount <= 0) {
            this.forceClear();
        }
    }

    private void forceClear() {
        long l = this.offset - this.lastOffset;
        if (l > 0L) {
            MemoryUtil.memCopy((long)(this.pointer + this.lastOffset), (long)this.pointer, (long)l);
        }
        this.offset = l;
        this.lastOffset = 0L;
        ++this.clearCount;
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            MEMORY_POOL.free(this.pointer);
            ALLOCATOR.free(this.pointer);
            this.pointer = 0L;
            this.clearCount = -1;
        }
    }

    private void ensureNotFreed() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Buffer has been freed");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class CloseableBuffer
    implements AutoCloseable {
        final private long offset;
        final private int size;
        final private int clearCount;
        private boolean closed;

        CloseableBuffer(long l, int i, int j) {
            this.offset = l;
            this.size = i;
            this.clearCount = j;
        }

        public ByteBuffer getBuffer() {
            if (!BufferAllocator.this.clearCountEquals(this.clearCount)) {
                throw new IllegalStateException("Buffer is no longer valid");
            }
            return MemoryUtil.memByteBuffer((long)(BufferAllocator.this.pointer + this.offset), (int)this.size);
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (BufferAllocator.this.clearCountEquals(this.clearCount)) {
                BufferAllocator.this.clearIfUnreferenced();
            }
        }
    }
}

