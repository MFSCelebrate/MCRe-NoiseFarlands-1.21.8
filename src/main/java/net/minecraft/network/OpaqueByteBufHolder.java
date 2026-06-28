/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.util.ReferenceCounted
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCounted;

public record OpaqueByteBufHolder(ByteBuf contents) implements ReferenceCounted
{
    public OpaqueByteBufHolder(ByteBuf buf) {
        this.contents = ByteBufUtil.ensureAccessible((ByteBuf)buf);
    }

    public static Object pack(Object buf) {
        if (buf instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf)buf;
            return new OpaqueByteBufHolder(byteBuf);
        }
        return buf;
    }

    public static Object unpack(Object holder) {
        if (holder instanceof OpaqueByteBufHolder) {
            OpaqueByteBufHolder opaqueByteBufHolder = (OpaqueByteBufHolder)holder;
            return ByteBufUtil.ensureAccessible((ByteBuf)opaqueByteBufHolder.contents);
        }
        return holder;
    }

    public int refCnt() {
        return this.contents.refCnt();
    }

    public OpaqueByteBufHolder net_minecraft_network_OpaqueByteBufHolder_retain() {
        this.contents.retain();
        return this;
    }

    public OpaqueByteBufHolder net_minecraft_network_OpaqueByteBufHolder_retain(int i) {
        this.contents.retain(i);
        return this;
    }

    public OpaqueByteBufHolder net_minecraft_network_OpaqueByteBufHolder_touch() {
        this.contents.touch();
        return this;
    }

    public OpaqueByteBufHolder net_minecraft_network_OpaqueByteBufHolder_touch(Object object) {
        this.contents.touch(object);
        return this;
    }

    public boolean release() {
        return this.contents.release();
    }

    public boolean release(int count) {
        return this.contents.release(count);
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_touch(Object object) {
        return this.net_minecraft_network_OpaqueByteBufHolder_touch(object);
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_touch() {
        return this.net_minecraft_network_OpaqueByteBufHolder_touch();
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_retain(int count) {
        return this.net_minecraft_network_OpaqueByteBufHolder_retain(count);
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_retain() {
        return this.net_minecraft_network_OpaqueByteBufHolder_retain();
    }
}

