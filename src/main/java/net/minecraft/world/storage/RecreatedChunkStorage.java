/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  org.apache.commons.io.FileUtils
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.StorageKey;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.io.FileUtils;

public class RecreatedChunkStorage
extends VersionedChunkStorage {
    final private StorageIoWorker recreationWorker;
    final private Path outputDirectory;

    public RecreatedChunkStorage(StorageKey storageKey, Path directory, StorageKey outputStorageKey, Path outputDirectory, DataFixer dataFixer, boolean dsync) {
        super(storageKey, directory, dataFixer, dsync);
        this.outputDirectory = outputDirectory;
        this.recreationWorker = new StorageIoWorker(outputStorageKey, outputDirectory, dsync);
    }

    @Override
    public CompletableFuture<Void> setNbt(ChunkPos chunkPos, Supplier<NbtCompound> nbtSupplier) {
        this.markFeatureUpdateResolved(chunkPos);
        return this.recreationWorker.setResult(chunkPos, nbtSupplier);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.recreationWorker.close();
        if (this.outputDirectory.toFile().exists()) {
            FileUtils.deleteDirectory((File)this.outputDirectory.toFile());
        }
    }
}

