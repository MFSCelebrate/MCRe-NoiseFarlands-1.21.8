/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock
extends AbstractSkullBlock {
    final static public MapCodec<SkullBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SkullType.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getSkullType), SkullBlock.createSettingsCodec()).apply((Applicative)instance, SkullBlock::new));
    final static public int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    final static private int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    final static public IntProperty ROTATION = Properties.ROTATION;
    final static private VoxelShape SHAPE = Block.createColumnShape(8.0, 0.0, 8.0);
    final static private VoxelShape PIGLIN_SHAPE = Block.createColumnShape(10.0, 0.0, 8.0);

    public MapCodec<? extends SkullBlock> getCodec() {
        return CODEC;
    }

    public SkullBlock(SkullType skullType, AbstractBlock.Settings settings) {
        super(skullType, settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(ROTATION, 0));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getSkullType() == Type.PIGLIN ? PIGLIN_SHAPE : SHAPE;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)super.getPlacementState(ctx).with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(ROTATION, rotation.rotate(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with(ROTATION, mirror.mirror(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ROTATION);
    }

    public static interface SkullType
    extends StringIdentifiable {
        final static public Map<String, SkullType> TYPES = new Object2ObjectArrayMap();
        final static public Codec<SkullType> CODEC = Codec.stringResolver(StringIdentifiable::asString, TYPES::get);
    }

    public static final class Type
    extends Enum<Type>
    implements SkullType {
        final static public Type SKELETON = new Type("skeleton");
        final static public Type WITHER_SKELETON = new Type("wither_skeleton");
        final static public Type PLAYER = new Type("player");
        final static public Type ZOMBIE = new Type("zombie");
        final static public Type CREEPER = new Type("creeper");
        final static public Type PIGLIN = new Type("piglin");
        final static public Type DRAGON = new Type("dragon");
        final private String id;
        final static private Type[] field_11509;

        public static Type[] values() {
            return (Type[])field_11509.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String id) {
            this.id = id;
            TYPES.put(id, this);
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static Type[] method_36710() {
            return new Type[]{SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, PIGLIN, DRAGON};
        }

        static {
            field_11509 = Type.method_36710();
        }
    }
}

