/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;

public class EntityStatusEffectS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<RegistryByteBuf, EntityStatusEffectS2CPacket> CODEC = Packet.createCodec(EntityStatusEffectS2CPacket::write, EntityStatusEffectS2CPacket::new);
    final static private int AMBIENT_MASK = 1;
    final static private int SHOW_PARTICLES_MASK = 2;
    final static private int SHOW_ICON_MASK = 4;
    final static private int KEEP_FADING_MASK = 8;
    final private int entityId;
    final private RegistryEntry<StatusEffect> effectId;
    final private int amplifier;
    final private int duration;
    final private byte flags;

    public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect, boolean keepFading) {
        this.entityId = entityId;
        this.effectId = effect.getEffectType();
        this.amplifier = effect.getAmplifier();
        this.duration = effect.getDuration();
        byte b = 0;
        if (effect.isAmbient()) {
            b = (byte)(b | 1);
        }
        if (effect.shouldShowParticles()) {
            b = (byte)(b | 2);
        }
        if (effect.shouldShowIcon()) {
            b = (byte)(b | 4);
        }
        if (keepFading) {
            b = (byte)(b | 8);
        }
        this.flags = b;
    }

    private EntityStatusEffectS2CPacket(RegistryByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.effectId = (RegistryEntry)StatusEffect.ENTRY_PACKET_CODEC.decode(buf);
        this.amplifier = buf.readVarInt();
        this.duration = buf.readVarInt();
        this.flags = buf.readByte();
    }

    private void write(RegistryByteBuf buf) {
        buf.writeVarInt(this.entityId);
        StatusEffect.ENTRY_PACKET_CODEC.encode(buf, this.effectId);
        buf.writeVarInt(this.amplifier);
        buf.writeVarInt(this.duration);
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.flags);
    }

    @Override
    public PacketType<EntityStatusEffectS2CPacket> getPacketType() {
        return PlayPackets.UPDATE_MOB_EFFECT;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityStatusEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public RegistryEntry<StatusEffect> getEffectId() {
        return this.effectId;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean shouldShowParticles() {
        return (this.flags & 2) != 0;
    }

    public boolean isAmbient() {
        return (this.flags & 1) != 0;
    }

    public boolean shouldShowIcon() {
        return (this.flags & 4) != 0;
    }

    public boolean keepFading() {
        return (this.flags & 8) != 0;
    }
}

