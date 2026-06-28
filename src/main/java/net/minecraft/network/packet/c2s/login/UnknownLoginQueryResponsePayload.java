/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;

public record UnknownLoginQueryResponsePayload() implements LoginQueryResponsePayload
{
    final static public UnknownLoginQueryResponsePayload INSTANCE = new UnknownLoginQueryResponsePayload();

    @Override
    public void write(PacketByteBuf buf) {
    }
}

