/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.component.type;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record ProfileComponent(Optional<String> name, Optional<UUID> uuid, PropertyMap properties, GameProfile gameProfile) {
    final static private Codec<ProfileComponent> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ProfileComponent::name), (App)Uuids.INT_STREAM_CODEC.optionalFieldOf("id").forGetter(ProfileComponent::uuid), (App)Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("properties", (Object)new PropertyMap()).forGetter(ProfileComponent::properties)).apply((Applicative)instance, ProfileComponent::new));
    final static public Codec<ProfileComponent> CODEC = Codec.withAlternative(BASE_CODEC, Codecs.PLAYER_NAME, name -> new ProfileComponent(Optional.of(name), Optional.empty(), new PropertyMap()));
    final static public PacketCodec<ByteBuf, ProfileComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.string(16).collect(PacketCodecs::optional), ProfileComponent::name, Uuids.PACKET_CODEC.collect(PacketCodecs::optional), ProfileComponent::uuid, PacketCodecs.PROPERTY_MAP, ProfileComponent::properties, ProfileComponent::new);

    public ProfileComponent(Optional<String> name, Optional<UUID> uuid, PropertyMap properties) {
        this(name, uuid, properties, ProfileComponent.createProfile(uuid, name, properties));
    }

    public ProfileComponent(GameProfile gameProfile) {
        this(Optional.of(gameProfile.getName()), Optional.of(gameProfile.getId()), gameProfile.getProperties(), gameProfile);
    }

    @Nullable
    public ProfileComponent resolve() {
        if (this.isCompleted()) {
            return this;
        }
        Optional optional = this.uuid.isPresent() ? (Optional)SkullBlockEntity.fetchProfileByUuid(this.uuid.get()).getNow(null) : (Optional)SkullBlockEntity.fetchProfileByName(this.name.orElseThrow()).getNow(null);
        if (optional != null) {
            return this.resolve(optional);
        }
        return null;
    }

    public CompletableFuture<ProfileComponent> getFuture() {
        if (this.isCompleted()) {
            return CompletableFuture.completedFuture(this);
        }
        if (this.uuid.isPresent()) {
            return SkullBlockEntity.fetchProfileByUuid(this.uuid.get()).thenApply(this::resolve);
        }
        return SkullBlockEntity.fetchProfileByName(this.name.orElseThrow()).thenApply(this::resolve);
    }

    private ProfileComponent resolve(Optional<GameProfile> profile) {
        return new ProfileComponent(profile.orElseGet(() -> ProfileComponent.createProfile(this.uuid, this.name)));
    }

    private static GameProfile createProfile(Optional<UUID> uuid, Optional<String> name) {
        return new GameProfile(uuid.orElse(Util.NIL_UUID), name.orElse(""));
    }

    private static GameProfile createProfile(Optional<UUID> uuid, Optional<String> name, PropertyMap properties) {
        GameProfile gameProfile = ProfileComponent.createProfile(uuid, name);
        gameProfile.getProperties().putAll((Multimap)properties);
        return gameProfile;
    }

    public boolean isCompleted() {
        if (!this.properties.isEmpty()) {
            return true;
        }
        return this.uuid.isPresent() == this.name.isPresent();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProfileComponent.class, "name;id;properties;gameProfile", "name", "uuid", "properties", "gameProfile"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProfileComponent.class, "name;id;properties;gameProfile", "name", "uuid", "properties", "gameProfile"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProfileComponent.class, "name;id;properties;gameProfile", "name", "uuid", "properties", "gameProfile"}, this, object);
    }
}

