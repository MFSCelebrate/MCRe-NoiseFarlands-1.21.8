/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public record GameJoinS2CPacket(int playerEntityId, boolean hardcore, Set<RegistryKey<World>> dimensionIds, int maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean doLimitedCrafting, CommonPlayerSpawnInfo commonPlayerSpawnInfo, boolean enforcesSecureChat) implements Packet<ClientPlayPacketListener>
{
    final static public PacketCodec<RegistryByteBuf, GameJoinS2CPacket> CODEC = Packet.createCodec(GameJoinS2CPacket::write, GameJoinS2CPacket::new);

    private GameJoinS2CPacket(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readBoolean(), buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readRegistryKey(RegistryKeys.WORLD)), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), new CommonPlayerSpawnInfo(buf), buf.readBoolean());
    }

    public GameJoinS2CPacket(int playerEntityId, boolean bl, Set<RegistryKey<World>> set, int i, int j, int k, boolean bl2, boolean bl3, boolean bl4, CommonPlayerSpawnInfo commonPlayerSpawnInfo, boolean bl5) {
        this.playerEntityId = playerEntityId;
        this.hardcore = bl;
        this.dimensionIds = set;
        this.maxPlayers = 1;
        this.viewDistance = j;
        this.simulationDistance = k;
        this.reducedDebugInfo = bl2;
        this.showDeathScreen = bl3;
        this.doLimitedCrafting = bl4;
        this.commonPlayerSpawnInfo = commonPlayerSpawnInfo;
        this.enforcesSecureChat = bl5;
    }

    private void write(RegistryByteBuf buf) {
        buf.net_minecraft_network_PacketByteBuf_writeInt(this.playerEntityId);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.hardcore);
        buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.viewDistance);
        buf.writeVarInt(this.simulationDistance);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.reducedDebugInfo);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.showDeathScreen);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.doLimitedCrafting);
        this.commonPlayerSpawnInfo.write(buf);
        buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.enforcesSecureChat);
    }

    @Override
    public PacketType<GameJoinS2CPacket> getPacketType() {
        return PlayPackets.LOGIN;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameJoin(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;levels;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;doLimitedCrafting;commonPlayerSpawnInfo;enforcesSecureChat", "playerEntityId", "hardcore", "dimensionIds", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "doLimitedCrafting", "commonPlayerSpawnInfo", "enforcesSecureChat"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;levels;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;doLimitedCrafting;commonPlayerSpawnInfo;enforcesSecureChat", "playerEntityId", "hardcore", "dimensionIds", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "doLimitedCrafting", "commonPlayerSpawnInfo", "enforcesSecureChat"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;levels;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;doLimitedCrafting;commonPlayerSpawnInfo;enforcesSecureChat", "playerEntityId", "hardcore", "dimensionIds", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "doLimitedCrafting", "commonPlayerSpawnInfo", "enforcesSecureChat"}, this, object);
    }
}

