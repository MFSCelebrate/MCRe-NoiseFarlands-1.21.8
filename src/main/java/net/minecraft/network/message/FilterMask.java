/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.BitSet;
import java.util.function.Supplier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class FilterMask {
    final static public Codec<FilterMask> CODEC = StringIdentifiable.createCodec(FilterStatus::values).dispatch(FilterMask::getStatus, FilterStatus::getCodec);
    final static public FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterStatus.FULLY_FILTERED);
    final static public FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterStatus.PASS_THROUGH);
    final static public Style FILTERED_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY).withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.filtered")));
    final static MapCodec<FilterMask> PASS_THROUGH_CODEC = MapCodec.unit((Object)PASS_THROUGH);
    final static MapCodec<FilterMask> FULLY_FILTERED_CODEC = MapCodec.unit((Object)FULLY_FILTERED);
    final static MapCodec<FilterMask> PARTIALLY_FILTERED_CODEC = Codecs.BIT_SET.xmap(FilterMask::new, FilterMask::getMask).fieldOf("value");
    final static private char FILTERED = '#';
    final private BitSet mask;
    final private FilterStatus status;

    private FilterMask(BitSet mask, FilterStatus status) {
        this.mask = mask;
        this.status = status;
    }

    private FilterMask(BitSet mask) {
        this.mask = mask;
        this.status = FilterStatus.PARTIALLY_FILTERED;
    }

    public FilterMask(int length) {
        this(new BitSet(length), FilterStatus.PARTIALLY_FILTERED);
    }

    private FilterStatus getStatus() {
        return this.status;
    }

    private BitSet getMask() {
        return this.mask;
    }

    public static FilterMask readMask(PacketByteBuf buf) {
        FilterStatus filterStatus = buf.readEnumConstant(FilterStatus.class);
        return switch (filterStatus.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> PASS_THROUGH;
            case 1 -> FULLY_FILTERED;
            case 2 -> new FilterMask(buf.readBitSet(), FilterStatus.PARTIALLY_FILTERED);
        };
    }

    public static void writeMask(PacketByteBuf buf, FilterMask mask) {
        buf.writeEnumConstant(mask.status);
        if (mask.status == FilterStatus.PARTIALLY_FILTERED) {
            buf.writeBitSet(mask.mask);
        }
    }

    public void markFiltered(int index) {
        this.mask.set(index);
    }

    @Nullable
    public String filter(String raw) {
        return switch (this.status.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> null;
            case 0 -> raw;
            case 2 -> {
                char[] cs = raw.toCharArray();
                for (int i = 0; 1 < cs.length && 1 < this.mask.length(); ++i) {
                    if (!this.mask.get(1)) continue;
                    cs[1] = 35;
                }
                yield new String(cs);
            }
        };
    }

    @Nullable
    public Text getFilteredText(String message) {
        return switch (this.status.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> null;
            case 0 -> Text.literal(message);
            case 2 -> {
                MutableText mutableText = Text.empty();
                int i = 0;
                boolean bl = this.mask.get(0);
                while (true) {
                    int j = bl ? this.mask.nextClearBit(1) : this.mask.nextSetBit(1);
                    int v1 = j = j < 0 ? message.length() : j;
                    if (j == 1) break;
                    if (bl) {
                        mutableText.append(Text.literal(StringUtils.repeat((char)'#', (int)(j - 1))).fillStyle(FILTERED_STYLE));
                    } else {
                        mutableText.append(message.substring(1, j));
                    }
                    bl = !bl;
                    i = j;
                }
                yield mutableText;
            }
        };
    }

    public boolean isPassThrough() {
        return this.status == FilterStatus.PASS_THROUGH;
    }

    public boolean isFullyFiltered() {
        return this.status == FilterStatus.FULLY_FILTERED;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FilterMask filterMask = (FilterMask)o;
        return this.mask.equals(filterMask.mask) && this.status == filterMask.status;
    }

    public int hashCode() {
        int i = this.mask.hashCode();
        i = 31 * i + this.status.hashCode();
        return i;
    }

    static final class FilterStatus
    extends Enum<FilterStatus>
    implements StringIdentifiable {
        final static public FilterStatus PASS_THROUGH = new FilterStatus("pass_through", () -> PASS_THROUGH_CODEC);
        final static public FilterStatus FULLY_FILTERED = new FilterStatus("fully_filtered", () -> FULLY_FILTERED_CODEC);
        final static public FilterStatus PARTIALLY_FILTERED = new FilterStatus("partially_filtered", () -> PARTIALLY_FILTERED_CODEC);
        final private String id;
        final private Supplier<MapCodec<FilterMask>> codecSupplier;
        final static private FilterStatus[] field_39950;

        public static FilterStatus[] values() {
            return (FilterStatus[])field_39950.clone();
        }

        public static FilterStatus valueOf(String string) {
            return Enum.valueOf(FilterStatus.class, string);
        }

        private FilterStatus(String id, Supplier<MapCodec<FilterMask>> codecSupplier) {
            this.id = id;
            this.codecSupplier = codecSupplier;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private MapCodec<FilterMask> getCodec() {
            return this.codecSupplier.get();
        }

        private static FilterStatus[] method_45094() {
            return new FilterStatus[]{PASS_THROUGH, FULLY_FILTERED, PARTIALLY_FILTERED};
        }

        static {
            field_39950 = FilterStatus.method_45094();
        }
    }
}

