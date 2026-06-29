/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NeedleAngleState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompassState
extends NeedleAngleState {
    final static public MapCodec<CompassState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("wobble", (Object)true).forGetter(NeedleAngleState::hasWobble), (App)Target.CODEC.fieldOf("target").forGetter(CompassState::getTarget)).apply((Applicative)instance, CompassState::new));
    final private NeedleAngleState.Angler aimedAngler;
    final private NeedleAngleState.Angler aimlessAngler;
    final private Target target;
    final private Random random = Random.create();

    public CompassState(boolean wobble, Target target) {
        super(wobble);
        this.aimedAngler = this.createAngler(0.8f);
        this.aimlessAngler = this.createAngler(0.8f);
        this.target = target;
    }

    @Override
    protected float getAngle(ItemStack stack, ClientWorld world, int seed, Entity user) {
        GlobalPos globalPos = this.target.getPosition(world, stack, user);
        long l = world.getTime();
        if (!CompassState.canPointTo(user, globalPos)) {
            return this.getAimlessAngle(seed, l);
        }
        return this.getAngleTo(user, l, globalPos.pos());
    }

    private float getAimlessAngle(int seed, long time) {
        if (this.aimlessAngler.shouldUpdate(time)) {
            this.aimlessAngler.update(time, this.random.nextFloat());
        }
        float f = this.aimlessAngler.getAngle() + (float)CompassState.scatter(seed) / 2.1474836E9f;
        return MathHelper.floorMod(f, 1.0f);
    }

    private float getAngleTo(Entity entity, long time, BlockPos pos) {
        float h;
        PlayerEntity playerEntity;
        float f = (float)CompassState.getAngleTo(entity, pos);
        float g = CompassState.getBodyYaw(entity);
        if (entity instanceof PlayerEntity && (playerEntity = (PlayerEntity)entity).isMainPlayer() && playerEntity.net_minecraft_world_World_getWorld().getTickManager().shouldTick()) {
            if (this.aimedAngler.shouldUpdate(time)) {
                this.aimedAngler.update(time, 0.5f - (g - 0.25f));
            }
            h = f + this.aimedAngler.getAngle();
        } else {
            h = 0.5f - (g - 0.25f - f);
        }
        return MathHelper.floorMod(h, 1.0f);
    }

    private static boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
        return pos != null && pos.dimension() == entity.net_minecraft_world_World_getWorld().getRegistryKey() && !(pos.pos().getSquaredDistance(entity.getPos()) < (double)1.0E-5f);
    }

    private static double getAngleTo(Entity entity, BlockPos pos) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX()) / 6.2831854820251465;
    }

    private static float getBodyYaw(Entity entity) {
        return MathHelper.floorMod(entity.getBodyYaw() / 360.0f, 1.0f);
    }

    private static int scatter(int seed) {
        return seed * 1327217883;
    }

    protected Target getTarget() {
        return this.target;
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract sealed class Target
    extends Enum<Target>
    implements StringIdentifiable {
        final static public Target NONE = new Target("none"){

            @Override
            @Nullable
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                return null;
            }
        };
        final static public Target LODESTONE = new Target("lodestone"){

            @Override
            @Nullable
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
                return lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse(null) : null;
            }
        };
        final static public Target SPAWN = new Target("spawn"){

            @Override
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                return GlobalPos.create(world.getRegistryKey(), world.getSpawnPos());
            }
        };
        final static public Target RECOVERY = new Target("recovery"){

            @Override
            @Nullable
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
                GlobalPos globalPos;
                if (holder instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity)holder;
                    globalPos = playerEntity.getLastDeathPos().orElse(null);
                } else {
                    globalPos = null;
                }
                return globalPos;
            }
        };
        final static public Codec<Target> CODEC;
        final private String name;
        final static private Target[] field_55395;

        public static Target[] values() {
            return (Target[])field_55395.clone();
        }

        public static Target valueOf(String string) {
            return Enum.valueOf(Target.class, string);
        }

        Target(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Nullable
        abstract GlobalPos getPosition(ClientWorld var1, ItemStack var2, Entity var3);

        private static Target[] method_65655() {
            return new Target[]{NONE, LODESTONE, SPAWN, RECOVERY};
        }

        static {
            field_55395 = Target.method_65655();
            CODEC = StringIdentifiable.createCodec(Target::values);
        }
    }
}

