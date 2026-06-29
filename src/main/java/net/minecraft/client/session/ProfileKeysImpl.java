/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.mojang.authlib.exceptions.MinecraftClientException
 *  com.mojang.authlib.minecraft.InsecurePublicKeyException$MissingException
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.yggdrasil.response.KeyPairResponse
 *  com.mojang.authlib.yggdrasil.response.KeyPairResponse$KeyPair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.session;

import com.google.common.base.Strings;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ProfileKeysImpl
implements ProfileKeys {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Duration TIME_UNTIL_FIRST_EXPIRY_CHECK = Duration.ofHours(1L);
    final static private Path PROFILE_KEYS_PATH = Path.of("profilekeys", new String[0]);
    final private UserApiService userApiService;
    final private Path jsonPath;
    private CompletableFuture<Optional<PlayerKeyPair>> keyFuture = CompletableFuture.completedFuture(Optional.empty());
    private Instant expiryCheckTime = Instant.EPOCH;

    public ProfileKeysImpl(UserApiService userApiService, UUID uuid, Path root) {
        this.userApiService = userApiService;
        this.jsonPath = root.resolve(PROFILE_KEYS_PATH).resolve(String.valueOf(uuid) + ".json");
    }

    @Override
    public CompletableFuture<Optional<PlayerKeyPair>> fetchKeyPair() {
        this.expiryCheckTime = Instant.now().plus(TIME_UNTIL_FIRST_EXPIRY_CHECK);
        this.keyFuture = this.keyFuture.thenCompose(this::getKeyPair);
        return this.keyFuture;
    }

    @Override
    public boolean isExpired() {
        if (this.keyFuture.isDone() && Instant.now().isAfter(this.expiryCheckTime)) {
            return this.keyFuture.join().map(PlayerKeyPair::needsRefreshing).orElse(true);
        }
        return false;
    }

    private CompletableFuture<Optional<PlayerKeyPair>> getKeyPair(Optional<PlayerKeyPair> currentKey) {
        return CompletableFuture.supplyAsync(() -> {
            if (currentKey.isPresent() && !((PlayerKeyPair)currentKey.get()).needsRefreshing()) {
                if (!SharedConstants.isDevelopment) {
                    this.saveKeyPairToFile(null);
                }
                return currentKey;
            }
            try {
                PlayerKeyPair playerKeyPair = this.fetchKeyPair(this.userApiService);
                this.saveKeyPairToFile(playerKeyPair);
                return Optional.ofNullable(playerKeyPair);
            }
            catch (MinecraftClientException | IOException | NetworkEncryptionException exception) {
                LOGGER.error("Failed to retrieve profile key pair", exception);
                this.saveKeyPairToFile(null);
                return currentKey;
            }
        }, Util.getDownloadWorkerExecutor());
    }

    private Optional<PlayerKeyPair> loadKeyPairFromFile() {
        Optional optional;
        block9: {
            if (Files.notExists(this.jsonPath, new LinkOption[0])) {
                return Optional.empty();
            }
            BufferedReader bufferedReader = Files.newBufferedReader(this.jsonPath);
            try {
                optional = PlayerKeyPair.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)StrictJsonParser.parse(bufferedReader)).result();
                if (bufferedReader == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception exception) {
                    LOGGER.error("Failed to read profile key pair file {}", (Object)this.jsonPath, (Object)exception);
                    return Optional.empty();
                }
            }
            bufferedReader.close();
        }
        return optional;
    }

    private void saveKeyPairToFile(@Nullable PlayerKeyPair keyPair) {
        try {
            Files.deleteIfExists(this.jsonPath);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to delete profile key pair file {}", (Object)this.jsonPath, (Object)iOException);
        }
        if (keyPair == null) {
            return;
        }
        if (!SharedConstants.isDevelopment) {
            return;
        }
        PlayerKeyPair.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)keyPair).ifSuccess(json -> {
            try {
                Files.createDirectories(this.jsonPath.getParent(), new FileAttribute[0]);
                Files.writeString(this.jsonPath, (CharSequence)json.toString(), new OpenOption[0]);
            }
            catch (Exception exception) {
                LOGGER.error("Failed to write profile key pair file {}", (Object)this.jsonPath, (Object)exception);
            }
        });
    }

    @Nullable
    private PlayerKeyPair fetchKeyPair(UserApiService userApiService) throws NetworkEncryptionException, IOException {
        KeyPairResponse keyPairResponse = userApiService.getKeyPair();
        if (keyPairResponse != null) {
            PlayerPublicKey.PublicKeyData publicKeyData = ProfileKeysImpl.decodeKeyPairResponse(keyPairResponse);
            return new PlayerKeyPair(NetworkEncryptionUtils.decodeRsaPrivateKeyPem(keyPairResponse.keyPair().privateKey()), new PlayerPublicKey(publicKeyData), Instant.parse(keyPairResponse.refreshedAfter()));
        }
        return null;
    }

    private static PlayerPublicKey.PublicKeyData decodeKeyPairResponse(KeyPairResponse keyPairResponse) throws NetworkEncryptionException {
        KeyPairResponse.KeyPair keyPair = keyPairResponse.keyPair();
        if (keyPair == null || Strings.isNullOrEmpty((String)keyPair.publicKey()) || keyPairResponse.publicKeySignature() == null || keyPairResponse.publicKeySignature().array().length == 0) {
            throw new NetworkEncryptionException((Throwable)new InsecurePublicKeyException.MissingException("Missing public key"));
        }
        try {
            Instant instant = Instant.parse(keyPairResponse.expiresAt());
            PublicKey publicKey = NetworkEncryptionUtils.decodeRsaPublicKeyPem(keyPair.publicKey());
            ByteBuffer byteBuffer = keyPairResponse.publicKeySignature();
            return new PlayerPublicKey.PublicKeyData(instant, publicKey, byteBuffer.array());
        }
        catch (IllegalArgumentException | DateTimeException runtimeException) {
            throw new NetworkEncryptionException(runtimeException);
        }
    }
}

