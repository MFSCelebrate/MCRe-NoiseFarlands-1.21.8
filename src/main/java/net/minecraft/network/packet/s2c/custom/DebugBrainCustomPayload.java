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
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record DebugBrainCustomPayload(Brain brainDump) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugBrainCustomPayload> CODEC = CustomPayload.codecOf(DebugBrainCustomPayload::write, DebugBrainCustomPayload::new);
    final static public CustomPayload.Id<DebugBrainCustomPayload> ID = CustomPayload.id("debug/brain");

    private DebugBrainCustomPayload(PacketByteBuf buf) {
        this(new Brain(buf));
    }

    private void write(PacketByteBuf buf) {
        this.brainDump.write(buf);
    }

    public CustomPayload.Id<DebugBrainCustomPayload> getId() {
        return ID;
    }

    public record Brain(UUID uuid, int entityId, String name, String profession, int experience, float health, float maxHealth, Vec3d pos, String inventory, @Nullable Path path, boolean wantsGolem, int angerLevel, List<String> possibleActivities, List<String> runningTasks, List<String> memories, List<String> gossips, Set<BlockPos> pois, Set<BlockPos> potentialPois) {
        public Brain(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readInt(), buf.readString(), buf.readString(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readVec3d(), buf.readString(), buf.readNullable(Path::fromBuf), buf.readBoolean(), buf.readInt(), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readString), buf.readCollection(HashSet::new, BlockPos.PACKET_CODEC), buf.readCollection(HashSet::new, BlockPos.PACKET_CODEC));
        }

        public void write(PacketByteBuf buf2) {
            buf2.writeUuid(this.uuid);
            buf2.net_minecraft_network_PacketByteBuf_writeInt(this.entityId);
            buf2.writeString(this.name);
            buf2.writeString(this.profession);
            buf2.net_minecraft_network_PacketByteBuf_writeInt(this.experience);
            buf2.net_minecraft_network_PacketByteBuf_writeFloat(this.health);
            buf2.net_minecraft_network_PacketByteBuf_writeFloat(this.maxHealth);
            buf2.writeVec3d(this.pos);
            buf2.writeString(this.inventory);
            buf2.writeNullable(this.path, (buf, path) -> path.toBuf((PacketByteBuf)((Object)buf)));
            buf2.net_minecraft_network_PacketByteBuf_writeBoolean(this.wantsGolem);
            buf2.net_minecraft_network_PacketByteBuf_writeInt(this.angerLevel);
            buf2.writeCollection(this.possibleActivities, PacketByteBuf::writeString);
            buf2.writeCollection(this.runningTasks, PacketByteBuf::writeString);
            buf2.writeCollection(this.memories, PacketByteBuf::writeString);
            buf2.writeCollection(this.gossips, PacketByteBuf::writeString);
            buf2.writeCollection(this.pois, BlockPos.PACKET_CODEC);
            buf2.writeCollection(this.potentialPois, BlockPos.PACKET_CODEC);
        }

        public boolean isPointOfInterest(BlockPos pos) {
            return this.pois.contains(pos);
        }

        public boolean isPotentialJobSite(BlockPos pos) {
            return this.potentialPois.contains(pos);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Brain.class, "uuid;id;name;profession;xp;health;maxHealth;pos;inventory;path;wantsGolem;angerLevel;activities;behaviors;memories;gossips;pois;potentialPois", "uuid", "entityId", "name", "profession", "experience", "health", "maxHealth", "pos", "inventory", "path", "wantsGolem", "angerLevel", "possibleActivities", "runningTasks", "memories", "gossips", "pois", "potentialPois"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Brain.class, "uuid;id;name;profession;xp;health;maxHealth;pos;inventory;path;wantsGolem;angerLevel;activities;behaviors;memories;gossips;pois;potentialPois", "uuid", "entityId", "name", "profession", "experience", "health", "maxHealth", "pos", "inventory", "path", "wantsGolem", "angerLevel", "possibleActivities", "runningTasks", "memories", "gossips", "pois", "potentialPois"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Brain.class, "uuid;id;name;profession;xp;health;maxHealth;pos;inventory;path;wantsGolem;angerLevel;activities;behaviors;memories;gossips;pois;potentialPois", "uuid", "entityId", "name", "profession", "experience", "health", "maxHealth", "pos", "inventory", "path", "wantsGolem", "angerLevel", "possibleActivities", "runningTasks", "memories", "gossips", "pois", "potentialPois"}, this, object);
        }
    }
}

