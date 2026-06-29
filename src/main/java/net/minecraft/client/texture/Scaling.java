/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public interface Scaling {
    final static public Codec<Scaling> CODEC = Type.CODEC.dispatch(Scaling::getType, Type::getCodec);
    final static public Scaling STRETCH = new Stretch();

    public Type getType();

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type>
    implements StringIdentifiable {
        final static public Type STRETCH = new Type("stretch", Stretch.CODEC);
        final static public Type TILE = new Type("tile", Tile.CODEC);
        final static public Type NINE_SLICE = new Type("nine_slice", NineSlice.CODEC);
        final static public Codec<Type> CODEC;
        final private String name;
        final private MapCodec<? extends Scaling> codec;
        final static private Type[] field_45662;

        public static Type[] values() {
            return (Type[])field_45662.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String name, MapCodec<? extends Scaling> codec) {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public MapCodec<? extends Scaling> getCodec() {
            return this.codec;
        }

        private static Type[] method_52887() {
            return new Type[]{STRETCH, TILE, NINE_SLICE};
        }

        static {
            field_45662 = Type.method_52887();
            CODEC = StringIdentifiable.createCodec(Type::values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Stretch() implements Scaling
    {
        final static public MapCodec<Stretch> CODEC = MapCodec.unit(Stretch::new);

        @Override
        public Type getType() {
            return Type.STRETCH;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record NineSlice(int width, int height, Border border, boolean stretchInner) implements Scaling
    {
        final static public MapCodec<NineSlice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.POSITIVE_INT.fieldOf("width").forGetter(NineSlice::width), (App)Codecs.POSITIVE_INT.fieldOf("height").forGetter(NineSlice::height), (App)Border.CODEC.fieldOf("border").forGetter(NineSlice::border), (App)Codec.BOOL.optionalFieldOf("stretch_inner", (Object)false).forGetter(NineSlice::stretchInner)).apply((Applicative)instance, NineSlice::new)).validate(NineSlice::validate);

        private static DataResult<NineSlice> validate(NineSlice nineSlice) {
            Border border = nineSlice.border();
            if (border.left() + border.right() >= nineSlice.width()) {
                return DataResult.error(() -> "Nine-sliced texture has no horizontal center slice: " + border.left() + " + " + border.right() + " >= " + nineSlice.width());
            }
            if (border.top() + border.bottom() >= nineSlice.height()) {
                return DataResult.error(() -> "Nine-sliced texture has no vertical center slice: " + border.top() + " + " + border.bottom() + " >= " + nineSlice.height());
            }
            return DataResult.success((Object)nineSlice);
        }

        @Override
        public Type getType() {
            return Type.NINE_SLICE;
        }

        @Environment(value=EnvType.CLIENT)
        public record Border(int left, int top, int right, int bottom) {
            final static private Codec<Border> UNIFORM_SIDE_SIZES_CODEC = Codecs.POSITIVE_INT.flatComapMap(size -> new Border((int)size, (int)size, (int)size, (int)size), border -> {
                OptionalInt optionalInt = border.getUniformSideSize();
                if (optionalInt.isPresent()) {
                    return DataResult.success((Object)optionalInt.getAsInt());
                }
                return DataResult.error(() -> "Border has different side sizes");
            });
            final static private Codec<Border> DIFFERENT_SIDE_SIZES_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(Border::left), (App)Codecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(Border::top), (App)Codecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(Border::right), (App)Codecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(Border::bottom)).apply((Applicative)instance, Border::new));
            final static Codec<Border> CODEC = Codec.either(UNIFORM_SIDE_SIZES_CODEC, DIFFERENT_SIDE_SIZES_CODEC).xmap(Either::unwrap, border -> {
                if (border.getUniformSideSize().isPresent()) {
                    return Either.left((Object)border);
                }
                return Either.right((Object)border);
            });

            private OptionalInt getUniformSideSize() {
                if (this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom()) {
                    return OptionalInt.of(this.left());
                }
                return OptionalInt.empty();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Tile(int width, int height) implements Scaling
    {
        final static public MapCodec<Tile> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.POSITIVE_INT.fieldOf("width").forGetter(Tile::width), (App)Codecs.POSITIVE_INT.fieldOf("height").forGetter(Tile::height)).apply((Applicative)instance, Tile::new));

        @Override
        public Type getType() {
            return Type.TILE;
        }
    }
}

