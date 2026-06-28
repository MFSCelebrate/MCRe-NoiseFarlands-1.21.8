/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.realms.FileUpload;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.CloseFailureRealmsUploadException;
import net.minecraft.client.realms.exception.upload.FailedRealmsUploadException;
import net.minecraft.client.realms.util.UploadCompressor;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.client.realms.util.UploadResult;
import net.minecraft.client.realms.util.UploadTokenCache;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUploader {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public int MAX_ATTEMPTS = 20;
    final private RealmsClient client = RealmsClient.create();
    final private Path directory;
    final private RealmsSlot options;
    final private Session session;
    final private long worldId;
    final private UploadProgressTracker progressTracker;
    private volatile boolean cancelled;
    @Nullable
    private FileUpload upload;

    public RealmsUploader(Path directory, RealmsSlot options, Session session, long worldId, UploadProgressTracker progressTracker) {
        this.directory = directory;
        this.options = options;
        this.session = session;
        this.worldId = worldId;
        this.progressTracker = progressTracker;
    }

    public CompletableFuture<?> upload() {
        return CompletableFuture.runAsync(() -> {
            File file = null;
            try {
                FileUpload fileUpload;
                UploadInfo uploadInfo = this.uploadSync();
                file = UploadCompressor.compress(this.directory, () -> this.cancelled);
                this.progressTracker.updateProgressDisplay();
                this.upload = fileUpload = new FileUpload(file, this.worldId, this.options.slotId, uploadInfo, this.session, SharedConstants.getGameVersion().name(), this.options.options.version, this.progressTracker.getUploadProgress());
                UploadResult uploadResult = fileUpload.upload();
                String string = uploadResult.getErrorMessage();
                if (string != null) {
                    throw new FailedRealmsUploadException(string);
                }
                UploadTokenCache.invalidate(this.worldId);
                this.client.updateSlot(this.worldId, this.options.slotId, this.options.options, this.options.settings);
                if (file == null) return;
            }
            catch (IOException iOException) {
                try {
                    throw new FailedRealmsUploadException(iOException.getMessage());
                    catch (RealmsServiceException realmsServiceException) {
                        throw new FailedRealmsUploadException(realmsServiceException.error.getText());
                    }
                    catch (InterruptedException | CancellationException exception) {
                        throw new CancelledRealmsUploadException();
                    }
                }
                catch (Throwable throwable) {
                    if (file == null) throw throwable;
                    LOGGER.debug("Deleting file {}", (Object)file.getAbsolutePath());
                    file.delete();
                    throw throwable;
                }
            }
            LOGGER.debug("Deleting file {}", (Object)file.getAbsolutePath());
            file.delete();
        }, Util.getMainWorkerExecutor());
    }

    public void cancel() {
        this.cancelled = true;
        if (this.upload != null) {
            this.upload.cancel();
            this.upload = null;
        }
    }

    private UploadInfo uploadSync() throws RealmsServiceException, InterruptedException {
        for (int i = 0; i < 20; ++i) {
            try {
                UploadInfo uploadInfo = this.client.upload(this.worldId);
                if (this.cancelled) {
                    throw new CancelledRealmsUploadException();
                }
                if (uploadInfo == null) continue;
                if (!uploadInfo.isWorldClosed()) {
                    throw new CloseFailureRealmsUploadException();
                }
                return uploadInfo;
            }
            catch (RetryCallException retryCallException) {
                Thread.sleep((long)retryCallException.delaySeconds * 1000L);
            }
        }
        throw new CloseFailureRealmsUploadException();
    }
}

