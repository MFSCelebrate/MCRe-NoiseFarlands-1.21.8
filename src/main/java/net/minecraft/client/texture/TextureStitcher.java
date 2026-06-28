/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextureStitcher<T extends Stitchable> {
    final static private Comparator<Holder<?>> COMPARATOR = Comparator.comparing(holder -> -holder.height).thenComparing(holder -> -holder.width).thenComparing(holder -> holder.sprite.getId());
    final private int mipLevel;
    final private List<Holder<T>> holders = new ArrayList<Holder<T>>();
    final private List<Slot<T>> slots = new ArrayList<Slot<T>>();
    private int width;
    private int height;
    final private int maxWidth;
    final private int maxHeight;

    public TextureStitcher(int maxWidth, int maxHeight, int mipLevel) {
        this.mipLevel = mipLevel;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void add(T info) {
        Holder<T> holder = new Holder<T>(info, this.mipLevel);
        this.holders.add(holder);
    }

    public void stitch() {
        ArrayList<Holder<T>> list = new ArrayList<Holder<T>>(this.holders);
        list.sort(COMPARATOR);
        for (Holder holder2 : list) {
            if (this.fit(holder2)) continue;
            throw new TextureStitcherCannotFitException((Stitchable)holder2.sprite, (Collection)list.stream().map(holder -> holder.sprite).collect(ImmutableList.toImmutableList()));
        }
    }

    public void getStitchedSprites(SpriteConsumer<T> consumer) {
        for (Slot<T> slot : this.slots) {
            slot.addAllFilledSlots(consumer);
        }
    }

    static int applyMipLevel(int size, int mipLevel) {
        return (size >> mipLevel) + ((size & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
    }

    private boolean fit(Holder<T> holder) {
        for (Slot<T> slot : this.slots) {
            if (!slot.fit(holder)) continue;
            return true;
        }
        return this.growAndFit(holder);
    }

    private boolean growAndFit(Holder<T> holder) {
        Slot<T> slot;
        boolean bl5;
        boolean bl4;
        boolean bl2;
        int i = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        int j = MathHelper.smallestEncompassingPowerOfTwo(this.height);
        int k = MathHelper.smallestEncompassingPowerOfTwo(this.width + holder.width);
        int l = MathHelper.smallestEncompassingPowerOfTwo(this.height + holder.height);
        boolean bl = k <= this.maxWidth;
        boolean bl3 = bl2 = l <= this.maxHeight;
        if (!bl && !bl2) {
            return false;
        }
        boolean bl32 = bl && i != k;
        boolean bl6 = bl4 = bl2 && j != l;
        if (bl32 ^ bl4) {
            bl5 = bl32;
        } else {
            boolean bl7 = bl5 = bl && i <= j;
        }
        if (bl5) {
            if (this.height == 0) {
                this.height = l;
            }
            slot = new Slot(this.width, 0, k - this.width, this.height);
            this.width = k;
        } else {
            slot = new Slot<T>(0, this.height, this.width, l - this.height);
            this.height = l;
        }
        slot.fit(holder);
        this.slots.add(slot);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static final class Holder<T extends Stitchable>
    extends Record {
        final T sprite;
        final int width;
        final int height;

        public Holder(T sprite, int mipLevel) {
            this(sprite, TextureStitcher.applyMipLevel(sprite.getWidth(), mipLevel), TextureStitcher.applyMipLevel(sprite.getHeight(), mipLevel));
        }

        private Holder(T stitchable, int i, int j) {
            this.sprite = stitchable;
            this.width = i;
            this.height = j;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Holder.class, "entry;width;height", "sprite", "width", "height"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Holder.class, "entry;width;height", "sprite", "width", "height"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Holder.class, "entry;width;height", "sprite", "width", "height"}, this, object);
        }

        public T sprite() {
            return this.sprite;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Stitchable {
        public int getWidth();

        public int getHeight();

        public Identifier getId();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Slot<T extends Stitchable> {
        final private int x;
        final private int y;
        final private int width;
        final private int height;
        @Nullable
        private List<Slot<T>> subSlots;
        @Nullable
        private Holder<T> texture;

        public Slot(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean fit(Holder<T> holder) {
            if (this.texture != null) {
                return false;
            }
            int i = holder.width;
            int j = holder.height;
            if (i > this.width || j > this.height) {
                return false;
            }
            if (i == this.width && j == this.height) {
                this.texture = holder;
                return true;
            }
            if (this.subSlots == null) {
                this.subSlots = new ArrayList<Slot<T>>(1);
                this.subSlots.add(new Slot<T>(this.x, this.y, i, j));
                int k = this.width - i;
                int l = this.height - j;
                if (l > 0 && k > 0) {
                    int n;
                    int m = Math.max(this.height, k);
                    if (m >= (n = Math.max(this.width, l))) {
                        this.subSlots.add(new Slot<T>(this.x, this.y + j, i, l));
                        this.subSlots.add(new Slot<T>(this.x + i, this.y, k, this.height));
                    } else {
                        this.subSlots.add(new Slot<T>(this.x + i, this.y, k, j));
                        this.subSlots.add(new Slot<T>(this.x, this.y + j, this.width, l));
                    }
                } else if (k == 0) {
                    this.subSlots.add(new Slot<T>(this.x, this.y + j, i, l));
                } else if (l == 0) {
                    this.subSlots.add(new Slot<T>(this.x + i, this.y, k, j));
                }
            }
            for (Slot<T> slot : this.subSlots) {
                if (!slot.fit(holder)) continue;
                return true;
            }
            return false;
        }

        public void addAllFilledSlots(SpriteConsumer<T> consumer) {
            if (this.texture != null) {
                consumer.load(this.texture.sprite, this.getX(), this.getY());
            } else if (this.subSlots != null) {
                for (Slot slot : this.subSlots) {
                    slot.addAllFilledSlots(consumer);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.x + ", originY=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture=" + String.valueOf(this.texture) + ", subSlots=" + String.valueOf(this.subSlots) + "}";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteConsumer<T extends Stitchable> {
        public void load(T var1, int var2, int var3);
    }
}

