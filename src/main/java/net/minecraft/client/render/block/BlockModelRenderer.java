/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer
 */
package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
public class BlockModelRenderer
implements FabricBlockModelRenderer {
    final static private Direction[] DIRECTIONS = Direction.values();
    final private BlockColors colors;
    final static private int BRIGHTNESS_CACHE_MAX_SIZE = 100;
    final static ThreadLocal<BrightnessCache> BRIGHTNESS_CACHE = ThreadLocal.withInitial(BrightnessCache::new);

    public BlockModelRenderer(BlockColors colors) {
        this.colors = colors;
    }

    public void render(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
        if (parts.isEmpty()) {
            return;
        }
        boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && parts.getFirst().useAmbientOcclusion();
        matrices.translate(state.getModelOffset(pos));
        try {
            if (bl) {
                this.renderSmooth(world, parts, state, pos, matrices, vertexConsumer, cull, overlay);
            } else {
                this.renderFlat(world, parts, state, pos, matrices, vertexConsumer, cull, overlay);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
            crashReportSection.add("Using AO", bl);
            throw new CrashException(crashReport);
        }
    }

    private static boolean shouldDrawFace(BlockRenderView world, BlockState state, boolean cull, Direction side, BlockPos pos) {
        if (!cull) {
            return true;
        }
        BlockState blockState = world.getBlockState(pos);
        return Block.shouldDrawSide(state, blockState, side);
    }

    public void renderSmooth(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
        AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
        int i = 0;
        int j = 0;
        for (BlockModelPart blockModelPart : parts) {
            for (Direction direction : DIRECTIONS) {
                List<BakedQuad> list;
                boolean bl2;
                int k = 1 << direction.ordinal();
                boolean bl = (i & k) == 1;
                boolean bl3 = bl2 = (j & k) == 1;
                if (bl && !bl2 || (list = blockModelPart.getQuads(direction)).isEmpty()) continue;
                if (!bl) {
                    bl2 = BlockModelRenderer.shouldDrawFace(world, state, cull, direction, ambientOcclusionCalculator.pos.set((Vec3i)pos, direction));
                    i |= k;
                    if (bl2) {
                        j |= k;
                    }
                }
                if (!bl2) continue;
                this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list, ambientOcclusionCalculator, overlay);
            }
            List<BakedQuad> list2 = blockModelPart.getQuads(null);
            if (list2.isEmpty()) continue;
            this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list2, ambientOcclusionCalculator, overlay);
        }
    }

    public void renderFlat(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
        LightmapCache lightmapCache = new LightmapCache();
        int i = 0;
        int j = 0;
        for (BlockModelPart blockModelPart : parts) {
            for (Direction direction : DIRECTIONS) {
                List<BakedQuad> list;
                boolean bl2;
                int k = 1 << direction.ordinal();
                boolean bl = (i & k) == 1;
                boolean bl3 = bl2 = (j & k) == 1;
                if (bl && !bl2 || (list = blockModelPart.getQuads(direction)).isEmpty()) continue;
                BlockPos.Mutable blockPos = lightmapCache.pos.set((Vec3i)pos, direction);
                if (!bl) {
                    bl2 = BlockModelRenderer.shouldDrawFace(world, state, cull, direction, blockPos);
                    i |= k;
                    if (bl2) {
                        j |= k;
                    }
                }
                if (!bl2) continue;
                int l = lightmapCache.brightnessCache.getInt(state, world, blockPos);
                this.renderQuadsFlat(world, state, pos, l, overlay, false, matrices, vertexConsumer, list, lightmapCache);
            }
            List<BakedQuad> list2 = blockModelPart.getQuads(null);
            if (list2.isEmpty()) continue;
            this.renderQuadsFlat(world, state, pos, -1, overlay, true, matrices, vertexConsumer, list2, lightmapCache);
        }
    }

    private void renderQuadsSmooth(BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, AmbientOcclusionCalculator ambientOcclusionCalculator, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            BlockModelRenderer.getQuadDimensions(world, state, pos, bakedQuad.vertexData(), bakedQuad.face(), ambientOcclusionCalculator);
            ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.face(), bakedQuad.shade());
            this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, ambientOcclusionCalculator, overlay);
        }
    }

    private void renderQuad(BlockRenderView world, BlockState state, BlockPos pos, VertexConsumer vertexConsumer, MatrixStack.Entry matrixEntry, BakedQuad quad, LightmapCache lightmap, int light) {
        float h;
        float g;
        float f;
        int i = quad.tintIndex();
        if (i != -1) {
            int j;
            if (lightmap.lastTintIndex == i) {
                j = lightmap.colorOfLastTintIndex;
            } else {
                j = this.colors.getColor(state, world, pos, i);
                lightmap.lastTintIndex = i;
                lightmap.colorOfLastTintIndex = j;
            }
            f = ColorHelper.getRedFloat(j);
            g = ColorHelper.getGreenFloat(j);
            h = ColorHelper.getBlueFloat(j);
        } else {
            f = 1.0f;
            g = 1.0f;
            h = 1.0f;
        }
        vertexConsumer.quad(matrixEntry, quad, lightmap.fs, f, g, h, 1.0f, lightmap.is, light, true);
    }

    private static void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, LightmapCache lightmap) {
        float m;
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (int l = 0; l < 4; ++l) {
            m = Float.intBitsToFloat(vertexData[l * 8]);
            float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
            float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }
        if (lightmap instanceof AmbientOcclusionCalculator) {
            AmbientOcclusionCalculator ambientOcclusionCalculator = (AmbientOcclusionCalculator)lightmap;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.WEST.index] = f;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.EAST.index] = i;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.DOWN.index] = g;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.UP.index] = j;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.NORTH.index] = h;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.SOUTH.index] = k;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_WEST.index] = 1.0f - f;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_EAST.index] = 1.0f - i;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_DOWN.index] = 1.0f - g;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_UP.index] = 1.0f - j;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_NORTH.index] = 1.0f - h;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_SOUTH.index] = 1.0f - k;
        }
        float p = 1.0E-4f;
        m = 0.9999f;
        lightmap.field_58161 = switch (face) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN, Direction.UP -> {
                if (f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f) {
                    yield true;
                }
                yield false;
            }
            case Direction.NORTH, Direction.SOUTH -> {
                if (f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST, Direction.EAST -> g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f;
        };
        lightmap.field_58160 = switch (face) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN -> {
                if (g == j && (g < 1.0E-4f || state.isFullCube(world, pos))) {
                    yield true;
                }
                yield false;
            }
            case Direction.UP -> {
                if (g == j && (j > 0.9999f || state.isFullCube(world, pos))) {
                    yield true;
                }
                yield false;
            }
            case Direction.NORTH -> {
                if (h == k && (h < 1.0E-4f || state.isFullCube(world, pos))) {
                    yield true;
                }
                yield false;
            }
            case Direction.SOUTH -> {
                if (h == k && (k > 0.9999f || state.isFullCube(world, pos))) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST -> {
                if (f == i && (f < 1.0E-4f || state.isFullCube(world, pos))) {
                    yield true;
                }
                yield false;
            }
            case Direction.EAST -> f == i && (i > 0.9999f || state.isFullCube(world, pos));
        };
    }

    private void renderQuadsFlat(BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, LightmapCache lightmap) {
        for (BakedQuad bakedQuad : quads) {
            float f;
            if (useWorldLight) {
                BlockModelRenderer.getQuadDimensions(world, state, pos, bakedQuad.vertexData(), bakedQuad.face(), lightmap);
                BlockPos blockPos = lightmap.field_58160 ? lightmap.pos.set((Vec3i)pos, bakedQuad.face()) : pos;
                light = lightmap.brightnessCache.getInt(state, world, blockPos);
            }
            lightmap.fs[0] = f = world.getBrightness(bakedQuad.face(), bakedQuad.shade());
            lightmap.fs[1] = f;
            lightmap.fs[2] = f;
            lightmap.fs[3] = f;
            lightmap.is[0] = light;
            lightmap.is[1] = light;
            lightmap.is[2] = light;
            lightmap.is[3] = light;
            this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, lightmap, overlay);
        }
    }

    public static void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, BlockStateModel model, float red, float green, float blue, int light, int overlay) {
        for (BlockModelPart blockModelPart : model.getParts(Random.create(42L))) {
            for (Direction direction : DIRECTIONS) {
                BlockModelRenderer.renderQuads(entry, vertexConsumer, red, green, blue, blockModelPart.getQuads(direction), light, overlay);
            }
            BlockModelRenderer.renderQuads(entry, vertexConsumer, red, green, blue, blockModelPart.getQuads(null), light, overlay);
        }
    }

    private static void renderQuads(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            float h;
            float g;
            float f;
            if (bakedQuad.hasTint()) {
                f = MathHelper.clamp(red, 0.0f, 1.0f);
                g = MathHelper.clamp(green, 0.0f, 1.0f);
                h = MathHelper.clamp(blue, 0.0f, 1.0f);
            } else {
                f = 1.0f;
                g = 1.0f;
                h = 1.0f;
            }
            vertexConsumer.quad(entry, bakedQuad, f, g, h, 1.0f, light, overlay);
        }
    }

    public static void enableBrightnessCache() {
        BRIGHTNESS_CACHE.get().enable();
    }

    public static void disableBrightnessCache() {
        BRIGHTNESS_CACHE.get().disable();
    }

    @Environment(value=EnvType.CLIENT)
    static class AmbientOcclusionCalculator
    extends LightmapCache {
        final float[] field_58158 = new float[NeighborOrientation.SIZE];

        public void apply(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, boolean bl) {
            float x;
            int u;
            float t;
            int s;
            float r;
            int q;
            float p;
            int o;
            float n;
            BlockState blockState9;
            boolean bl5;
            BlockPos blockPos = this.field_58160 ? pos.net_minecraft_util_math_BlockPos_offset(direction) : pos;
            NeighborData neighborData = NeighborData.getData(direction);
            BlockPos.Mutable mutable = this.pos;
            mutable.set((Vec3i)blockPos, neighborData.faces[0]);
            BlockState blockState = world.getBlockState(mutable);
            int i = this.brightnessCache.getInt(blockState, world, mutable);
            float f = this.brightnessCache.getFloat(blockState, world, mutable);
            mutable.set((Vec3i)blockPos, neighborData.faces[1]);
            BlockState blockState2 = world.getBlockState(mutable);
            int j = this.brightnessCache.getInt(blockState2, world, mutable);
            float g = this.brightnessCache.getFloat(blockState2, world, mutable);
            mutable.set((Vec3i)blockPos, neighborData.faces[2]);
            BlockState blockState3 = world.getBlockState(mutable);
            int k = this.brightnessCache.getInt(blockState3, world, mutable);
            float h = this.brightnessCache.getFloat(blockState3, world, mutable);
            mutable.set((Vec3i)blockPos, neighborData.faces[3]);
            BlockState blockState4 = world.getBlockState(mutable);
            int l = this.brightnessCache.getInt(blockState4, world, mutable);
            float m = this.brightnessCache.getFloat(blockState4, world, mutable);
            BlockState blockState5 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[0]).move(direction));
            boolean bl2 = !blockState5.shouldBlockVision(world, mutable) || blockState5.getOpacity() == 0;
            BlockState blockState6 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[1]).move(direction));
            boolean bl3 = !blockState6.shouldBlockVision(world, mutable) || blockState6.getOpacity() == 0;
            BlockState blockState7 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[2]).move(direction));
            boolean bl4 = !blockState7.shouldBlockVision(world, mutable) || blockState7.getOpacity() == 0;
            BlockState blockState8 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[3]).move(direction));
            boolean bl6 = bl5 = !blockState8.shouldBlockVision(world, mutable) || blockState8.getOpacity() == 0;
            if (bl4 || bl2) {
                mutable.set((Vec3i)blockPos, neighborData.faces[0]).move(neighborData.faces[2]);
                blockState9 = world.getBlockState(mutable);
                n = this.brightnessCache.getFloat(blockState9, world, mutable);
                o = this.brightnessCache.getInt(blockState9, world, mutable);
            } else {
                n = f;
                o = i;
            }
            if (bl5 || bl2) {
                mutable.set((Vec3i)blockPos, neighborData.faces[0]).move(neighborData.faces[3]);
                blockState9 = world.getBlockState(mutable);
                p = this.brightnessCache.getFloat(blockState9, world, mutable);
                q = this.brightnessCache.getInt(blockState9, world, mutable);
            } else {
                p = f;
                q = i;
            }
            if (bl4 || bl3) {
                mutable.set((Vec3i)blockPos, neighborData.faces[1]).move(neighborData.faces[2]);
                blockState9 = world.getBlockState(mutable);
                r = this.brightnessCache.getFloat(blockState9, world, mutable);
                s = this.brightnessCache.getInt(blockState9, world, mutable);
            } else {
                r = f;
                s = i;
            }
            if (bl5 || bl3) {
                mutable.set((Vec3i)blockPos, neighborData.faces[1]).move(neighborData.faces[3]);
                blockState9 = world.getBlockState(mutable);
                t = this.brightnessCache.getFloat(blockState9, world, mutable);
                u = this.brightnessCache.getInt(blockState9, world, mutable);
            } else {
                t = f;
                u = i;
            }
            int v = this.brightnessCache.getInt(state, world, pos);
            mutable.set((Vec3i)pos, direction);
            BlockState blockState10 = world.getBlockState(mutable);
            if (this.field_58160 || !blockState10.isOpaqueFullCube()) {
                v = this.brightnessCache.getInt(blockState10, world, mutable);
            }
            float w = this.field_58160 ? this.brightnessCache.getFloat(world.getBlockState(blockPos), world, blockPos) : this.brightnessCache.getFloat(world.getBlockState(pos), world, pos);
            Translation translation = Translation.getTranslations(direction);
            if (!this.field_58161 || !neighborData.nonCubicWeight) {
                x = (m + f + p + w) * 0.25f;
                y = (h + f + n + w) * 0.25f;
                z = (h + g + r + w) * 0.25f;
                aa = (m + g + t + w) * 0.25f;
                this.is[translation.firstCorner] = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, i, q, v);
                this.is[translation.secondCorner] = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, i, o, v);
                this.is[translation.thirdCorner] = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, j, s, v);
                this.is[translation.fourthCorner] = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, j, u, v);
                this.fs[translation.firstCorner] = x;
                this.fs[translation.secondCorner] = y;
                this.fs[translation.thirdCorner] = z;
                this.fs[translation.fourthCorner] = aa;
            } else {
                x = (m + f + p + w) * 0.25f;
                y = (h + f + n + w) * 0.25f;
                z = (h + g + r + w) * 0.25f;
                aa = (m + g + t + w) * 0.25f;
                float ab = this.field_58158[neighborData.field_4192[0].index] * this.field_58158[neighborData.field_4192[1].index];
                float ac = this.field_58158[neighborData.field_4192[2].index] * this.field_58158[neighborData.field_4192[3].index];
                float ad = this.field_58158[neighborData.field_4192[4].index] * this.field_58158[neighborData.field_4192[5].index];
                float ae = this.field_58158[neighborData.field_4192[6].index] * this.field_58158[neighborData.field_4192[7].index];
                float af = this.field_58158[neighborData.field_4185[0].index] * this.field_58158[neighborData.field_4185[1].index];
                float ag = this.field_58158[neighborData.field_4185[2].index] * this.field_58158[neighborData.field_4185[3].index];
                float ah = this.field_58158[neighborData.field_4185[4].index] * this.field_58158[neighborData.field_4185[5].index];
                float ai = this.field_58158[neighborData.field_4185[6].index] * this.field_58158[neighborData.field_4185[7].index];
                float aj = this.field_58158[neighborData.field_4180[0].index] * this.field_58158[neighborData.field_4180[1].index];
                float ak = this.field_58158[neighborData.field_4180[2].index] * this.field_58158[neighborData.field_4180[3].index];
                float al = this.field_58158[neighborData.field_4180[4].index] * this.field_58158[neighborData.field_4180[5].index];
                float am = this.field_58158[neighborData.field_4180[6].index] * this.field_58158[neighborData.field_4180[7].index];
                float an = this.field_58158[neighborData.field_4188[0].index] * this.field_58158[neighborData.field_4188[1].index];
                float ao = this.field_58158[neighborData.field_4188[2].index] * this.field_58158[neighborData.field_4188[3].index];
                float ap = this.field_58158[neighborData.field_4188[4].index] * this.field_58158[neighborData.field_4188[5].index];
                float aq = this.field_58158[neighborData.field_4188[6].index] * this.field_58158[neighborData.field_4188[7].index];
                this.fs[translation.firstCorner] = Math.clamp(x * ab + y * ac + z * ad + aa * ae, 0.0f, 1.0f);
                this.fs[translation.secondCorner] = Math.clamp(x * af + y * ag + z * ah + aa * ai, 0.0f, 1.0f);
                this.fs[translation.thirdCorner] = Math.clamp(x * aj + y * ak + z * al + aa * am, 0.0f, 1.0f);
                this.fs[translation.fourthCorner] = Math.clamp(x * an + y * ao + z * ap + aa * aq, 0.0f, 1.0f);
                int ar = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, i, q, v);
                int as = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, i, o, v);
                int at = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, j, s, v);
                int au = AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, j, u, v);
                this.is[translation.firstCorner] = AmbientOcclusionCalculator.getBrightness(ar, as, at, au, ab, ac, ad, ae);
                this.is[translation.secondCorner] = AmbientOcclusionCalculator.getBrightness(ar, as, at, au, af, ag, ah, ai);
                this.is[translation.thirdCorner] = AmbientOcclusionCalculator.getBrightness(ar, as, at, au, aj, ak, al, am);
                this.is[translation.fourthCorner] = AmbientOcclusionCalculator.getBrightness(ar, as, at, au, an, ao, ap, aq);
            }
            x = world.getBrightness(direction, bl);
            int av = 0;
            while (av < this.fs.length) {
                int n2 = av++;
                this.fs[n2] = this.fs[n2] * x;
            }
        }

        private static int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
            if (i == 0) {
                i = l;
            }
            if (j == 0) {
                j = l;
            }
            if (k == 0) {
                k = l;
            }
            return i + j + k + l >> 2 & 0xFF00FF;
        }

        private static int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
            int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
            int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
            return n << 16 | o;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LightmapCache {
        final public BlockPos.Mutable pos = new BlockPos.Mutable();
        public boolean field_58160;
        public boolean field_58161;
        final public float[] fs = new float[4];
        final public int[] is = new int[4];
        public int lastTintIndex = -1;
        public int colorOfLastTintIndex;
        final public BrightnessCache brightnessCache = BRIGHTNESS_CACHE.get();

        LightmapCache() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BrightnessCache {
        private boolean enabled;
        final private Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int newN) {
                }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
        });
        final private Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int newN) {
                }
            };
            long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap;
        });
        final private WorldRenderer.BrightnessGetter brightnessCache = (world, pos) -> {
            long l = pos.asLong();
            int i = this.intCache.get(l);
            if (i != Integer.MAX_VALUE) {
                return i;
            }
            int j = WorldRenderer.BrightnessGetter.DEFAULT.packedBrightness(world, pos);
            if (this.intCache.size() == 100) {
                this.intCache.removeFirstInt();
            }
            this.intCache.put(l, j);
            return j;
        };

        private BrightnessCache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.intCache.clear();
            this.floatCache.clear();
        }

        public int getInt(BlockState state, BlockRenderView world, BlockPos pos) {
            return WorldRenderer.getLightmapCoordinates(this.enabled ? this.brightnessCache : WorldRenderer.BrightnessGetter.DEFAULT, world, state, pos);
        }

        public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
            float f;
            long l = pos.asLong();
            if (this.enabled && !Float.isNaN(f = this.floatCache.get(l))) {
                return f;
            }
            f = state.getAmbientOcclusionLightLevel(blockView, pos);
            if (this.enabled) {
                if (this.floatCache.size() == 100) {
                    this.floatCache.removeFirstFloat();
                }
                this.floatCache.put(l, f);
            }
            return f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class NeighborOrientation
    extends Enum<NeighborOrientation> {
        final static public NeighborOrientation DOWN = new NeighborOrientation(0);
        final static public NeighborOrientation UP = new NeighborOrientation(1);
        final static public NeighborOrientation NORTH = new NeighborOrientation(2);
        final static public NeighborOrientation SOUTH = new NeighborOrientation(3);
        final static public NeighborOrientation WEST = new NeighborOrientation(4);
        final static public NeighborOrientation EAST = new NeighborOrientation(5);
        final static public NeighborOrientation FLIP_DOWN = new NeighborOrientation(6);
        final static public NeighborOrientation FLIP_UP = new NeighborOrientation(7);
        final static public NeighborOrientation FLIP_NORTH = new NeighborOrientation(8);
        final static public NeighborOrientation FLIP_SOUTH = new NeighborOrientation(9);
        final static public NeighborOrientation FLIP_WEST = new NeighborOrientation(10);
        final static public NeighborOrientation FLIP_EAST = new NeighborOrientation(11);
        final static public int SIZE;
        final int index;
        final static private NeighborOrientation[] field_4223;

        public static NeighborOrientation[] values() {
            return (NeighborOrientation[])field_4223.clone();
        }

        public static NeighborOrientation valueOf(String string) {
            return Enum.valueOf(NeighborOrientation.class, string);
        }

        private NeighborOrientation(int index) {
            this.index = index;
        }

        private static NeighborOrientation[] method_36919() {
            return new NeighborOrientation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
        }

        static {
            field_4223 = NeighborOrientation.method_36919();
            SIZE = NeighborOrientation.values().length;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class NeighborData
    extends Enum<NeighborData> {
        final static public NeighborData DOWN = new NeighborData(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH});
        final static public NeighborData UP = new NeighborData(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH});
        final static public NeighborData NORTH = new NeighborData(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST});
        final static public NeighborData SOUTH = new NeighborData(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST});
        final static public NeighborData WEST = new NeighborData(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH});
        final static public NeighborData EAST = new NeighborData(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH});
        final Direction[] faces;
        final boolean nonCubicWeight;
        final NeighborOrientation[] field_4192;
        final NeighborOrientation[] field_4185;
        final NeighborOrientation[] field_4180;
        final NeighborOrientation[] field_4188;
        final static private NeighborData[] VALUES;
        final static private NeighborData[] field_4193;

        public static NeighborData[] values() {
            return (NeighborData[])field_4193.clone();
        }

        public static NeighborData valueOf(String string) {
            return Enum.valueOf(NeighborData.class, string);
        }

        private NeighborData(Direction[] faces, float f, boolean nonCubicWeight, NeighborOrientation[] neighborOrientations, NeighborOrientation[] neighborOrientations2, NeighborOrientation[] neighborOrientations3, NeighborOrientation[] neighborOrientations4) {
            this.faces = faces;
            this.nonCubicWeight = nonCubicWeight;
            this.field_4192 = neighborOrientations;
            this.field_4185 = neighborOrientations2;
            this.field_4180 = neighborOrientations3;
            this.field_4188 = neighborOrientations4;
        }

        public static NeighborData getData(Direction direction) {
            return VALUES[direction.getIndex()];
        }

        private static NeighborData[] method_36917() {
            return new NeighborData[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        }

        static {
            field_4193 = NeighborData.method_36917();
            VALUES = Util.make(new NeighborData[6], values -> {
                values[Direction.DOWN.getIndex()] = DOWN;
                values[Direction.UP.getIndex()] = UP;
                values[Direction.NORTH.getIndex()] = NORTH;
                values[Direction.SOUTH.getIndex()] = SOUTH;
                values[Direction.WEST.getIndex()] = WEST;
                values[Direction.EAST.getIndex()] = EAST;
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Translation
    extends Enum<Translation> {
        final static public Translation DOWN = new Translation(0, 1, 2, 3);
        final static public Translation UP = new Translation(2, 3, 0, 1);
        final static public Translation NORTH = new Translation(3, 0, 1, 2);
        final static public Translation SOUTH = new Translation(0, 1, 2, 3);
        final static public Translation WEST = new Translation(3, 0, 1, 2);
        final static public Translation EAST = new Translation(1, 2, 3, 0);
        final int firstCorner;
        final int secondCorner;
        final int thirdCorner;
        final int fourthCorner;
        final static private Translation[] VALUES;
        final static private Translation[] field_4208;

        public static Translation[] values() {
            return (Translation[])field_4208.clone();
        }

        public static Translation valueOf(String string) {
            return Enum.valueOf(Translation.class, string);
        }

        private Translation(int firstCorner, int secondCorner, int thirdCorner, int fourthCorner) {
            this.firstCorner = firstCorner;
            this.secondCorner = secondCorner;
            this.thirdCorner = thirdCorner;
            this.fourthCorner = fourthCorner;
        }

        public static Translation getTranslations(Direction direction) {
            return VALUES[direction.getIndex()];
        }

        private static Translation[] method_36918() {
            return new Translation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        }

        static {
            field_4208 = Translation.method_36918();
            VALUES = Util.make(new Translation[6], values -> {
                values[Direction.DOWN.getIndex()] = DOWN;
                values[Direction.UP.getIndex()] = UP;
                values[Direction.NORTH.getIndex()] = NORTH;
                values[Direction.SOUTH.getIndex()] = SOUTH;
                values[Direction.WEST.getIndex()] = WEST;
                values[Direction.EAST.getIndex()] = EAST;
            });
        }
    }
}

