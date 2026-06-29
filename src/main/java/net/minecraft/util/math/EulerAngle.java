/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;

public final class EulerAngle
extends Record {
    final float pitch;
    final float yaw;
    final float roll;
    final static public Codec<EulerAngle> CODEC = Codec.FLOAT.listOf().comapFlatMap(list -> Util.decodeFixedLengthList(list, 3).map(angles -> new EulerAngle(((Float)angles.get(0)).floatValue(), ((Float)angles.get(1)).floatValue(), ((Float)angles.get(2)).floatValue())), angle -> List.of(Float.valueOf(angle.pitch()), Float.valueOf(angle.yaw()), Float.valueOf(angle.roll())));
    final static public PacketCodec<ByteBuf, EulerAngle> PACKET_CODEC = new PacketCodec<ByteBuf, EulerAngle>(){

        @Override
        public EulerAngle decode(ByteBuf byteBuf) {
            return new EulerAngle(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat());
        }

        @Override
        public void encode(ByteBuf byteBuf, EulerAngle eulerAngle) {
            byteBuf.writeFloat(eulerAngle.pitch);
            byteBuf.writeFloat(eulerAngle.yaw);
            byteBuf.writeFloat(eulerAngle.roll);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (EulerAngle)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };

    public EulerAngle(float f, float g, float h) {
        f = Float.isInfinite(f) || Float.isNaN(f) ? 0.0f : f % 360.0f;
        g = Float.isInfinite(g) || Float.isNaN(g) ? 0.0f : g % 360.0f;
        h = Float.isInfinite(h) || Float.isNaN(h) ? 0.0f : h % 360.0f;
        this.pitch = f;
        this.yaw = g;
        this.roll = h;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EulerAngle.class, "x;y;z", "pitch", "yaw", "roll"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EulerAngle.class, "x;y;z", "pitch", "yaw", "roll"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EulerAngle.class, "x;y;z", "pitch", "yaw", "roll"}, this, object);
    }

    public float pitch() {
        return this.pitch;
    }

    public float yaw() {
        return this.yaw;
    }

    public float roll() {
        return this.roll;
    }
}

