/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.util.Identifier;

public class LoginPackets {
    final static public PacketType<LoginQueryRequestS2CPacket> CUSTOM_QUERY = LoginPackets.s2c("custom_query");
    final static public PacketType<LoginSuccessS2CPacket> LOGIN_FINISHED = LoginPackets.s2c("login_finished");
    final static public PacketType<LoginHelloS2CPacket> HELLO_S2C = LoginPackets.s2c("hello");
    final static public PacketType<LoginCompressionS2CPacket> LOGIN_COMPRESSION = LoginPackets.s2c("login_compression");
    final static public PacketType<LoginDisconnectS2CPacket> LOGIN_DISCONNECT = LoginPackets.s2c("login_disconnect");
    final static public PacketType<LoginQueryResponseC2SPacket> CUSTOM_QUERY_ANSWER = LoginPackets.c2s("custom_query_answer");
    final static public PacketType<LoginHelloC2SPacket> HELLO_C2S = LoginPackets.c2s("hello");
    final static public PacketType<LoginKeyC2SPacket> KEY = LoginPackets.c2s("key");
    final static public PacketType<EnterConfigurationC2SPacket> LOGIN_ACKNOWLEDGED = LoginPackets.c2s("login_acknowledged");

    private static <T extends Packet<ClientLoginPacketListener>> PacketType<T> s2c(String id) {
        return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
    }

    private static <T extends Packet<ServerLoginPacketListener>> PacketType<T> c2s(String id) {
        return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
    }
}

