/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.EncoderException
 *  io.netty.handler.codec.MessageToByteEncoder
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.encoding.VarInts;

@ChannelHandler.Sharable
public class SizePrepender
extends MessageToByteEncoder<ByteBuf> {
    final static public int MAX_PREPEND_LENGTH = 3;

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        int j = VarInts.getSizeInBytes(1);
        if (j > 3) {
            throw new EncoderException("Packet too large: size " + 1 + " is over 8");
        }
        byteBuf2.ensureWritable(j + 1);
        VarInts.write(byteBuf2, 1);
        byteBuf2.writeBytes(byteBuf, byteBuf.readerIndex(), 1);
    }

    protected void encode(ChannelHandlerContext ctx, Object input, ByteBuf output) throws Exception {
        this.encode(ctx, (ByteBuf)input, output);
    }
}

