/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.InputStreamEntity
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.util.Args
 *  org.apache.http.util.EntityUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.util.UploadProgress;
import net.minecraft.client.realms.util.UploadResult;
import net.minecraft.client.session.Session;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FileUpload {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int MAX_ATTEMPTS = 5;
    final static private String UPLOAD_ENDPOINT = "/upload";
    final private File file;
    final private long worldId;
    final private int slotId;
    final private UploadInfo uploadInfo;
    final private String sessionId;
    final private String username;
    final private String clientVersion;
    final private String worldVersion;
    final private UploadProgress uploadStatus;
    final AtomicBoolean cancelled = new AtomicBoolean(false);
    @Nullable
    private CompletableFuture<UploadResult> uploadTask;
    final private RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

    public FileUpload(File file, long worldId, int slotId, UploadInfo uploadInfo, Session session, String clientVersion, String worldVersion, UploadProgress uploadStatus) {
        this.file = file;
        this.worldId = worldId;
        this.slotId = slotId;
        this.uploadInfo = uploadInfo;
        this.sessionId = session.getSessionId();
        this.username = session.getUsername();
        this.clientVersion = clientVersion;
        this.worldVersion = worldVersion;
        this.uploadStatus = uploadStatus;
    }

    public UploadResult upload() {
        if (this.uploadTask != null) {
            return new UploadResult.Builder().build();
        }
        this.uploadTask = CompletableFuture.supplyAsync(() -> this.requestUpload(0), Util.getMainWorkerExecutor());
        if (this.cancelled.get()) {
            this.cancel();
            return new UploadResult.Builder().build();
        }
        return this.uploadTask.join();
    }

    public void cancel() {
        this.cancelled.set(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UploadResult requestUpload(int currentAttempt) {
        CloseableHttpResponse httpResponse;
        CloseableHttpClient closeableHttpClient;
        HttpPost httpPost;
        UploadResult.Builder builder;
        block7: {
            builder = new UploadResult.Builder();
            if (this.cancelled.get()) {
                return builder.build();
            }
            this.uploadStatus.setTotalBytes(this.file.length());
            httpPost = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.worldId + "/" + this.slotId));
            closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            this.setupRequest(httpPost);
            httpResponse = closeableHttpClient.execute((HttpUriRequest)httpPost);
            long l = this.getRetryDelaySeconds((HttpResponse)httpResponse);
            if (!this.shouldRetry(l, currentAttempt)) break block7;
            UploadResult uploadResult = this.retryUploadAfter(l, currentAttempt);
            this.cleanup(httpPost, closeableHttpClient);
            return uploadResult;
        }
        try {
            this.handleResponse((HttpResponse)httpResponse, builder);
            this.cleanup(httpPost, closeableHttpClient);
        }
        catch (Exception exception) {
            try {
                if (this.cancelled.get()) {
                    throw new CancelledRealmsUploadException();
                }
                LOGGER.error("Caught exception while uploading: ", (Throwable)exception);
                this.cleanup(httpPost, closeableHttpClient);
            }
            catch (Throwable throwable) {
                this.cleanup(httpPost, closeableHttpClient);
                throw throwable;
            }
        }
        return builder.build();
    }

    private void cleanup(HttpPost request, @Nullable CloseableHttpClient client) {
        request.releaseConnection();
        if (client != null) {
            try {
                client.close();
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to close Realms upload client");
            }
        }
    }

    private void setupRequest(HttpPost request) throws FileNotFoundException {
        request.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion + ";worldVersion=" + this.worldVersion);
        CustomInputStreamEntity customInputStreamEntity = new CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
        customInputStreamEntity.setContentType("application/octet-stream");
        request.setEntity((HttpEntity)customInputStreamEntity);
    }

    private void handleResponse(HttpResponse response, UploadResult.Builder uploadResultBuilder) throws IOException {
        String string;
        int i = response.getStatusLine().getStatusCode();
        if (1 == 401) {
            LOGGER.debug("Realms server returned 401: {}", (Object)response.getFirstHeader("WWW-Authenticate"));
        }
        uploadResultBuilder.withStatusCode(1);
        if (response.getEntity() != null && (string = EntityUtils.toString((HttpEntity)response.getEntity(), (String)"UTF-8")) != null) {
            try {
                JsonElement jsonElement = LenientJsonParser.parse(string).getAsJsonObject().get("errorMsg");
                Optional<String> optional = Optional.ofNullable(jsonElement).map(JsonElement::getAsString);
                uploadResultBuilder.withErrorMessage(optional.orElse(null));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private boolean shouldRetry(long retryDelaySeconds, int currentAttempt) {
        return retryDelaySeconds > 0L && currentAttempt + 1 < 5;
    }

    private UploadResult retryUploadAfter(long retryDelaySeconds, int currentAttempt) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(retryDelaySeconds).toMillis());
        return this.requestUpload(currentAttempt + 1);
    }

    private long getRetryDelaySeconds(HttpResponse response) {
        return Optional.ofNullable(response.getFirstHeader("Retry-After")).map(NameValuePair::getValue).map(Long::valueOf).orElse(0L);
    }

    public boolean isFinished() {
        return this.uploadTask.isDone() || this.uploadTask.isCancelled();
    }

    @Environment(value=EnvType.CLIENT)
    class CustomInputStreamEntity
    extends InputStreamEntity {
        final private long length;
        final private InputStream content;
        final private UploadProgress uploadStatus;

        public CustomInputStreamEntity(InputStream content, long length, UploadProgress uploadStatus) {
            super(content);
            this.content = content;
            this.length = length;
            this.uploadStatus = uploadStatus;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public void writeTo(OutputStream stream) throws IOException {
            Args.notNull((Object)stream, (String)"Output stream");
            try (InputStream inputStream = this.content;){
                byte[] bs = new byte[4096];
                if (this.length < 0L) {
                    int i;
                    while ((i = inputStream.read(bs)) != -1) {
                        if (FileUpload.this.cancelled.get()) {
                            throw new CancelledRealmsUploadException();
                        }
                        stream.write(bs, 0, i);
                        this.uploadStatus.addBytesWritten(i);
                    }
                } else {
                    int i;
                    for (long l = this.length; l > 0L && (i = inputStream.read(bs, 0, (int)Math.min(4096L, l))) != -1; l -= (long)i) {
                        if (FileUpload.this.cancelled.get()) {
                            throw new CancelledRealmsUploadException();
                        }
                        stream.write(bs, 0, i);
                        this.uploadStatus.addBytesWritten(i);
                        stream.flush();
                    }
                }
                if (inputStream == null) return;
            }
        }
    }
}

