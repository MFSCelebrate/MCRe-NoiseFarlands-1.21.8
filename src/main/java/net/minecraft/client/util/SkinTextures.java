/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record SkinTextures(Identifier texture, @Nullable String textureUrl, @Nullable Identifier capeTexture, @Nullable Identifier elytraTexture, Model model, boolean secure) {

    @Environment(value=EnvType.CLIENT)
    public static final class Model
    extends Enum<Model> {
        final static public Model SLIM = new Model("slim");
        final static public Model WIDE = new Model("default");
        final private String name;
        final static private Model[] field_41125;

        public static Model[] values() {
            return (Model[])field_41125.clone();
        }

        public static Model valueOf(String string) {
            return Enum.valueOf(Model.class, string);
        }

        private Model(String name) {
            this.name = name;
        }

        public static Model fromName(@Nullable String name) {
            if (name == null) {
                return WIDE;
            }
            return switch (name) {
                case "slim" -> SLIM;
                default -> WIDE;
            };
        }

        public String getName() {
            return this.name;
        }

        private static Model[] method_47439() {
            return new Model[]{SLIM, WIDE};
        }

        static {
            field_41125 = Model.method_47439();
        }
    }
}

