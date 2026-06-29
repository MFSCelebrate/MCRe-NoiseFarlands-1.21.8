/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextCollector {
    final private List<StringVisitable> texts = Lists.newArrayList();

    public void add(StringVisitable text) {
        this.texts.add(text);
    }

    @Nullable
    public StringVisitable getRawCombined() {
        if (this.texts.isEmpty()) {
            return null;
        }
        if (this.texts.size() == 1) {
            return this.texts.get(0);
        }
        return StringVisitable.concat(this.texts);
    }

    public StringVisitable getCombined() {
        StringVisitable stringVisitable = this.getRawCombined();
        return stringVisitable != null ? stringVisitable : StringVisitable.EMPTY;
    }

    public void clear() {
        this.texts.clear();
    }
}

