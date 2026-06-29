/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public final class DataFixTypes
extends Enum<DataFixTypes> {
    final static public DataFixTypes LEVEL = new DataFixTypes(TypeReferences.LEVEL);
    final static public DataFixTypes LEVEL_SUMMARY = new DataFixTypes(TypeReferences.LIGHTWEIGHT_LEVEL);
    final static public DataFixTypes PLAYER = new DataFixTypes(TypeReferences.PLAYER);
    final static public DataFixTypes CHUNK = new DataFixTypes(TypeReferences.CHUNK);
    final static public DataFixTypes HOTBAR = new DataFixTypes(TypeReferences.HOTBAR);
    final static public DataFixTypes OPTIONS = new DataFixTypes(TypeReferences.OPTIONS);
    final static public DataFixTypes STRUCTURE = new DataFixTypes(TypeReferences.STRUCTURE);
    final static public DataFixTypes STATS = new DataFixTypes(TypeReferences.STATS);
    final static public DataFixTypes SAVED_DATA_COMMAND_STORAGE = new DataFixTypes(TypeReferences.SAVED_DATA_COMMAND_STORAGE);
    final static public DataFixTypes SAVED_DATA_FORCED_CHUNKS = new DataFixTypes(TypeReferences.TICKETS_SAVED_DATA);
    final static public DataFixTypes SAVED_DATA_MAP_DATA = new DataFixTypes(TypeReferences.SAVED_DATA_MAP_DATA);
    final static public DataFixTypes SAVED_DATA_MAP_INDEX = new DataFixTypes(TypeReferences.SAVED_DATA_IDCOUNTS);
    final static public DataFixTypes SAVED_DATA_RAIDS = new DataFixTypes(TypeReferences.SAVED_DATA_RAIDS);
    final static public DataFixTypes SAVED_DATA_RANDOM_SEQUENCES = new DataFixTypes(TypeReferences.SAVED_DATA_RANDOM_SEQUENCES);
    final static public DataFixTypes SAVED_DATA_SCOREBOARD = new DataFixTypes(TypeReferences.SAVED_DATA_SCOREBOARD);
    final static public DataFixTypes SAVED_DATA_STRUCTURE_FEATURE_INDICES = new DataFixTypes(TypeReferences.SAVED_DATA_STRUCTURE_FEATURE_INDICES);
    final static public DataFixTypes ADVANCEMENTS = new DataFixTypes(TypeReferences.ADVANCEMENTS);
    final static public DataFixTypes POI_CHUNK = new DataFixTypes(TypeReferences.POI_CHUNK);
    final static public DataFixTypes WORLD_GEN_SETTINGS = new DataFixTypes(TypeReferences.WORLD_GEN_SETTINGS);
    final static public DataFixTypes ENTITY_CHUNK = new DataFixTypes(TypeReferences.ENTITY_CHUNK);
    final static public Set<DSL.TypeReference> REQUIRED_TYPES;
    final private DSL.TypeReference typeReference;
    final static private DataFixTypes[] field_19223;

    public static DataFixTypes[] values() {
        return (DataFixTypes[])field_19223.clone();
    }

    public static DataFixTypes valueOf(String string) {
        return Enum.valueOf(DataFixTypes.class, string);
    }

    private DataFixTypes(DSL.TypeReference typeReference) {
        this.typeReference = typeReference;
    }

    static int getSaveVersionId() {
        return SharedConstants.getGameVersion().dataVersion().id();
    }

    public <A> Codec<A> createDataFixingCodec(final Codec<A> baseCodec, DataFixer dataFixer, int currentDataVersion) {
        return new Codec<A>(currentDataVersion, dataFixer){
            final int field_46088;
            final DataFixer field_46089;
            {
                this.field_46088 = 1;
                this.field_46089 = dataFixer;
            }

            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return baseCodec.encode(input, ops, prefix).flatMap(encoded -> ops.mergeToMap(encoded, ops.createString("DataVersion"), ops.createInt(DataFixTypes.getSaveVersionId())));
            }

            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                int i = ops.get(input, "DataVersion").flatMap(arg_0 -> ops.getNumberValue(arg_0)).map(Number::intValue).result().orElse(this.field_46088);
                Dynamic dynamic = new Dynamic(ops, ops.remove(input, "DataVersion"));
                Dynamic dynamic2 = DataFixTypes.this.update(this.field_46089, dynamic, 1);
                return baseCodec.decode(dynamic2);
            }
        };
    }

    public <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion) {
        return dataFixer.update(this.typeReference, dynamic, oldVersion, newVersion);
    }

    public <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion) {
        return this.update(dataFixer, dynamic, oldVersion, DataFixTypes.getSaveVersionId());
    }

    public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion, int newVersion) {
        return (NbtCompound)this.update(dataFixer, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt), oldVersion, newVersion).getValue();
    }

    public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion) {
        return this.update(dataFixer, nbt, oldVersion, DataFixTypes.getSaveVersionId());
    }

    private static DataFixTypes[] method_36589() {
        return new DataFixTypes[]{LEVEL, LEVEL_SUMMARY, PLAYER, CHUNK, HOTBAR, OPTIONS, STRUCTURE, STATS, SAVED_DATA_COMMAND_STORAGE, SAVED_DATA_FORCED_CHUNKS, SAVED_DATA_MAP_DATA, SAVED_DATA_MAP_INDEX, SAVED_DATA_RAIDS, SAVED_DATA_RANDOM_SEQUENCES, SAVED_DATA_SCOREBOARD, SAVED_DATA_STRUCTURE_FEATURE_INDICES, ADVANCEMENTS, POI_CHUNK, WORLD_GEN_SETTINGS, ENTITY_CHUNK};
    }

    static {
        field_19223 = DataFixTypes.method_36589();
        REQUIRED_TYPES = Set.of(DataFixTypes.LEVEL_SUMMARY.typeReference);
    }
}

