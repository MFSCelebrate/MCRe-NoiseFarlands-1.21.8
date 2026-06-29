/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record Transformation(Target target, Keyframe[] keyframes) {

    @Environment(value=EnvType.CLIENT)
    public static interface Target {
        public void apply(ModelPart var1, Vector3f var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Interpolations {
        final static public Interpolation LINEAR = (dest, delta, keyframes, start, end, scale) -> {
            Vector3f vector3f = keyframes[start].target();
            Vector3f vector3f2 = keyframes[end].target();
            return vector3f.lerp((Vector3fc)vector3f2, delta, dest).mul(scale);
        };
        final static public Interpolation CUBIC = (dest, delta, keyframes, start, end, scale) -> {
            Vector3f vector3f = keyframes[Math.max(0, start - 1)].target();
            Vector3f vector3f2 = keyframes[start].target();
            Vector3f vector3f3 = keyframes[end].target();
            Vector3f vector3f4 = keyframes[Math.min(keyframes.length - 1, end + 1)].target();
            dest.set(MathHelper.catmullRom(delta, vector3f.x(), vector3f2.x(), vector3f3.x(), vector3f4.x()) * scale, MathHelper.catmullRom(delta, vector3f.y(), vector3f2.y(), vector3f3.y(), vector3f4.y()) * scale, MathHelper.catmullRom(delta, vector3f.z(), vector3f2.z(), vector3f3.z(), vector3f4.z()) * scale);
            return dest;
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class Targets {
        final static public Target MOVE_ORIGIN = ModelPart::moveOrigin;
        final static public Target ROTATE = ModelPart::rotate;
        final static public Target SCALE = ModelPart::scale;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Interpolation {
        public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}

