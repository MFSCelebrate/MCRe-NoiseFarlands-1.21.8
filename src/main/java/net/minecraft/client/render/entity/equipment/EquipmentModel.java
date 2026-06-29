/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record EquipmentModel(Map<LayerType, List<Layer>> layers) {
    final static private Codec<List<Layer>> LAYER_LIST_CODEC = Codecs.nonEmptyList(Layer.CODEC.listOf());
    final static public Codec<EquipmentModel> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.nonEmptyMap(Codec.unboundedMap(LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentModel::layers)).apply((Applicative)instance, EquipmentModel::new));

    public static Builder builder() {
        return new Builder();
    }

    public List<Layer> getLayers(LayerType layerType) {
        return this.layers.getOrDefault(layerType, List.of());
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final private Map<LayerType, List<Layer>> layers = new EnumMap<LayerType, List<Layer>>(LayerType.class);

        Builder() {
        }

        public Builder addHumanoidLayers(Identifier textureId) {
            return this.addHumanoidLayers(textureId, false);
        }

        public Builder addHumanoidLayers(Identifier textureId, boolean dyeable) {
            this.addLayers(LayerType.HUMANOID_LEGGINGS, Layer.createWithLeatherColor(textureId, dyeable));
            this.addMainHumanoidLayer(textureId, dyeable);
            return this;
        }

        public Builder addMainHumanoidLayer(Identifier textureId, boolean dyeable) {
            return this.addLayers(LayerType.HUMANOID, Layer.createWithLeatherColor(textureId, dyeable));
        }

        public Builder addLayers(LayerType layerType2, Layer ... layers) {
            Collections.addAll(this.layers.computeIfAbsent(layerType2, layerType -> new ArrayList()), layers);
            return this;
        }

        public EquipmentModel build() {
            return new EquipmentModel((Map)this.layers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> List.copyOf((Collection)entry.getValue()))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class LayerType
    extends Enum<LayerType>
    implements StringIdentifiable {
        final static public LayerType HUMANOID = new LayerType("humanoid");
        final static public LayerType HUMANOID_LEGGINGS = new LayerType("humanoid_leggings");
        final static public LayerType WINGS = new LayerType("wings");
        final static public LayerType WOLF_BODY = new LayerType("wolf_body");
        final static public LayerType HORSE_BODY = new LayerType("horse_body");
        final static public LayerType LLAMA_BODY = new LayerType("llama_body");
        final static public LayerType PIG_SADDLE = new LayerType("pig_saddle");
        final static public LayerType STRIDER_SADDLE = new LayerType("strider_saddle");
        final static public LayerType CAMEL_SADDLE = new LayerType("camel_saddle");
        final static public LayerType HORSE_SADDLE = new LayerType("horse_saddle");
        final static public LayerType DONKEY_SADDLE = new LayerType("donkey_saddle");
        final static public LayerType MULE_SADDLE = new LayerType("mule_saddle");
        final static public LayerType ZOMBIE_HORSE_SADDLE = new LayerType("zombie_horse_saddle");
        final static public LayerType SKELETON_HORSE_SADDLE = new LayerType("skeleton_horse_saddle");
        final static public LayerType HAPPY_GHAST_BODY = new LayerType("happy_ghast_body");
        final static public Codec<LayerType> CODEC;
        final private String name;
        final static private LayerType[] field_54133;

        public static LayerType[] values() {
            return (LayerType[])field_54133.clone();
        }

        public static LayerType valueOf(String string) {
            return Enum.valueOf(LayerType.class, string);
        }

        private LayerType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public String getTrimsDirectory() {
            return "trims/entity/" + this.name;
        }

        private static LayerType[] method_64010() {
            return new LayerType[]{HUMANOID, HUMANOID_LEGGINGS, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY, PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE, ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, HAPPY_GHAST_BODY};
        }

        static {
            field_54133 = LayerType.method_64010();
            CODEC = StringIdentifiable.createCodec(LayerType::values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Layer(Identifier textureId, Optional<Dyeable> dyeable, boolean usePlayerTexture) {
        final static public Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(Layer::textureId), (App)Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(Layer::dyeable), (App)Codec.BOOL.optionalFieldOf("use_player_texture", (Object)false).forGetter(Layer::usePlayerTexture)).apply((Applicative)instance, Layer::new));

        public Layer(Identifier textureId) {
            this(textureId, Optional.empty(), false);
        }

        public static Layer createWithLeatherColor(Identifier textureId, boolean dyeable) {
            return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
        }

        public static Layer create(Identifier textureId, boolean dyeable) {
            return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.empty())) : Optional.empty(), false);
        }

        public Identifier getFullTextureId(LayerType layerType) {
            return this.textureId.withPath(textureName -> "textures/entity/equipment/" + layerType.asString() + "/" + textureName + ".png");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Dyeable(Optional<Integer> colorWhenUndyed) {
        final static public Codec<Dyeable> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.RGB.optionalFieldOf("color_when_undyed").forGetter(Dyeable::colorWhenUndyed)).apply((Applicative)instance, Dyeable::new));
    }
}

