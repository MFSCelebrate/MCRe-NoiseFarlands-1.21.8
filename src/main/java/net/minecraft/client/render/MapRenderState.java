/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MapRenderState
implements FabricRenderState {
    @Nullable
    public Identifier texture;
    final public List<Decoration> decorations = new ArrayList<Decoration>();

    @Environment(value=EnvType.CLIENT)
    public static class Decoration
    implements FabricRenderState {
        @Nullable
        public Sprite sprite;
        public byte x;
        public byte z;
        public byte rotation;
        public boolean alwaysRendered;
        @Nullable
        public Text name;
    }
}

