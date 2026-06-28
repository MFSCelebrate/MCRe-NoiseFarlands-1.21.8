/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class TitleFadeS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, TitleFadeS2CPacket> CODEC = Packet.createCodec(TitleFadeS2CPacket::write, TitleFadeS2CPacket::new);
    final private int fadeInTicks;
    final private int stayTicks;
    final private int fadeOutTicks;

    public TitleFadeS2CPacket(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
    }

    private TitleFadeS2CPacket(PacketByteBuf buf) {
        this.fadeInTicks = buf.readInt();
        this.stayTicks = buf.readInt();
        this.fadeOutTicks = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.fadeInTicks);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.stayTicks);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.fadeOutTicks);
    }

    @Override
    public PacketType<TitleFadeS2CPacket> getPacketType() {
        return PlayPackets.SET_TITLES_ANIMATION;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onTitleFade(this);
    }

    public int getFadeInTicks() {
        return this.fadeInTicks;
    }

    public int getStayTicks() {
        return this.stayTicks;
    }

    public int getFadeOutTicks() {
        return this.fadeOutTicks;
    }
}

