/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.family;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;

public class BlockFamily {
    final private Block baseBlock;
    final Map<Variant, Block> variants = Maps.newHashMap();
    boolean generateModels = true;
    boolean generateRecipes = true;
    @Nullable
    String group;
    @Nullable
    String unlockCriterionName;

    BlockFamily(Block baseBlock) {
        this.baseBlock = baseBlock;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public Map<Variant, Block> getVariants() {
        return this.variants;
    }

    public Block getVariant(Variant variant) {
        return this.variants.get((Object)variant);
    }

    public boolean shouldGenerateModels() {
        return this.generateModels;
    }

    public boolean shouldGenerateRecipes() {
        return this.generateRecipes;
    }

    public Optional<String> getGroup() {
        if (StringHelper.isBlank(this.group)) {
            return Optional.empty();
        }
        return Optional.of(this.group);
    }

    public Optional<String> getUnlockCriterionName() {
        if (StringHelper.isBlank(this.unlockCriterionName)) {
            return Optional.empty();
        }
        return Optional.of(this.unlockCriterionName);
    }

    public static class Builder {
        final private BlockFamily family;

        public Builder(Block baseBlock) {
            this.family = new BlockFamily(baseBlock);
        }

        public BlockFamily build() {
            return this.family;
        }

        public Builder button(Block block) {
            this.family.variants.put(Variant.BUTTON, block);
            return this;
        }

        public Builder chiseled(Block block) {
            this.family.variants.put(Variant.CHISELED, block);
            return this;
        }

        public Builder mosaic(Block block) {
            this.family.variants.put(Variant.MOSAIC, block);
            return this;
        }

        public Builder cracked(Block block) {
            this.family.variants.put(Variant.CRACKED, block);
            return this;
        }

        public Builder cut(Block block) {
            this.family.variants.put(Variant.CUT, block);
            return this;
        }

        public Builder door(Block block) {
            this.family.variants.put(Variant.DOOR, block);
            return this;
        }

        public Builder customFence(Block block) {
            this.family.variants.put(Variant.CUSTOM_FENCE, block);
            return this;
        }

        public Builder fence(Block block) {
            this.family.variants.put(Variant.FENCE, block);
            return this;
        }

        public Builder customFenceGate(Block block) {
            this.family.variants.put(Variant.CUSTOM_FENCE_GATE, block);
            return this;
        }

        public Builder fenceGate(Block block) {
            this.family.variants.put(Variant.FENCE_GATE, block);
            return this;
        }

        public Builder sign(Block block, Block wallBlock) {
            this.family.variants.put(Variant.SIGN, block);
            this.family.variants.put(Variant.WALL_SIGN, wallBlock);
            return this;
        }

        public Builder slab(Block block) {
            this.family.variants.put(Variant.SLAB, block);
            return this;
        }

        public Builder stairs(Block block) {
            this.family.variants.put(Variant.STAIRS, block);
            return this;
        }

        public Builder pressurePlate(Block block) {
            this.family.variants.put(Variant.PRESSURE_PLATE, block);
            return this;
        }

        public Builder polished(Block block) {
            this.family.variants.put(Variant.POLISHED, block);
            return this;
        }

        public Builder trapdoor(Block block) {
            this.family.variants.put(Variant.TRAPDOOR, block);
            return this;
        }

        public Builder wall(Block block) {
            this.family.variants.put(Variant.WALL, block);
            return this;
        }

        public Builder noGenerateModels() {
            this.family.generateModels = false;
            return this;
        }

        public Builder noGenerateRecipes() {
            this.family.generateRecipes = false;
            return this;
        }

        public Builder group(String group) {
            this.family.group = group;
            return this;
        }

        public Builder unlockCriterionName(String unlockCriterionName) {
            this.family.unlockCriterionName = unlockCriterionName;
            return this;
        }
    }

    public static final class Variant
    extends Enum<Variant> {
        final static public Variant BUTTON = new Variant("button");
        final static public Variant CHISELED = new Variant("chiseled");
        final static public Variant CRACKED = new Variant("cracked");
        final static public Variant CUT = new Variant("cut");
        final static public Variant DOOR = new Variant("door");
        final static public Variant CUSTOM_FENCE = new Variant("fence");
        final static public Variant FENCE = new Variant("fence");
        final static public Variant CUSTOM_FENCE_GATE = new Variant("fence_gate");
        final static public Variant FENCE_GATE = new Variant("fence_gate");
        final static public Variant MOSAIC = new Variant("mosaic");
        final static public Variant SIGN = new Variant("sign");
        final static public Variant SLAB = new Variant("slab");
        final static public Variant STAIRS = new Variant("stairs");
        final static public Variant PRESSURE_PLATE = new Variant("pressure_plate");
        final static public Variant POLISHED = new Variant("polished");
        final static public Variant TRAPDOOR = new Variant("trapdoor");
        final static public Variant WALL = new Variant("wall");
        final static public Variant WALL_SIGN = new Variant("wall_sign");
        final private String name;
        final static private Variant[] field_28547;

        public static Variant[] values() {
            return (Variant[])field_28547.clone();
        }

        public static Variant valueOf(String string) {
            return Enum.valueOf(Variant.class, string);
        }

        private Variant(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        private static Variant[] method_36938() {
            return new Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, CUSTOM_FENCE, FENCE, CUSTOM_FENCE_GATE, FENCE_GATE, MOSAIC, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN};
        }

        static {
            field_28547 = Variant.method_36938();
        }
    }
}

