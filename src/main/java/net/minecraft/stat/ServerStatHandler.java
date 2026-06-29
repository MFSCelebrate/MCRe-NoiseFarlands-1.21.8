/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.stat;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatHandler
extends StatHandler {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Codec<Map<Stat<?>, Integer>> CODEC = Codec.dispatchedMap(Registries.STAT_TYPE.getCodec(), Util.memoize(ServerStatHandler::createCodec)).xmap(statsByTypes -> {
        HashMap map = new HashMap();
        statsByTypes.forEach((type, stats) -> map.putAll(stats));
        return map;
    }, stats -> stats.entrySet().stream().collect(Collectors.groupingBy(entry -> ((Stat)entry.getKey()).getType(), Util.toMap())));
    final private MinecraftServer server;
    final private File file;
    final private Set<Stat<?>> pendingStats = Sets.newHashSet();

    private static <T> Codec<Map<Stat<?>, Integer>> createCodec(StatType<T> statType) {
        Codec<T> codec = statType.getRegistry().getCodec();
        Codec codec2 = codec.flatComapMap(statType::getOrCreateStat, stat -> {
            if (stat.getType() == statType) {
                return DataResult.success(stat.getValue());
            }
            return DataResult.error(() -> "Expected type " + String.valueOf(statType) + ", but got " + String.valueOf(stat.getType()));
        });
        return Codec.unboundedMap((Codec)codec2, (Codec)Codec.INT);
    }

    public ServerStatHandler(MinecraftServer server, File file) {
        this.server = server;
        this.file = file;
        if (file.isFile()) {
            try {
                this.parse(server.getDataFixer(), FileUtils.readFileToString((File)file));
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't read statistics file {}", (Object)file, (Object)iOException);
            }
            catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse statistics file {}", (Object)file, (Object)jsonParseException);
            }
        }
    }

    public void save() {
        try {
            FileUtils.writeStringToFile((File)this.file, (String)this.asString());
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save stats", (Throwable)iOException);
        }
    }

    @Override
    public void setStat(PlayerEntity player, Stat<?> stat, int value) {
        super.setStat(player, stat, value);
        this.pendingStats.add(stat);
    }

    private Set<Stat<?>> takePendingStats() {
        HashSet set = Sets.newHashSet(this.pendingStats);
        this.pendingStats.clear();
        return set;
    }

    public void parse(DataFixer dataFixer, String json) {
        try {
            JsonElement jsonElement = StrictJsonParser.parse(json);
            if (jsonElement.isJsonNull()) {
                LOGGER.error("Unable to parse Stat data from {}", (Object)this.file);
                return;
            }
            Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement);
            dynamic = DataFixTypes.STATS.update(dataFixer, dynamic, NbtHelper.getDataVersion(dynamic, 1343));
            this.statMap.putAll(CODEC.parse(dynamic.get("stats").orElseEmptyMap()).resultOrPartial(string -> LOGGER.error("Failed to parse statistics for {}: {}", (Object)this.file, string)).orElse(Map.of()));
        }
        catch (JsonParseException jsonParseException) {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.file, (Object)jsonParseException);
        }
    }

    protected String asString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("stats", (JsonElement)CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.statMap).getOrThrow());
        jsonObject.addProperty("DataVersion", (Number)SharedConstants.getGameVersion().dataVersion().id());
        return jsonObject.toString();
    }

    public void updateStatSet() {
        this.pendingStats.addAll((Collection<Stat<?>>)this.statMap.keySet());
    }

    public void sendStats(ServerPlayerEntity player) {
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        for (Stat<?> stat : this.takePendingStats()) {
            object2IntMap.put(stat, this.getStat(stat));
        }
        player.networkHandler.sendPacket(new StatisticsS2CPacket((Object2IntMap<Stat<?>>)object2IntMap));
    }
}

