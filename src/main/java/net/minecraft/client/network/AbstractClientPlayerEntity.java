/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity
extends PlayerEntity {
    @Nullable
    private PlayerListEntry playerListEntry;
    protected Vec3d lastVelocity = Vec3d.ZERO;
    public float elytraPitch;
    public float elytraYaw;
    public float elytraRoll;
    final public ClientWorld clientWorld;
    public float lastDistanceMoved;
    public float distanceMoved;

    public AbstractClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
        this.clientWorld = world;
    }

    @Override
    @Nullable
    public GameMode getGameMode() {
        PlayerListEntry playerListEntry = this.getPlayerListEntry();
        return playerListEntry != null ? playerListEntry.getGameMode() : null;
    }

    @Nullable
    protected PlayerListEntry getPlayerListEntry() {
        if (this.playerListEntry == null) {
            this.playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
        }
        return this.playerListEntry;
    }

    @Override
    public void tick() {
        this.lastDistanceMoved = this.distanceMoved;
        this.lastVelocity = this.getVelocity();
        super.tick();
    }

    public Vec3d lerpVelocity(float tickProgress) {
        return this.lastVelocity.lerp(this.getVelocity(), tickProgress);
    }

    public SkinTextures getSkinTextures() {
        PlayerListEntry playerListEntry = this.getPlayerListEntry();
        return playerListEntry == null ? DefaultSkinHelper.getSkinTextures(this.getUuid()) : playerListEntry.getSkinTextures();
    }

    public float getFovMultiplier(boolean firstPerson, float fovEffectScale) {
        float h;
        float g;
        float f = 1.0f;
        if (this.getAbilities().flying) {
            f *= 1.1f;
        }
        if ((g = this.getAbilities().getWalkSpeed()) != 0.0f) {
            h = (float)this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) / g;
            f *= (h + 1.0f) / 2.0f;
        }
        if (this.isUsingItem()) {
            if (this.getActiveItem().isOf(Items.BOW)) {
                h = Math.min((float)this.getItemUseTime() / 20.0f, 1.0f);
                f *= 1.0f - MathHelper.square(h) * 0.15f;
            } else if (firstPerson && this.isUsingSpyglass()) {
                return 0.1f;
            }
        }
        return MathHelper.lerp(fovEffectScale, 1.0f, f);
    }
}

