/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.HashFunction
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.NetworkUtils;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.logging.LogWriter;
import net.minecraft.util.path.CacheFiles;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Downloader
implements AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int MAX_RETAINED_CACHE_FILES = 20;
    final private Path directory;
    final private LogWriter<LogEntry> logWriter;
    final private SimpleConsecutiveExecutor executor = new SimpleConsecutiveExecutor(Util.getDownloadWorkerExecutor(), "download-queue");

    public Downloader(Path directory) throws IOException {
        this.directory = directory;
        PathUtil.createDirectories(directory);
        this.logWriter = LogWriter.create(LogEntry.CODEC, directory.resolve("log.json"));
        CacheFiles.clear(directory, 20);
    }

    private DownloadResult download(Config config, Map<UUID, DownloadEntry> entries) {
        DownloadResult downloadResult = new DownloadResult();
        entries.forEach((id, entry) -> {
            Path path = this.directory.resolve(id.toString());
            Path path2 = null;
            try {
                path2 = NetworkUtils.download(path, entry.url, config.headers, config.hashFunction, entry.hash, config.maxSize, config.proxy, config.listener);
                downloadResult.downloaded.put((UUID)id, path2);
            }
            catch (Exception exception) {
                LOGGER.error("Failed to download {}", (Object)entry.url, (Object)exception);
                downloadResult.failed.add((UUID)id);
            }
            try {
                this.logWriter.write(new LogEntry((UUID)id, entry.url.toString(), Instant.now(), Optional.ofNullable(entry.hash).map(HashCode::toString), path2 != null ? this.getFileInfo(path2) : Either.left((Object)"download_failed")));
            }
            catch (Exception exception) {
                LOGGER.error("Failed to log download of {}", (Object)entry.url, (Object)exception);
            }
        });
        return downloadResult;
    }

    private Either<String, FileInfo> getFileInfo(Path path) {
        try {
            long l = Files.size(path);
            Path path2 = this.directory.relativize(path);
            return Either.right((Object)new FileInfo(path2.toString(), l));
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to get file size of {}", (Object)path, (Object)iOException);
            return Either.left((Object)"no_access");
        }
    }

    public CompletableFuture<DownloadResult> downloadAsync(Config config, Map<UUID, DownloadEntry> entries) {
        return CompletableFuture.supplyAsync(() -> this.download(config, entries), this.executor::send);
    }

    @Override
    public void close() throws IOException {
        this.executor.close();
        this.logWriter.close();
    }

    record LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, FileInfo> errorOrFileInfo) {
        final static public Codec<LogEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Uuids.STRING_CODEC.fieldOf("id").forGetter(LogEntry::id), (App)Codec.STRING.fieldOf("url").forGetter(LogEntry::url), (App)Codecs.INSTANT.fieldOf("time").forGetter(LogEntry::time), (App)Codec.STRING.optionalFieldOf("hash").forGetter(LogEntry::hash), (App)Codec.mapEither((MapCodec)Codec.STRING.fieldOf("error"), (MapCodec)FileInfo.CODEC.fieldOf("file")).forGetter(LogEntry::errorOrFileInfo)).apply((Applicative)instance, LogEntry::new));
    }

    public static final class DownloadResult
    extends Record {
        final Map<UUID, Path> downloaded;
        final Set<UUID> failed;

        public DownloadResult() {
            this(new HashMap<UUID, Path>(), new HashSet<UUID>());
        }

        public DownloadResult(Map<UUID, Path> map, Set<UUID> set) {
            this.downloaded = map;
            this.failed = set;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DownloadResult.class, "downloaded;failed", "downloaded", "failed"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DownloadResult.class, "downloaded;failed", "downloaded", "failed"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DownloadResult.class, "downloaded;failed", "downloaded", "failed"}, this, object);
        }

        public Map<UUID, Path> downloaded() {
            return this.downloaded;
        }

        public Set<UUID> failed() {
            return this.failed;
        }
    }

    public static final class Config
    extends Record {
        final HashFunction hashFunction;
        final int maxSize;
        final Map<String, String> headers;
        final Proxy proxy;
        final NetworkUtils.DownloadListener listener;

        public Config(HashFunction hashFunction, int i, Map<String, String> map, Proxy proxy, NetworkUtils.DownloadListener downloadListener) {
            this.hashFunction = hashFunction;
            this.maxSize = i;
            this.headers = map;
            this.proxy = proxy;
            this.listener = downloadListener;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Config.class, "hashFunction;maxSize;headers;proxy;listener", "hashFunction", "maxSize", "headers", "proxy", "listener"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Config.class, "hashFunction;maxSize;headers;proxy;listener", "hashFunction", "maxSize", "headers", "proxy", "listener"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Config.class, "hashFunction;maxSize;headers;proxy;listener", "hashFunction", "maxSize", "headers", "proxy", "listener"}, this, object);
        }

        public HashFunction hashFunction() {
            return this.hashFunction;
        }

        public int maxSize() {
            return this.maxSize;
        }

        public Map<String, String> headers() {
            return this.headers;
        }

        public Proxy proxy() {
            return this.proxy;
        }

        public NetworkUtils.DownloadListener listener() {
            return this.listener;
        }
    }

    record FileInfo(String name, long size) {
        final static public Codec<FileInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("name").forGetter(FileInfo::name), (App)Codec.LONG.fieldOf("size").forGetter(FileInfo::size)).apply((Applicative)instance, FileInfo::new));
    }

    public static final class DownloadEntry
    extends Record {
        final URL url;
        @Nullable
        final HashCode hash;

        public DownloadEntry(URL uRL, @Nullable HashCode hashCode) {
            this.url = uRL;
            this.hash = hashCode;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DownloadEntry.class, "url;hash", "url", "hash"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DownloadEntry.class, "url;hash", "url", "hash"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DownloadEntry.class, "url;hash", "url", "hash"}, this, object);
        }

        public URL url() {
            return this.url;
        }

        @Nullable
        public HashCode hash() {
            return this.hash;
        }
    }
}

