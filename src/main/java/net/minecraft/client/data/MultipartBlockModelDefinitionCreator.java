/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder;
import net.minecraft.client.render.model.json.WeightedVariant;

@Environment(value=EnvType.CLIENT)
public class MultipartBlockModelDefinitionCreator
implements BlockModelDefinitionCreator {
    final private Block block;
    final private List<Part> multiparts = new ArrayList<Part>();

    private MultipartBlockModelDefinitionCreator(Block block) {
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultipartBlockModelDefinitionCreator create(Block block) {
        return new MultipartBlockModelDefinitionCreator(block);
    }

    public MultipartBlockModelDefinitionCreator with(WeightedVariant part) {
        this.multiparts.add(new Part(Optional.empty(), part));
        return this;
    }

    private void validate(MultipartModelCondition selector) {
        selector.instantiate(this.block.getStateManager());
    }

    public MultipartBlockModelDefinitionCreator with(MultipartModelCondition condition, WeightedVariant part) {
        this.validate(condition);
        this.multiparts.add(new Part(Optional.of(condition), part));
        return this;
    }

    public MultipartBlockModelDefinitionCreator with(MultipartModelConditionBuilder conditionBuilder, WeightedVariant part) {
        return this.with(conditionBuilder.build(), part);
    }

    @Override
    public BlockModelDefinition createBlockModelDefinition() {
        return new BlockModelDefinition(Optional.empty(), Optional.of(new BlockModelDefinition.Multipart(this.multiparts.stream().map(Part::toComponent).toList())));
    }

    @Environment(value=EnvType.CLIENT)
    record Part(Optional<MultipartModelCondition> condition, WeightedVariant variants) {
        public MultipartModelComponent toComponent() {
            return new MultipartModelComponent(this.condition, this.variants.toModel());
        }
    }
}

