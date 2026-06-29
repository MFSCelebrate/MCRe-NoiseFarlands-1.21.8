/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.source.BiomeAccess;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public abstract class StandardFogModifier
extends FogModifier {
    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        float s;
        float r;
        float k;
        float f = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(skyDarkness) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
        BiomeAccess biomeAccess = world.getBiomeAccess();
        Vec3d vec3d = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
        Vec3d vec3d2 = world.getDimensionEffects().adjustFogColor(CubicSampler.sampleColor(vec3d, (biomeX, biomeY, biomeZ) -> Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(biomeX, biomeY, biomeZ).value().getFogColor())), f);
        float g = (float)vec3d2.getX();
        float h = (float)vec3d2.getY();
        float i = (float)vec3d2.getZ();
        if (viewDistance >= 4) {
            float j = MathHelper.sin(world.getSkyAngleRadians(skyDarkness)) > 0.0f ? -1.0f : 1.0f;
            Vector3f vector3f = new Vector3f(j, 0.0f, 0.0f);
            k = camera.getHorizontalPlane().dot((Vector3fc)vector3f);
            if (k > 0.0f && world.getDimensionEffects().isSunRisingOrSetting(world.getSkyAngle(skyDarkness))) {
                int l = world.getDimensionEffects().getSkyColor(world.getSkyAngle(skyDarkness));
                g = MathHelper.lerp(k *= ColorHelper.getAlphaFloat(l), g, ColorHelper.getRedFloat(l));
                h = MathHelper.lerp(k, h, ColorHelper.getGreenFloat(l));
                i = MathHelper.lerp(k, i, ColorHelper.getBlueFloat(l));
            }
        }
        int m = world.getSkyColor(camera.getPos(), skyDarkness);
        float n = ColorHelper.getRedFloat(m);
        k = ColorHelper.getGreenFloat(m);
        float o = ColorHelper.getBlueFloat(m);
        float p = 0.25f + 0.75f * (float)viewDistance / 32.0f;
        p = 1.0f - (float)Math.pow(p, 0.25);
        g += (n - g) * p;
        h += (k - h) * p;
        i += (o - i) * p;
        float q = world.getRainGradient(skyDarkness);
        if (q > 0.0f) {
            r = 1.0f - q * 0.5f;
            s = 1.0f - q * 0.4f;
            g *= r;
            h *= r;
            i *= s;
        }
        if ((r = world.getThunderGradient(skyDarkness)) > 0.0f) {
            s = 1.0f - r * 0.5f;
            g *= s;
            h *= s;
            i *= s;
        }
        return ColorHelper.fromFloats(1.0f, g, h, i);
    }
}

