/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.ChunkCompressionFormat;
import net.minecraft.world.storage.StorageKey;

@Category(value={"Minecraft", "Storage"})
@StackTrace(value=false)
@Enabled(value=false)
public abstract class ChunkRegionEvent
extends Event {
    @Name(value="regionPosX")
    @Label(value="Region X Position")
    final public int regionPosX;
    @Name(value="regionPosZ")
    @Label(value="Region Z Position")
    final public int regionPosZ;
    @Name(value="localPosX")
    @Label(value="Local X Position")
    final public int localChunkPosX;
    @Name(value="localPosZ")
    @Label(value="Local Z Position")
    final public int localChunkPosZ;
    @Name(value="chunkPosX")
    @Label(value="Chunk X Position")
    final public int chunkPosX;
    @Name(value="chunkPosZ")
    @Label(value="Chunk Z Position")
    final public int chunkPosZ;
    @Name(value="level")
    @Label(value="Level Id")
    final public String worldId;
    @Name(value="dimension")
    @Label(value="Dimension")
    final public String dimension;
    @Name(value="type")
    @Label(value="Type")
    final public String type;
    @Name(value="compression")
    @Label(value="Compression")
    final public String compression;
    @Name(value="bytes")
    @Label(value="Bytes")
    final public int bytes;

    public ChunkRegionEvent(StorageKey key, ChunkPos chunkPos, ChunkCompressionFormat format, int bytes) {
        this.regionPosX = chunkPos.getRegionX();
        this.regionPosZ = chunkPos.getRegionZ();
        this.localChunkPosX = chunkPos.getRegionRelativeX();
        this.localChunkPosZ = chunkPos.getRegionRelativeZ();
        this.chunkPosX = chunkPos.x;
        this.chunkPosZ = chunkPos.z;
        this.worldId = key.level();
        this.dimension = key.dimension().getValue().toString();
        this.type = key.type();
        this.compression = "standard:" + format.getId();
        this.bytes = bytes;
    }

    public static class Names {
        final static public String REGION_POS_X = "regionPosX";
        final static public String REGION_POS_Z = "regionPosZ";
        final static public String LOCAL_POS_X = "localPosX";
        final static public String LOCAL_POS_Z = "localPosZ";
        final static public String CHUNK_POS_X = "chunkPosX";
        final static public String CHUNK_POS_Z = "chunkPosZ";
        final static public String LEVEL = "level";
        final static public String DIMENSION = "dimension";
        final static public String TYPE = "type";
        final static public String COMPRESSION = "compression";
        final static public String BYTES = "bytes";

        private Names() {
        }
    }
}

