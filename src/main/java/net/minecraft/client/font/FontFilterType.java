/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public final class FontFilterType
extends Enum<FontFilterType>
implements StringIdentifiable {
    final static public FontFilterType UNIFORM = new FontFilterType("uniform");
    final static public FontFilterType JAPANESE_VARIANTS = new FontFilterType("jp");
    final static public Codec<FontFilterType> CODEC;
    final private String id;
    final static private FontFilterType[] field_49116;

    public static FontFilterType[] values() {
        return (FontFilterType[])field_49116.clone();
    }

    public static FontFilterType valueOf(String string) {
        return Enum.valueOf(FontFilterType.class, string);
    }

    private FontFilterType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static FontFilterType[] method_57030() {
        return new FontFilterType[]{UNIFORM, JAPANESE_VARIANTS};
    }

    static {
        field_49116 = FontFilterType.method_57030();
        CODEC = StringIdentifiable.createCodec(FontFilterType::values);
    }

    @Environment(value=EnvType.CLIENT)
    public static class FilterMap {
        final private Map<FontFilterType, Boolean> activeFilters;
        final static public Codec<FilterMap> CODEC = Codec.unboundedMap(CODEC, (Codec)Codec.BOOL).xmap(FilterMap::new, filterMap -> filterMap.activeFilters);
        final static public FilterMap NO_FILTER = new FilterMap(Map.of());

        public FilterMap(Map<FontFilterType, Boolean> activeFilters) {
            this.activeFilters = activeFilters;
        }

        public boolean isAllowed(Set<FontFilterType> activeFilters) {
            for (Map.Entry<FontFilterType, Boolean> entry : this.activeFilters.entrySet()) {
                if (activeFilters.contains(entry.getKey()) == entry.getValue().booleanValue()) continue;
                return false;
            }
            return true;
        }

        public FilterMap apply(FilterMap activeFilters) {
            HashMap<FontFilterType, Boolean> map = new HashMap<FontFilterType, Boolean>(activeFilters.activeFilters);
            map.putAll(this.activeFilters);
            return new FilterMap(Map.copyOf(map));
        }
    }
}

