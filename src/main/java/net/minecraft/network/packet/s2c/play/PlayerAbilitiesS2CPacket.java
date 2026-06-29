/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class PlayerAbilitiesS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, PlayerAbilitiesS2CPacket> CODEC = Packet.createCodec(PlayerAbilitiesS2CPacket::write, PlayerAbilitiesS2CPacket::new);
    final static private int INVULNERABLE_MASK = 1;
    final static private int FLYING_MASK = 2;
    final static private int ALLOW_FLYING_MASK = 4;
    final static private int CREATIVE_MODE_MASK = 8;
    final private boolean invulnerable;
    final private boolean flying;
    final private boolean allowFlying;
    final private boolean creativeMode;
    final private float flySpeed;
    final private float walkSpeed;

    public PlayerAbilitiesS2CPacket(PlayerAbilities abilities) {
        this.invulnerable = abilities.invulnerable;
        this.flying = abilities.flying;
        this.allowFlying = abilities.allowFlying;
        this.creativeMode = abilities.creativeMode;
        this.flySpeed = abilities.getFlySpeed();
        this.walkSpeed = abilities.getWalkSpeed();
    }

    private PlayerAbilitiesS2CPacket(PacketByteBuf buf) {
        byte b = buf.readByte();
        this.invulnerable = (b & 1) != 0;
        this.flying = (b & 2) != 0;
        this.allowFlying = (b & 4) != 0;
        this.creativeMode = (b & 8) != 0;
        this.flySpeed = buf.readFloat();
        this.walkSpeed = buf.readFloat();
    }

    private void write(PacketByteBuf buf) {
        byte b = 0;
        if (this.invulnerable) {
            b = (byte)(b | 1);
        }
        if (this.flying) {
            b = (byte)(b | 2);
        }
        if (this.allowFlying) {
            b = (byte)(b | 4);
        }
        if (this.creativeMode) {
            b = (byte)(b | 8);
        }
        buf.net_minecraft_network_PacketByteBuf_writeByte(b);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.flySpeed);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.walkSpeed);
    }

    @Override
    public PacketType<PlayerAbilitiesS2CPacket> getPacketType() {
        return PlayPackets.PLAYER_ABILITIES_S2C;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean allowFlying() {
        return this.allowFlying;
    }

    public boolean isCreativeMode() {
        return this.creativeMode;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }
}

