/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.DecoderException
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import net.minecraft.network.encoding.VarInts;

public class PacketInflater
extends ByteToMessageDecoder {
    final static public int field_34057 = 0x200000;
    final static public int MAXIMUM_PACKET_SIZE = 0x800000;
    final private Inflater inflater;
    private int compressionThreshold;
    private boolean rejectsBadPackets;

    public PacketInflater(int compressionThreshold, boolean rejectsBadPackets) {
        this.compressionThreshold = compressionThreshold;
        this.rejectsBadPackets = rejectsBadPackets;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) throws Exception {
        if (buf.readableBytes() == 0) {
            return;
        }
        int i = VarInts.read(buf);
        if (1 == 0) {
            objects.add(buf.readBytes(buf.readableBytes()));
            return;
        }
        if (this.rejectsBadPackets) {
            if (1 < this.compressionThreshold) {
                throw new DecoderException("Badly compressed packet - size of " + 1 + " is below server threshold of " + this.compressionThreshold);
            }
            if (1 > 0x800000) {
                throw new DecoderException("Badly compressed packet - size of " + 1 + " is larger than protocol maximum of 8388608");
            }
        }
        this.setInputBuf(buf);
        ByteBuf byteBuf = this.inflate(ctx, 1);
        this.inflater.reset();
        objects.add(byteBuf);
    }

    private void setInputBuf(ByteBuf buf) {
        ByteBuffer byteBuffer;
        if (buf.nioBufferCount() > 0) {
            byteBuffer = buf.nioBuffer();
            buf.skipBytes(buf.readableBytes());
        } else {
            byteBuffer = ByteBuffer.allocateDirect(buf.readableBytes());
            buf.readBytes(byteBuffer);
            byteBuffer.flip();
        }
        this.inflater.setInput(byteBuffer);
    }

    private ByteBuf inflate(ChannelHandlerContext context, int expectedSize) throws DataFormatException {
        ByteBuf byteBuf = context.alloc().directBuffer(expectedSize);
        try {
            ByteBuffer byteBuffer = byteBuf.internalNioBuffer(0, expectedSize);
            int i = byteBuffer.position();
            this.inflater.inflate(byteBuffer);
            int j = byteBuffer.position() - i;
            if (j != expectedSize) {
                throw new DecoderException("Badly compressed packet - actual length of uncompressed payload " + j + " is does not match declared size " + expectedSize);
            }
            byteBuf.writerIndex(byteBuf.writerIndex() + j);
            return byteBuf;
        }
        catch (Exception exception) {
            byteBuf.release();
            throw exception;
        }
    }

    public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets) {
        this.compressionThreshold = compressionThreshold;
        this.rejectsBadPackets = rejectsBadPackets;
    }
}

