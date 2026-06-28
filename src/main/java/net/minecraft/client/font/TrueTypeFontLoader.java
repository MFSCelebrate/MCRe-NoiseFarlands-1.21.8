/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FreeType
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

@Environment(value=EnvType.CLIENT)
public record TrueTypeFontLoader(Identifier location, float size, float oversample, Shift shift, String skip) implements FontLoader
{
    final static private Codec<String> SKIP_CODEC = Codec.withAlternative((Codec)Codec.STRING, (Codec)Codec.STRING.listOf(), chars -> String.join((CharSequence)"", chars));
    final static public MapCodec<TrueTypeFontLoader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("file").forGetter(TrueTypeFontLoader::location), (App)Codec.FLOAT.optionalFieldOf("size", (Object)Float.valueOf(11.0f)).forGetter(TrueTypeFontLoader::size), (App)Codec.FLOAT.optionalFieldOf("oversample", (Object)Float.valueOf(1.0f)).forGetter(TrueTypeFontLoader::oversample), (App)Shift.CODEC.optionalFieldOf("shift", (Object)Shift.NONE).forGetter(TrueTypeFontLoader::shift), (App)SKIP_CODEC.optionalFieldOf("skip", (Object)"").forGetter(TrueTypeFontLoader::skip)).apply((Applicative)instance, TrueTypeFontLoader::new));

    @Override
    public FontType getType() {
        return FontType.TTF;
    }

    @Override
    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        return Either.left(this::load);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    private Font load(ResourceManager resourceManager) throws IOException {
        FT_Face fT_Face = null;
        ByteBuffer byteBuffer = null;
        try {
            TrueTypeFont trueTypeFont;
            try (InputStream inputStream = resourceManager.open(this.location.withPrefixedPath("font/"));){
                byteBuffer = TextureUtil.readResource(inputStream);
                byteBuffer.flip();
                Object object = FreeTypeUtil.LOCK;
                synchronized (object) {
                    String string;
                    block17: {
                        try (MemoryStack memoryStack = MemoryStack.stackPush();){
                            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                            FreeTypeUtil.checkFatalError(FreeType.FT_New_Memory_Face((long)FreeTypeUtil.initialize(), (ByteBuffer)byteBuffer, 0L, (PointerBuffer)pointerBuffer), "Initializing font face");
                            fT_Face = FT_Face.create((long)pointerBuffer.get());
                            if (memoryStack == null) break block17;
                        }
                    }
                    if (!"TrueType".equals(string = FreeType.FT_Get_Font_Format((FT_Face)fT_Face))) {
                        throw new IOException("Font is not in TTF format, was " + string);
                    }
                    FreeTypeUtil.checkFatalError(FreeType.FT_Select_Charmap((FT_Face)fT_Face, (int)FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
                    trueTypeFont = new TrueTypeFont(byteBuffer, fT_Face, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
                }
                if (inputStream == null) return trueTypeFont;
            }
            return trueTypeFont;
        }
        catch (Exception exception) {
            Object object = FreeTypeUtil.LOCK;
            synchronized (object) {
                if (fT_Face != null) {
                    FreeType.FT_Done_Face(fT_Face);
                }
            }
            MemoryUtil.memFree((Buffer)byteBuffer);
            throw exception;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Shift
    extends Record {
        final float x;
        final float y;
        final static public Shift NONE = new Shift(0.0f, 0.0f);
        final static public Codec<Shift> CODEC = Codec.floatRange(-512.0f, 512.0f).listOf().comapFlatMap(floatList2 -> Util.decodeFixedLengthList(floatList2, 2).map(floatList -> new Shift(((Float)floatList.get(0)).floatValue(), ((Float)floatList.get(1)).floatValue())), shift -> List.of(Float.valueOf(shift.x), Float.valueOf(shift.y)));

        public Shift(float f, float g) {
            this.x = f;
            this.y = g;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Shift.class, "x;y", "x", "y"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Shift.class, "x;y", "x", "y"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Shift.class, "x;y", "x", "y"}, this, object);
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }
    }
}

