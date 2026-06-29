/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.provider.nbt;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.Nullable;

public class ContextLootNbtProvider
implements LootNbtProvider {
    final static private String BLOCK_ENTITY_TARGET_NAME = "block_entity";
    final static private Target BLOCK_ENTITY_TARGET = new Target(){

        @Override
        public NbtElement getNbt(LootContext context) {
            BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
            return blockEntity != null ? blockEntity.createNbtWithIdentifyingData(blockEntity.getWorld().getRegistryManager()) : null;
        }

        @Override
        public String getName() {
            return ContextLootNbtProvider.BLOCK_ENTITY_TARGET_NAME;
        }

        @Override
        public Set<ContextParameter<?>> getRequiredParameters() {
            return Set.of(LootContextParameters.BLOCK_ENTITY);
        }
    };
    final static public ContextLootNbtProvider BLOCK_ENTITY = new ContextLootNbtProvider(BLOCK_ENTITY_TARGET);
    final static private Codec<Target> TARGET_CODEC = Codec.STRING.xmap(type -> {
        if (type.equals(BLOCK_ENTITY_TARGET_NAME)) {
            return BLOCK_ENTITY_TARGET;
        }
        LootContext.EntityTarget entityTarget = LootContext.EntityTarget.fromString(type);
        return ContextLootNbtProvider.getTarget(entityTarget);
    }, Target::getName);
    final static public MapCodec<ContextLootNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TARGET_CODEC.fieldOf("target").forGetter(provider -> provider.target)).apply((Applicative)instance, ContextLootNbtProvider::new));
    final static public Codec<ContextLootNbtProvider> INLINE_CODEC = TARGET_CODEC.xmap(ContextLootNbtProvider::new, provider -> provider.target);
    final private Target target;

    private static Target getTarget(final LootContext.EntityTarget entityTarget) {
        return new Target(){

            @Override
            @Nullable
            public NbtElement getNbt(LootContext context) {
                Entity entity = context.get(entityTarget.getParameter());
                return entity != null ? NbtPredicate.entityToNbt(entity) : null;
            }

            @Override
            public String getName() {
                return entityTarget.name();
            }

            @Override
            public Set<ContextParameter<?>> getRequiredParameters() {
                return Set.of(entityTarget.getParameter());
            }
        };
    }

    private ContextLootNbtProvider(Target target) {
        this.target = target;
    }

    @Override
    public LootNbtProviderType getType() {
        return LootNbtProviderTypes.CONTEXT;
    }

    @Override
    @Nullable
    public NbtElement getNbt(LootContext context) {
        return this.target.getNbt(context);
    }

    @Override
    public Set<ContextParameter<?>> getRequiredParameters() {
        return this.target.getRequiredParameters();
    }

    public static LootNbtProvider fromTarget(LootContext.EntityTarget target) {
        return new ContextLootNbtProvider(ContextLootNbtProvider.getTarget(target));
    }

    static interface Target {
        @Nullable
        public NbtElement getNbt(LootContext var1);

        public String getName();

        public Set<ContextParameter<?>> getRequiredParameters();
    }
}

