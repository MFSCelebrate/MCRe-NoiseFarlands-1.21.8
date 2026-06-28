/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NeedleAngleState;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class TimeProperty
extends NeedleAngleState
implements NumericProperty {
    final static public MapCodec<TimeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("wobble", (Object)true).forGetter(NeedleAngleState::hasWobble), (App)Source.CODEC.fieldOf("source").forGetter(property -> property.source)).apply((Applicative)instance, TimeProperty::new));
    final private Source source;
    final private Random random = Random.create();
    final private NeedleAngleState.Angler angler;

    public TimeProperty(boolean wobble, Source source) {
        super(wobble);
        this.source = source;
        this.angler = this.createAngler(0.9f);
    }

    @Override
    protected float getAngle(ItemStack stack, ClientWorld world, int seed, Entity user) {
        float f = this.source.getAngle(world, stack, user, this.random);
        long l = world.getTime();
        if (this.angler.shouldUpdate(l)) {
            this.angler.update(l, f);
        }
        return this.angler.getAngle();
    }

    public MapCodec<TimeProperty> getCodec() {
        return CODEC;
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract sealed class Source
    extends Enum<Source>
    implements StringIdentifiable {
        final static public Source RANDOM = new Source("random"){

            @Override
            public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
                return random.nextFloat();
            }
        };
        final static public Source DAYTIME = new Source("daytime"){

            @Override
            public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
                return world.getSkyAngle(1.0f);
            }
        };
        final static public Source MOON_PHASE = new Source("moon_phase"){

            @Override
            public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
                return (float)world.getMoonPhase() / 8.0f;
            }
        };
        final static public Codec<Source> CODEC;
        final private String name;
        final static private Source[] field_55562;

        public static Source[] values() {
            return (Source[])field_55562.clone();
        }

        public static Source valueOf(String string) {
            return Enum.valueOf(Source.class, string);
        }

        Source(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        abstract float getAngle(ClientWorld var1, ItemStack var2, Entity var3, Random var4);

        private static Source[] method_65913() {
            return new Source[]{RANDOM, DAYTIME, MOON_PHASE};
        }

        static {
            field_55562 = Source.method_65913();
            CODEC = StringIdentifiable.createCodec(Source::values);
        }
    }
}

