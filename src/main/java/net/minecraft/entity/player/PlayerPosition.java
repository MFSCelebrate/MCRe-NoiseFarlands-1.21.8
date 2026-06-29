/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public record PlayerPosition(Vec3d position, Vec3d deltaMovement, float yaw, float pitch) {
    final static public PacketCodec<PacketByteBuf, PlayerPosition> PACKET_CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, PlayerPosition::position, Vec3d.PACKET_CODEC, PlayerPosition::deltaMovement, PacketCodecs.FLOAT, PlayerPosition::yaw, PacketCodecs.FLOAT, PlayerPosition::pitch, PlayerPosition::new);

    public static PlayerPosition fromEntity(Entity entity) {
        if (entity.isInterpolating()) {
            return new PlayerPosition(entity.getInterpolator().getLerpedPos(), entity.getMovement(), entity.getInterpolator().getLerpedYaw(), entity.getInterpolator().getLerpedPitch());
        }
        return new PlayerPosition(entity.getPos(), entity.getMovement(), entity.getYaw(), entity.getPitch());
    }

    public static PlayerPosition fromTeleportTarget(TeleportTarget teleportTarget) {
        return new PlayerPosition(teleportTarget.position(), teleportTarget.velocity(), teleportTarget.yaw(), teleportTarget.pitch());
    }

    public static PlayerPosition apply(PlayerPosition currentPos, PlayerPosition newPos, Set<PositionFlag> flags) {
        double d = flags.contains((Object)PositionFlag.X) ? currentPos.position.x : 0.0;
        double e = flags.contains((Object)PositionFlag.Y) ? currentPos.position.y : 0.0;
        double f = flags.contains((Object)PositionFlag.Z) ? currentPos.position.z : 0.0;
        float g = flags.contains((Object)PositionFlag.Y_ROT) ? currentPos.yaw : 0.0f;
        float h = flags.contains((Object)PositionFlag.X_ROT) ? currentPos.pitch : 0.0f;
        Vec3d vec3d = new Vec3d(d + newPos.position.x, e + newPos.position.y, f + newPos.position.z);
        float i = g + newPos.yaw;
        float j = MathHelper.clamp(h + newPos.pitch, -90.0f, 90.0f);
        Vec3d vec3d2 = currentPos.deltaMovement;
        if (flags.contains((Object)PositionFlag.ROTATE_DELTA)) {
            float k = currentPos.yaw - i;
            float l = currentPos.pitch - j;
            vec3d2 = vec3d2.rotateX((float)Math.toRadians(l));
            vec3d2 = vec3d2.rotateY((float)Math.toRadians(k));
        }
        Vec3d vec3d3 = new Vec3d(PlayerPosition.resolve(vec3d2.x, newPos.deltaMovement.x, flags, PositionFlag.DELTA_X), PlayerPosition.resolve(vec3d2.y, newPos.deltaMovement.y, flags, PositionFlag.DELTA_Y), PlayerPosition.resolve(vec3d2.z, newPos.deltaMovement.z, flags, PositionFlag.DELTA_Z));
        return new PlayerPosition(vec3d, vec3d3, i, j);
    }

    private static double resolve(double delta, double value, Set<PositionFlag> flags, PositionFlag deltaFlag) {
        return flags.contains((Object)deltaFlag) ? delta + value : value;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerPosition.class, "position;deltaMovement;yRot;xRot", "position", "deltaMovement", "yaw", "pitch"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerPosition.class, "position;deltaMovement;yRot;xRot", "position", "deltaMovement", "yaw", "pitch"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerPosition.class, "position;deltaMovement;yRot;xRot", "position", "deltaMovement", "yaw", "pitch"}, this, object);
    }
}

