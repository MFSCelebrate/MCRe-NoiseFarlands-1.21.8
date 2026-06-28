/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugGoalSelectorCustomPayload(int entityId, BlockPos pos, List<Goal> goals) implements CustomPayload
{
    final static public PacketCodec<PacketByteBuf, DebugGoalSelectorCustomPayload> CODEC = CustomPayload.codecOf(DebugGoalSelectorCustomPayload::write, DebugGoalSelectorCustomPayload::new);
    final static public CustomPayload.Id<DebugGoalSelectorCustomPayload> ID = CustomPayload.id("debug/goal_selector");

    private DebugGoalSelectorCustomPayload(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBlockPos(), buf.readList(Goal::new));
    }

    private void write(PacketByteBuf buf2) {
        buf2.net_minecraft_network_PacketByteBuf_writeInt(this.entityId);
        buf2.writeBlockPos(this.pos);
        buf2.writeCollection(this.goals, (buf, goal) -> goal.write((PacketByteBuf)((Object)buf)));
    }

    public CustomPayload.Id<DebugGoalSelectorCustomPayload> getId() {
        return ID;
    }

    public record Goal(int priority, boolean isRunning, String name) {
        public Goal(PacketByteBuf buf) {
            this(buf.readInt(), buf.readBoolean(), buf.readString(255));
        }

        public void write(PacketByteBuf buf) {
            buf.net_minecraft_network_PacketByteBuf_writeInt(this.priority);
            buf.net_minecraft_network_PacketByteBuf_writeBoolean(this.isRunning);
            buf.writeString(this.name);
        }
    }
}

