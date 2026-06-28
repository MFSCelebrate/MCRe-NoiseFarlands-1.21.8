/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Random RANDOM = Random.create();
    final static private float MAX_ADVANCE = 32.0f;
    final private TextureManager textureManager;
    final private Identifier id;
    private BakedGlyph blankBakedGlyph;
    private BakedGlyph whiteRectangleBakedGlyph;
    private List<Font.FontFilterPair> allFonts = List.of();
    private List<Font> availableFonts = List.of();
    final private GlyphContainer<BakedGlyph> bakedGlyphCache = new GlyphContainer(BakedGlyph[]::new, rowCount -> new BakedGlyph[rowCount][]);
    final private GlyphContainer<GlyphPair> glyphCache = new GlyphContainer(GlyphPair[]::new, rowCount -> new GlyphPair[rowCount][]);
    final private Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap();
    final private List<GlyphAtlasTexture> glyphAtlases = Lists.newArrayList();
    final private IntFunction<GlyphPair> glyphFinder = this::findGlyph;
    final private IntFunction<BakedGlyph> glyphBaker = this::bake;

    public FontStorage(TextureManager textureManager, Identifier id) {
        this.textureManager = textureManager;
        this.id = id;
    }

    public void setFonts(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
        this.allFonts = allFonts;
        this.setActiveFilters(activeFilters);
    }

    public void setActiveFilters(Set<FontFilterType> activeFilters) {
        this.availableFonts = List.of();
        this.clear();
        this.availableFonts = this.applyFilters(this.allFonts, activeFilters);
    }

    private void clear() {
        this.glyphAtlases.clear();
        this.bakedGlyphCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankBakedGlyph = BuiltinEmptyGlyph.MISSING.bake(this::bake);
        this.whiteRectangleBakedGlyph = BuiltinEmptyGlyph.WHITE.bake(this::bake);
    }

    private List<Font> applyFilters(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
        IntOpenHashSet intSet = new IntOpenHashSet();
        ArrayList<Font> list = new ArrayList<Font>();
        for (Font.FontFilterPair fontFilterPair : allFonts) {
            if (!fontFilterPair.filter().isAllowed(activeFilters)) continue;
            list.add(fontFilterPair.provider());
            intSet.addAll((IntCollection)fontFilterPair.provider().getProvidedGlyphs());
        }
        HashSet set = Sets.newHashSet();
        intSet.forEach(codePoint -> {
            for (Font font : list) {
                Glyph glyph = font.getGlyph(codePoint);
                if (glyph == null) continue;
                set.add(font);
                if (glyph == BuiltinEmptyGlyph.MISSING) break;
                ((IntList)this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), i -> new IntArrayList())).add(codePoint);
                break;
            }
        });
        return list.stream().filter(set::contains).toList();
    }

    @Override
    public void close() {
        this.glyphAtlases.clear();
    }

    private static boolean isAdvanceInvalid(Glyph glyph) {
        float f = glyph.getAdvance(false);
        if (f < 0.0f || f > 32.0f) {
            return true;
        }
        float g = glyph.getAdvance(true);
        return g < 0.0f || g > 32.0f;
    }

    private GlyphPair findGlyph(int codePoint) {
        Glyph glyph = null;
        for (Font font : this.availableFonts) {
            Glyph glyph2 = font.getGlyph(codePoint);
            if (glyph2 == null) continue;
            if (glyph == null) {
                glyph = glyph2;
            }
            if (FontStorage.isAdvanceInvalid(glyph2)) continue;
            return new GlyphPair(glyph, glyph2);
        }
        if (glyph != null) {
            return new GlyphPair(glyph, BuiltinEmptyGlyph.MISSING);
        }
        return GlyphPair.MISSING;
    }

    public Glyph getGlyph(int codePoint, boolean validateAdvance) {
        return this.glyphCache.computeIfAbsent(codePoint, this.glyphFinder).getGlyph(validateAdvance);
    }

    private BakedGlyph bake(int codePoint) {
        for (Font font : this.availableFonts) {
            Glyph glyph = font.getGlyph(codePoint);
            if (glyph == null) continue;
            return glyph.bake(this::bake);
        }
        LOGGER.warn("Couldn't find glyph for character {} (\\u{})", (Object)Character.toString(codePoint), (Object)String.format("%04x", codePoint));
        return this.blankBakedGlyph;
    }

    public BakedGlyph getBaked(int codePoint) {
        return this.bakedGlyphCache.computeIfAbsent(codePoint, this.glyphBaker);
    }

    private BakedGlyph bake(RenderableGlyph c) {
        for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
            BakedGlyph bakedGlyph = glyphAtlasTexture.bake(c);
            if (bakedGlyph == null) continue;
            return bakedGlyph;
        }
        Identifier identifier = this.id.withSuffixedPath("/" + this.glyphAtlases.size());
        boolean bl = c.hasColor();
        TextRenderLayerSet textRenderLayerSet = bl ? TextRenderLayerSet.of(identifier) : TextRenderLayerSet.ofIntensity(identifier);
        GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(identifier::toString, textRenderLayerSet, bl);
        this.glyphAtlases.add(glyphAtlasTexture2);
        this.textureManager.registerTexture(identifier, glyphAtlasTexture2);
        BakedGlyph bakedGlyph2 = glyphAtlasTexture2.bake(c);
        return bakedGlyph2 == null ? this.blankBakedGlyph : bakedGlyph2;
    }

    public BakedGlyph getObfuscatedBakedGlyph(Glyph glyph) {
        IntList intList = (IntList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
        if (intList != null && !intList.isEmpty()) {
            return this.getBaked(intList.getInt(RANDOM.nextInt(intList.size())));
        }
        return this.blankBakedGlyph;
    }

    public Identifier getId() {
        return this.id;
    }

    public BakedGlyph getRectangleBakedGlyph() {
        return this.whiteRectangleBakedGlyph;
    }

    @Environment(value=EnvType.CLIENT)
    record GlyphPair(Glyph glyph, Glyph advanceValidatedGlyph) {
        final static GlyphPair MISSING = new GlyphPair(BuiltinEmptyGlyph.MISSING, BuiltinEmptyGlyph.MISSING);

        Glyph getGlyph(boolean validateAdvance) {
            return validateAdvance ? this.advanceValidatedGlyph : this.glyph;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GlyphPair.class, "glyphInfo;glyphInfoNotFishy", "glyph", "advanceValidatedGlyph"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GlyphPair.class, "glyphInfo;glyphInfoNotFishy", "glyph", "advanceValidatedGlyph"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GlyphPair.class, "glyphInfo;glyphInfoNotFishy", "glyph", "advanceValidatedGlyph"}, this, object);
        }
    }
}

