/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.slf4j.Logger
 */
package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Locale;
import net.minecraft.network.QueryableServer;
import net.minecraft.network.handler.LegacyQueries;
import org.slf4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final QueryableServer server;

    public LegacyQueryHandler(QueryableServer server) {
        this.server = server;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.markReaderIndex();
        boolean reset = true;
        try {
            if (byteBuf.readUnsignedByte() != 254) {
                resetReaderIndexAndRemove(ctx, byteBuf);
                return;
            }
            // 处理不同版本
            if (byteBuf.readableBytes() == 0) {
                // 1.3.x
                reply(ctx, createBuf(ctx.alloc(), getResponseFor1_2(server)));
                return;
            }
            if (byteBuf.readUnsignedByte() == 1) {
                if (!byteBuf.isReadable() || !isLegacyQuery(byteBuf)) {
                    resetReaderIndexAndRemove(ctx, byteBuf);
                    return;
                }
                // 1.6+
                String response = getResponse(server);
                reply(ctx, createBuf(ctx.alloc(), response));
                return;
            }
            resetReaderIndexAndRemove(ctx, byteBuf);
        } catch (RuntimeException | Throwable e) {
            if (reset) {
                resetReaderIndexAndRemove(ctx, byteBuf);
            }
            throw e;
        } finally {
            byteBuf.release();
        }
    }

    private void resetReaderIndexAndRemove(ChannelHandlerContext ctx, ByteBuf buf) {
        buf.resetReaderIndex();
        ctx.channel().pipeline().remove(this);
        ctx.fireChannelRead(buf);
    }

    private static boolean isLegacyQuery(ByteBuf buf) {
        short s = buf.readUnsignedByte();
        if (s != 250) {
            return false;
        }
        String string = LegacyQueries.read(buf);
        if (!"MC|PingHost".equals(string)) {
            return false;
        }
        int i = buf.readUnsignedShort();
        if (buf.readableBytes() != 1) {
            return false;
        }
        short t = buf.readUnsignedByte();
        if (t < 73) {
            return false;
        }
        String string2 = LegacyQueries.read(buf);
        int j = buf.readInt();
        return j <= 65535;
    }

    private static String getResponseFor1_2(QueryableServer server) {
        return String.format(Locale.ROOT, "%s\u00a7%d\u00a7%d", server.getServerMotd(), server.getCurrentPlayerCount(), server.getMaxPlayerCount());
    }

    private static String getResponse(QueryableServer server) {
        return String.format(Locale.ROOT, "\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, server.getVersion(), server.getServerMotd(), server.getCurrentPlayerCount(), server.getMaxPlayerCount());
    }

    private static void reply(ChannelHandlerContext context, ByteBuf buf) {
        context.pipeline().firstContext().writeAndFlush((Object) buf).addListener((GenericFutureListener) ChannelFutureListener.CLOSE);
    }

    private static ByteBuf createBuf(ByteBufAllocator allocator, String string) {
        ByteBuf byteBuf = allocator.buffer();
        byteBuf.writeByte(255);
        LegacyQueries.write(byteBuf, string);
        return byteBuf;
    }
}
