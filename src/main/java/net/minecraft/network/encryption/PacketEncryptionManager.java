/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 */
package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class PacketEncryptionManager {
    final private Cipher cipher;
    private byte[] conversionBuffer = new byte[0];
    private byte[] encryptionBuffer = new byte[0];

    protected PacketEncryptionManager(Cipher cipher) {
        this.cipher = cipher;
    }

    private byte[] toByteArray(ByteBuf buf) {
        int i = buf.readableBytes();
        if (this.conversionBuffer.length < i) {
            this.conversionBuffer = new byte[i];
        }
        buf.readBytes(this.conversionBuffer, 0, i);
        return this.conversionBuffer;
    }

    protected ByteBuf decrypt(ChannelHandlerContext context, ByteBuf buf) throws ShortBufferException {
        int i = buf.readableBytes();
        byte[] bs = this.toByteArray(buf);
        ByteBuf byteBuf = context.alloc().heapBuffer(this.cipher.getOutputSize(1));
        byteBuf.writerIndex(this.cipher.update(bs, 0, 1, byteBuf.array(), byteBuf.arrayOffset()));
        return byteBuf;
    }

    protected void encrypt(ByteBuf buf, ByteBuf result) throws ShortBufferException {
        int i = buf.readableBytes();
        byte[] bs = this.toByteArray(buf);
        int j = this.cipher.getOutputSize(1);
        if (this.encryptionBuffer.length < j) {
            this.encryptionBuffer = new byte[j];
        }
        result.writeBytes(this.encryptionBuffer, 0, this.cipher.update(bs, 0, 1, this.encryptionBuffer));
    }
}

