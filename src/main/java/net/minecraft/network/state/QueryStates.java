/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.state;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.PingPackets;
import net.minecraft.network.packet.StatusPackets;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.state.NetworkStateBuilder;
import net.minecraft.network.state.NetworkStateFactory;

public class QueryStates {
    final static public NetworkStateFactory<ServerQueryPacketListener, ByteBuf> C2S_FACTORY = NetworkStateBuilder.c2s(NetworkPhase.STATUS, builder -> builder.add(StatusPackets.STATUS_REQUEST, QueryRequestC2SPacket.CODEC).add(PingPackets.PING_REQUEST, QueryPingC2SPacket.CODEC));
    final static public NetworkState<ServerQueryPacketListener> C2S = C2S_FACTORY.bind(buf -> buf);
    final static public NetworkStateFactory<ClientQueryPacketListener, PacketByteBuf> S2C_FACTORY = NetworkStateBuilder.s2c(NetworkPhase.STATUS, builder -> builder.add(StatusPackets.STATUS_RESPONSE, QueryResponseS2CPacket.CODEC).add(PingPackets.PONG_RESPONSE, PingResultS2CPacket.CODEC));
    final static public NetworkState<ClientQueryPacketListener> S2C = S2C_FACTORY.bind(PacketByteBuf::new);
}

