/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.hit;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public abstract class HitResult {
    final protected Vec3d pos;

    protected HitResult(Vec3d pos) {
        this.pos = pos;
    }

    public double squaredDistanceTo(Entity entity) {
        double d = this.pos.x - entity.getX();
        double e = this.pos.y - entity.getY();
        double f = this.pos.z - entity.getZ();
        return d * d + e * e + f * f;
    }

    public abstract Type getType();

    public Vec3d getPos() {
        return this.pos;
    }

    public static final class Type
    extends Enum<Type> {
        final static public Type MISS = new Type();
        final static public Type BLOCK = new Type();
        final static public Type ENTITY = new Type();
        final static private Type[] field_1334;

        public static Type[] values() {
            return (Type[])field_1334.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static Type[] method_36796() {
            return new Type[]{MISS, BLOCK, ENTITY};
        }

        static {
            field_1334 = Type.method_36796();
        }
    }
}

