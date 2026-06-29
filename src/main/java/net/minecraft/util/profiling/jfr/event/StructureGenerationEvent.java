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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;

@Name(value="minecraft.StructureGeneration")
@Label(value="Structure Generation")
@Category(value={"Minecraft", "World Generation"})
@StackTrace(value=false)
@Enabled(value=false)
@DontObfuscate
public class StructureGenerationEvent
extends Event {
    final static public String EVENT_NAME = "minecraft.StructureGeneration";
    final static public EventType TYPE = EventType.getEventType(StructureGenerationEvent.class);
    @Name(value="chunkPosX")
    @Label(value="Chunk X Position")
    final public int chunkPosX;
    @Name(value="chunkPosZ")
    @Label(value="Chunk Z Position")
    final public int chunkPosZ;
    @Name(value="structure")
    @Label(value="Structure")
    final public String structure;
    @Name(value="level")
    @Label(value="Level")
    final public String level;
    @Name(value="success")
    @Label(value="Success")
    public boolean success;

    public StructureGenerationEvent(ChunkPos chunkPos, RegistryEntry<Structure> structure, RegistryKey<World> dimension) {
        this.chunkPosX = chunkPos.x;
        this.chunkPosZ = chunkPos.z;
        this.structure = structure.getIdAsString();
        this.level = dimension.getValue().toString();
    }

    public static interface Names {
        final static public String CHUNK_POS_X = "chunkPosX";
        final static public String CHUNK_POS_Z = "chunkPosZ";
        final static public String STRUCTURE = "structure";
        final static public String LEVEL = "level";
        final static public String SUCCESS = "success";
    }
}

