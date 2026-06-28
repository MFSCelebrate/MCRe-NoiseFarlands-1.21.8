/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public class VariantsBlockModelDefinitionCreator
implements BlockModelDefinitionCreator {
    final private Block block;
    final private List<Entry> variants;
    final private Set<Property<?>> definedProperties;

    VariantsBlockModelDefinitionCreator(Block block, List<Entry> variants, Set<Property<?>> definedProperties) {
        this.block = block;
        this.variants = variants;
        this.definedProperties = definedProperties;
    }

    static Set<Property<?>> validateAndAddProperties(Set<Property<?>> definedProperties, Block block, BlockStateVariantMap<?> variantMap) {
        List<Property<?>> list = variantMap.getProperties();
        list.forEach(property -> {
            if (block.getStateManager().getProperty(property.getName()) != property) {
                throw new IllegalStateException("Property " + String.valueOf(property) + " is not defined for block " + String.valueOf(block));
            }
            if (definedProperties.contains(property)) {
                throw new IllegalStateException("Values of property " + String.valueOf(property) + " already defined for block " + String.valueOf(block));
            }
        });
        HashSet set = new HashSet(definedProperties);
        set.addAll(list);
        return set;
    }

    public VariantsBlockModelDefinitionCreator coordinate(BlockStateVariantMap<ModelVariantOperator> variantMap) {
        Set<Property<?>> set = VariantsBlockModelDefinitionCreator.validateAndAddProperties(this.definedProperties, this.block, variantMap);
        List<Entry> list = this.variants.stream().flatMap(variant -> variant.apply(variantMap)).toList();
        return new VariantsBlockModelDefinitionCreator(this.block, list, set);
    }

    public VariantsBlockModelDefinitionCreator apply(ModelVariantOperator operator) {
        List<Entry> list = this.variants.stream().flatMap(variant -> variant.apply(operator)).toList();
        return new VariantsBlockModelDefinitionCreator(this.block, list, this.definedProperties);
    }

    @Override
    public BlockModelDefinition createBlockModelDefinition() {
        HashMap<String, BlockStateModel.Unbaked> map = new HashMap<String, BlockStateModel.Unbaked>();
        for (Entry entry : this.variants) {
            map.put(entry.properties.asString(), entry.variant.toModel());
        }
        return new BlockModelDefinition(Optional.of(new BlockModelDefinition.Variants(map)), Optional.empty());
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static Empty of(Block block) {
        return new Empty(block);
    }

    public static VariantsBlockModelDefinitionCreator of(Block block, WeightedVariant model) {
        return new VariantsBlockModelDefinitionCreator(block, List.of(new Entry(PropertiesMap.EMPTY, model)), Set.of());
    }

    @Environment(value=EnvType.CLIENT)
    static final class Entry
    extends Record {
        final PropertiesMap properties;
        final WeightedVariant variant;

        Entry(PropertiesMap propertiesMap, WeightedVariant weightedVariant) {
            this.properties = propertiesMap;
            this.variant = weightedVariant;
        }

        public Stream<Entry> apply(BlockStateVariantMap<ModelVariantOperator> operatorMap) {
            return operatorMap.getVariants().entrySet().stream().map(variant -> {
                PropertiesMap propertiesMap = this.properties.copyOf((PropertiesMap)variant.getKey());
                WeightedVariant weightedVariant = this.variant.apply((ModelVariantOperator)variant.getValue());
                return new Entry(propertiesMap, weightedVariant);
            });
        }

        public Stream<Entry> apply(ModelVariantOperator operator) {
            return Stream.of(new Entry(this.properties, this.variant.apply(operator)));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "properties;variant", "properties", "variant"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "properties;variant", "properties", "variant"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "properties;variant", "properties", "variant"}, this, object);
        }

        public PropertiesMap properties() {
            return this.properties;
        }

        public WeightedVariant variant() {
            return this.variant;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Empty {
        final private Block block;

        public Empty(Block block) {
            this.block = block;
        }

        public VariantsBlockModelDefinitionCreator with(BlockStateVariantMap<WeightedVariant> variantMap) {
            Set<Property<?>> set = VariantsBlockModelDefinitionCreator.validateAndAddProperties(Set.of(), this.block, variantMap);
            List<Entry> list = variantMap.getVariants().entrySet().stream().map(entry -> new Entry((PropertiesMap)entry.getKey(), (WeightedVariant)entry.getValue())).toList();
            return new VariantsBlockModelDefinitionCreator(this.block, list, set);
        }
    }
}

