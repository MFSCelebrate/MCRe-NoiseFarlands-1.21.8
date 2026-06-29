/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.DelegatingDataOutput;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

public class NbtIo {
    private static final OpenOption[] OPEN_OPTIONS = new OpenOption
            []{StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

    /*
     * Loose catch block
     */
    public static NbtCompound readCompressed(Path path, NbtSizeTracker tagSizeTracker)
            throws IOException {
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);
                FixedBufferInputStream inputStream2 = new FixedBufferInputStream(inputStream)) {
            return NbtIo.readCompressed(inputStream2, tagSizeTracker);
        }
    }

    private static DataInputStream decompress(InputStream stream) throws IOException {
        return new DataInputStream(new FixedBufferInputStream(new GZIPInputStream(stream)));
    }

    private static DataOutputStream compress(OutputStream stream) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));
    }

    public static NbtCompound readCompressed(InputStream stream, NbtSizeTracker tagSizeTracker)
            throws IOException {
        NbtCompound nbtCompound;
        block5:
        {
            DataInputStream dataInputStream = NbtIo.decompress(stream);
            try {
                nbtCompound = NbtIo.readCompound(dataInputStream, tagSizeTracker);
                if (dataInputStream == null) break block5;
            } catch (Throwable throwable) {
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            dataInputStream.close();
        }
        return nbtCompound;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void scanCompressed(Path path, NbtScanner scanner, NbtSizeTracker tracker)
            throws IOException {
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]); ) {
            try (FixedBufferInputStream inputStream2 = new FixedBufferInputStream(inputStream); ) {
                NbtIo.scanCompressed(inputStream2, scanner, tracker);
            }
            if (inputStream == null) return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void scanCompressed(InputStream stream, NbtScanner scanner, NbtSizeTracker tracker)
            throws IOException {
        try (DataInputStream dataInputStream = NbtIo.decompress(stream); ) {
            NbtIo.scan(dataInputStream, scanner, tracker);
            if (dataInputStream == null) return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void writeCompressed(NbtCompound nbt, Path path) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(path, OPEN_OPTIONS); ) {
            try (BufferedOutputStream outputStream2 = new BufferedOutputStream(outputStream); ) {
                NbtIo.writeCompressed(nbt, outputStream2);
            }
            if (outputStream == null) return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void writeCompressed(NbtCompound nbt, OutputStream stream) throws IOException {
        try (DataOutputStream dataOutputStream = NbtIo.compress(stream); ) {
            NbtIo.writeCompound(nbt, dataOutputStream);
            if (dataOutputStream == null) return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void write(NbtCompound nbt, Path path) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(path, OPEN_OPTIONS); ) {
            try (BufferedOutputStream outputStream2 = new BufferedOutputStream(outputStream);
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream2); ) {
                NbtIo.writeCompound(nbt, dataOutputStream);
            }
            if (outputStream == null) return;
        }
    }

    /*
     * Loose catch block
     */
    @Nullable
    public static NbtCompound read(Path path) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            return null;
        }
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);
                DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return NbtIo.readCompound(dataInputStream, NbtSizeTracker.ofUnlimitedBytes());
        }
    }

    public static NbtCompound readCompound(DataInput input) throws IOException {
        return NbtIo.readCompound(input, NbtSizeTracker.ofUnlimitedBytes());
    }

    public static NbtCompound readCompound(DataInput input, NbtSizeTracker tracker)
            throws IOException {
        NbtElement nbtElement = NbtIo.readElement(input, tracker);
        if (nbtElement instanceof NbtCompound) {
            return (NbtCompound) nbtElement;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void writeCompound(NbtCompound nbt, DataOutput output) throws IOException {
        NbtIo.write((NbtElement) nbt, output);
    }

    public static void scan(DataInput input, NbtScanner scanner, NbtSizeTracker tracker)
            throws IOException {
        NbtType<?> nbtType = NbtTypes.byId(input.readByte());
        if (nbtType == NbtEnd.TYPE) {
            if (scanner.start(NbtEnd.TYPE) == NbtScanner.Result.CONTINUE) {
                scanner.visitEnd();
            }
            return;
        }
        switch (scanner.start(nbtType)) {
            case HALT:
                {
                    break;
                }
            case BREAK:
                {
                    NbtString.skip(input);
                    nbtType.skip(input, tracker);
                    break;
                }
            case CONTINUE:
                {
                    NbtString.skip(input);
                    nbtType.doAccept(input, scanner, tracker);
                }
        }
    }

    public static NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        byte b = input.readByte();
        if (b == 0) {
            return NbtEnd.INSTANCE;
        }
        return NbtIo.readElement(input, tracker, b);
    }

    public static void writeForPacket(NbtElement nbt, DataOutput output) throws IOException {
        output.writeByte(nbt.getType());
        if (nbt.getType() == 0) {
            return;
        }
        nbt.write(output);
    }

    public static void writeUnsafe(NbtElement nbt, DataOutput output) throws IOException {
        output.writeByte(nbt.getType());
        if (nbt.getType() == 0) {
            return;
        }
        output.writeUTF("");
        nbt.write(output);
    }

    public static void write(NbtElement nbt, DataOutput output) throws IOException {
        NbtIo.writeUnsafe(nbt, new InvalidUtfSkippingDataOutput(output));
    }

    @VisibleForTesting
    public static NbtElement readElement(DataInput input, NbtSizeTracker tracker)
            throws IOException {
        byte b = input.readByte();
        if (b == 0) {
            return NbtEnd.INSTANCE;
        }
        NbtString.skip(input);
        return NbtIo.readElement(input, tracker, b);
    }

    private static NbtElement readElement(DataInput input, NbtSizeTracker tracker, byte typeId) {
        try {
            return NbtTypes.byId(typeId).read(input, tracker);
        } catch (IOException iOException) {
            CrashReport crashReport = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
            crashReportSection.add("Tag type", typeId);
            throw new NbtCrashException(crashReport);
        }
    }

    public static class InvalidUtfSkippingDataOutput extends DelegatingDataOutput {
        public InvalidUtfSkippingDataOutput(DataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void writeUTF(String string) throws IOException {
            try {
                super.writeUTF(string);
            } catch (UTFDataFormatException uTFDataFormatException) {
                Util.logErrorOrPause("Failed to write NBT String", uTFDataFormatException);
                super.writeUTF("");
            }
        }
    }
}
