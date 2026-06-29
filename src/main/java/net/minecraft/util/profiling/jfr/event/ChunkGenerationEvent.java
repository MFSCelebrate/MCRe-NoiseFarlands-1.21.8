/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

@Name(value="minecraft.ChunkGeneration")
@Label(value="Chunk Generation")
@Category(value={"Minecraft", "World Generation"})
@StackTrace(value=false)
@Enabled(value=false)
@DontObfuscate
public class ChunkGenerationEvent
extends Event {
    final static public String EVENT_NAME = "minecraft.ChunkGeneration";
    final static public EventType TYPE = EventType.getEventType(ChunkGenerationEvent.class);
    @Name(value="worldPosX")
    @Label(value="First Block X World Position")
    final public int worldPosX;
    @Name(value="worldPosZ")
    @Label(value="First Block Z World Position")
    final public int worldPosZ;
    @Name(value="chunkPosX")
    @Label(value="Chunk X Position")
    final public int chunkPosX;
    @Name(value="chunkPosZ")
    @Label(value="Chunk Z Position")
    final public int chunkPosZ;
    @Name(value="status")
    @Label(value="Status")
    final public String targetStatus;
    @Name(value="level")
    @Label(value="Level")
    final public String level;

    public ChunkGenerationEvent(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
        this.targetStatus = targetStatus;
        this.level = world.getValue().toString();
        this.chunkPosX = chunkPos.x;
        this.chunkPosZ = chunkPos.z;
        this.worldPosX = chunkPos.getStartX();
        this.worldPosZ = chunkPos.getStartZ();
    }

    public static class Names {
        final static public String WORLD_POS_X = "worldPosX";
        final static public String WORLD_POS_Z = "worldPosZ";
        final static public String CHUNK_POS_X = "chunkPosX";
        final static public String CHUNK_POS_Z = "chunkPosZ";
        final static public String STATUS = "status";
        final static public String LEVEL = "level";

        private Names() {
        }
    }
}

