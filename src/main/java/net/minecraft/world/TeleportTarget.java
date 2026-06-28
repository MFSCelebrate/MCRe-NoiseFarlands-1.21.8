/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;

public final class TeleportTarget
extends Record {
    final private ServerWorld world;
    final private Vec3d position;
    final private Vec3d velocity;
    final private float yaw;
    final private float pitch;
    final private boolean missingRespawnBlock;
    final private boolean asPassenger;
    final private Set<PositionFlag> relatives;
    final private PostDimensionTransition postTeleportTransition;
    final static public PostDimensionTransition NO_OP = entity -> {};
    final static public PostDimensionTransition SEND_TRAVEL_THROUGH_PORTAL_PACKET = TeleportTarget::sendTravelThroughPortalPacket;
    final static public PostDimensionTransition ADD_PORTAL_CHUNK_TICKET = TeleportTarget::addPortalChunkTicket;

    public TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, PostDimensionTransition postDimensionTransition) {
        this(world, pos, velocity, yaw, pitch, Set.of(), postDimensionTransition);
    }

    public TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, Set<PositionFlag> flags, PostDimensionTransition postDimensionTransition) {
        this(world, pos, velocity, yaw, pitch, false, false, flags, postDimensionTransition);
    }

    public TeleportTarget(ServerWorld world, Entity entity, PostDimensionTransition postDimensionTransition) {
        this(world, TeleportTarget.getWorldSpawnPos(world, entity), Vec3d.ZERO, world.getSpawnAngle(), 0.0f, false, false, Set.of(), postDimensionTransition);
    }

    public TeleportTarget(ServerWorld serverWorld, Vec3d vec3d, Vec3d vec3d2, float f, float g, boolean bl, boolean bl2, Set<PositionFlag> set, PostDimensionTransition postDimensionTransition) {
        this.world = serverWorld;
        this.position = vec3d;
        this.velocity = vec3d2;
        this.yaw = f;
        this.pitch = g;
        this.missingRespawnBlock = bl;
        this.asPassenger = bl2;
        this.relatives = set;
        this.postTeleportTransition = postDimensionTransition;
    }

    private static void sendTravelThroughPortalPacket(Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            serverPlayerEntity.networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.TRAVEL_THROUGH_PORTAL, BlockPos.ORIGIN, 0, false));
        }
    }

    private static void addPortalChunkTicket(Entity entity) {
        entity.addPortalChunkTicketAt(BlockPos.ofFloored(entity.getPos()));
    }

    public static TeleportTarget missingSpawnBlock(ServerWorld world, Entity entity, PostDimensionTransition postDimensionTransition) {
        return new TeleportTarget(world, TeleportTarget.getWorldSpawnPos(world, entity), Vec3d.ZERO, world.getSpawnAngle(), 0.0f, true, false, Set.of(), postDimensionTransition);
    }

    private static Vec3d getWorldSpawnPos(ServerWorld world, Entity entity) {
        return entity.getWorldSpawnPos(world, world.getSpawnPos()).toBottomCenterPos();
    }

    public TeleportTarget withRotation(float yaw, float pitch) {
        return new TeleportTarget(this.world(), this.position(), this.velocity(), yaw, pitch, this.missingRespawnBlock(), this.boolean_asPassenger(), this.relatives(), this.postTeleportTransition());
    }

    public TeleportTarget withPosition(Vec3d position) {
        return new TeleportTarget(this.world(), position, this.velocity(), this.yaw(), this.pitch(), this.missingRespawnBlock(), this.boolean_asPassenger(), this.relatives(), this.postTeleportTransition());
    }

    public TeleportTarget net_minecraft_world_TeleportTarget_asPassenger() {
        return new TeleportTarget(this.world(), this.position(), this.velocity(), this.yaw(), this.pitch(), this.missingRespawnBlock(), true, this.relatives(), this.postTeleportTransition());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TeleportTarget.class, "newLevel;position;deltaMovement;yRot;xRot;missingRespawnBlock;asPassenger;relatives;postTeleportTransition", "world", "position", "velocity", "yaw", "pitch", "missingRespawnBlock", "asPassenger", "relatives", "postTeleportTransition"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TeleportTarget.class, "newLevel;position;deltaMovement;yRot;xRot;missingRespawnBlock;asPassenger;relatives;postTeleportTransition", "world", "position", "velocity", "yaw", "pitch", "missingRespawnBlock", "asPassenger", "relatives", "postTeleportTransition"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TeleportTarget.class, "newLevel;position;deltaMovement;yRot;xRot;missingRespawnBlock;asPassenger;relatives;postTeleportTransition", "world", "position", "velocity", "yaw", "pitch", "missingRespawnBlock", "asPassenger", "relatives", "postTeleportTransition"}, this, object);
    }

    public ServerWorld world() {
        return this.world;
    }

    public Vec3d position() {
        return this.position;
    }

    public Vec3d velocity() {
        return this.velocity;
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public boolean missingRespawnBlock() {
        return this.missingRespawnBlock;
    }

    public boolean boolean_asPassenger() {
        return this.asPassenger;
    }

    public Set<PositionFlag> relatives() {
        return this.relatives;
    }

    public PostDimensionTransition postTeleportTransition() {
        return this.postTeleportTransition;
    }

    @FunctionalInterface
    public static interface PostDimensionTransition {
        public void onTransition(Entity var1);

        default public PostDimensionTransition then(PostDimensionTransition next) {
            return entity -> {
                this.onTransition(entity);
                next.onTransition(entity);
            };
        }
    }
}

