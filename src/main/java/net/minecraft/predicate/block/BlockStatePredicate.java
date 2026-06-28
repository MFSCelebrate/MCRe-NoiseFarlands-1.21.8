/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public class BlockStatePredicate
implements Predicate<BlockState> {
    final static public Predicate<BlockState> ANY = state -> true;
    final private StateManager<Block, BlockState> manager;
    final private Map<Property<?>, Predicate<Object>> propertyTests = Maps.newHashMap();

    private BlockStatePredicate(StateManager<Block, BlockState> manager) {
        this.manager = manager;
    }

    public static BlockStatePredicate forBlock(Block block) {
        return new BlockStatePredicate(block.getStateManager());
    }

    @Override
    public boolean test(@Nullable BlockState blockState) {
        if (blockState == null || !blockState.getBlock().equals(this.manager.getOwner())) {
            return false;
        }
        if (this.propertyTests.isEmpty()) {
            return true;
        }
        for (Map.Entry<Property<?>, Predicate<Object>> entry : this.propertyTests.entrySet()) {
            if (this.testProperty(blockState, entry.getKey(), entry.getValue())) continue;
            return false;
        }
        return true;
    }

    protected <T extends Comparable<T>> boolean testProperty(BlockState blockState, Property<T> property, Predicate<Object> predicate) {
        T comparable = blockState.get(property);
        return predicate.test(comparable);
    }

    public <V extends Comparable<V>> BlockStatePredicate with(Property<V> property, Predicate<Object> predicate) {
        if (!this.manager.getProperties().contains(property)) {
            throw new IllegalArgumentException(String.valueOf(this.manager) + " cannot support property " + String.valueOf(property));
        }
        this.propertyTests.put(property, predicate);
        return this;
    }

    @Override
    public boolean test(@Nullable Object state) {
        return this.test((BlockState)state);
    }
}

