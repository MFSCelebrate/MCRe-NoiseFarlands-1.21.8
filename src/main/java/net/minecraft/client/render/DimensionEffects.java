/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

@Environment(value=EnvType.CLIENT)
public abstract class DimensionEffects {
    final static private Object2ObjectMap<Identifier, DimensionEffects> BY_IDENTIFIER = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), map -> {
        Overworld overworld = new Overworld();
        map.defaultReturnValue((Object)overworld);
        map.put((Object)DimensionTypes.OVERWORLD_ID, (Object)overworld);
        map.put((Object)DimensionTypes.THE_NETHER_ID, (Object)new Nether());
        map.put((Object)DimensionTypes.THE_END_ID, (Object)new End());
    });
    final private SkyType skyType;
    final private boolean brightenLighting;
    final private boolean darkened;

    public DimensionEffects(SkyType skyType, boolean alternateSkyColor, boolean darkened) {
        this.skyType = skyType;
        this.brightenLighting = alternateSkyColor;
        this.darkened = darkened;
    }

    public static DimensionEffects byDimensionType(DimensionType dimensionType) {
        return (DimensionEffects)BY_IDENTIFIER.get((Object)dimensionType.effects());
    }

    public boolean isSunRisingOrSetting(float skyAngle) {
        return false;
    }

    public int getSkyColor(float skyAngle) {
        return 0;
    }

    public abstract Vec3d adjustFogColor(Vec3d var1, float var2);

    public abstract boolean useThickFog(int var1, int var2);

    public SkyType getSkyType() {
        return this.skyType;
    }

    public boolean shouldBrightenLighting() {
        return this.brightenLighting;
    }

    public boolean isDarkened() {
        return this.darkened;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class SkyType
    extends Enum<SkyType> {
        final static public SkyType NONE = new SkyType();
        final static public SkyType NORMAL = new SkyType();
        final static public SkyType END = new SkyType();
        final static private SkyType[] field_25642;

        public static SkyType[] values() {
            return (SkyType[])field_25642.clone();
        }

        public static SkyType valueOf(String string) {
            return Enum.valueOf(SkyType.class, string);
        }

        private static SkyType[] method_36912() {
            return new SkyType[]{NONE, NORMAL, END};
        }

        static {
            field_25642 = SkyType.method_36912();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Overworld
    extends DimensionEffects {
        final static private float SUN_RISE_SET_THRESHOLD = 0.4f;

        public Overworld() {
            super(SkyType.NORMAL, false, false);
        }

        @Override
        public boolean isSunRisingOrSetting(float skyAngle) {
            float f = MathHelper.cos(skyAngle * ((float)Math.PI * 2));
            return f >= -0.4f && f <= 0.4f;
        }

        @Override
        public int getSkyColor(float skyAngle) {
            float f = MathHelper.cos(skyAngle * ((float)Math.PI * 2));
            float g = f / 0.4f * 0.5f + 0.5f;
            float h = MathHelper.square(1.0f - (1.0f - MathHelper.sin(g * (float)Math.PI)) * 0.99f);
            return ColorHelper.fromFloats(h, g * 0.3f + 0.7f, g * g * 0.7f + 0.2f, 0.2f);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color.multiply(sunHeight * 0.94f + 0.06f, sunHeight * 0.94f + 0.06f, sunHeight * 0.91f + 0.09f);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Nether
    extends DimensionEffects {
        public Nether() {
            super(SkyType.NONE, false, true);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color;
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class End
    extends DimensionEffects {
        public End() {
            super(SkyType.END, true, false);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color.multiply(0.15f);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }
    }
}

