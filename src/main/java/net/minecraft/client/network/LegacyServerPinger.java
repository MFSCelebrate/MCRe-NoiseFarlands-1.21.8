/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.util.concurrent.GenericFutureListener
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.handler.LegacyQueries;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LegacyServerPinger
extends SimpleChannelInboundHandler<ByteBuf> {
    final static private Splitter SPLITTER = Splitter.on((char)'\u0000').limit(6);
    final private ServerAddress serverAddress;
    final private ResponseHandler handler;

    public LegacyServerPinger(ServerAddress serverAddress, ResponseHandler handler) {
        this.serverAddress = serverAddress;
        this.handler = handler;
    }

    public void channelActive(ChannelHandlerContext context) throws Exception {
        super.channelActive(context);
        ByteBuf byteBuf = context.alloc().buffer();
        try {
            byteBuf.writeByte(254);
            byteBuf.writeByte(1);
            byteBuf.writeByte(250);
            LegacyQueries.write(byteBuf, "MC|PingHost");
            int i = byteBuf.writerIndex();
            byteBuf.writeShort(0);
            int j = byteBuf.writerIndex();
            byteBuf.writeByte(127);
            LegacyQueries.write(byteBuf, this.serverAddress.getAddress());
            byteBuf.writeInt(this.serverAddress.getPort());
            int k = byteBuf.writerIndex() - j;
            byteBuf.setShort(1, k);
            context.channel().writeAndFlush((Object)byteBuf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        catch (Exception exception) {
            byteBuf.release();
            throw exception;
        }
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        String string;
        List list;
        short s = byteBuf.readUnsignedByte();
        if (s == 255 && "\u00a71".equals((list = SPLITTER.splitToList((CharSequence)(string = LegacyQueries.read(byteBuf)))).get(0))) {
            int i = MathHelper.parseInt((String)list.get(1), 0);
            String string2 = (String)list.get(2);
            String string3 = (String)list.get(3);
            int j = MathHelper.parseInt((String)list.get(4), -1);
            int k = MathHelper.parseInt((String)list.get(5), -1);
            this.handler.handleResponse(i, string2, string3, j, k);
        }
        channelHandlerContext.close();
    }

    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) {
        context.close();
    }

    protected void channelRead0(ChannelHandlerContext context, Object buf) throws Exception {
        this.channelRead0(context, (ByteBuf)buf);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface ResponseHandler {
        public void handleResponse(int var1, String var2, String var3, int var4, int var5);
    }
}

