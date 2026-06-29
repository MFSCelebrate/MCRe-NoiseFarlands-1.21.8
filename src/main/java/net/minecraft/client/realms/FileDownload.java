/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value = EnvType.CLIENT)
public class FileDownload {
    static final Logger LOGGER = LogUtils.getLogger();
    volatile boolean cancelled;
    volatile boolean finished;
    volatile boolean error;
    volatile boolean extracting;
    @Nullable private volatile File backupFile;
    volatile File resourcePackPath;
    @Nullable private volatile HttpGet httpRequest;
    @Nullable private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String
            []{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String downloadLink) {
        long l;
        CloseableHttpClient closeableHttpClient;
        block14:
        {
            closeableHttpClient = null;
            HttpGet httpGet = null;
            try {
                httpGet = new HttpGet(downloadLink);
                closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest) httpGet);
                l = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
                if (httpGet == null) break block14;
            } catch (Throwable throwable) {
                long l2;
                block15:
                {
                    try {
                        LOGGER.error("Unable to get content length for download");
                        l2 = 0L;
                        if (httpGet == null) break block15;
                    } catch (Throwable throwable2) {
                        if (httpGet != null) {
                            httpGet.releaseConnection();
                        }
                        if (closeableHttpClient != null) {
                            try {
                                closeableHttpClient.close();
                            } catch (IOException iOException2) {
                                LOGGER.error("Could not close http client", (Throwable) iOException2);
                            }
                        }
                        throw throwable2;
                    }
                    httpGet.releaseConnection();
                }
                if (closeableHttpClient != null) {
                    try {
                        closeableHttpClient.close();
                    } catch (IOException iOException) {
                        LOGGER.error("Could not close http client", (Throwable) iOException);
                    }
                }
                return l2;
            }
            httpGet.releaseConnection();
        }
        if (closeableHttpClient != null) {
            try {
                closeableHttpClient.close();
            } catch (IOException iOException) {
                LOGGER.error("Could not close http client", (Throwable) iOException);
            }
        }
        return l;
    }

    public void downloadWorld(WorldDownload download, String message, RealmsDownloadLatestWorldScreen.DownloadStatus status, LevelStorage storage) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            boolean success = false;
            try {
                // 1. 下载主世界文件
                this.backupFile = File.createTempFile("backup", ".tar.gz");
                try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build()) {
                    HttpGet get = new HttpGet(download.downloadLink);
                    this.httpRequest = get;
                    try (CloseableHttpResponse response = httpClient.execute((HttpUriRequest) get)) {
                        if (response.getStatusLine().getStatusCode() != 200) {
                            this.error = true;
                            return;
                        }
                        status.totalBytes = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
                        try (FileOutputStream fos = new FileOutputStream(this.backupFile);
                                DownloadCountingOutputStream cos = new DownloadCountingOutputStream(fos)) {
                            ProgressListener listener = new ProgressListener(message.trim(), this.backupFile, storage, status);
                            cos.setListener(listener);
                            IOUtils.copy(response.getEntity().getContent(), cos);
                        }
                    }
                    if (this.error || this.cancelled) {
                        return;
                    }
                    // 2. 下载资源包（如果有）
                    if (!download.resourcePackUrl.isEmpty() && !download.resourcePackHash.isEmpty()) {
                        this.backupFile = File.createTempFile("resources", ".tar.gz");
                        HttpGet resourceGet = new HttpGet(download.resourcePackUrl);
                        this.httpRequest = resourceGet;
                        try (CloseableHttpResponse response = httpClient.execute((HttpUriRequest) resourceGet)) {
                            if (response.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                return;
                            }
                            status.totalBytes = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
                            try (FileOutputStream fos = new FileOutputStream(this.backupFile);
                                    DownloadCountingOutputStream cos = new DownloadCountingOutputStream(fos)) {
                                ResourcePackProgressListener listener = new ResourcePackProgressListener(this.backupFile, status, download);
                                cos.setListener(listener);
                                IOUtils.copy(response.getEntity().getContent(), cos);
                            }
                        }
                    } else {
                        this.finished = true;
                    }
                    success = true;
                } catch (Exception e) {
                    LOGGER.error("Caught exception during download: {}", e.getMessage());
                    this.error = true;
                } finally {
                    if (this.httpRequest != null) {
                        this.httpRequest.releaseConnection();
                        this.httpRequest = null;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Fatal error in download thread: {}", e.getMessage());
                this.error = true;
            } finally {
                if (this.backupFile != null && !this.cancelled && !success) {
                    this.backupFile.delete();
                }
                this.backupFile = null;
            }
        });
        this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.httpRequest != null) {
            this.httpRequest.abort();
        }
        if (this.backupFile != null) {
            this.backupFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String folder) {
        folder = ((String) folder).replaceAll("[\\./\"]", "_");
        for (String string : INVALID_FILE_NAMES) {
            if (!((String) folder).equalsIgnoreCase(string)) continue;
            folder = "_" + (String) folder + "_";
        }
        return folder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    void untarGzipArchive(String name, @Nullable File archive, LevelStorage storage)
            throws IOException {
        try {
            // TODO: 待修复
        } catch (Exception e) {
            LOGGER.error("Error extracting world", e);
            this.error = true;
        }
    }

    @Environment(value = EnvType.CLIENT)
    class ResourcePackProgressListener implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        ResourcePackProgressListener(File tempFile, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
            this.tempFile = tempFile;
            this.downloadStatus = downloadStatus;
            this.worldDownload = worldDownload;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream) ((Object) e.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String string = Hashing.sha1().hashBytes(Files.toByteArray((File) this.tempFile)).toString();
                    if (string.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile((File) this.tempFile, (File) FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", (Object) this.worldDownload.resourcePackHash, (Object) string);
                        FileUtils.deleteQuietly((File) this.tempFile);
                        FileDownload.this.error = true;
                    }
                } catch (IOException iOException) {
                    LOGGER.error("Error copying resourcepack file: {}", (Object) iOException.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    @Environment(value = EnvType.CLIENT)
    static class DownloadCountingOutputStream extends CountingOutputStream {
        @Nullable private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream stream) {
            super(stream);
        }

        public void setListener(ActionListener listener) {
            this.listener = listener;
        }

        protected void afterWrite(int n) throws IOException {
            super.afterWrite(n);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent((Object) this, 0, null));
            }
        }
    }

    @Environment(value = EnvType.CLIENT)
    class ProgressListener implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorage levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        ProgressListener(String worldName, File tempFile, LevelStorage levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus) {
            this.worldName = worldName;
            this.tempFile = tempFile;
            this.levelStorageSource = levelStorageSource;
            this.downloadStatus = downloadStatus;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream) ((Object) e.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                } catch (IOException iOException) {
                    LOGGER.error("Error extracting archive", (Throwable) iOException);
                    FileDownload.this.error = true;
                }
            }
        }
    }
}
