/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.hash.Funnels
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.HashFunction
 *  com.google.common.hash.Hasher
 *  com.google.common.hash.PrimitiveSink
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;
import java.util.OptionalLong;
import net.minecraft.util.path.PathUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NetworkUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    private NetworkUtils() {}

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static Path download(Path path, URL url, Map<
                    String, String> headers, HashFunction hashFunction, @Nullable
                    HashCode hashCode, int maxBytes, Proxy proxy, DownloadListener listener) {
        listener.onStart();
        if (hashCode != null) {
            Path cachedPath = resolve(path, hashCode);
            try {
                if (validateHash(cachedPath, hashFunction, hashCode)) {
                    LOGGER.info("Returning cached file since actual hash matches requested");
                    listener.onFinish(true);
                    updateModificationTime(cachedPath);
                    return cachedPath;
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to check cached file {}", cachedPath, e);
            }
            try {
                Files.deleteIfExists(cachedPath);
            } catch (IOException e) {
                listener.onFinish(false);
                throw new UncheckedIOException("Failed to remove existing file " + cachedPath, e);
            }
        }
        Path tempPath = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setInstanceFollowRedirects(true);
            headers.forEach(connection::setRequestProperty);
            try (InputStream inputStream = connection.getInputStream()) {
                long contentLength = connection.getContentLengthLong();
                OptionalLong optionalLength = contentLength != -1 ? OptionalLong.of(contentLength) : OptionalLong.empty();
                listener.onContentLength(optionalLength);
                if (optionalLength.isPresent() && optionalLength.getAsLong() > maxBytes) {
                    throw new IOException("Filesize is bigger than maximum allowed (file is " + optionalLength.getAsLong() + ", limit is " + maxBytes + ")");
                }
                PathUtil.createDirectories(path);
                tempPath = Files.createTempFile(path, "download", ".tmp");
                HashCode actualHash = write(hashFunction, maxBytes, listener, inputStream, tempPath);
                Path targetPath = resolve(path, actualHash);
                if (!validateHash(targetPath, hashFunction, actualHash)) {
                    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    updateModificationTime(targetPath);
                }
                listener.onFinish(true);
                return targetPath;
            }
        } catch (Throwable t) {
            try {
                if (tempPath != null) Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {
            }
            listener.onFinish(false);
            throw new IllegalStateException("Failed to download file " + url, t);
        }
    }

    private static void updateModificationTime(Path path) {
        try {
            Files.setLastModifiedTime(path, FileTime.from(Instant.now()));
        } catch (IOException iOException) {
            LOGGER.warn("Failed to update modification time of {}", (Object) path, (Object) iOException);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    private static HashCode hash(Path path, HashFunction hashFunction) throws IOException {
        Hasher hasher = hashFunction.newHasher();
        try (OutputStream outputStream = Funnels.asOutputStream((PrimitiveSink) hasher); ) {
            block8:
            {
                try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]); ) {
                    inputStream.transferTo(outputStream);
                    if (inputStream == null) break block8;
                }
            }
            if (outputStream == null) return hasher.hash();
        }
        return hasher.hash();
    }

    private static boolean validateHash(Path path, HashFunction hashFunction, HashCode hashCode)
            throws IOException {
        if (Files.exists(path, new LinkOption[0])) {
            HashCode hashCode2 = NetworkUtils.hash(path, hashFunction);
            if (hashCode2.equals((Object) hashCode)) {
                return true;
            }
            LOGGER.warn("Mismatched hash of file {}, expected {} but found {}", new Object
                    []{path, hashCode, hashCode2});
        }
        return false;
    }

    private static Path resolve(Path path, HashCode hashCode) {
        return path.resolve(hashCode.toString());
    }

    private static HashCode write(HashFunction hashFunction, int maxBytes, DownloadListener listener, InputStream stream, Path path)
            throws IOException {
        HashCode hashCode;
        block8:
        {
            OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
            try {
                int i;
                Hasher hasher = hashFunction.newHasher();
                byte[] bs = new byte[8196];
                long l = 0L;
                while ((i = stream.read(bs)) >= 0) {
                    listener.onProgress(l += (long) i);
                    if (l > (long) maxBytes) {
                        throw new IOException("Filesize was bigger than maximum allowed (got >= " + l + ", limit was " + maxBytes + ")");
                    }
                    if (Thread.interrupted()) {
                        LOGGER.error("INTERRUPTED");
                        throw new IOException("Download interrupted");
                    }
                    outputStream.write(bs, 0, i);
                    hasher.putBytes(bs, 0, i);
                }
                hashCode = hasher.hash();
                if (outputStream == null) break block8;
            } catch (Throwable throwable) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            outputStream.close();
        }
        return hashCode;
    }

    public static int findLocalPort() {
        int n;
        ServerSocket serverSocket = new ServerSocket(0);
        try {
            n = serverSocket.getLocalPort();
        } catch (Throwable throwable) {
            try {
                try {
                    serverSocket.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (IOException iOException) {
                return 25564;
            }
        }
        serverSocket.close();
        return n;
    }

    public static boolean isPortAvailable(int port) {
        boolean bl;
        if (port < 0 || port > 65535) {
            return false;
        }
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            bl = serverSocket.getLocalPort() == port;
        } catch (Throwable throwable) {
            try {
                try {
                    serverSocket.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (IOException iOException) {
                return false;
            }
        }
        serverSocket.close();
        return bl;
    }

    public static interface DownloadListener {
        public void onStart();

        public void onContentLength(OptionalLong var1);

        public void onProgress(long var1);

        public void onFinish(boolean var1);
    }
}
