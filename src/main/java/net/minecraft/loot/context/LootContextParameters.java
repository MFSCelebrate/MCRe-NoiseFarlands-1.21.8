/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.context;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

public class LootContextParameters {
    final static public ContextParameter<Entity> THIS_ENTITY = ContextParameter.of("this_entity");
    final static public ContextParameter<PlayerEntity> LAST_DAMAGE_PLAYER = ContextParameter.of("last_damage_player");
    final static public ContextParameter<DamageSource> DAMAGE_SOURCE = ContextParameter.of("damage_source");
    final static public ContextParameter<Entity> ATTACKING_ENTITY = ContextParameter.of("attacking_entity");
    final static public ContextParameter<Entity> DIRECT_ATTACKING_ENTITY = ContextParameter.of("direct_attacking_entity");
    final static public ContextParameter<Vec3d> ORIGIN = ContextParameter.of("origin");
    final static public ContextParameter<BlockState> BLOCK_STATE = ContextParameter.of("block_state");
    final static public ContextParameter<BlockEntity> BLOCK_ENTITY = ContextParameter.of("block_entity");
    final static public ContextParameter<ItemStack> TOOL = ContextParameter.of("tool");
    final static public ContextParameter<Float> EXPLOSION_RADIUS = ContextParameter.of("explosion_radius");
    final static public ContextParameter<Integer> ENCHANTMENT_LEVEL = ContextParameter.of("enchantment_level");
    final static public ContextParameter<Boolean> ENCHANTMENT_ACTIVE = ContextParameter.of("enchantment_active");
}

