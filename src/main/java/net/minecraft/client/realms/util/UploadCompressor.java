/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 */
package net.minecraft.client.realms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.TooBigRealmsUploadException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

@Environment(value=EnvType.CLIENT)
public class UploadCompressor {
    final static private long MAX_SIZE = 0x140000000L;
    final static private String field_54369 = "world";
    final private BooleanSupplier cancellationSupplier;
    final private Path directory;

    public static File compress(Path directory, BooleanSupplier cancellationSupplier) throws IOException {
        return new UploadCompressor(directory, cancellationSupplier).run();
    }

    private UploadCompressor(Path directory, BooleanSupplier cancellationSupplier) {
        this.cancellationSupplier = cancellationSupplier;
        this.directory = directory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File run() throws IOException {
        File file;
        block4: {
            TarArchiveOutputStream tarArchiveOutputStream = null;
            try {
                File file2 = File.createTempFile("realms-upload-file", ".tar.gz");
                tarArchiveOutputStream = new TarArchiveOutputStream((OutputStream)new GZIPOutputStream(new FileOutputStream(file2)));
                tarArchiveOutputStream.setLongFileMode(3);
                this.compress(tarArchiveOutputStream, this.directory, field_54369, true);
                if (this.cancellationSupplier.getAsBoolean()) {
                    throw new CancelledRealmsUploadException();
                }
                tarArchiveOutputStream.finish();
                this.validateSize(file2.length());
                file = file2;
                if (tarArchiveOutputStream == null) break block4;
            }
            catch (Throwable throwable) {
                if (tarArchiveOutputStream != null) {
                    tarArchiveOutputStream.close();
                }
                throw throwable;
            }
            tarArchiveOutputStream.close();
        }
        return file;
    }

    private void compress(TarArchiveOutputStream stream, Path directory, String prefix, boolean root) throws IOException {
        block8: {
            String string;
            File file;
            block7: {
                if (this.cancellationSupplier.getAsBoolean()) {
                    throw new CancelledRealmsUploadException();
                }
                this.validateSize(stream.getBytesWritten());
                file = directory.toFile();
                string = root ? prefix : prefix + file.getName();
                TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, string);
                stream.putArchiveEntry(tarArchiveEntry);
                if (!file.isFile()) break block7;
                try (FileInputStream inputStream = new FileInputStream(file);){
                    ((InputStream)inputStream).transferTo((OutputStream)stream);
                }
                stream.closeArchiveEntry();
                break block8;
            }
            stream.closeArchiveEntry();
            File[] files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    this.compress(stream, file2.toPath(), string + "/", false);
                }
            }
        }
    }

    private void validateSize(long size) {
        if (size > 0x140000000L) {
            throw new TooBigRealmsUploadException(0x140000000L);
        }
    }
}

