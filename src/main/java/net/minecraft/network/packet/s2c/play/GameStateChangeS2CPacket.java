/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class GameStateChangeS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<PacketByteBuf, GameStateChangeS2CPacket> CODEC = Packet.createCodec(GameStateChangeS2CPacket::write, GameStateChangeS2CPacket::new);
    final static public Reason NO_RESPAWN_BLOCK = new Reason(0);
    final static public Reason RAIN_STARTED = new Reason(1);
    final static public Reason RAIN_STOPPED = new Reason(2);
    final static public Reason GAME_MODE_CHANGED = new Reason(3);
    final static public Reason GAME_WON = new Reason(4);
    final static public Reason DEMO_MESSAGE_SHOWN = new Reason(5);
    final static public Reason PROJECTILE_HIT_PLAYER = new Reason(6);
    final static public Reason RAIN_GRADIENT_CHANGED = new Reason(7);
    final static public Reason THUNDER_GRADIENT_CHANGED = new Reason(8);
    final static public Reason PUFFERFISH_STING = new Reason(9);
    final static public Reason ELDER_GUARDIAN_EFFECT = new Reason(10);
    final static public Reason IMMEDIATE_RESPAWN = new Reason(11);
    final static public Reason LIMITED_CRAFTING_TOGGLED = new Reason(12);
    final static public Reason INITIAL_CHUNKS_COMING = new Reason(13);
    final static public int DEMO_OPEN_SCREEN = 0;
    final static public int DEMO_MOVEMENT_HELP = 101;
    final static public int DEMO_JUMP_HELP = 102;
    final static public int DEMO_INVENTORY_HELP = 103;
    final static public int DEMO_EXPIRY_NOTICE = 104;
    final private Reason reason;
    final private float value;

    public GameStateChangeS2CPacket(Reason reason, float value) {
        this.reason = reason;
        this.value = value;
    }

    private GameStateChangeS2CPacket(PacketByteBuf buf) {
        this.reason = (Reason)Reason.REASONS.get((int)buf.readUnsignedByte());
        this.value = buf.readFloat();
    }

    private void write(PacketByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeByte(this.reason.id);
        buf.net_minecraft_network_PacketByteBuf_writeFloat(this.value);
    }

    @Override
    public PacketType<GameStateChangeS2CPacket> getPacketType() {
        return PlayPackets.GAME_EVENT;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameStateChange(this);
    }

    public Reason getReason() {
        return this.reason;
    }

    public float getValue() {
        return this.value;
    }

    public static class Reason {
        final static Int2ObjectMap<Reason> REASONS = new Int2ObjectOpenHashMap();
        final int id;

        public Reason(int id) {
            this.id = id;
            REASONS.put(id, (Object)this);
        }
    }
}

