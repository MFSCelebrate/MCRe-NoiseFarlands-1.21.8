/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ProjectileUtil {
    final static public float DEFAULT_MARGIN = 0.3f;

    public static HitResult getCollision(Entity entity, Predicate<Entity> predicate) {
        Vec3d vec3d = entity.getVelocity();
        World world = entity.net_minecraft_world_World_getWorld();
        Vec3d vec3d2 = entity.getPos();
        return ProjectileUtil.getCollision(vec3d2, entity, predicate, vec3d, world, ProjectileUtil.getToleranceMargin(entity), RaycastContext.ShapeType.COLLIDER);
    }

    public static HitResult getCollision(Entity entity, Predicate<Entity> predicate, RaycastContext.ShapeType raycastShapeType) {
        Vec3d vec3d = entity.getVelocity();
        World world = entity.net_minecraft_world_World_getWorld();
        Vec3d vec3d2 = entity.getPos();
        return ProjectileUtil.getCollision(vec3d2, entity, predicate, vec3d, world, ProjectileUtil.getToleranceMargin(entity), raycastShapeType);
    }

    public static HitResult getCollision(Entity entity, Predicate<Entity> predicate, double range) {
        Vec3d vec3d = entity.getRotationVec(0.0f).multiply(range);
        World world = entity.net_minecraft_world_World_getWorld();
        Vec3d vec3d2 = entity.getEyePos();
        return ProjectileUtil.getCollision(vec3d2, entity, predicate, vec3d, world, 0.0f, RaycastContext.ShapeType.COLLIDER);
    }

    private static HitResult getCollision(Vec3d pos, Entity entity, Predicate<Entity> predicate, Vec3d velocity, World world, float margin, RaycastContext.ShapeType raycastShapeType) {
        EntityHitResult hitResult2;
        Vec3d vec3d = pos.add(velocity);
        HitResult hitResult = world.getCollisionsIncludingWorldBorder(new RaycastContext(pos, vec3d, raycastShapeType, RaycastContext.FluidHandling.NONE, entity));
        if (((HitResult)hitResult).getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getPos();
        }
        if ((hitResult2 = ProjectileUtil.getEntityCollision(world, entity, pos, vec3d, entity.getBoundingBox().stretch(velocity).expand(1.0), predicate, margin)) != null) {
            hitResult = hitResult2;
        }
        return hitResult;
    }

    @Nullable
    public static EntityHitResult raycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double maxDistance) {
        World world = entity.net_minecraft_world_World_getWorld();
        double d = maxDistance;
        Entity entity2 = null;
        Vec3d vec3d = null;
        for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
            Vec3d vec3d2;
            double e;
            Box box2 = entity3.getBoundingBox().expand(entity3.getTargetingMargin());
            Optional<Vec3d> optional = box2.raycast(min, max);
            if (box2.contains(min)) {
                if (!(d >= 0.0)) continue;
                entity2 = entity3;
                vec3d = optional.orElse(min);
                d = 0.0;
                continue;
            }
            if (!optional.isPresent() || !((e = min.squaredDistanceTo(vec3d2 = optional.get())) < d) && d != 0.0) continue;
            if (entity3.getRootVehicle() == entity.getRootVehicle()) {
                if (d != 0.0) continue;
                entity2 = entity3;
                vec3d = vec3d2;
                continue;
            }
            entity2 = entity3;
            vec3d = vec3d2;
            d = e;
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2, vec3d);
    }

    @Nullable
    public static EntityHitResult getEntityCollision(World world, ProjectileEntity projectile, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate) {
        return ProjectileUtil.getEntityCollision(world, projectile, min, max, box, predicate, ProjectileUtil.getToleranceMargin(projectile));
    }

    public static float getToleranceMargin(Entity entity) {
        return Math.max(0.0f, Math.min(0.3f, (float)(entity.age - 2) / 20.0f));
    }

    @Nullable
    public static EntityHitResult getEntityCollision(World world, Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, float margin) {
        double d = Double.MAX_VALUE;
        Optional<Object> optional = Optional.empty();
        Entity entity2 = null;
        for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
            double e;
            Box box2 = entity3.getBoundingBox().expand(margin);
            Optional<Vec3d> optional2 = box2.raycast(min, max);
            if (!optional2.isPresent() || !((e = min.squaredDistanceTo(optional2.get())) < d)) continue;
            entity2 = entity3;
            d = e;
            optional = optional2;
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2, (Vec3d)optional.get());
    }

    public static void setRotationFromVelocity(Entity entity, float tickProgress) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.lengthSquared() == 0.0) {
            return;
        }
        double d = vec3d.horizontalLength();
        entity.setYaw((float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875) + 90.0f);
        entity.setPitch((float)(MathHelper.atan2(d, vec3d.y) * 57.2957763671875) - 90.0f);
        while (entity.getPitch() - entity.lastPitch < -180.0f) {
            entity.lastPitch -= 360.0f;
        }
        while (entity.getPitch() - entity.lastPitch >= 180.0f) {
            entity.lastPitch += 360.0f;
        }
        while (entity.getYaw() - entity.lastYaw < -180.0f) {
            entity.lastYaw -= 360.0f;
        }
        while (entity.getYaw() - entity.lastYaw >= 180.0f) {
            entity.lastYaw += 360.0f;
        }
        entity.setPitch(MathHelper.lerp(tickProgress, entity.lastPitch, entity.getPitch()));
        entity.setYaw(MathHelper.lerp(tickProgress, entity.lastYaw, entity.getYaw()));
    }

    public static Hand getHandPossiblyHolding(LivingEntity entity, Item item) {
        return entity.getMainHandStack().isOf(item) ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static PersistentProjectileEntity createArrowProjectile(LivingEntity entity, ItemStack stack, float damageModifier, @Nullable ItemStack bow) {
        ArrowItem arrowItem = (ArrowItem)(stack.getItem() instanceof ArrowItem ? stack.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(entity.net_minecraft_world_World_getWorld(), stack, entity, bow);
        persistentProjectileEntity.applyDamageModifier(damageModifier);
        return persistentProjectileEntity;
    }
}

