/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.explosion;

import java.lang.runtime.SwitchBootstraps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Explosion {
    public static DamageSource createDamageSource(World world, @Nullable Entity source) {
        return world.getDamageSources().explosion(source, Explosion.getCausingEntity(source));
    }

    @Nullable
    public static LivingEntity getCausingEntity(@Nullable Entity entity) {
        LivingEntity livingEntity;
        Entity entity2 = entity;
        int n = 0;
        block5: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{TntEntity.class, LivingEntity.class, ProjectileEntity.class}, (Object)entity2, n)) {
                case 0: {
                    TntEntity tntEntity = (TntEntity)entity2;
                    livingEntity = tntEntity.net_minecraft_entity_LivingEntity_getOwner();
                    break block5;
                }
                case 1: {
                    LivingEntity livingEntity2;
                    livingEntity = livingEntity2 = (LivingEntity)entity2;
                    break block5;
                }
                case 2: {
                    ProjectileEntity projectileEntity = (ProjectileEntity)entity2;
                    Entity entity3 = projectileEntity.net_minecraft_entity_Entity_getOwner();
                    if (!(entity3 instanceof LivingEntity)) {
                        n = 3;
                        continue block5;
                    }
                    LivingEntity livingEntity2 = (LivingEntity)entity3;
                    livingEntity = livingEntity2;
                    break block5;
                }
                default: {
                    livingEntity = null;
                    break block5;
                }
            }
            break;
        }
        return livingEntity;
    }

    public ServerWorld getWorld();

    public DestructionType getDestructionType();

    @Nullable
    public LivingEntity getCausingEntity();

    @Nullable
    public Entity getEntity();

    public float getPower();

    public Vec3d getPosition();

    public boolean canTriggerBlocks();

    public boolean preservesDecorativeEntities();

    public static final class DestructionType
    extends Enum<DestructionType> {
        final static public DestructionType KEEP = new DestructionType(false);
        final static public DestructionType DESTROY = new DestructionType(true);
        final static public DestructionType DESTROY_WITH_DECAY = new DestructionType(true);
        final static public DestructionType TRIGGER_BLOCK = new DestructionType(false);
        final private boolean destroysBlocks;
        final static private DestructionType[] field_18688;

        public static DestructionType[] values() {
            return (DestructionType[])field_18688.clone();
        }

        public static DestructionType valueOf(String string) {
            return Enum.valueOf(DestructionType.class, string);
        }

        private DestructionType(boolean destroysBlocks) {
            this.destroysBlocks = destroysBlocks;
        }

        public boolean destroysBlocks() {
            return this.destroysBlocks;
        }

        private static DestructionType[] method_36693() {
            return new DestructionType[]{KEEP, DESTROY, DESTROY_WITH_DECAY, TRIGGER_BLOCK};
        }

        static {
            field_18688 = DestructionType.method_36693();
        }
    }
}

