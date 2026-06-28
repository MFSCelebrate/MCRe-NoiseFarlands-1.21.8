/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.passive;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.ModelAndTexture;
import net.minecraft.util.StringIdentifiable;

public record PigVariant(ModelAndTexture<Model> modelAndTexture, SpawnConditionSelectors spawnConditions) implements VariantSelectorProvider<SpawnContext, SpawnCondition>
{
    final static public Codec<PigVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ModelAndTexture.createMapCodec(Model.CODEC, Model.NORMAL).forGetter(PigVariant::modelAndTexture), (App)SpawnConditionSelectors.CODEC.fieldOf("spawn_conditions").forGetter(PigVariant::spawnConditions)).apply((Applicative)instance, PigVariant::new));
    final static public Codec<PigVariant> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ModelAndTexture.createMapCodec(Model.CODEC, Model.NORMAL).forGetter(PigVariant::modelAndTexture)).apply((Applicative)instance, PigVariant::new));
    final static public Codec<RegistryEntry<PigVariant>> ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.PIG_VARIANT);
    final static public PacketCodec<RegistryByteBuf, RegistryEntry<PigVariant>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.PIG_VARIANT);

    private PigVariant(ModelAndTexture<Model> modelAndTexture) {
        this(modelAndTexture, SpawnConditionSelectors.EMPTY);
    }

    @Override
    public List<VariantSelectorProvider.Selector<SpawnContext, SpawnCondition>> getSelectors() {
        return this.spawnConditions.selectors();
    }

    public static final class Model
    extends Enum<Model>
    implements StringIdentifiable {
        final static public Model NORMAL = new Model("normal");
        final static public Model COLD = new Model("cold");
        final static public Codec<Model> CODEC;
        final private String id;
        final static private Model[] field_55695;

        public static Model[] values() {
            return (Model[])field_55695.clone();
        }

        public static Model valueOf(String string) {
            return Enum.valueOf(Model.class, string);
        }

        private Model(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static Model[] method_66311() {
            return new Model[]{NORMAL, COLD};
        }

        static {
            field_55695 = Model.method_66311();
            CODEC = StringIdentifiable.createCodec(Model::values);
        }
    }
}

