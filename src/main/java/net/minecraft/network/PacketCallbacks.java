/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.channel.ChannelFutureListener
 *  org.slf4j.Logger
 */
package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFutureListener;
import java.util.function.Supplier;
import net.minecraft.network.packet.Packet;
import org.slf4j.Logger;

public class PacketCallbacks {
    final static private Logger LOGGER = LogUtils.getLogger();

    public static ChannelFutureListener always(Runnable runnable) {
        return channelFuture -> {
            runnable.run();
            if (!channelFuture.isSuccess()) {
                channelFuture.channel().pipeline().fireExceptionCaught(channelFuture.cause());
            }
        };
    }

    public static ChannelFutureListener of(Supplier<Packet<?>> failurePacket) {
        return channelFuture -> {
            if (!channelFuture.isSuccess()) {
                Packet packet = (Packet)failurePacket.get();
                if (packet != null) {
                    LOGGER.warn("Failed to deliver packet, sending fallback {}", packet.getPacketType(), (Object)channelFuture.cause());
                    channelFuture.channel().writeAndFlush((Object)packet, channelFuture.channel().voidPromise());
                } else {
                    channelFuture.channel().pipeline().fireExceptionCaught(channelFuture.cause());
                }
            }
        };
    }
}

