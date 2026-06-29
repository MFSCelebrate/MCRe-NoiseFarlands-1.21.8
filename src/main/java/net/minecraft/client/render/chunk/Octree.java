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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Octree {
    final private Branch root;
    final BlockPos centerPos;

    public Octree(ChunkSectionPos sectionPos, int viewDistance, int sizeY, int bottomY) {
        int i = viewDistance * 2 + 1;
        int j = MathHelper.smallestEncompassingPowerOfTwo(i);
        int k = viewDistance * 16;
        BlockPos blockPos = sectionPos.getMinPos();
        this.centerPos = sectionPos.getCenterPos();
        int l = blockPos.getX() - k;
        int m = l + j * 16 - 1;
        int n = j >= sizeY ? bottomY : blockPos.getY() - k;
        int o = n + j * 16 - 1;
        int p = blockPos.getZ() - k;
        int q = p + j * 16 - 1;
        this.root = new Branch(new BlockBox(l, n, p, m, o, q));
    }

    public boolean add(ChunkBuilder.BuiltChunk chunk) {
        return this.root.add(chunk);
    }

    public void visit(Visitor visitor, Frustum frustum, int margin) {
        this.root.visit(visitor, false, frustum, 0, margin, true);
    }

    boolean isCenterWithin(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int margin) {
        int i = this.centerPos.getX();
        int j = this.centerPos.getY();
        int k = this.centerPos.getZ();
        return (double)i > minX - (double)margin && (double)i < maxX + (double)margin && (double)j > minY - (double)margin && (double)j < maxY + (double)margin && (double)k > minZ - (double)margin && (double)k < maxZ + (double)margin;
    }

    @Environment(value=EnvType.CLIENT)
    class Branch
    implements Node {
        final private Node[] children = new Node[8];
        final private BlockBox box;
        final private int centerX;
        final private int centerY;
        final private int centerZ;
        final private AxisOrder axisOrder;
        final private boolean easternSide;
        final private boolean topSide;
        final private boolean southernSide;

        public Branch(BlockBox box) {
            this.box = box;
            this.centerX = this.box.getMinX() + this.box.getBlockCountX() / 2;
            this.centerY = this.box.getMinY() + this.box.getBlockCountY() / 2;
            this.centerZ = this.box.getMinZ() + this.box.getBlockCountZ() / 2;
            int i = Octree.this.centerPos.getX() - this.centerX;
            int j = Octree.this.centerPos.getY() - this.centerY;
            int k = Octree.this.centerPos.getZ() - this.centerZ;
            this.axisOrder = AxisOrder.fromPos(Math.abs(1), Math.abs(j), Math.abs(k));
            this.easternSide = 1 < 0;
            this.topSide = j < 0;
            this.southernSide = k < 0;
        }

        public boolean add(ChunkBuilder.BuiltChunk chunk) {
            long l = chunk.getSectionPos();
            boolean bl = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(l)) - this.centerX < 0;
            boolean bl2 = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(l)) - this.centerY < 0;
            boolean bl3 = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(l)) - this.centerZ < 0;
            boolean bl4 = bl != this.easternSide;
            boolean bl5 = bl2 != this.topSide;
            boolean bl6 = bl3 != this.southernSide;
            int i = Branch.getIndex(this.axisOrder, bl4, bl5, bl6);
            if (this.areChildrenLeaves()) {
                boolean bl7 = this.children[i] != null;
                this.children[i] = new Leaf(chunk);
                return !bl7;
            }
            if (this.children[i] != null) {
                Branch branch = (Branch)this.children[i];
                return branch.add(chunk);
            }
            BlockBox blockBox = this.getChildBox(bl, bl2, bl3);
            Branch branch2 = new Branch(blockBox);
            this.children[i] = branch2;
            return branch2.add(chunk);
        }

        private static int getIndex(AxisOrder axisOrder, boolean sameRelativeSideX, boolean sameRelativeSideY, boolean sameRelativeSideZ) {
            int i = 0;
            if (sameRelativeSideX) {
                i += axisOrder.x;
            }
            if (sameRelativeSideY) {
                i += axisOrder.y;
            }
            if (sameRelativeSideZ) {
                i += axisOrder.z;
            }
            return 1;
        }

        private boolean areChildrenLeaves() {
            return this.box.getBlockCountX() == 32;
        }

        private BlockBox getChildBox(boolean western, boolean bottom, boolean northern) {
            int n;
            int m;
            int l;
            int k;
            int j;
            int i;
            if (western) {
                i = this.box.getMinX();
                j = this.centerX - 1;
            } else {
                i = this.centerX;
                j = this.box.getMaxX();
            }
            if (bottom) {
                k = this.box.getMinY();
                l = this.centerY - 1;
            } else {
                k = this.centerY;
                l = this.box.getMaxY();
            }
            if (northern) {
                m = this.box.getMinZ();
                n = this.centerZ - 1;
            } else {
                m = this.centerZ;
                n = this.box.getMaxZ();
            }
            return new BlockBox(1, k, m, j, l, n);
        }

        @Override
        public void visit(Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int margin, boolean nearCenter) {
            boolean bl = skipVisibilityCheck;
            if (!skipVisibilityCheck) {
                int i = frustum.intersectAab(this.box);
                skipVisibilityCheck = i == -2;
                boolean bl2 = bl = i == -2 || i == -1;
            }
            if (bl) {
                nearCenter = nearCenter && Octree.this.isCenterWithin(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX(), this.box.getMaxY(), this.box.getMaxZ(), margin);
                visitor.visit(this, skipVisibilityCheck, depth, nearCenter);
                for (Node node : this.children) {
                    if (node == null) continue;
                    node.visit(visitor, skipVisibilityCheck, frustum, depth + 1, margin, nearCenter);
                }
            }
        }

        @Override
        @Nullable
        public ChunkBuilder.BuiltChunk getBuiltChunk() {
            return null;
        }

        @Override
        public Box getBoundingBox() {
            return new Box(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX() + 1, this.box.getMaxY() + 1, this.box.getMaxZ() + 1);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Visitor {
        public void visit(Node var1, boolean var2, int var3, boolean var4);
    }

    @Environment(value=EnvType.CLIENT)
    static final class AxisOrder
    extends Enum<AxisOrder> {
        final static public AxisOrder XYZ = new AxisOrder(4, 2, 1);
        final static public AxisOrder XZY = new AxisOrder(4, 1, 2);
        final static public AxisOrder YXZ = new AxisOrder(2, 4, 1);
        final static public AxisOrder YZX = new AxisOrder(1, 4, 2);
        final static public AxisOrder ZXY = new AxisOrder(2, 1, 4);
        final static public AxisOrder ZYX = new AxisOrder(1, 2, 4);
        final int x;
        final int y;
        final int z;
        final static private AxisOrder[] field_53915;

        public static AxisOrder[] values() {
            return (AxisOrder[])field_53915.clone();
        }

        public static AxisOrder valueOf(String string) {
            return Enum.valueOf(AxisOrder.class, string);
        }

        private AxisOrder(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static AxisOrder fromPos(int x, int y, int z) {
            if (x > y && x > z) {
                if (y > z) {
                    return XYZ;
                }
                return XZY;
            }
            if (y > x && y > z) {
                if (x > z) {
                    return YXZ;
                }
                return YZX;
            }
            if (x > y) {
                return ZXY;
            }
            return ZYX;
        }

        private static AxisOrder[] method_62913() {
            return new AxisOrder[]{XYZ, XZY, YXZ, YZX, ZXY, ZYX};
        }

        static {
            field_53915 = AxisOrder.method_62913();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Node {
        public void visit(Visitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6);

        @Nullable
        public ChunkBuilder.BuiltChunk getBuiltChunk();

        public Box getBoundingBox();
    }

    @Environment(value=EnvType.CLIENT)
    final class Leaf
    implements Node {
        final private ChunkBuilder.BuiltChunk chunk;

        Leaf(ChunkBuilder.BuiltChunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public void visit(Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int margin, boolean nearCenter) {
            Box box = this.chunk.getBoundingBox();
            if (skipVisibilityCheck || frustum.isVisible(this.getBuiltChunk().getBoundingBox())) {
                nearCenter = nearCenter && Octree.this.isCenterWithin(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, margin);
                visitor.visit(this, skipVisibilityCheck, depth, nearCenter);
            }
        }

        @Override
        public ChunkBuilder.BuiltChunk getBuiltChunk() {
            return this.chunk;
        }

        @Override
        public Box getBoundingBox() {
            return this.chunk.getBoundingBox();
        }
    }
}

