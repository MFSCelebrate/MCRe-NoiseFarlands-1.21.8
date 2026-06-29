/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontManager
implements ResourceReloader,
AutoCloseable {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private String FONTS_JSON = "fonts.json";
    final static public Identifier MISSING_STORAGE_ID = Identifier.ofVanilla("missing");
    final static private ResourceFinder FINDER = ResourceFinder.json("font");
    final static private Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    final private FontStorage missingStorage;
    final private List<Font> fonts = new ArrayList<Font>();
    final private Map<Identifier, FontStorage> fontStorages = new HashMap<Identifier, FontStorage>();
    final private TextureManager textureManager;
    @Nullable
    private volatile FontStorage currentStorage;

    public FontManager(TextureManager manager) {
        this.textureManager = manager;
        this.missingStorage = Util.make(new FontStorage(manager, MISSING_STORAGE_ID), fontStorage -> fontStorage.setFonts(List.of(FontManager.createEmptyFont()), Set.of()));
    }

    private static Font.FontFilterPair createEmptyFont() {
        return new Font.FontFilterPair(new BlankFont(), FontFilterType.FilterMap.NO_FILTER);
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
        return ((CompletableFuture)this.loadIndex(resourceManager, executor).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(index -> this.reload((ProviderIndex)index, Profilers.get()), executor2);
    }

    private CompletableFuture<ProviderIndex> loadIndex(ResourceManager resourceManager, Executor executor) {
        ArrayList<CompletableFuture<FontEntry>> list = new ArrayList<CompletableFuture<FontEntry>>();
        for (Map.Entry<Identifier, List<Resource>> entry : FINDER.findAllResources(resourceManager).entrySet()) {
            Identifier identifier = FINDER.toResourceId(entry.getKey());
            list.add(CompletableFuture.supplyAsync(() -> {
                List<Pair<FontKey, FontLoader.Provider>> list = FontManager.loadFontProviders((List)entry.getValue(), identifier);
                FontEntry fontEntry = new FontEntry(identifier);
                for (Pair<FontKey, FontLoader.Provider> pair : list) {
                    FontKey fontKey = (FontKey)pair.getFirst();
                    FontFilterType.FilterMap filterMap = ((FontLoader.Provider)pair.getSecond()).filter();
                    ((FontLoader.Provider)pair.getSecond()).definition().build().ifLeft(loadable -> {
                        CompletableFuture<Optional<Font>> completableFuture = this.load(fontKey, (FontLoader.Loadable)loadable, resourceManager, executor);
                        fontEntry.addBuilder(fontKey, filterMap, completableFuture);
                    }).ifRight(reference -> fontEntry.addReferenceBuilder(fontKey, filterMap, (FontLoader.Reference)reference));
                }
                return fontEntry;
            }, executor));
        }
        return Util.combineSafe(list).thenCompose(entries -> {
            List list = entries.stream().flatMap(FontEntry::getImmediateProviders).collect(Util.toArrayList());
            Font.FontFilterPair fontFilterPair = FontManager.createEmptyFont();
            list.add(CompletableFuture.completedFuture(Optional.of(fontFilterPair.provider())));
            return Util.combineSafe(list).thenCompose(providers -> {
                Map<Identifier, List<Font.FontFilterPair>> map = this.getRequiredFontProviders((List<FontEntry>)entries);
                CompletableFuture[] completableFutures = (CompletableFuture[])map.values().stream().map(dest -> CompletableFuture.runAsync(() -> this.insertFont((List<Font.FontFilterPair>)dest, fontFilterPair), executor)).toArray(CompletableFuture[]::new);
                return CompletableFuture.allOf(completableFutures).thenApply(ignored -> {
                    List<Font> list2 = providers.stream().flatMap(Optional::stream).toList();
                    return new ProviderIndex(map, list2);
                });
            });
        });
    }

    private CompletableFuture<Optional<Font>> load(FontKey key, FontLoader.Loadable loadable, ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(loadable.load(resourceManager));
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load builder {}, rejecting", (Object)key, (Object)exception);
                return Optional.empty();
            }
        }, executor);
    }

    private Map<Identifier, List<Font.FontFilterPair>> getRequiredFontProviders(List<FontEntry> entries) {
        HashMap<Identifier, List<Font.FontFilterPair>> map = new HashMap<Identifier, List<Font.FontFilterPair>>();
        DependencyTracker<Identifier, FontEntry> dependencyTracker = new DependencyTracker<Identifier, FontEntry>();
        entries.forEach(entry -> dependencyTracker.add(entry.fontId, (FontEntry)entry));
        dependencyTracker.traverse((dependent, fontEntry) -> fontEntry.getRequiredFontProviders(map::get).ifPresent(fonts -> map.put((Identifier)dependent, (List<Font.FontFilterPair>)fonts)));
        return map;
    }

    private void insertFont(List<Font.FontFilterPair> fonts, Font.FontFilterPair font) {
        fonts.add(0, font);
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (Font.FontFilterPair fontFilterPair : fonts) {
            intSet.addAll((IntCollection)fontFilterPair.provider().getProvidedGlyphs());
        }
        intSet.forEach(codePoint -> {
            Font.FontFilterPair fontFilterPair;
            if (codePoint == 32) {
                return;
            }
            Iterator iterator = Lists.reverse((List)fonts).iterator();
            while (iterator.hasNext() && (fontFilterPair = (Font.FontFilterPair)iterator.next()).provider().getGlyph(codePoint) == null) {
            }
        });
    }

    private static Set<FontFilterType> getActiveFilters(GameOptions options) {
        EnumSet<FontFilterType> set = EnumSet.noneOf(FontFilterType.class);
        if (options.getForceUnicodeFont().getValue().booleanValue()) {
            set.add(FontFilterType.UNIFORM);
        }
        if (options.getJapaneseGlyphVariants().getValue().booleanValue()) {
            set.add(FontFilterType.JAPANESE_VARIANTS);
        }
        return set;
    }

    private void reload(ProviderIndex index, Profiler profiler) {
        profiler.push("closing");
        this.currentStorage = null;
        this.fontStorages.values().forEach(FontStorage::close);
        this.fontStorages.clear();
        this.fonts.forEach(Font::close);
        this.fonts.clear();
        Set<FontFilterType> set = FontManager.getActiveFilters(MinecraftClient.getInstance().options);
        profiler.swap("reloading");
        index.fontSets().forEach((id, fonts) -> {
            FontStorage fontStorage = new FontStorage(this.textureManager, (Identifier)id);
            fontStorage.setFonts(Lists.reverse((List)fonts), set);
            this.fontStorages.put((Identifier)id, fontStorage);
        });
        this.fonts.addAll(index.allProviders);
        profiler.pop();
        if (!this.fontStorages.containsKey(MinecraftClient.DEFAULT_FONT_ID)) {
            throw new IllegalStateException("Default font failed to load");
        }
    }

    public void setActiveFilters(GameOptions options) {
        Set<FontFilterType> set = FontManager.getActiveFilters(options);
        for (FontStorage fontStorage : this.fontStorages.values()) {
            fontStorage.setActiveFilters(set);
        }
    }

    private static List<Pair<FontKey, FontLoader.Provider>> loadFontProviders(List<Resource> fontResources, Identifier id) {
        ArrayList<Pair<FontKey, FontLoader.Provider>> list = new ArrayList<Pair<FontKey, FontLoader.Provider>>();
        for (Resource resource : fontResources) {
            try {
                BufferedReader reader = resource.getReader();
                try {
                    JsonElement jsonElement = (JsonElement)GSON.fromJson((Reader)reader, JsonElement.class);
                    Providers providers = (Providers)Providers.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonParseException::new);
                    List<FontLoader.Provider> list2 = providers.providers;
                    for (int i = list2.size() - 1; i >= 0; --i) {
                        FontKey fontKey = new FontKey(id, resource.getPackId(), i);
                        list.add((Pair<FontKey, FontLoader.Provider>)Pair.of((Object)fontKey, (Object)list2.get(i)));
                    }
                    if (reader == null) continue;
                }
                catch (Throwable throwable) {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ((Reader)reader).close();
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{id, FONTS_JSON, resource.getPackId(), exception});
            }
        }
        return list;
    }

    public TextRenderer createTextRenderer() {
        return new TextRenderer(this::getStorage, false);
    }

    public TextRenderer createAdvanceValidatingTextRenderer() {
        return new TextRenderer(this::getStorage, true);
    }

    private FontStorage getStorageInternal(Identifier id) {
        return this.fontStorages.getOrDefault(id, this.missingStorage);
    }

    private FontStorage getStorage(Identifier id) {
        FontStorage fontStorage2;
        FontStorage fontStorage = this.currentStorage;
        if (fontStorage != null && id.equals(fontStorage.getId())) {
            return fontStorage;
        }
        this.currentStorage = fontStorage2 = this.getStorageInternal(id);
        return fontStorage2;
    }

    @Override
    public void close() {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fonts.forEach(Font::close);
        this.missingStorage.close();
    }

    @Environment(value=EnvType.CLIENT)
    record FontKey(Identifier fontId, String pack, int index) {
        @Override
        public String toString() {
            return "(" + String.valueOf(this.fontId) + ": builder #" + this.index + " from pack " + this.pack + ")";
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class ProviderIndex
    extends Record {
        final private Map<Identifier, List<Font.FontFilterPair>> fontSets;
        final List<Font> allProviders;

        ProviderIndex(Map<Identifier, List<Font.FontFilterPair>> map, List<Font> list) {
            this.fontSets = map;
            this.allProviders = list;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProviderIndex.class, "fontSets;allProviders", "fontSets", "allProviders"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProviderIndex.class, "fontSets;allProviders", "fontSets", "allProviders"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProviderIndex.class, "fontSets;allProviders", "fontSets", "allProviders"}, this, object);
        }

        public Map<Identifier, List<Font.FontFilterPair>> fontSets() {
            return this.fontSets;
        }

        public List<Font> allProviders() {
            return this.allProviders;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Providers
    extends Record {
        final List<FontLoader.Provider> providers;
        final static public Codec<Providers> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)FontLoader.Provider.CODEC.listOf().fieldOf("providers").forGetter(Providers::providers)).apply((Applicative)instance, Providers::new));

        private Providers(List<FontLoader.Provider> list) {
            this.providers = list;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Providers.class, "providers", "providers"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Providers.class, "providers", "providers"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Providers.class, "providers", "providers"}, this, object);
        }

        public List<FontLoader.Provider> providers() {
            return this.providers;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class FontEntry
    extends Record
    implements DependencyTracker.Dependencies<Identifier> {
        final Identifier fontId;
        final private List<Builder> builders;
        final private Set<Identifier> dependencies;

        public FontEntry(Identifier fontId) {
            this(fontId, new ArrayList<Builder>(), new HashSet<Identifier>());
        }

        private FontEntry(Identifier identifier, List<Builder> list, Set<Identifier> set) {
            this.fontId = identifier;
            this.builders = list;
            this.dependencies = set;
        }

        public void addReferenceBuilder(FontKey key, FontFilterType.FilterMap filters, FontLoader.Reference reference) {
            this.builders.add(new Builder(key, filters, (Either<CompletableFuture<Optional<Font>>, Identifier>)Either.right((Object)reference.id())));
            this.dependencies.add(reference.id());
        }

        public void addBuilder(FontKey key, FontFilterType.FilterMap filters, CompletableFuture<Optional<Font>> fontFuture) {
            this.builders.add(new Builder(key, filters, (Either<CompletableFuture<Optional<Font>>, Identifier>)Either.left(fontFuture)));
        }

        private Stream<CompletableFuture<Optional<Font>>> getImmediateProviders() {
            return this.builders.stream().flatMap(builder -> builder.result.left().stream());
        }

        public Optional<List<Font.FontFilterPair>> getRequiredFontProviders(Function<Identifier, List<Font.FontFilterPair>> fontRetriever) {
            ArrayList list = new ArrayList();
            for (Builder builder : this.builders) {
                Optional<List<Font.FontFilterPair>> optional = builder.build(fontRetriever);
                if (optional.isPresent()) {
                    list.addAll(optional.get());
                    continue;
                }
                return Optional.empty();
            }
            return Optional.of(list);
        }

        @Override
        public void forDependencies(Consumer<Identifier> callback) {
            this.dependencies.forEach(callback);
        }

        @Override
        public void forOptionalDependencies(Consumer<Identifier> callback) {
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this, object);
        }

        public Identifier fontId() {
            return this.fontId;
        }

        public List<Builder> builders() {
            return this.builders;
        }

        public Set<Identifier> dependencies() {
            return this.dependencies;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Builder
    extends Record {
        final private FontKey id;
        final private FontFilterType.FilterMap filter;
        final Either<CompletableFuture<Optional<Font>>, Identifier> result;

        Builder(FontKey fontKey, FontFilterType.FilterMap filterMap, Either<CompletableFuture<Optional<Font>>, Identifier> either) {
            this.id = fontKey;
            this.filter = filterMap;
            this.result = either;
        }

        public Optional<List<Font.FontFilterPair>> build(Function<Identifier, List<Font.FontFilterPair>> fontRetriever) {
            return (Optional)this.result.map(future -> ((Optional)future.join()).map(font -> List.of(new Font.FontFilterPair((Font)font, this.filter))), referee -> {
                List list = (List)fontRetriever.apply((Identifier)referee);
                if (list == null) {
                    LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", referee, (Object)this.id);
                    return Optional.empty();
                }
                return Optional.of(list.stream().map(this::applyFilter).toList());
            });
        }

        private Font.FontFilterPair applyFilter(Font.FontFilterPair font) {
            return new Font.FontFilterPair(font.provider(), this.filter.apply(font.filter()));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Builder.class, "id;filter;result", "id", "filter", "result"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Builder.class, "id;filter;result", "id", "filter", "result"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Builder.class, "id;filter;result", "id", "filter", "result"}, this, object);
        }

        public FontKey id() {
            return this.id;
        }

        public FontFilterType.FilterMap filter() {
            return this.filter;
        }

        public Either<CompletableFuture<Optional<Font>>, Identifier> result() {
            return this.result;
        }
    }
}

