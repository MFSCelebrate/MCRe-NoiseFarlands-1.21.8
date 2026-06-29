/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.custom;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record DebugBeeCustomPayload(Bee beeInfo) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugBeeCustomPayload> CODEC = CustomPayload.codecOf(DebugBeeCustomPayload::write, DebugBeeCustomPayload::new);
    final static public CustomPayload.Id<DebugBeeCustomPayload> ID = CustomPayload.id("debug/bee");

    private DebugBeeCustomPayload(PacketByteBuf buf) {
        this(new Bee(buf));
    }

    private void write(PacketByteBuf buf) {
        this.beeInfo.write(buf);
    }

    public CustomPayload.Id<DebugBeeCustomPayload> getId() {
        return ID;
    }

    public record Bee(UUID uuid, int entityId, Vec3d pos, @Nullable Path path, @Nullable BlockPos hivePos, @Nullable BlockPos flowerPos, int travelTicks, Set<String> goals, List<BlockPos> disallowedHives) {
        public Bee(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readInt(), buf.readVec3d(), buf.readNullable(Path::fromBuf), buf.readNullable(BlockPos.PACKET_CODEC), buf.readNullable(BlockPos.PACKET_CODEC), buf.readInt(), buf.readCollection(HashSet::new, PacketByteBuf::readString), buf.readList(BlockPos.PACKET_CODEC));
        }

        public void write(PacketByteBuf buf2) {
            buf2.writeUuid(this.uuid);
            buf2.net_minecraft_network_PacketByteBuf_writeInt(this.entityId);
            buf2.writeVec3d(this.pos);
            buf2.writeNullable(this.path, (buf, path) -> path.toBuf((PacketByteBuf)((Object)buf)));
            buf2.writeNullable(this.hivePos, BlockPos.PACKET_CODEC);
            buf2.writeNullable(this.flowerPos, BlockPos.PACKET_CODEC);
            buf2.net_minecraft_network_PacketByteBuf_writeInt(this.travelTicks);
            buf2.writeCollection(this.goals, PacketByteBuf::writeString);
            buf2.writeCollection(this.disallowedHives, BlockPos.PACKET_CODEC);
        }

        public boolean isHiveAt(BlockPos pos) {
            return Objects.equals(pos, this.hivePos);
        }

        public String getName() {
            return NameGenerator.name(this.uuid);
        }

        @Override
        public String toString() {
            return this.getName();
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Bee.class, "uuid;id;pos;path;hivePos;flowerPos;travelTicks;goals;blacklistedHives", "uuid", "entityId", "pos", "path", "hivePos", "flowerPos", "travelTicks", "goals", "disallowedHives"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Bee.class, "uuid;id;pos;path;hivePos;flowerPos;travelTicks;goals;blacklistedHives", "uuid", "entityId", "pos", "path", "hivePos", "flowerPos", "travelTicks", "goals", "disallowedHives"}, this, object);
        }
    }
}

