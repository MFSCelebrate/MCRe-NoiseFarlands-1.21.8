/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRendererRegion
implements BlockRenderView {
    final static public int field_52160 = 1;
    final static public int SIDE_LENGTH_CHUNKS = 3;
    final private int baseX;
    final private int baseY;
    final private int baseZ;
    final private RenderedChunk[] renderedChunks;
    final private World world;

    ChunkRendererRegion(World world, int baseX, int baseY, int baseZ, RenderedChunk[] renderedChunks) {
        this.world = world;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseZ = baseZ;
        this.renderedChunks = renderedChunks;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.getRenderedChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getY()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getRenderedChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getY()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockState(pos).getFluidState();
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return this.world.getBrightness(direction, shaded);
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.getRenderedChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getY()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockEntity(pos);
    }

    private RenderedChunk getRenderedChunk(int sectionX, int sectionY, int sectionZ) {
        return this.renderedChunks[ChunkRendererRegion.getIndex(this.baseX, this.baseY, this.baseZ, sectionX, sectionY, sectionZ)];
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return this.world.getColor(pos, colorResolver);
    }

    @Override
    public int getBottomY() {
        return this.world.getBottomY();
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    public static int getIndex(int xOffset, int yOffset, int zOffset, int sectionX, int sectionY, int sectionZ) {
        return sectionX - xOffset + (sectionY - yOffset) * 3 + (sectionZ - zOffset) * 3 * 3;
    }
}

