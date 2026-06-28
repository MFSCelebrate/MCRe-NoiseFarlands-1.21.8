/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class LegacyQueries {
    final static public int HEADER = 250;
    final static public String PING_HOST = "MC|PingHost";
    final static public int QUERY_PACKET_ID = 254;
    final static public int field_44995 = 1;
    final static public int BUFFER_SIZE = 255;
    final static public int PROTOCOL_VERSION = 127;

    public static void write(ByteBuf buf, String string) {
        buf.writeShort(string.length());
        buf.writeCharSequence((CharSequence)string, StandardCharsets.UTF_16BE);
    }

    public static String read(ByteBuf buf) {
        short i = buf.readShort();
        int j = i * 2;
        String string = buf.toString(buf.readerIndex(), j, StandardCharsets.UTF_16BE);
        buf.skipBytes(j);
        return string;
    }
}

