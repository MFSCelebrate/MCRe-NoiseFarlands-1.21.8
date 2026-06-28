/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity
extends BlockEntity {
    final static private String PROFILE_NBT_KEY = "profile";
    final static private String NOTE_BLOCK_SOUND_NBT_KEY = "note_block_sound";
    final static private String CUSTOM_NAME_NBT_KEY = "custom_name";
    @Nullable
    static private Executor currentExecutor;
    @Nullable
    static private LoadingCache<String, CompletableFuture<Optional<GameProfile>>> nameToProfileCache;
    @Nullable
    static private LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> uuidToProfileCache;
    final static public Executor EXECUTOR;
    @Nullable
    private ProfileComponent owner;
    @Nullable
    private Identifier noteBlockSound;
    private int poweredTicks;
    private boolean powered;
    @Nullable
    private Text customName;

    public SkullBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SKULL, pos, state);
    }

    public static void setServices(final ApiServices apiServices, Executor executor) {
        currentExecutor = executor;
        final BooleanSupplier booleanSupplier = () -> uuidToProfileCache == null;
        nameToProfileCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build((CacheLoader)new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>(){

            public CompletableFuture<Optional<GameProfile>> load(String string) {
                return SkullBlockEntity.fetchProfileByName(string, apiServices);
            }

            public Object load(Object name) throws Exception {
                return this.load((String)name);
            }
        });
        uuidToProfileCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build((CacheLoader)new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>(){

            public CompletableFuture<Optional<GameProfile>> load(UUID uUID) {
                return SkullBlockEntity.fetchProfileByUuid(uUID, apiServices, booleanSupplier);
            }

            public Object load(Object uuid) throws Exception {
                return this.load((UUID)uuid);
            }
        });
    }

    static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String name, ApiServices apiServices) {
        return apiServices.userCache().findByNameAsync(name).thenCompose(optional -> {
            LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingCache = uuidToProfileCache;
            if (loadingCache == null || optional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return ((CompletableFuture)loadingCache.getUnchecked((Object)((GameProfile)optional.get()).getId())).thenApply(optional2 -> optional2.or(() -> optional));
        });
    }

    static CompletableFuture<Optional<GameProfile>> fetchProfileByUuid(UUID uuid, ApiServices apiServices, BooleanSupplier booleanSupplier) {
        return CompletableFuture.supplyAsync(() -> {
            if (booleanSupplier.getAsBoolean()) {
                return Optional.empty();
            }
            ProfileResult profileResult = apiServices.sessionService().fetchProfile(uuid, true);
            return Optional.ofNullable(profileResult).map(ProfileResult::profile);
        }, Util.getMainWorkerExecutor().named("fetchProfile"));
    }

    public static void clearServices() {
        currentExecutor = null;
        nameToProfileCache = null;
        uuidToProfileCache = null;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putNullable(PROFILE_NBT_KEY, ProfileComponent.CODEC, this.owner);
        view.putNullable(NOTE_BLOCK_SOUND_NBT_KEY, Identifier.CODEC, this.noteBlockSound);
        view.putNullable(CUSTOM_NAME_NBT_KEY, TextCodecs.CODEC, this.customName);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.setOwner(view.read(PROFILE_NBT_KEY, ProfileComponent.CODEC).orElse(null));
        this.noteBlockSound = view.read(NOTE_BLOCK_SOUND_NBT_KEY, Identifier.CODEC).orElse(null);
        this.customName = SkullBlockEntity.tryParseCustomName(view, CUSTOM_NAME_NBT_KEY);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
        if (state.contains(SkullBlock.POWERED) && state.get(SkullBlock.POWERED).booleanValue()) {
            blockEntity.powered = true;
            ++blockEntity.poweredTicks;
        } else {
            blockEntity.powered = false;
        }
    }

    public float getPoweredTicks(float tickProgress) {
        if (this.powered) {
            return (float)this.poweredTicks + tickProgress;
        }
        return this.poweredTicks;
    }

    @Nullable
    public ProfileComponent getOwner() {
        return this.owner;
    }

    @Nullable
    public Identifier getNoteBlockSound() {
        return this.noteBlockSound;
    }

    public BlockEntityUpdateS2CPacket net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOwner(@Nullable ProfileComponent profile) {
        SkullBlockEntity skullBlockEntity = this;
        synchronized (skullBlockEntity) {
            this.owner = profile;
        }
        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        if (this.owner == null || this.owner.isCompleted()) {
            this.markDirty();
            return;
        }
        this.owner.getFuture().thenAcceptAsync(owner -> {
            this.owner = owner;
            this.markDirty();
        }, EXECUTOR);
    }

    public static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String name) {
        LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingCache = nameToProfileCache;
        if (loadingCache != null && StringHelper.isValidPlayerName(name)) {
            return (CompletableFuture)loadingCache.getUnchecked((Object)name);
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public static CompletableFuture<Optional<GameProfile>> fetchProfileByUuid(UUID uuid) {
        LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingCache = uuidToProfileCache;
        if (loadingCache != null) {
            return (CompletableFuture)loadingCache.getUnchecked((Object)uuid);
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.setOwner(components.get(DataComponentTypes.PROFILE));
        this.noteBlockSound = components.get(DataComponentTypes.NOTE_BLOCK_SOUND);
        this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.PROFILE, this.owner);
        builder.add(DataComponentTypes.NOTE_BLOCK_SOUND, this.noteBlockSound);
        builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove(PROFILE_NBT_KEY);
        view.remove(NOTE_BLOCK_SOUND_NBT_KEY);
        view.remove(CUSTOM_NAME_NBT_KEY);
    }

    public Packet net_minecraft_network_packet_Packet_toUpdatePacket() {
        return this.net_minecraft_network_packet_s2c_play_BlockEntityUpdateS2CPacket_toUpdatePacket();
    }

    static {
        EXECUTOR = runnable -> {
            Executor executor = currentExecutor;
            if (executor != null) {
                executor.execute(runnable);
            }
        };
    }
}

