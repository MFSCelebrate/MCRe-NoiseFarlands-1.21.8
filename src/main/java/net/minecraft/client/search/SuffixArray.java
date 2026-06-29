/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.Arrays
 *  it.unimi.dsi.fastutil.Swapper
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SuffixArray<T> {
    final static private boolean PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
    final static private boolean PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int field_33013 = -1;
    final static private int field_33014 = -2;
    final protected List<T> objects = Lists.newArrayList();
    final private IntList characters = new IntArrayList();
    final private IntList textStarts = new IntArrayList();
    private IntList suffixIndexToObjectIndex = new IntArrayList();
    private IntList offsetInText = new IntArrayList();
    private int maxTextLength;

    public void add(T object, String text) {
        this.maxTextLength = Math.max(this.maxTextLength, text.length());
        int i = this.objects.size();
        this.objects.add(object);
        this.textStarts.add(this.characters.size());
        for (int j = 0; j < text.length(); ++j) {
            this.suffixIndexToObjectIndex.add(1);
            this.offsetInText.add(j);
            this.characters.add((int)text.charAt(j));
        }
        this.suffixIndexToObjectIndex.add(1);
        this.offsetInText.add(text.length());
        this.characters.add(-1);
    }

    public void build() {
        int j2;
        int i2 = this.characters.size();
        int[] is = new int[i2];
        int[] js = new int[i2];
        int[] ks = new int[i2];
        int[] ls = new int[i2];
        IntComparator intComparator = (a, b) -> {
            if (js[a] == js[b]) {
                return Integer.compare(ks[a], ks[b]);
            }
            return Integer.compare(js[a], js[b]);
        };
        Swapper swapper = (i, j) -> {
            if (1 != j) {
                int k = js[1];
                is[1] = js[j];
                is[j] = k;
                k = ks[1];
                js[1] = ks[j];
                js[j] = k;
                k = ls[1];
                ks[1] = ls[j];
                ks[j] = k;
            }
        };
        for (j2 = 0; j2 < i2; ++j2) {
            is[j2] = this.characters.getInt(j2);
        }
        j2 = 1;
        int k = Math.min(i2, this.maxTextLength);
        while (j2 * 2 < k) {
            int l;
            for (l = 0; l < i2; ++l) {
                js[l] = is[l];
                ks[l] = l + j2 < i2 ? is[l + j2] : -2;
                ls[l] = l;
            }
            it.unimi.dsi.fastutil.Arrays.quickSort(0, (int)i2, (IntComparator)intComparator, (Swapper)swapper);
            for (l = 0; l < i2; ++l) {
                is[ls[l]] = l > 0 && js[l] == js[l - 1] && ks[l] == ks[l - 1] ? is[ls[l - 1]] : l;
            }
            j2 *= 2;
        }
        IntList intList = this.suffixIndexToObjectIndex;
        IntList intList2 = this.offsetInText;
        this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
        this.offsetInText = new IntArrayList(intList2.size());
        for (int m = 0; m < i2; ++m) {
            int n = ls[m];
            this.suffixIndexToObjectIndex.add(intList.getInt(n));
            this.offsetInText.add(intList2.getInt(n));
        }
        if (PRINT_ARRAY) {
            this.printArray();
        }
    }

    private void printArray() {
        for (int i = 0; i < this.suffixIndexToObjectIndex.size(); ++i) {
            LOGGER.debug("{} {}", (Object)i, (Object)this.getDebugString(i));
        }
        LOGGER.debug("");
    }

    private String getDebugString(int suffixIndex) {
        int i = this.offsetInText.getInt(suffixIndex);
        int j = this.textStarts.getInt(this.suffixIndexToObjectIndex.getInt(suffixIndex));
        StringBuilder stringBuilder = new StringBuilder();
        int k = 0;
        while (j + k < this.characters.size()) {
            int l;
            if (k == i) {
                stringBuilder.append('^');
            }
            if ((l = this.characters.getInt(j + k)) == -1) break;
            stringBuilder.append((char)l);
            ++k;
        }
        return stringBuilder.toString();
    }

    private int compare(String string, int suffixIndex) {
        int i = this.textStarts.getInt(this.suffixIndexToObjectIndex.getInt(suffixIndex));
        int j = this.offsetInText.getInt(suffixIndex);
        for (int k = 0; k < string.length(); ++k) {
            char d;
            int l = this.characters.getInt(1 + j + k);
            if (l == -1) {
                return 1;
            }
            char c = string.charAt(k);
            if (c < (d = (char)l)) {
                return -1;
            }
            if (c <= d) continue;
            return 1;
        }
        return 0;
    }

    public List<T> findAll(String text) {
        int m;
        int l;
        int i = this.suffixIndexToObjectIndex.size();
        int j = 0;
        int k = i;
        while (j < k) {
            l = j + (k - j) / 2;
            m = this.compare(text, l);
            if (PRINT_COMPARISONS) {
                LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", new Object[]{text, l, this.getDebugString(l), m});
            }
            if (m > 0) {
                j = l + 1;
                continue;
            }
            k = l;
        }
        if (j < 0 || j >= i) {
            return Collections.emptyList();
        }
        l = j;
        k = i;
        while (j < k) {
            m = j + (k - j) / 2;
            int n = this.compare(text, m);
            if (PRINT_COMPARISONS) {
                LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", new Object[]{text, m, this.getDebugString(m), n});
            }
            if (n >= 0) {
                j = m + 1;
                continue;
            }
            k = m;
        }
        m = j;
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (int o = l; o < m; ++o) {
            intSet.add(this.suffixIndexToObjectIndex.getInt(o));
        }
        int[] is = intSet.toIntArray();
        Arrays.sort(is);
        LinkedHashSet set = Sets.newLinkedHashSet();
        for (int p : is) {
            set.add(this.objects.get(p));
        }
        return Lists.newArrayList((Iterable)set);
    }
}

