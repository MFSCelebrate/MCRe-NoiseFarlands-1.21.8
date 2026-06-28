/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource.language;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.ReorderingUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.DeprecatedLanguageData;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TranslationStorage
extends Language {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Map<String, String> translations;
    final private boolean rightToLeft;

    private TranslationStorage(Map<String, String> translations, boolean rightToLeft) {
        this.translations = translations;
        this.rightToLeft = rightToLeft;
    }

    public static TranslationStorage load(ResourceManager resourceManager, List<String> definitions, boolean rightToLeft) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (String string : definitions) {
            String string2 = String.format(Locale.ROOT, "lang/%s.json", string);
            for (String string3 : resourceManager.getAllNamespaces()) {
                try {
                    Identifier identifier = Identifier.of(string3, string2);
                    TranslationStorage.load(string, resourceManager.getAllResources(identifier), map);
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{string3, string2, exception.toString()});
                }
            }
        }
        DeprecatedLanguageData.create().apply(map);
        return new TranslationStorage(Map.copyOf(map), rightToLeft);
    }

    private static void load(String langCode, List<Resource> resourceRefs, Map<String, String> translations) {
        for (Resource resource : resourceRefs) {
            try {
                InputStream inputStream = resource.getInputStream();
                try {
                    Language.load(inputStream, translations::put);
                    if (inputStream == null) continue;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputStream.close();
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to load translations for {} from pack {}", new Object[]{langCode, resource.getPackId(), iOException});
            }
        }
    }

    @Override
    public String get(String key, String fallback) {
        return this.translations.getOrDefault(key, fallback);
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.translations.containsKey(key);
    }

    @Override
    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    @Override
    public OrderedText reorder(StringVisitable text) {
        return ReorderingUtil.reorder(text, this.rightToLeft);
    }
}

