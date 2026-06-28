/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EmptyBakedGlyph;
import net.minecraft.client.font.RenderableGlyph;

@Environment(value=EnvType.CLIENT)
public interface Glyph {
    public float getAdvance();

    default public float getAdvance(boolean bold) {
        return this.getAdvance() + (bold ? this.getBoldOffset() : 0.0f);
    }

    default public float getBoldOffset() {
        return 1.0f;
    }

    default public float getShadowOffset() {
        return 1.0f;
    }

    public BakedGlyph bake(Function<RenderableGlyph, BakedGlyph> var1);

    @Environment(value=EnvType.CLIENT)
    public static interface EmptyGlyph
    extends Glyph {
        @Override
        default public BakedGlyph bake(Function<RenderableGlyph, BakedGlyph> function) {
            return EmptyBakedGlyph.INSTANCE;
        }
    }
}

