/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;
import net.minecraft.network.encoding.VarInts;

public class PacketDeflater
extends MessageToByteEncoder<ByteBuf> {
    final private byte[] deflateBuffer = new byte[8192];
    final private Deflater deflater;
    private int compressionThreshold;

    public PacketDeflater(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        this.deflater = new Deflater();
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        if (1 > 0x800000) {
            throw new IllegalArgumentException("Packet too big (is " + 1 + ", should be less than 8388608)");
        }
        if (1 < this.compressionThreshold) {
            VarInts.write(byteBuf2, 0);
            byteBuf2.writeBytes(byteBuf);
        } else {
            byte[] bs = new byte[1];
            byteBuf.readBytes(bs);
            VarInts.write(byteBuf2, bs.length);
            this.deflater.setInput(bs, 0, 1);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.deflateBuffer);
                byteBuf2.writeBytes(this.deflateBuffer, 0, j);
            }
            this.deflater.reset();
        }
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    protected void encode(ChannelHandlerContext ctx, Object input, ByteBuf output) throws Exception {
        this.encode(ctx, (ByteBuf)input, output);
    }
}

