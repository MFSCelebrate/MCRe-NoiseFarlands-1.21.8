/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public final class EntityPose
extends Enum<EntityPose> {
    final static public EntityPose STANDING = new EntityPose(0);
    final static public EntityPose GLIDING = new EntityPose(1);
    final static public EntityPose SLEEPING = new EntityPose(2);
    final static public EntityPose SWIMMING = new EntityPose(3);
    final static public EntityPose SPIN_ATTACK = new EntityPose(4);
    final static public EntityPose CROUCHING = new EntityPose(5);
    final static public EntityPose LONG_JUMPING = new EntityPose(6);
    final static public EntityPose DYING = new EntityPose(7);
    final static public EntityPose CROAKING = new EntityPose(8);
    final static public EntityPose USING_TONGUE = new EntityPose(9);
    final static public EntityPose SITTING = new EntityPose(10);
    final static public EntityPose ROARING = new EntityPose(11);
    final static public EntityPose SNIFFING = new EntityPose(12);
    final static public EntityPose EMERGING = new EntityPose(13);
    final static public EntityPose DIGGING = new EntityPose(14);
    final static public EntityPose SLIDING = new EntityPose(15);
    final static public EntityPose SHOOTING = new EntityPose(16);
    final static public EntityPose INHALING = new EntityPose(17);
    final static public IntFunction<EntityPose> INDEX_TO_VALUE;
    final static public PacketCodec<ByteBuf, EntityPose> PACKET_CODEC;
    final private int index;
    final static private EntityPose[] field_18083;

    public static EntityPose[] values() {
        return (EntityPose[])field_18083.clone();
    }

    public static EntityPose valueOf(String string) {
        return Enum.valueOf(EntityPose.class, string);
    }

    private EntityPose(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    private static EntityPose[] method_36612() {
        return new EntityPose[]{STANDING, GLIDING, SLEEPING, SWIMMING, SPIN_ATTACK, CROUCHING, LONG_JUMPING, DYING, CROAKING, USING_TONGUE, SITTING, ROARING, SNIFFING, EMERGING, DIGGING, SLIDING, SHOOTING, INHALING};
    }

    static {
        field_18083 = EntityPose.method_36612();
        INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(EntityPose::getIndex, EntityPose.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, EntityPose::getIndex);
    }
}

