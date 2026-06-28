/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AreaEffectCloudEntity
extends Entity
implements Ownable {
    final static private int field_29972 = 5;
    final static private TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    final static private TrackedData<ParticleEffect> PARTICLE = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
    final static private float MAX_RADIUS = 32.0f;
    final static private int field_57566 = 0;
    final static private int field_57567 = 0;
    final static private float DEFAULT_RADIUS_ON_USE = 0.0f;
    final static private float DEFAULT_RADIUS_GROWTH = 0.0f;
    final static private float field_57570 = 1.0f;
    final static private float field_40730 = 0.5f;
    final static private float DEFAULT_RADIUS = 3.0f;
    final static public float field_40732 = 6.0f;
    final static public float field_40733 = 0.5f;
    final static public int DEFAULT_DURATION = -1;
    final static public int field_57565 = 600;
    final static private int DEFAULT_WAIT_TIME = 20;
    final static private int DEFAULT_REAPPLICATION_DELAY = 20;
    final static private TintedParticleEffect DEFAULT_PARTICLE_EFFECT = TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, -1);
    @Nullable
    private ParticleEffect customParticle;
    private PotionContentsComponent potionContentsComponent = PotionContentsComponent.DEFAULT;
    private float potionDurationScale = 1.0f;
    final private Map<Entity, Integer> affectedEntities = Maps.newHashMap();
    private int duration = -1;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private int durationOnUse = 0;
    private float radiusOnUse = 0.0f;
    private float radiusGrowth = 0.0f;
    @Nullable
    private LazyEntityReference<LivingEntity> owner;

    public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
    }

    public AreaEffectCloudEntity(World world, double x, double y, double z) {
        this((EntityType<? extends AreaEffectCloudEntity>)EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(RADIUS, Float.valueOf(3.0f));
        builder.add(WAITING, false);
        builder.add(PARTICLE, DEFAULT_PARTICLE_EFFECT);
    }

    public void setRadius(float radius) {
        if (!this.net_minecraft_world_World_getWorld().isClient) {
            this.getDataTracker().set(RADIUS, Float.valueOf(MathHelper.clamp(radius, 0.0f, 32.0f)));
        }
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    public float getRadius() {
        return this.getDataTracker().get(RADIUS).floatValue();
    }

    public void setPotionContents(PotionContentsComponent potionContentsComponent) {
        this.potionContentsComponent = potionContentsComponent;
        this.updateParticle();
    }

    public void setParticleType(@Nullable ParticleEffect customParticle) {
        this.customParticle = customParticle;
        this.updateParticle();
    }

    public void setPotionDurationScale(float potionDurationScale) {
        this.potionDurationScale = potionDurationScale;
    }

    private void updateParticle() {
        if (this.customParticle != null) {
            this.dataTracker.set(PARTICLE, this.customParticle);
        } else {
            int i = ColorHelper.fullAlpha(this.potionContentsComponent.getColor());
            this.dataTracker.set(PARTICLE, TintedParticleEffect.create(DEFAULT_PARTICLE_EFFECT.getType(), i));
        }
    }

    public void addEffect(StatusEffectInstance effect) {
        this.setPotionContents(this.potionContentsComponent.with(effect));
    }

    public ParticleEffect getParticleType() {
        return this.getDataTracker().get(PARTICLE);
    }

    protected void setWaiting(boolean waiting) {
        this.getDataTracker().set(WAITING, waiting);
    }

    public boolean isWaiting() {
        return this.getDataTracker().get(WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void tick() {
        super.tick();
        World world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.serverTick(serverWorld);
        } else {
            this.clientTick();
        }
    }

    private void clientTick() {
        float g;
        int i;
        boolean bl = this.isWaiting();
        float f = this.getRadius();
        if (bl && this.random.nextBoolean()) {
            return;
        }
        ParticleEffect particleEffect = this.getParticleType();
        if (bl) {
            i = 2;
            g = 0.2f;
        } else {
            i = MathHelper.ceil((float)Math.PI * f * f);
            g = f;
        }
        for (int j = 0; j < 1; ++j) {
            float h = this.random.nextFloat() * ((float)Math.PI * 2);
            float k = MathHelper.sqrt(this.random.nextFloat()) * g;
            double d = this.getX() + (double)(MathHelper.cos(h) * k);
            double e = this.getY();
            double l = this.getZ() + (double)(MathHelper.sin(h) * k);
            if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
                if (bl && this.random.nextBoolean()) {
                    this.net_minecraft_world_World_getWorld().addImportantParticleClient(DEFAULT_PARTICLE_EFFECT, d, e, l, 0.0, 0.0, 0.0);
                    continue;
                }
                this.net_minecraft_world_World_getWorld().addImportantParticleClient(particleEffect, d, e, l, 0.0, 0.0, 0.0);
                continue;
            }
            if (bl) {
                this.net_minecraft_world_World_getWorld().addImportantParticleClient(particleEffect, d, e, l, 0.0, 0.0, 0.0);
                continue;
            }
            this.net_minecraft_world_World_getWorld().addImportantParticleClient(particleEffect, d, e, l, (0.5 - this.random.nextDouble()) * 0.15, 0.01f, (0.5 - this.random.nextDouble()) * 0.15);
        }
    }

    private void serverTick(ServerWorld world) {
        boolean bl2;
        if (this.duration != -1 && this.age - this.waitTime >= this.duration) {
            this.discard();
            return;
        }
        boolean bl = this.isWaiting();
        boolean bl3 = bl2 = this.age < this.waitTime;
        if (bl != bl2) {
            this.setWaiting(bl2);
        }
        if (bl2) {
            return;
        }
        float f = this.getRadius();
        if (this.radiusGrowth != 0.0f) {
            if ((f += this.radiusGrowth) < 0.5f) {
                this.discard();
                return;
            }
            this.setRadius(f);
        }
        if (this.age % 5 == 0) {
            this.affectedEntities.entrySet().removeIf(entity -> this.age >= (Integer)entity.getValue());
            if (!this.potionContentsComponent.hasEffects()) {
                this.affectedEntities.clear();
            } else {
                ArrayList list = new ArrayList();
                this.potionContentsComponent.forEachEffect(list::add, this.potionDurationScale);
                List<LivingEntity> list2 = this.net_minecraft_world_World_getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
                if (!list2.isEmpty()) {
                    for (LivingEntity livingEntity : list2) {
                        double e;
                        double d;
                        double g;
                        if (this.affectedEntities.containsKey(livingEntity) || !livingEntity.isAffectedBySplashPotions()) continue;
                        if (list.stream().noneMatch(livingEntity::canHaveStatusEffect) || !((g = (d = livingEntity.getX() - this.getX()) * d + (e = livingEntity.getZ() - this.getZ()) * e) <= (double)(f * f))) continue;
                        this.affectedEntities.put(livingEntity, this.age + this.reapplicationDelay);
                        for (StatusEffectInstance statusEffectInstance : list) {
                            if (statusEffectInstance.getEffectType().value().isInstant()) {
                                statusEffectInstance.getEffectType().value().applyInstantEffect(world, this, this.net_minecraft_entity_LivingEntity_getOwner(), livingEntity, statusEffectInstance.getAmplifier(), 0.5);
                                continue;
                            }
                            livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance), this);
                        }
                        if (this.radiusOnUse != 0.0f) {
                            if ((f += this.radiusOnUse) < 0.5f) {
                                this.discard();
                                return;
                            }
                            this.setRadius(f);
                        }
                        if (this.durationOnUse == 0 || this.duration == -1) continue;
                        this.duration += this.durationOnUse;
                        if (this.duration > 0) continue;
                        this.discard();
                        return;
                    }
                }
            }
        }
    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float radiusOnUse) {
        this.radiusOnUse = radiusOnUse;
    }

    public float getRadiusGrowth() {
        return this.radiusGrowth;
    }

    public void setRadiusGrowth(float radiusGrowth) {
        this.radiusGrowth = radiusGrowth;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int durationOnUse) {
        this.durationOnUse = durationOnUse;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner != null ? new LazyEntityReference<LivingEntity>(owner) : null;
    }

    @Override
    @Nullable
    public LivingEntity net_minecraft_entity_LivingEntity_getOwner() {
        return LazyEntityReference.resolve(this.owner, this.net_minecraft_world_World_getWorld(), LivingEntity.class);
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.age = view.getInt("Age", 0);
        this.duration = view.getInt("Duration", -1);
        this.waitTime = view.getInt("WaitTime", 20);
        this.reapplicationDelay = view.getInt("ReapplicationDelay", 20);
        this.durationOnUse = view.getInt("DurationOnUse", 0);
        this.radiusOnUse = view.getFloat("RadiusOnUse", 0.0f);
        this.radiusGrowth = view.getFloat("RadiusPerTick", 0.0f);
        this.setRadius(view.getFloat("Radius", 3.0f));
        this.owner = LazyEntityReference.fromData(view, "Owner");
        this.setParticleType(view.read("custom_particle", ParticleTypes.TYPE_CODEC).orElse(null));
        this.setPotionContents(view.read("potion_contents", PotionContentsComponent.CODEC).orElse(PotionContentsComponent.DEFAULT));
        this.potionDurationScale = view.getFloat("potion_duration_scale", 1.0f);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt("Age", this.age);
        view.putInt("Duration", this.duration);
        view.putInt("WaitTime", this.waitTime);
        view.putInt("ReapplicationDelay", this.reapplicationDelay);
        view.putInt("DurationOnUse", this.durationOnUse);
        view.putFloat("RadiusOnUse", this.radiusOnUse);
        view.putFloat("RadiusPerTick", this.radiusGrowth);
        view.putFloat("Radius", this.getRadius());
        view.putNullable("custom_particle", ParticleTypes.TYPE_CODEC, this.customParticle);
        LazyEntityReference.writeData(this.owner, view, "Owner");
        if (!this.potionContentsComponent.equals(PotionContentsComponent.DEFAULT)) {
            view.put("potion_contents", PotionContentsComponent.CODEC, this.potionContentsComponent);
        }
        if (this.potionDurationScale != 1.0f) {
            view.putFloat("potion_duration_scale", this.potionDurationScale);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (RADIUS.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(this.getRadius() * 2.0f, 0.5f);
    }

    @Override
    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    @Nullable
    public <T> T get(ComponentType<? extends T> type) {
        if (type == DataComponentTypes.POTION_CONTENTS) {
            return AreaEffectCloudEntity.castComponentValue(type, this.potionContentsComponent);
        }
        if (type == DataComponentTypes.POTION_DURATION_SCALE) {
            return AreaEffectCloudEntity.castComponentValue(type, Float.valueOf(this.potionDurationScale));
        }
        return super.get(type);
    }

    @Override
    protected void copyComponentsFrom(ComponentsAccess from) {
        this.copyComponentFrom(from, DataComponentTypes.POTION_CONTENTS);
        this.copyComponentFrom(from, DataComponentTypes.POTION_DURATION_SCALE);
        super.copyComponentsFrom(from);
    }

    @Override
    protected <T> boolean setApplicableComponent(ComponentType<T> type, T value) {
        if (type == DataComponentTypes.POTION_CONTENTS) {
            this.setPotionContents(AreaEffectCloudEntity.castComponentValue(DataComponentTypes.POTION_CONTENTS, value));
            return true;
        }
        if (type == DataComponentTypes.POTION_DURATION_SCALE) {
            this.setPotionDurationScale(AreaEffectCloudEntity.castComponentValue(DataComponentTypes.POTION_DURATION_SCALE, value).floatValue());
            return true;
        }
        return super.setApplicableComponent(type, value);
    }

    @Override
    @Nullable
    public Entity net_minecraft_entity_Entity_getOwner() {
        return this.net_minecraft_entity_LivingEntity_getOwner();
    }
}

