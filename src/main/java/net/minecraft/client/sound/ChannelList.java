/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.BufferUtils
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;

@Environment(value=EnvType.CLIENT)
public class ChannelList
implements FloatConsumer {
    final private List<ByteBuffer> buffers = Lists.newArrayList();
    final private int size;
    private int currentBufferSize;
    private ByteBuffer buffer;

    public ChannelList(int size) {
        this.size = size + 1 & 0xFFFFFFFE;
        this.buffer = BufferUtils.createByteBuffer((int)size);
    }

    public void accept(float value) {
        if (this.buffer.remaining() == 0) {
            this.buffer.flip();
            this.buffers.add(this.buffer);
            this.buffer = BufferUtils.createByteBuffer((int)this.size);
        }
        int i = MathHelper.clamp((int)(value * 32767.5f - 0.5f), Short.MIN_VALUE, Short.MAX_VALUE);
        this.buffer.putShort((short)i);
        this.currentBufferSize += 2;
    }

    public ByteBuffer getBuffer() {
        this.buffer.flip();
        if (this.buffers.isEmpty()) {
            return this.buffer;
        }
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int)this.currentBufferSize);
        this.buffers.forEach(byteBuffer::put);
        byteBuffer.put(this.buffer);
        byteBuffer.flip();
        return byteBuffer;
    }

    public int getCurrentBufferSize() {
        return this.currentBufferSize;
    }
}

