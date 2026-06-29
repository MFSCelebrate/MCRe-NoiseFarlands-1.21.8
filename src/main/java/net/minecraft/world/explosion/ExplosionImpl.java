/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.explosion;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class ExplosionImpl
implements Explosion {
    final static private ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    final static private int field_52618 = 16;
    final static private float field_52619 = 2.0f;
    final private boolean createFire;
    final private Explosion.DestructionType destructionType;
    final private ServerWorld world;
    final private Vec3d pos;
    @Nullable
    final private Entity entity;
    final private float power;
    final private DamageSource damageSource;
    final private ExplosionBehavior behavior;
    final private Map<PlayerEntity, Vec3d> knockbackByPlayer = new HashMap<PlayerEntity, Vec3d>();

    public ExplosionImpl(ServerWorld world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, Vec3d pos, float power, boolean createFire, Explosion.DestructionType destructionType) {
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.pos = pos;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? world.getDamageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.makeBehavior(entity) : behavior;
    }

    private ExplosionBehavior makeBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity);
    }

    public static float calculateReceivedDamage(Vec3d pos, Entity entity) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (d < 0.0 || e < 0.0 || f < 0.0) {
            return 0.0f;
        }
        int i = 0;
        int j = 0;
        for (double k = 0.0; k <= 1.0; k += d) {
            for (double l = 0.0; l <= 1.0; l += e) {
                for (double m = 0.0; m <= 1.0; m += f) {
                    double n = MathHelper.lerp(k, box.minX, box.maxX);
                    double o = MathHelper.lerp(l, box.minY, box.maxY);
                    double p = MathHelper.lerp(m, box.minZ, box.maxZ);
                    Vec3d vec3d = new Vec3d(n + g, o, p + h);
                    if (entity.net_minecraft_world_World_getWorld().raycast(new RaycastContext(vec3d, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS) {
                        ++i;
                    }
                    ++j;
                }
            }
        }
        return (float)i / (float)j;
    }

    @Override
    public float getPower() {
        return this.power;
    }

    @Override
    public Vec3d getPosition() {
        return this.pos;
    }

    private List<BlockPos> getBlocksToDestroy() {
        HashSet<BlockPos> set = new HashSet<BlockPos>();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                block2: for (int l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d = (float)j / 15.0f * 2.0f - 1.0f;
                    double e = (float)k / 15.0f * 2.0f - 1.0f;
                    double f = (float)l / 15.0f * 2.0f - 1.0f;
                    double g = Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    double m = this.pos.x;
                    double n = this.pos.y;
                    double o = this.pos.z;
                    float p = 0.3f;
                    for (float h = this.power * (0.7f + this.world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = BlockPos.ofFloored(m, n, o);
                        BlockState blockState = this.world.getBlockState(blockPos);
                        FluidState fluidState = this.world.getFluidState(blockPos);
                        if (!this.world.isInBuildLimit(blockPos)) continue block2;
                        Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                        if (optional.isPresent()) {
                            h -= (optional.get().floatValue() + 0.3f) * 0.3f;
                        }
                        if (h > 0.0f && this.behavior.canDestroyBlock(this, this.world, blockPos, blockState, h)) {
                            set.add(blockPos);
                        }
                        m += d * (double)0.3f;
                        n += e * (double)0.3f;
                        o += f * (double)0.3f;
                    }
                }
            }
        }
        return new ObjectArrayList(set);
    }

    private void damageEntities() {
        float f = this.power * 2.0f;
        int i = MathHelper.floor(this.pos.x - (double)f - 1.0);
        int j = MathHelper.floor(this.pos.x + (double)f + 1.0);
        int k = MathHelper.floor(this.pos.y - (double)f - 1.0);
        int l = MathHelper.floor(this.pos.y + (double)f + 1.0);
        int m = MathHelper.floor(this.pos.z - (double)f - 1.0);
        int n = MathHelper.floor(this.pos.z + (double)f + 1.0);
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box(i, k, m, j, l, n));
        for (Entity entity : list) {
            PlayerEntity playerEntity;
            double s;
            float q;
            double h;
            double g;
            double e;
            double o;
            double d;
            if (entity.isImmuneToExplosion(this) || !((d = Math.sqrt(entity.squaredDistanceTo(this.pos)) / (double)f) <= 1.0) || (o = Math.sqrt((e = entity.getX() - this.pos.x) * e + (g = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.pos.y) * g + (h = entity.getZ() - this.pos.z) * h)) == 0.0) continue;
            e /= o;
            g /= o;
            h /= o;
            boolean bl = this.behavior.shouldDamage(this, entity);
            float p = this.behavior.getKnockbackModifier(entity);
            float f2 = q = bl || p != 0.0f ? ExplosionImpl.calculateReceivedDamage(this.pos, entity) : 0.0f;
            if (bl) {
                entity.damage(this.world, this.damageSource, this.behavior.calculateDamage(this, entity, q));
            }
            double r = (1.0 - d) * (double)q * (double)p;
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                s = r * (1.0 - livingEntity.getAttributeValue(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE));
            } else {
                s = r;
            }
            Vec3d vec3d = new Vec3d(e *= s, g *= s, h *= s);
            entity.addVelocity(vec3d);
            if (!(!(entity instanceof PlayerEntity) || (playerEntity = (PlayerEntity)entity).isSpectator() || playerEntity.isCreative() && playerEntity.getAbilities().flying)) {
                this.knockbackByPlayer.put(playerEntity, vec3d);
            }
            entity.onExplodedBy(this.entity);
        }
    }

    private void destroyBlocks(List<BlockPos> positions) {
        ArrayList list = new ArrayList();
        Util.shuffle(positions, this.world.random);
        for (BlockPos blockPos : positions) {
            this.world.getBlockState(blockPos).onExploded(this.world, blockPos, this, (item, pos) -> ExplosionImpl.addDroppedItem(list, item, pos));
        }
        for (DroppedItem droppedItem : list) {
            Block.dropStack((World)this.world, droppedItem.pos, droppedItem.item);
        }
    }

    private void createFire(List<BlockPos> positions) {
        for (BlockPos blockPos : positions) {
            if (this.world.random.nextInt(3) != 0 || !this.world.getBlockState(blockPos).isAir() || !this.world.getBlockState(blockPos.net_minecraft_util_math_BlockPos_down()).isOpaqueFullCube()) continue;
            this.world.setBlockState(blockPos, AbstractFireBlock.getState(this.world, blockPos));
        }
    }

    public void explode() {
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, this.pos);
        List<BlockPos> list = this.getBlocksToDestroy();
        this.damageEntities();
        if (this.shouldDestroyBlocks()) {
            Profiler profiler = Profilers.get();
            profiler.push("explosion_blocks");
            this.destroyBlocks(list);
            profiler.pop();
        }
        if (this.createFire) {
            this.createFire(list);
        }
    }

    private static void addDroppedItem(List<DroppedItem> droppedItemsOut, ItemStack item, BlockPos pos) {
        for (DroppedItem droppedItem : droppedItemsOut) {
            droppedItem.merge(item);
            if (!item.isEmpty()) continue;
            return;
        }
        droppedItemsOut.add(new DroppedItem(pos, item));
    }

    private boolean shouldDestroyBlocks() {
        return this.destructionType != Explosion.DestructionType.KEEP;
    }

    public Map<PlayerEntity, Vec3d> getKnockbackByPlayer() {
        return this.knockbackByPlayer;
    }

    @Override
    public ServerWorld getWorld() {
        return this.world;
    }

    @Override
    @Nullable
    public LivingEntity getCausingEntity() {
        return Explosion.getCausingEntity(this.entity);
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    @Override
    public Explosion.DestructionType getDestructionType() {
        return this.destructionType;
    }

    @Override
    public boolean canTriggerBlocks() {
        if (this.destructionType != Explosion.DestructionType.TRIGGER_BLOCK) {
            return false;
        }
        if (this.entity != null && this.entity.getType() == EntityType.BREEZE_WIND_CHARGE) {
            return this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        }
        return true;
    }

    @Override
    public boolean preservesDecorativeEntities() {
        boolean bl2;
        boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        boolean bl3 = bl2 = this.entity == null || this.entity.getType() != EntityType.BREEZE_WIND_CHARGE && this.entity.getType() != EntityType.WIND_CHARGE;
        if (bl) {
            return bl2;
        }
        return this.destructionType.destroysBlocks() && bl2;
    }

    public boolean isSmall() {
        return this.power < 2.0f || !this.shouldDestroyBlocks();
    }

    static class DroppedItem {
        final BlockPos pos;
        ItemStack item;

        DroppedItem(BlockPos pos, ItemStack item) {
            this.pos = pos;
            this.item = item;
        }

        public void merge(ItemStack other) {
            if (ItemEntity.canMerge(this.item, other)) {
                this.item = ItemEntity.merge(this.item, other, 16);
            }
        }
    }
}

