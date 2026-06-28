/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.decoration;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InteractionEntity
extends Entity
implements Attackable,
Targeter {
    final static private TrackedData<Float> WIDTH = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Float> HEIGHT = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
    final static private TrackedData<Boolean> RESPONSE = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    final static private String WIDTH_KEY = "width";
    final static private String HEIGHT_KEY = "height";
    final static private String ATTACK_KEY = "attack";
    final static private String INTERACTION_KEY = "interaction";
    final static private String RESPONSE_KEY = "response";
    final static private float DEFAULT_WIDTH = 1.0f;
    final static private float DEFAULT_HEIGHT = 1.0f;
    final static private boolean DEFAULT_RESPONSE = false;
    @Nullable
    private Interaction attack;
    @Nullable
    private Interaction interaction;

    public InteractionEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(WIDTH, Float.valueOf(1.0f));
        builder.add(HEIGHT, Float.valueOf(1.0f));
        builder.add(RESPONSE, false);
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.setInteractionWidth(view.getFloat(WIDTH_KEY, 1.0f));
        this.setInteractionHeight(view.getFloat(HEIGHT_KEY, 1.0f));
        this.attack = view.read(ATTACK_KEY, Interaction.CODEC).orElse(null);
        this.interaction = view.read(INTERACTION_KEY, Interaction.CODEC).orElse(null);
        this.setResponse(view.getBoolean(RESPONSE_KEY, false));
        this.setBoundingBox(this.calculateBoundingBox());
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putFloat(WIDTH_KEY, this.getInteractionWidth());
        view.putFloat(HEIGHT_KEY, this.getInteractionHeight());
        view.putNullable(ATTACK_KEY, Interaction.CODEC, this.attack);
        view.putNullable(INTERACTION_KEY, Interaction.CODEC, this.interaction);
        view.putBoolean(RESPONSE_KEY, this.shouldRespond());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (HEIGHT.equals(data) || WIDTH.equals(data)) {
            this.calculateDimensions();
        }
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)attacker;
            this.attack = new Interaction(playerEntity.getUuid(), this.net_minecraft_world_World_getWorld().getTime());
            if (playerEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
                Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, this, playerEntity.getDamageSources().generic(), 1.0f, 1.0f, false);
            }
            return !this.shouldRespond();
        }
        return false;
    }

    @Override
    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.net_minecraft_world_World_getWorld().isClient) {
            return this.shouldRespond() ? ActionResult.SUCCESS : ActionResult.CONSUME;
        }
        this.interaction = new Interaction(player.getUuid(), this.net_minecraft_world_World_getWorld().getTime());
        return ActionResult.CONSUME;
    }

    @Override
    public void tick() {
    }

    @Override
    @Nullable
    public LivingEntity getLastAttacker() {
        if (this.attack != null) {
            return this.net_minecraft_world_World_getWorld().getPlayerByUuid(this.attack.player());
        }
        return null;
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        if (this.interaction != null) {
            return this.net_minecraft_world_World_getWorld().getPlayerByUuid(this.interaction.player());
        }
        return null;
    }

    public final void setInteractionWidth(float width) {
        this.dataTracker.set(WIDTH, Float.valueOf(width));
    }

    public final float getInteractionWidth() {
        return this.dataTracker.get(WIDTH).floatValue();
    }

    public final void setInteractionHeight(float height) {
        this.dataTracker.set(HEIGHT, Float.valueOf(height));
    }

    public final float getInteractionHeight() {
        return this.dataTracker.get(HEIGHT).floatValue();
    }

    public final void setResponse(boolean response) {
        this.dataTracker.set(RESPONSE, response);
    }

    public final boolean shouldRespond() {
        return this.dataTracker.get(RESPONSE);
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.changing(this.getInteractionWidth(), this.getInteractionHeight());
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.getDimensions();
    }

    @Override
    protected Box calculateDefaultBoundingBox(Vec3d pos) {
        return this.getDimensions().getBoxAt(pos);
    }

    record Interaction(UUID player, long timestamp) {
        final static public Codec<Interaction> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Uuids.INT_STREAM_CODEC.fieldOf("player").forGetter(Interaction::player), (App)Codec.LONG.fieldOf("timestamp").forGetter(Interaction::timestamp)).apply((Applicative)instance, Interaction::new));
    }
}

