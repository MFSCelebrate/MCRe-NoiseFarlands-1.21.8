/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.SignatureState
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftProfileTextures
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    final static Logger LOGGER = LogUtils.getLogger();
    final private MinecraftSessionService sessionService;
    final private LoadingCache<Key, CompletableFuture<Optional<SkinTextures>>> cache;
    final private FileCache skinCache;
    final private FileCache capeCache;
    final private FileCache elytraCache;

    public PlayerSkinProvider(Path directory, final MinecraftSessionService sessionService, final Executor executor) {
        this.sessionService = sessionService;
        this.skinCache = new FileCache(directory, MinecraftProfileTexture.Type.SKIN);
        this.capeCache = new FileCache(directory, MinecraftProfileTexture.Type.CAPE);
        this.elytraCache = new FileCache(directory, MinecraftProfileTexture.Type.ELYTRA);
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build((CacheLoader)new CacheLoader<Key, CompletableFuture<Optional<SkinTextures>>>(){

            public CompletableFuture<Optional<SkinTextures>> load(Key key) {
                return ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
                    Property property = key.packedTextures();
                    if (property == null) {
                        return MinecraftProfileTextures.EMPTY;
                    }
                    MinecraftProfileTextures minecraftProfileTextures = sessionService.unpackTextures(property);
                    if (minecraftProfileTextures.signatureState() == SignatureState.INVALID) {
                        LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", (Object)key.profileId());
                    }
                    return minecraftProfileTextures;
                }, Util.getMainWorkerExecutor().named("unpackSkinTextures")).thenComposeAsync(textures -> PlayerSkinProvider.this.fetchSkinTextures(key.profileId(), (MinecraftProfileTextures)textures), executor)).handle((skinTextures, throwable) -> {
                    if (throwable != null) {
                        LOGGER.warn("Failed to load texture for profile {}", (Object)key.profileId, throwable);
                    }
                    return Optional.ofNullable(skinTextures);
                });
            }

            public Object load(Object value) throws Exception {
                return this.load((Key)value);
            }
        });
    }

    public Supplier<SkinTextures> getSkinTexturesSupplier(GameProfile profile) {
        CompletableFuture<Optional<SkinTextures>> completableFuture = this.fetchSkinTextures(profile);
        SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(profile);
        return () -> completableFuture.getNow(Optional.empty()).orElse(skinTextures);
    }

    public SkinTextures getSkinTextures(GameProfile profile) {
        SkinTextures skinTextures = this.getSkinTextures(profile, null);
        if (skinTextures != null) {
            return skinTextures;
        }
        return DefaultSkinHelper.getSkinTextures(profile);
    }

    @Nullable
    public SkinTextures getSkinTextures(GameProfile profile, @Nullable SkinTextures fallback) {
        return this.fetchSkinTextures(profile).getNow(Optional.empty()).orElse(fallback);
    }

    public CompletableFuture<Optional<SkinTextures>> fetchSkinTextures(GameProfile profile) {
        Property property = this.sessionService.getPackedTextures(profile);
        return (CompletableFuture)this.cache.getUnchecked((Object)new Key(profile.getId(), property));
    }

    CompletableFuture<SkinTextures> fetchSkinTextures(UUID uuid, MinecraftProfileTextures textures) {
        SkinTextures.Model model;
        CompletableFuture<Identifier> completableFuture;
        MinecraftProfileTexture minecraftProfileTexture = textures.skin();
        if (minecraftProfileTexture != null) {
            completableFuture = this.skinCache.get(minecraftProfileTexture);
            model = SkinTextures.Model.fromName(minecraftProfileTexture.getMetadata("model"));
        } else {
            SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(uuid);
            completableFuture = CompletableFuture.completedFuture(skinTextures.texture());
            model = skinTextures.model();
        }
        String string = Nullables.map(minecraftProfileTexture, MinecraftProfileTexture::getUrl);
        MinecraftProfileTexture minecraftProfileTexture2 = textures.cape();
        CompletableFuture<Object> completableFuture2 = minecraftProfileTexture2 != null ? this.capeCache.get(minecraftProfileTexture2) : CompletableFuture.completedFuture(null);
        MinecraftProfileTexture minecraftProfileTexture3 = textures.elytra();
        CompletableFuture<Object> completableFuture3 = minecraftProfileTexture3 != null ? this.elytraCache.get(minecraftProfileTexture3) : CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3).thenApply(v -> new SkinTextures((Identifier)completableFuture.join(), string, (Identifier)completableFuture2.join(), (Identifier)completableFuture3.join(), model, textures.signatureState() == SignatureState.SIGNED));
    }

    @Environment(value=EnvType.CLIENT)
    static class FileCache {
        final private Path directory;
        final private MinecraftProfileTexture.Type type;
        final private Map<String, CompletableFuture<Identifier>> hashToTexture = new Object2ObjectOpenHashMap();

        FileCache(Path directory, MinecraftProfileTexture.Type type) {
            this.directory = directory;
            this.type = type;
        }

        public CompletableFuture<Identifier> get(MinecraftProfileTexture texture) {
            String string = texture.getHash();
            CompletableFuture<Identifier> completableFuture = this.hashToTexture.get(string);
            if (completableFuture == null) {
                completableFuture = this.store(texture);
                this.hashToTexture.put(string, completableFuture);
            }
            return completableFuture;
        }

        private CompletableFuture<Identifier> store(MinecraftProfileTexture texture) {
            String string = Hashing.sha1().hashUnencodedChars((CharSequence)texture.getHash()).toString();
            Identifier identifier = this.getTexturePath(string);
            Path path = this.directory.resolve(string.length() > 2 ? string.substring(0, 2) : "xx").resolve(string);
            return PlayerSkinTextureDownloader.downloadAndRegisterTexture(identifier, path, texture.getUrl(), this.type == MinecraftProfileTexture.Type.SKIN);
        }

        private Identifier getTexturePath(String hash) {
            String string = switch (this.type) {
                default -> throw new MatchException(null, null);
                case MinecraftProfileTexture.Type.SKIN -> "skins";
                case MinecraftProfileTexture.Type.CAPE -> "capes";
                case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
            };
            return Identifier.ofVanilla(string + "/" + hash);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Key
    extends Record {
        final UUID profileId;
        @Nullable
        final private Property packedTextures;

        Key(UUID uUID, @Nullable Property property) {
            this.profileId = uUID;
            this.packedTextures = property;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this, object);
        }

        public UUID profileId() {
            return this.profileId;
        }

        @Nullable
        public Property packedTextures() {
            return this.packedTextures;
        }
    }
}

