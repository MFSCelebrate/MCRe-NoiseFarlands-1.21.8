/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.AttachedToLeavesTreeDecorator;
import net.minecraft.world.gen.treedecorator.AttachedToLogsTreeDecorator;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.CocoaTreeDecorator;
import net.minecraft.world.gen.treedecorator.CreakingHeartTreeDecorator;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.PaleMossTreeDecorator;
import net.minecraft.world.gen.treedecorator.PlaceOnGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;

public class TreeDecoratorType<P extends TreeDecorator> {
    final static public TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = TreeDecoratorType.register("trunk_vine", TrunkVineTreeDecorator.CODEC);
    final static public TreeDecoratorType<LeavesVineTreeDecorator> LEAVE_VINE = TreeDecoratorType.register("leave_vine", LeavesVineTreeDecorator.CODEC);
    final static public TreeDecoratorType<PaleMossTreeDecorator> PALE_MOSS = TreeDecoratorType.register("pale_moss", PaleMossTreeDecorator.CODEC);
    final static public TreeDecoratorType<CreakingHeartTreeDecorator> CREAKING_HEART = TreeDecoratorType.register("creaking_heart", CreakingHeartTreeDecorator.CODEC);
    final static public TreeDecoratorType<CocoaTreeDecorator> COCOA = TreeDecoratorType.register("cocoa", CocoaTreeDecorator.CODEC);
    final static public TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = TreeDecoratorType.register("beehive", BeehiveTreeDecorator.CODEC);
    final static public TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = TreeDecoratorType.register("alter_ground", AlterGroundTreeDecorator.CODEC);
    final static public TreeDecoratorType<AttachedToLeavesTreeDecorator> ATTACHED_TO_LEAVES = TreeDecoratorType.register("attached_to_leaves", AttachedToLeavesTreeDecorator.CODEC);
    final static public TreeDecoratorType<PlaceOnGroundTreeDecorator> PLACE_ON_GROUND = TreeDecoratorType.register("place_on_ground", PlaceOnGroundTreeDecorator.CODEC);
    final static public TreeDecoratorType<AttachedToLogsTreeDecorator> ATTACHED_TO_LOGS = TreeDecoratorType.register("attached_to_logs", AttachedToLogsTreeDecorator.CODEC);
    final private MapCodec<P> codec;

    private static <P extends TreeDecorator> TreeDecoratorType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.TREE_DECORATOR_TYPE, id, new TreeDecoratorType<P>(codec));
    }

    public TreeDecoratorType(MapCodec<P> codec) {
        this.codec = codec;
    }

    public MapCodec<P> getCodec() {
        return this.codec;
    }
}

