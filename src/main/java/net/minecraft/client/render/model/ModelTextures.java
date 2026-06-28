/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelTextures {
    final static public ModelTextures EMPTY = new ModelTextures(Map.of());
    final static private char TEXTURE_REFERENCE_PREFIX = '#';
    final private Map<String, SpriteIdentifier> textures;

    ModelTextures(Map<String, SpriteIdentifier> textures) {
        this.textures = textures;
    }

    @Nullable
    public SpriteIdentifier get(String textureId) {
        if (ModelTextures.isTextureReference(textureId)) {
            textureId = textureId.substring(1);
        }
        return this.textures.get(textureId);
    }

    private static boolean isTextureReference(String textureId) {
        return textureId.charAt(0) == '#';
    }

    public static Textures fromJson(JsonObject json, Identifier atlasTexture) {
        Textures.Builder builder = new Textures.Builder();
        for (Map.Entry entry : json.entrySet()) {
            ModelTextures.add(atlasTexture, (String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString(), builder);
        }
        return builder.build();
    }

    private static void add(Identifier atlasTexture, String textureId, String value, Textures.Builder builder) {
        if (ModelTextures.isTextureReference(value)) {
            builder.addTextureReference(textureId, value.substring(1));
        } else {
            Identifier identifier = Identifier.tryParse(value);
            if (identifier == null) {
                throw new JsonParseException(value + " is not valid resource location");
            }
            builder.addSprite(textureId, new SpriteIdentifier(atlasTexture, identifier));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Textures
    extends Record {
        final Map<String, Entry> values;
        final static public Textures EMPTY = new Textures(Map.of());

        public Textures(Map<String, Entry> map) {
            this.values = map;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Textures.class, "values", "values"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Textures.class, "values", "values"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Textures.class, "values", "values"}, this, object);
        }

        public Map<String, Entry> values() {
            return this.values;
        }

        @Environment(value=EnvType.CLIENT)
        public static class Builder {
            final private Map<String, Entry> entries = new HashMap<String, Entry>();

            public Builder addTextureReference(String textureId, String target) {
                this.entries.put(textureId, new TextureReferenceEntry(target));
                return this;
            }

            public Builder addSprite(String textureId, SpriteIdentifier spriteId) {
                this.entries.put(textureId, new SpriteEntry(spriteId));
                return this;
            }

            public Textures build() {
                if (this.entries.isEmpty()) {
                    return EMPTY;
                }
                return new Textures(Map.copyOf(this.entries));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final static private Logger LOGGER = LogUtils.getLogger();
        final private List<Textures> textures = new ArrayList<Textures>();

        public Builder addLast(Textures textures) {
            this.textures.addLast(textures);
            return this;
        }

        public Builder addFirst(Textures textures) {
            this.textures.addFirst(textures);
            return this;
        }

        public ModelTextures build(SimpleModel modelNameSupplier) {
            if (this.textures.isEmpty()) {
                return EMPTY;
            }
            Object2ObjectArrayMap object2ObjectMap = new Object2ObjectArrayMap();
            Object2ObjectArrayMap object2ObjectMap2 = new Object2ObjectArrayMap();
            for (Textures textures : Lists.reverse(this.textures)) {
                textures.values.forEach((arg_0, arg_1) -> Builder.method_65552((Object2ObjectMap)object2ObjectMap2, (Object2ObjectMap)object2ObjectMap, arg_0, arg_1));
            }
            if (object2ObjectMap2.isEmpty()) {
                return new ModelTextures((Map<String, SpriteIdentifier>)object2ObjectMap);
            }
            boolean bl = true;
            while (bl) {
                bl = false;
                ObjectIterator objectIterator = Object2ObjectMaps.fastIterator((Object2ObjectMap)object2ObjectMap2);
                while (objectIterator.hasNext()) {
                    Object2ObjectMap.Entry entry2 = (Object2ObjectMap.Entry)objectIterator.next();
                    SpriteIdentifier spriteIdentifier = (SpriteIdentifier)object2ObjectMap.get((Object)((TextureReferenceEntry)entry2.getValue()).target);
                    if (spriteIdentifier == null) continue;
                    object2ObjectMap.put((Object)((String)entry2.getKey()), (Object)spriteIdentifier);
                    objectIterator.remove();
                    bl = true;
                }
            }
            if (!object2ObjectMap2.isEmpty()) {
                LOGGER.warn("Unresolved texture references in {}:\n{}", (Object)modelNameSupplier.name(), (Object)object2ObjectMap2.entrySet().stream().map(entry -> "\t#" + (String)entry.getKey() + "-> #" + ((TextureReferenceEntry)entry.getValue()).target + "\n").collect(Collectors.joining()));
            }
            return new ModelTextures((Map<String, SpriteIdentifier>)object2ObjectMap);
        }

        private static void method_65552(Object2ObjectMap object2ObjectMap, Object2ObjectMap object2ObjectMap2, String textureId, Entry entry) {
            Entry entry2 = entry;
            Objects.requireNonNull(entry2);
            Entry entry22 = entry2;
            int i = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SpriteEntry.class, TextureReferenceEntry.class}, (Object)entry22, i)) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: {
                    SpriteEntry spriteEntry = (SpriteEntry)entry22;
                    object2ObjectMap.remove((Object)textureId);
                    object2ObjectMap2.put((Object)textureId, (Object)spriteEntry.material());
                    break;
                }
                case 1: {
                    TextureReferenceEntry textureReferenceEntry = (TextureReferenceEntry)entry22;
                    object2ObjectMap2.remove((Object)textureId);
                    object2ObjectMap.put((Object)textureId, (Object)textureReferenceEntry);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class TextureReferenceEntry
    extends Record
    implements Entry {
        final String target;

        TextureReferenceEntry(String string) {
            this.target = string;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TextureReferenceEntry.class, "target", "target"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TextureReferenceEntry.class, "target", "target"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TextureReferenceEntry.class, "target", "target"}, this, object);
        }

        public String target() {
            return this.target;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record SpriteEntry(SpriteIdentifier material) implements Entry
    {
    }

    @Environment(value=EnvType.CLIENT)
    public static sealed interface Entry
    permits SpriteEntry, TextureReferenceEntry {
    }
}

