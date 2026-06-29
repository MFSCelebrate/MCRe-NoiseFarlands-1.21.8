/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightSourceView;
import org.jetbrains.annotations.Nullable;

public interface ChunkProvider {
    @Nullable
    public LightSourceView getChunk(int var1, int var2);

    default public void onLightUpdate(LightType type, ChunkSectionPos pos) {
    }

    public BlockView net_minecraft_world_BlockView_getWorld();
}

