/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class PlaySoundS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<RegistryByteBuf, PlaySoundS2CPacket> CODEC = Packet.createCodec(PlaySoundS2CPacket::write, PlaySoundS2CPacket::new);
    final static public float COORDINATE_SCALE = 8.0f;
    final private RegistryEntry<SoundEvent> sound;
    final private SoundCategory category;
    final private int fixedX;
    final private int fixedY;
    final private int fixedZ;
    final private float volume;
    final private float pitch;
    final private long seed;

    public PlaySoundS2CPacket(RegistryEntry<SoundEvent> sound, SoundCategory category, double x, double y, double z, float volume, float pitch, long seed) {
        this.sound = sound;
        this.category = category;
        this.fixedX = (int)(x * 8.0);
        this.fixedY = (int)(y * 8.0);
        this.fixedZ = (int)(z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    private PlaySoundS2CPacket(RegistryByteBuf buf) {
        this.sound = (RegistryEntry)SoundEvent.ENTRY_PACKET_CODEC.decode(buf);
        this.category = buf.readEnumConstant(SoundCategory.class);
        this.fixedX = buf.readInt();
        this.fixedY = buf.readInt();
        this.fixedZ = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.seed = buf.readLong();
    }

    private void write(RegistryByteBuf buf) {
        SoundEvent.ENTRY_PACKET_CODEC.encode(buf, this.sound);
        buf.writeEnumConstant(this.category);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.fixedX);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.fixedY);
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.fixedZ);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.volume);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.pitch);
        buf.net_minecraft_network_PacketByteBuf_writeLong(this.seed);
    }

    @Override
    public PacketType<PlaySoundS2CPacket> getPacketType() {
        return PlayPackets.SOUND;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlaySound(this);
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public SoundCategory getCategory() {
        return this.category;
    }

    public double getX() {
        return (float)this.fixedX / 8.0f;
    }

    public double getY() {
        return (float)this.fixedY / 8.0f;
    }

    public double getZ() {
        return (float)this.fixedZ / 8.0f;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public long getSeed() {
        return this.seed;
    }
}

