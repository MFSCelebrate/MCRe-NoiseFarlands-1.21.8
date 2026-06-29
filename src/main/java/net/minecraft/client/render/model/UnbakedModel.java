/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface UnbakedModel {
    final static public String PARTICLE_TEXTURE = "particle";

    @Nullable
    default public Boolean ambientOcclusion() {
        return null;
    }

    @Nullable
    default public GuiLight guiLight() {
        return null;
    }

    @Nullable
    default public ModelTransformation transformations() {
        return null;
    }

    default public ModelTextures.Textures textures() {
        return ModelTextures.Textures.EMPTY;
    }

    @Nullable
    default public Geometry geometry() {
        return null;
    }

    @Nullable
    default public Identifier parent() {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class GuiLight
    extends Enum<GuiLight> {
        final static public GuiLight ITEM = new GuiLight("front");
        final static public GuiLight BLOCK = new GuiLight("side");
        final private String name;
        final static private GuiLight[] field_21861;

        public static GuiLight[] values() {
            return (GuiLight[])field_21861.clone();
        }

        public static GuiLight valueOf(String string) {
            return Enum.valueOf(GuiLight.class, string);
        }

        private GuiLight(String name) {
            this.name = name;
        }

        public static GuiLight byName(String value) {
            for (GuiLight guiLight : GuiLight.values()) {
                if (!guiLight.name.equals(value)) continue;
                return guiLight;
            }
            throw new IllegalArgumentException("Invalid gui light: " + value);
        }

        public boolean isSide() {
            return this == BLOCK;
        }

        private static GuiLight[] method_36920() {
            return new GuiLight[]{ITEM, BLOCK};
        }

        static {
            field_21861 = GuiLight.method_36920();
        }
    }
}

