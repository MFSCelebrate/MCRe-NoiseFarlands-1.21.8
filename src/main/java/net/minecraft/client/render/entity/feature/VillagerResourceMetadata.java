/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public record VillagerResourceMetadata(HatType hatType) {
    final static public Codec<VillagerResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)HatType.CODEC.optionalFieldOf("hat", (Object)HatType.NONE).forGetter(VillagerResourceMetadata::hatType)).apply((Applicative)instance, VillagerResourceMetadata::new));
    final static public ResourceMetadataSerializer<VillagerResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<VillagerResourceMetadata>("villager", CODEC);

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{VillagerResourceMetadata.class, "hat", "hatType"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VillagerResourceMetadata.class, "hat", "hatType"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VillagerResourceMetadata.class, "hat", "hatType"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class HatType
    extends Enum<HatType>
    implements StringIdentifiable {
        final static public HatType NONE = new HatType("none");
        final static public HatType PARTIAL = new HatType("partial");
        final static public HatType FULL = new HatType("full");
        final static public Codec<HatType> CODEC;
        final private String name;
        final static private HatType[] field_17165;

        public static HatType[] values() {
            return (HatType[])field_17165.clone();
        }

        public static HatType valueOf(String string) {
            return Enum.valueOf(HatType.class, string);
        }

        private HatType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static HatType[] method_36924() {
            return new HatType[]{NONE, PARTIAL, FULL};
        }

        static {
            field_17165 = HatType.method_36924();
            CODEC = StringIdentifiable.createCodec(HatType::values);
        }
    }
}

