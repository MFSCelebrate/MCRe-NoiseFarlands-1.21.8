/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.CorruptedFrameException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.handler.PacketSizeLogger;
import org.jetbrains.annotations.Nullable;

public class SplitterHandler
extends ByteToMessageDecoder {
    final static private int LENGTH_BYTES = 3;
    final private ByteBuf reusableBuf = Unpooled.directBuffer(3);
    @Nullable
    final private PacketSizeLogger packetSizeLogger;

    public SplitterHandler(@Nullable PacketSizeLogger packetSizeLogger) {
        this.packetSizeLogger = packetSizeLogger;
    }

    protected void handlerRemoved0(ChannelHandlerContext context) {
        this.reusableBuf.release();
    }

    private static boolean shouldSplit(ByteBuf source, ByteBuf sizeBuf) {
        for (int i = 0; i < 3; ++i) {
            if (!source.isReadable()) {
                return false;
            }
            byte b = source.readByte();
            sizeBuf.writeByte((int)b);
            if (VarInts.shouldContinueRead(b)) continue;
            return true;
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> bytes) {
        buf.markReaderIndex();
        this.reusableBuf.clear();
        if (!SplitterHandler.shouldSplit(buf, this.reusableBuf)) {
            buf.resetReaderIndex();
            return;
        }
        int i = VarInts.read(this.reusableBuf);
        if (buf.readableBytes() < 1) {
            buf.resetReaderIndex();
            return;
        }
        if (this.packetSizeLogger != null) {
            this.packetSizeLogger.increment(1 + VarInts.getSizeInBytes(1));
        }
        bytes.add(buf.readBytes(1));
    }
}

