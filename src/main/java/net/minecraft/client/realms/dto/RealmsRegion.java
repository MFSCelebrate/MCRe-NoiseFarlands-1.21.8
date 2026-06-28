/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public final class RealmsRegion
extends Enum<RealmsRegion> {
    final static public RealmsRegion AUSTRALIA_EAST = new RealmsRegion("AustraliaEast", "realms.configuration.region.australia_east");
    final static public RealmsRegion AUSTRALIA_SOUTHEAST = new RealmsRegion("AustraliaSoutheast", "realms.configuration.region.australia_southeast");
    final static public RealmsRegion BRAZIL_SOUTH = new RealmsRegion("BrazilSouth", "realms.configuration.region.brazil_south");
    final static public RealmsRegion CENTRAL_INDIA = new RealmsRegion("CentralIndia", "realms.configuration.region.central_india");
    final static public RealmsRegion CENTRAL_US = new RealmsRegion("CentralUs", "realms.configuration.region.central_us");
    final static public RealmsRegion EAST_ASIA = new RealmsRegion("EastAsia", "realms.configuration.region.east_asia");
    final static public RealmsRegion EAST_US = new RealmsRegion("EastUs", "realms.configuration.region.east_us");
    final static public RealmsRegion EAST_US_2 = new RealmsRegion("EastUs2", "realms.configuration.region.east_us_2");
    final static public RealmsRegion FRANCE_CENTRAL = new RealmsRegion("FranceCentral", "realms.configuration.region.france_central");
    final static public RealmsRegion JAPAN_EAST = new RealmsRegion("JapanEast", "realms.configuration.region.japan_east");
    final static public RealmsRegion JAPAN_WEST = new RealmsRegion("JapanWest", "realms.configuration.region.japan_west");
    final static public RealmsRegion KOREA_CENTRAL = new RealmsRegion("KoreaCentral", "realms.configuration.region.korea_central");
    final static public RealmsRegion NORTH_CENTRAL_US = new RealmsRegion("NorthCentralUs", "realms.configuration.region.north_central_us");
    final static public RealmsRegion NORTH_EUROPE = new RealmsRegion("NorthEurope", "realms.configuration.region.north_europe");
    final static public RealmsRegion SOUTH_CENTRAL_US = new RealmsRegion("SouthCentralUs", "realms.configuration.region.south_central_us");
    final static public RealmsRegion SOUTHEAST_ASIA = new RealmsRegion("SoutheastAsia", "realms.configuration.region.southeast_asia");
    final static public RealmsRegion SWEDEN_CENTRAL = new RealmsRegion("SwedenCentral", "realms.configuration.region.sweden_central");
    final static public RealmsRegion UAE_NORTH = new RealmsRegion("UAENorth", "realms.configuration.region.uae_north");
    final static public RealmsRegion UK_SOUTH = new RealmsRegion("UKSouth", "realms.configuration.region.uk_south");
    final static public RealmsRegion WEST_CENTRAL_US = new RealmsRegion("WestCentralUs", "realms.configuration.region.west_central_us");
    final static public RealmsRegion WEST_EUROPE = new RealmsRegion("WestEurope", "realms.configuration.region.west_europe");
    final static public RealmsRegion WEST_US = new RealmsRegion("WestUs", "realms.configuration.region.west_us");
    final static public RealmsRegion WEST_US_2 = new RealmsRegion("WestUs2", "realms.configuration.region.west_us_2");
    final static public RealmsRegion INVALID_REGION = new RealmsRegion("invalid", "");
    final public String name;
    final public String translationKey;
    final static private RealmsRegion[] field_60175;

    public static RealmsRegion[] values() {
        return (RealmsRegion[])field_60175.clone();
    }

    public static RealmsRegion valueOf(String string) {
        return Enum.valueOf(RealmsRegion.class, string);
    }

    private RealmsRegion(String name, String translationKey) {
        this.name = name;
        this.translationKey = translationKey;
    }

    @Nullable
    public static RealmsRegion fromName(String name) {
        for (RealmsRegion realmsRegion : RealmsRegion.values()) {
            if (!realmsRegion.name.equals(name)) continue;
            return realmsRegion;
        }
        return null;
    }

    private static RealmsRegion[] method_71172() {
        return new RealmsRegion[]{AUSTRALIA_EAST, AUSTRALIA_SOUTHEAST, BRAZIL_SOUTH, CENTRAL_INDIA, CENTRAL_US, EAST_ASIA, EAST_US, EAST_US_2, FRANCE_CENTRAL, JAPAN_EAST, JAPAN_WEST, KOREA_CENTRAL, NORTH_CENTRAL_US, NORTH_EUROPE, SOUTH_CENTRAL_US, SOUTHEAST_ASIA, SWEDEN_CENTRAL, UAE_NORTH, UK_SOUTH, WEST_CENTRAL_US, WEST_EUROPE, WEST_US, WEST_US_2, INVALID_REGION};
    }

    static {
        field_60175 = RealmsRegion.method_71172();
    }

    @Environment(value=EnvType.CLIENT)
    public static class RegionTypeAdapter
    extends TypeAdapter<RealmsRegion> {
        final static private Logger LOGGER = LogUtils.getLogger();

        public void write(JsonWriter jsonWriter, RealmsRegion realmsRegion) throws IOException {
            jsonWriter.value(realmsRegion.name);
        }

        public RealmsRegion net_minecraft_client_realms_dto_RealmsRegion_read(JsonReader jsonReader) throws IOException {
            String string = jsonReader.nextString();
            RealmsRegion realmsRegion = RealmsRegion.fromName(string);
            if (realmsRegion == null) {
                LOGGER.warn("Unsupported RealmsRegion {}", (Object)string);
                return INVALID_REGION;
            }
            return realmsRegion;
        }

        public Object java_lang_Object_read(JsonReader reader) throws IOException {
            return this.net_minecraft_client_realms_dto_RealmsRegion_read(reader);
        }

        public void write(JsonWriter writer, Object region) throws IOException {
            this.write(writer, (RealmsRegion)((Object)region));
        }
    }
}

