/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityQueriable;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jetbrains.annotations.Nullable;

public final class LazyEntityReference<StoredEntityType extends UniquelyIdentifiable> {
    final static private Codec<? extends LazyEntityReference<?>> CODEC = Uuids.INT_STREAM_CODEC.xmap(LazyEntityReference::new, LazyEntityReference::getUuid);
    final static private PacketCodec<ByteBuf, ? extends LazyEntityReference<?>> PACKET_CODEC = Uuids.PACKET_CODEC.xmap(LazyEntityReference::new, LazyEntityReference::getUuid);
    private Either<UUID, StoredEntityType> value;

    public static <Type extends UniquelyIdentifiable> Codec<LazyEntityReference<Type>> createCodec() {
        return CODEC;
    }

    public static <Type extends UniquelyIdentifiable> PacketCodec<ByteBuf, LazyEntityReference<Type>> createPacketCodec() {
        return PACKET_CODEC;
    }

    public LazyEntityReference(StoredEntityType value) {
        this.value = Either.right(value);
    }

    public LazyEntityReference(UUID value) {
        this.value = Either.left((Object)value);
    }

    public UUID getUuid() {
        return (UUID)this.value.map(uuid -> uuid, UniquelyIdentifiable::getUuid);
    }

    @Nullable
    public StoredEntityType resolve(EntityQueriable<? super StoredEntityType> world, Class<StoredEntityType> type) {
        StoredEntityType uniquelyIdentifiable2;
        Optional optional2;
        Optional optional = this.value.right();
        if (optional.isPresent()) {
            UniquelyIdentifiable uniquelyIdentifiable = (UniquelyIdentifiable)optional.get();
            if (uniquelyIdentifiable.isRemoved()) {
                this.value = Either.left((Object)uniquelyIdentifiable.getUuid());
            } else {
                return (StoredEntityType)uniquelyIdentifiable;
            }
        }
        if ((optional2 = this.value.left()).isPresent() && (uniquelyIdentifiable2 = this.cast((UniquelyIdentifiable)world.getEntity((UUID)optional2.get()), type)) != null && !uniquelyIdentifiable2.isRemoved()) {
            this.value = Either.right(uniquelyIdentifiable2);
            return uniquelyIdentifiable2;
        }
        return null;
    }

    @Nullable
    private StoredEntityType cast(@Nullable UniquelyIdentifiable entity, Class<StoredEntityType> clazz) {
        if (entity != null && clazz.isAssignableFrom(entity.getClass())) {
            return (StoredEntityType)((UniquelyIdentifiable)clazz.cast(entity));
        }
        return null;
    }

    public boolean uuidEquals(StoredEntityType o) {
        return this.getUuid().equals(o.getUuid());
    }

    public void writeData(WriteView view, String key) {
        view.put(key, Uuids.INT_STREAM_CODEC, this.getUuid());
    }

    public static void writeData(@Nullable LazyEntityReference<?> entityRef, WriteView view, String key) {
        if (entityRef != null) {
            entityRef.writeData(view, key);
        }
    }

    @Nullable
    public static <StoredEntityType extends UniquelyIdentifiable> StoredEntityType resolve(@Nullable LazyEntityReference<StoredEntityType> entity, EntityQueriable<? super StoredEntityType> world, Class<StoredEntityType> type) {
        return entity != null ? (StoredEntityType)entity.resolve(world, type) : null;
    }

    @Nullable
    public static <StoredEntityType extends UniquelyIdentifiable> LazyEntityReference<StoredEntityType> fromData(ReadView view, String key) {
        return view.read(key, LazyEntityReference.createCodec()).orElse(null);
    }

    @Nullable
    public static <StoredEntityType extends UniquelyIdentifiable> LazyEntityReference<StoredEntityType> fromDataOrPlayerName(ReadView view, String key, World world) {
        Optional<UUID> optional = view.read(key, Uuids.INT_STREAM_CODEC);
        if (optional.isPresent()) {
            return new LazyEntityReference<StoredEntityType>(optional.get());
        }
        return view.getOptionalString(key).map(name -> ServerConfigHandler.getPlayerUuidByName(world.getServer(), name)).map(LazyEntityReference::new).orElse(null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof LazyEntityReference)) return false;
        LazyEntityReference lazyEntityReference = (LazyEntityReference)object;
        if (!this.getUuid().equals(lazyEntityReference.getUuid())) return false;
        return true;
    }

    public int hashCode() {
        return this.getUuid().hashCode();
    }
}

