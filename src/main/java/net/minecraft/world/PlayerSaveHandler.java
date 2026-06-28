/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.DateTimeFormatters;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

public class PlayerSaveHandler {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private File playerDataDir;
    final protected DataFixer dataFixer;
    final static private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatters.create();

    public PlayerSaveHandler(LevelStorage.Session session, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.playerDataDir = session.getDirectory(WorldSavePath.PLAYERDATA).toFile();
        this.playerDataDir.mkdirs();
    }

    public void savePlayerData(PlayerEntity player) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(player.getErrorReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, player.getRegistryManager());
            player.writeData(nbtWriteView);
            Path path = this.playerDataDir.toPath();
            Path path2 = Files.createTempFile(path, player.getUuidAsString() + "-", ".dat", new FileAttribute[0]);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            NbtIo.writeCompressed(nbtCompound, path2);
            Path path3 = path.resolve(player.getUuidAsString() + ".dat");
            Path path4 = path.resolve(player.getUuidAsString() + ".dat_old");
            Util.backupAndReplace(path3, path2, path4);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save player data for {}", (Object)player.getName().getString());
        }
    }

    private void backupCorruptedPlayerData(PlayerEntity player, String extension) {
        Path path = this.playerDataDir.toPath();
        Path path2 = path.resolve(player.getUuidAsString() + extension);
        Path path3 = path.resolve(player.getUuidAsString() + "_corrupted_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + extension);
        if (!Files.isRegularFile(path2, new LinkOption[0])) {
            return;
        }
        try {
            Files.copy(path2, path3, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to copy the player.dat file for {}", (Object)player.getName().getString(), (Object)exception);
        }
    }

    private Optional<NbtCompound> loadPlayerData(PlayerEntity player, String extension) {
        File file = new File(this.playerDataDir, player.getUuidAsString() + extension);
        if (file.exists() && file.isFile()) {
            try {
                return Optional.of(NbtIo.readCompressed(file.toPath(), NbtSizeTracker.ofUnlimitedBytes()));
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load player data for {}", (Object)player.getName().getString());
            }
        }
        return Optional.empty();
    }

    public Optional<ReadView> loadPlayerData(PlayerEntity player, ErrorReporter errorReporter) {
        Optional<NbtCompound> optional = this.loadPlayerData(player, ".dat");
        if (optional.isEmpty()) {
            this.backupCorruptedPlayerData(player, ".dat");
        }
        return optional.or(() -> this.loadPlayerData(player, ".dat_old")).map(nbt -> {
            int i = NbtHelper.getDataVersion(nbt, -1);
            nbt = DataFixTypes.PLAYER.update(this.dataFixer, (NbtCompound)nbt, 1);
            ReadView readView = NbtReadView.create(errorReporter, player.getRegistryManager(), nbt);
            player.readData(readView);
            return readView;
        });
    }
}

