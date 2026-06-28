/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 */
package net.minecraft.world.level.storage;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.util.path.PathUtil;

public class SessionLock
implements AutoCloseable {
    final static public String SESSION_LOCK = "session.lock";
    final private FileChannel channel;
    final private FileLock lock;
    final static private ByteBuffer SNOWMAN;

    public static SessionLock create(Path path) throws IOException {
        Path path2 = path.resolve(SESSION_LOCK);
        PathUtil.createDirectories(path);
        FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            fileChannel.write(SNOWMAN.duplicate());
            fileChannel.force(true);
            FileLock fileLock = fileChannel.tryLock();
            if (fileLock == null) {
                throw AlreadyLockedException.create(path2);
            }
            return new SessionLock(fileChannel, fileLock);
        }
        catch (IOException iOException) {
            try {
                fileChannel.close();
            }
            catch (IOException iOException2) {
                iOException.addSuppressed(iOException2);
            }
            throw iOException;
        }
    }

    private SessionLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    @Override
    public void close() throws IOException {
        block4: {
            try {
                if (!this.lock.isValid()) break block4;
                this.lock.release();
            }
            catch (Throwable throwable) {
                if (this.channel.isOpen()) {
                    this.channel.close();
                }
                throw throwable;
            }
        }
        if (this.channel.isOpen()) {
            this.channel.close();
        }
    }

    public boolean isValid() {
        return this.lock.isValid();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public static boolean isLocked(Path path) throws IOException {
        Path path2 = path.resolve(SESSION_LOCK);
        try {
            boolean bl;
            try (FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.WRITE);){
                FileLock fileLock = fileChannel.tryLock();
                boolean bl2 = bl = fileLock == null;
                if (fileLock != null) {
                    fileLock.close();
                }
                if (fileChannel == null) return bl;
            }
            return bl;
        }
        catch (AccessDeniedException accessDeniedException) {
            return true;
        }
        catch (NoSuchFileException noSuchFileException) {
            return false;
        }
    }

    static {
        byte[] bs = "\u2603".getBytes(Charsets.UTF_8);
        SNOWMAN = ByteBuffer.allocateDirect(bs.length);
        SNOWMAN.put(bs);
        SNOWMAN.flip();
    }

    public static class AlreadyLockedException
    extends IOException {
        private AlreadyLockedException(Path path, String message) {
            super(String.valueOf(path.toAbsolutePath()) + ": " + message);
        }

        public static AlreadyLockedException create(Path path) {
            return new AlreadyLockedException(path, "already locked (possibly by other Minecraft instance?)");
        }
    }
}

