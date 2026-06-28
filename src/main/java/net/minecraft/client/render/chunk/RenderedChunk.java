/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RenderedChunk {
    final private Map<BlockPos, BlockEntity> blockEntities;
    @Nullable
    final private PalettedContainer<BlockState> blockPalette;
    final private boolean debugWorld;
    final private HeightLimitView heightLimitView;

    RenderedChunk(WorldChunk chunk, int sectionIndex) {
        this.heightLimitView = chunk;
        this.debugWorld = chunk.getWorld().isDebugWorld();
        this.blockEntities = ImmutableMap.copyOf(chunk.getBlockEntities());
        if (chunk instanceof EmptyChunk) {
            this.blockPalette = null;
        } else {
            ChunkSection chunkSection;
            ChunkSection[] chunkSections = chunk.getSectionArray();
            this.blockPalette = sectionIndex < 0 || sectionIndex >= chunkSections.length ? null : ((chunkSection = chunkSections[sectionIndex]).isEmpty() ? null : chunkSection.getBlockStateContainer().copy());
        }
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.blockEntities.get(pos);
    }

    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (this.debugWorld) {
            BlockState blockState = null;
            if (j == 60) {
                blockState = Blocks.BARRIER.getDefaultState();
            }
            if (j == 70) {
                blockState = DebugChunkGenerator.getBlockState(i, k);
            }
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }
        if (this.blockPalette == null) {
            return Blocks.AIR.getDefaultState();
        }
        try {
            return this.blockPalette.get(i & 0xF, j & 0xF, k & 0xF);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString(this.heightLimitView, i, j, k));
            throw new CrashException(crashReport);
        }
    }
}

