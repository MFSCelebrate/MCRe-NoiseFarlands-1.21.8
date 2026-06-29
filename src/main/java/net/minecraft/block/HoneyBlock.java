/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HoneyBlock
extends TranslucentBlock {
    final static public MapCodec<HoneyBlock> CODEC = HoneyBlock.createCodec(HoneyBlock::new);
    final static private double field_31101 = 0.13;
    final static private double field_31102 = 0.08;
    final static private double field_31103 = 0.05;
    final static private int TICKS_PER_SECOND = 20;
    final static private VoxelShape SHAPE = Block.createColumnShape(14.0, 0.0, 15.0);

    public MapCodec<HoneyBlock> getCodec() {
        return CODEC;
    }

    public HoneyBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TntEntity || entity instanceof AbstractBoatEntity;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
        if (!world.isClient) {
            world.sendEntityStatus(entity, EntityStatuses.DRIP_RICH_HONEY);
        }
        if (entity.handleFallDamage(fallDistance, 0.2f, world.getDamageSources().fall())) {
            entity.playSound(this.soundGroup.getFallSound(), this.soundGroup.getVolume() * 0.5f, this.soundGroup.getPitch() * 0.75f);
        }
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if (this.isSliding(pos, entity)) {
            this.triggerAdvancement(entity, pos);
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }
        super.onEntityCollision(state, world, pos, entity, handler);
    }

    private static double method_65067(double d) {
        return d / (double)0.98f + 0.08;
    }

    private static double method_65068(double d) {
        return (d - 0.08) * (double)0.98f;
    }

    private boolean isSliding(BlockPos pos, Entity entity) {
        if (entity.isOnGround()) {
            return false;
        }
        if (entity.getY() > (double)pos.getY() + 0.9375 - 1.0E-7) {
            return false;
        }
        if (HoneyBlock.method_65067(entity.getVelocity().y) >= -0.08) {
            return false;
        }
        double d = Math.abs((double)pos.getX() + 0.5 - entity.getX());
        double e = Math.abs((double)pos.getZ() + 0.5 - entity.getZ());
        double f = 0.4375 + (double)(entity.getWidth() / 2.0f);
        return d + 1.0E-7 > f || e + 1.0E-7 > f;
    }

    private void triggerAdvancement(Entity entity, BlockPos pos) {
        if (entity instanceof ServerPlayerEntity && entity.net_minecraft_world_World_getWorld().getTime() % 20L == 0L) {
            Criteria.SLIDE_DOWN_BLOCK.trigger((ServerPlayerEntity)entity, entity.net_minecraft_world_World_getWorld().getBlockState(pos));
        }
    }

    private void updateSlidingVelocity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (HoneyBlock.method_65067(entity.getVelocity().y) < -0.13) {
            double d = -0.05 / HoneyBlock.method_65067(entity.getVelocity().y);
            entity.setVelocity(new Vec3d(vec3d.x * d, HoneyBlock.method_65068(-0.05), vec3d.z * d));
        } else {
            entity.setVelocity(new Vec3d(vec3d.x, HoneyBlock.method_65068(-0.05), vec3d.z));
        }
        entity.onLanding();
    }

    private void addCollisionEffects(World world, Entity entity) {
        if (HoneyBlock.hasHoneyBlockEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
            }
            if (!world.isClient && world.random.nextInt(5) == 0) {
                world.sendEntityStatus(entity, EntityStatuses.DRIP_HONEY);
            }
        }
    }

    public static void addRegularParticles(Entity entity) {
        HoneyBlock.addParticles(entity, 5);
    }

    public static void addRichParticles(Entity entity) {
        HoneyBlock.addParticles(entity, 10);
    }

    private static void addParticles(Entity entity, int count) {
        if (!entity.net_minecraft_world_World_getWorld().isClient) {
            return;
        }
        BlockState blockState = Blocks.HONEY_BLOCK.getDefaultState();
        for (int i = 0; i < count; ++i) {
            entity.net_minecraft_world_World_getWorld().addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
        }
    }
}

