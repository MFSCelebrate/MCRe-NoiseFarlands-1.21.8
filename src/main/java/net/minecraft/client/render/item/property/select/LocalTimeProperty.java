/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.DateFormat
 *  com.ibm.icu.text.SimpleDateFormat
 *  com.ibm.icu.util.Calendar
 *  com.ibm.icu.util.TimeZone
 *  com.ibm.icu.util.ULocale
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LocalTimeProperty
implements SelectProperty<String> {
    final static public String DEFAULT_FORMATTED_TIME = "";
    final static private long MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1L);
    final static public Codec<String> VALUE_CODEC = Codec.STRING;
    final static private Codec<TimeZone> TIME_ZONE_CODEC = VALUE_CODEC.comapFlatMap(timeZone -> {
        TimeZone timeZone2 = TimeZone.getTimeZone((String)timeZone);
        if (timeZone2.equals((Object)TimeZone.UNKNOWN_ZONE)) {
            return DataResult.error(() -> "Unknown timezone: " + timeZone);
        }
        return DataResult.success((Object)timeZone2);
    }, TimeZone::getID);
    final static private MapCodec<Data> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("pattern").forGetter(data -> data.format), (App)Codec.STRING.optionalFieldOf("locale", (Object)DEFAULT_FORMATTED_TIME).forGetter(data -> data.localeId), (App)TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter(data -> data.timeZone)).apply((Applicative)instance, Data::new));
    final static public SelectProperty.Type<LocalTimeProperty, String> TYPE = SelectProperty.Type.create(DATA_CODEC.flatXmap(LocalTimeProperty::validate, property -> DataResult.success((Object)property.data)), VALUE_CODEC);
    final private Data data;
    final private DateFormat dateFormat;
    private long nextUpdateTime;
    private String currentTimeFormatted = "";

    private LocalTimeProperty(Data data, DateFormat dateFormat) {
        this.data = data;
        this.dateFormat = dateFormat;
    }

    public static LocalTimeProperty create(String pattern, String locale, Optional<TimeZone> timeZone) {
        return (LocalTimeProperty)LocalTimeProperty.validate(new Data(pattern, locale, timeZone)).getOrThrow(format -> new IllegalStateException("Failed to validate format: " + format));
    }

    private static DataResult<LocalTimeProperty> validate(Data data) {
        ULocale uLocale = new ULocale(data.localeId);
        Calendar calendar = data.timeZone.map(timeZone -> Calendar.getInstance((TimeZone)timeZone, (ULocale)uLocale)).orElseGet(() -> Calendar.getInstance((ULocale)uLocale));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(data.format, uLocale);
        simpleDateFormat.setCalendar(calendar);
        try {
            simpleDateFormat.format(new Date());
        }
        catch (Exception exception) {
            return DataResult.error(() -> "Invalid time format '" + String.valueOf(simpleDateFormat) + "': " + exception.getMessage());
        }
        return DataResult.success((Object)new LocalTimeProperty(data, (DateFormat)simpleDateFormat));
    }

    @Override
    @Nullable
    public String java_lang_String_getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        long l = Util.getMeasuringTimeMs();
        if (l > this.nextUpdateTime) {
            this.currentTimeFormatted = this.formatCurrentTime();
            this.nextUpdateTime = l + MILLIS_PER_SECOND;
        }
        return this.currentTimeFormatted;
    }

    private String formatCurrentTime() {
        return this.dateFormat.format(new Date());
    }

    @Override
    public SelectProperty.Type<LocalTimeProperty, String> getType() {
        return TYPE;
    }

    @Override
    public Codec<String> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public Object java_lang_Object_getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.java_lang_String_getValue(stack, world, user, seed, displayContext);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Data
    extends Record {
        final String format;
        final String localeId;
        final Optional<TimeZone> timeZone;

        Data(String string, String string2, Optional<TimeZone> optional) {
            this.format = string;
            this.localeId = string2;
            this.timeZone = optional;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this, object);
        }

        public String format() {
            return this.format;
        }

        public String localeId() {
            return this.localeId;
        }

        public Optional<TimeZone> timeZone() {
            return this.timeZone;
        }
    }
}

